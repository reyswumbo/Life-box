package com.reyswumbo.lifeinbox.data.repository

import com.reyswumbo.lifeinbox.data.local.PetConfigDao
import com.reyswumbo.lifeinbox.data.local.entity.toDomain
import com.reyswumbo.lifeinbox.data.local.entity.toEntity
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PetConfigRepository(
    private val petConfigDao: PetConfigDao
) {

    fun observeConfig(): Flow<PetConfig> =
        petConfigDao.observeConfig().map { it?.toDomain() ?: PetConfig.Default }

    suspend fun saveConfig(config: PetConfig) {
        petConfigDao.save(config.toEntity())
    }
}