package com.reyswumbo.lifeinbox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reyswumbo.lifeinbox.data.local.entity.PetConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetConfigDao {

    @Query("SELECT * FROM pet_config WHERE id = 1 LIMIT 1")
    fun observeConfig(): Flow<PetConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(config: PetConfigEntity)
}