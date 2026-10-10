package com.reyswumbo.lifeinbox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(link: LinkEntity): Long

    @Update
    suspend fun update(link: LinkEntity)

    @Query("DELETE FROM links WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM links WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LinkEntity?

    @Query("SELECT * FROM links WHERE url = :normalizedUrl LIMIT 1")
    suspend fun findByNormalizedUrl(normalizedUrl: String): LinkEntity?

    @Query("SELECT * FROM links ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<LinkEntity>>

    @Query("SELECT COUNT(*) FROM links WHERE createdAt >= :startOfDay")
    fun observeCreatedSince(startOfDay: Long): Flow<Int>

    @Query(
        """
        SELECT * FROM links
        WHERE :query = ''
           OR title LIKE '%' || :query || '%'
           OR url LIKE '%' || :query || '%'
           OR domain LIKE '%' || :query || '%'
           OR notes LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
        """
    )
    fun search(query: String): Flow<List<LinkEntity>>

    @Query("UPDATE links SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean, updatedAt: Long)
}
