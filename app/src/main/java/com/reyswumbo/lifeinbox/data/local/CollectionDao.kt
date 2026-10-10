package com.reyswumbo.lifeinbox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(collection: CollectionEntity): Long

    @Query("SELECT * FROM collections ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CollectionEntity?

    @Query("SELECT * FROM collections WHERE isDefault = 1 ORDER BY sortOrder ASC LIMIT 1")
    suspend fun getDefault(): CollectionEntity?
}
