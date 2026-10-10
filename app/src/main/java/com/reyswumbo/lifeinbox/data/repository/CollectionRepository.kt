package com.reyswumbo.lifeinbox.data.repository

import com.reyswumbo.lifeinbox.data.local.CollectionDao
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

class CollectionRepository(
    private val collectionDao: CollectionDao
) {

    fun observeCollections(): Flow<List<CollectionEntity>> = collectionDao.observeAll()

    suspend fun getById(id: Long): CollectionEntity? = collectionDao.getById(id)

    suspend fun defaultCollectionId(): Long =
        collectionDao.getDefault()?.id ?: LifeInboxDatabase.INBOX_COLLECTION_ID
}
