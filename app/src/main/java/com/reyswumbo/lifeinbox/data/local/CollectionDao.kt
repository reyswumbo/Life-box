package com.reyswumbo.lifeinbox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(collection: CollectionEntity): Long

    @Query("SELECT * FROM collections ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Query(
        """
        SELECT c.id, c.name, c.colorHex, c.icon, c.isDefault, c.sortOrder, c.createdAt,
               (SELECT COUNT(*) FROM links l WHERE l.collectionId = c.id) AS linkCount
        FROM collections c
        ORDER BY c.sortOrder ASC, c.name ASC
        """
    )
    fun observeWithCounts(): Flow<List<CollectionWithCount>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CollectionEntity?

    @Query("SELECT * FROM collections WHERE isDefault = 1 ORDER BY sortOrder ASC LIMIT 1")
    suspend fun getDefault(): CollectionEntity?

    @Query("SELECT * FROM collections WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun findByName(name: String): CollectionEntity?

    @Query("SELECT MAX(sortOrder) FROM collections")
    suspend fun maxSortOrder(): Int?

    @Query("UPDATE collections SET name = :name, colorHex = :colorHex, icon = :icon WHERE id = :id")
    suspend fun update(id: Long, name: String, colorHex: String, icon: String)

    @Query(
        "UPDATE links SET collectionId = :newCollectionId, updatedAt = :updatedAt " +
            "WHERE collectionId = :oldCollectionId"
    )
    suspend fun reassignLinks(oldCollectionId: Long, newCollectionId: Long, updatedAt: Long)

    @Query("DELETE FROM collections WHERE id = :id AND isDefault = 0")
    suspend fun deleteById(id: Long): Int

    @Transaction
    suspend fun deleteAndReassign(id: Long, fallbackId: Long, updatedAt: Long): Int {
        reassignLinks(id, fallbackId, updatedAt)
        return deleteById(id)
    }
}
