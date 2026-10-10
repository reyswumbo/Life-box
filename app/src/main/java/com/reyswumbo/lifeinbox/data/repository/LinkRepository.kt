package com.reyswumbo.lifeinbox.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import com.reyswumbo.lifeinbox.data.local.LinkDao
import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity
import com.reyswumbo.lifeinbox.domain.UrlValidationResult
import com.reyswumbo.lifeinbox.domain.UrlValidator
import kotlinx.coroutines.flow.Flow

sealed interface SaveLinkResult {
    data class Success(val id: Long) : SaveLinkResult
    data class Duplicate(val existing: LinkEntity) : SaveLinkResult
    data class InvalidUrl(val reason: String) : SaveLinkResult
    data class Error(val message: String) : SaveLinkResult
}

class LinkRepository(
    private val linkDao: LinkDao,
    private val collectionRepository: CollectionRepository,
    private val clock: () -> Long = System::currentTimeMillis
) {

    fun observeLinks(query: String): Flow<List<LinkEntity>> =
        if (query.isBlank()) linkDao.observeAll() else linkDao.search(query.trim())

    suspend fun addLink(
        rawUrl: String,
        title: String?,
        notes: String,
        collectionId: Long?
    ): SaveLinkResult {
        val validation = UrlValidator.validate(rawUrl)
        if (validation is UrlValidationResult.Invalid) {
            return SaveLinkResult.InvalidUrl(validation.reason)
        }
        val valid = validation as UrlValidationResult.Valid

        linkDao.findByNormalizedUrl(valid.normalizedUrl)?.let {
            return SaveLinkResult.Duplicate(it)
        }

        val targetCollection = collectionId ?: collectionRepository.defaultCollectionId()
        val now = clock()
        val entity = LinkEntity(
            url = valid.normalizedUrl,
            originalUrl = valid.originalUrl,
            title = title?.trim().takeUnless { it.isNullOrEmpty() } ?: valid.suggestedTitle,
            domain = valid.domain,
            notes = notes.trim(),
            collectionId = targetCollection,
            isFavorite = false,
            createdAt = now,
            updatedAt = now
        )

        return try {
            SaveLinkResult.Success(linkDao.insert(entity))
        } catch (e: SQLiteConstraintException) {
            val existing = linkDao.findByNormalizedUrl(valid.normalizedUrl)
            if (existing != null) {
                SaveLinkResult.Duplicate(existing)
            } else {
                SaveLinkResult.Error(e.message ?: "Could not save link.")
            }
        } catch (e: Exception) {
            SaveLinkResult.Error(e.message ?: "Could not save link.")
        }
    }

    suspend fun updateLink(
        id: Long,
        title: String,
        notes: String,
        collectionId: Long
    ): SaveLinkResult {
        val existing = linkDao.getById(id)
            ?: return SaveLinkResult.Error("This link no longer exists.")
        val updated = existing.copy(
            title = title.trim().ifEmpty { existing.domain },
            notes = notes.trim(),
            collectionId = collectionId,
            updatedAt = clock()
        )
        return try {
            linkDao.update(updated)
            SaveLinkResult.Success(updated.id)
        } catch (e: Exception) {
            SaveLinkResult.Error(e.message ?: "Could not update link.")
        }
    }

    suspend fun moveLink(id: Long, collectionId: Long): SaveLinkResult {
        val existing = linkDao.getById(id)
            ?: return SaveLinkResult.Error("This link no longer exists.")
        if (existing.collectionId == collectionId) {
            return SaveLinkResult.Success(existing.id)
        }
        return updateLink(id, existing.title, existing.notes, collectionId)
    }

    suspend fun deleteLink(id: Long): Boolean = try {
        linkDao.deleteById(id)
        true
    } catch (e: Exception) {
        false
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean): Boolean = try {
        linkDao.updateFavorite(id, isFavorite, clock())
        true
    } catch (e: Exception) {
        false
    }

    suspend fun isDuplicate(rawUrl: String): Boolean {
        val validation = UrlValidator.validate(rawUrl)
        if (validation !is UrlValidationResult.Valid) return false
        return linkDao.findByNormalizedUrl(validation.normalizedUrl) != null
    }

    companion object {
        val DEFAULT_COLLECTION_ID = LifeInboxDatabase.INBOX_COLLECTION_ID
    }
}
