package com.reyswumbo.lifeinbox.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.reyswumbo.lifeinbox.data.local.CollectionDao
import com.reyswumbo.lifeinbox.data.local.CollectionWithCount
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

sealed interface CollectionResult {
    data class Success(val id: Long) : CollectionResult
    data object DuplicateName : CollectionResult
    data class Invalid(val reason: String) : CollectionResult
    data class Error(val message: String) : CollectionResult
}

class CollectionRepository(
    private val collectionDao: CollectionDao,
    private val clock: () -> Long = System::currentTimeMillis
) {

    fun observeCollections(): Flow<List<CollectionEntity>> = collectionDao.observeAll()

    fun observeCollectionsWithCount(): Flow<List<CollectionWithCount>> =
        collectionDao.observeWithCounts()

    suspend fun getById(id: Long): CollectionEntity? = collectionDao.getById(id)

    suspend fun findByName(name: String): CollectionEntity? =
        collectionDao.findByName(name.trim())

    suspend fun defaultCollectionId(): Long =
        collectionDao.getDefault()?.id ?: LifeInboxDatabase.INBOX_COLLECTION_ID

    suspend fun createCollection(
        name: String,
        colorHex: String,
        icon: String
    ): CollectionResult {
        val trimmed = name.trim()
        validateName(trimmed)?.let { return CollectionResult.Invalid(it) }

        if (collectionDao.findByName(trimmed) != null) {
            return CollectionResult.DuplicateName
        }

        val entity = CollectionEntity(
            name = trimmed,
            colorHex = colorHex,
            icon = icon,
            isDefault = false,
            sortOrder = (collectionDao.maxSortOrder() ?: -1) + 1,
            createdAt = clock()
        )

        return try {
            CollectionResult.Success(collectionDao.insert(entity))
        } catch (e: SQLiteConstraintException) {
            CollectionResult.DuplicateName
        } catch (e: Exception) {
            CollectionResult.Error(e.message ?: "Could not create collection.")
        }
    }

    suspend fun renameCollection(
        id: Long,
        name: String,
        colorHex: String,
        icon: String
    ): CollectionResult {
        val trimmed = name.trim()
        validateName(trimmed)?.let { return CollectionResult.Invalid(it) }

        val existing = collectionDao.getById(id)
            ?: return CollectionResult.Error("This collection no longer exists.")

        val clash = collectionDao.findByName(trimmed)
        if (clash != null && clash.id != id) {
            return CollectionResult.DuplicateName
        }

        return try {
            collectionDao.update(id, trimmed, colorHex, icon)
            CollectionResult.Success(existing.id)
        } catch (e: SQLiteConstraintException) {
            CollectionResult.DuplicateName
        } catch (e: Exception) {
            CollectionResult.Error(e.message ?: "Could not rename collection.")
        }
    }

    suspend fun deleteCollection(id: Long): CollectionResult {
        val existing = collectionDao.getById(id)
            ?: return CollectionResult.Error("This collection no longer exists.")

        if (existing.isDefault) {
            return CollectionResult.Invalid("The Inbox collection cannot be deleted.")
        }

        val fallback = defaultCollectionId()
        return try {
            val deleted = collectionDao.deleteAndReassign(id, fallback, clock())
            if (deleted > 0) {
                CollectionResult.Success(id)
            } else {
                CollectionResult.Error("Could not delete collection.")
            }
        } catch (e: Exception) {
            CollectionResult.Error(e.message ?: "Could not delete collection.")
        }
    }

    private fun validateName(name: String): String? = when {
        name.isEmpty() -> "Enter a collection name."
        name.length > MAX_NAME_LENGTH -> "Keep names to $MAX_NAME_LENGTH characters or fewer."
        else -> null
    }

    companion object {
        const val MAX_NAME_LENGTH = 40
    }
}
