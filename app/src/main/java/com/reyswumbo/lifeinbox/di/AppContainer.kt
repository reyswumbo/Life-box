package com.reyswumbo.lifeinbox.di

import android.content.Context
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import com.reyswumbo.lifeinbox.data.repository.CollectionRepository
import com.reyswumbo.lifeinbox.data.repository.LinkRepository
import com.reyswumbo.lifeinbox.data.repository.PetConfigRepository
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val database: LifeInboxDatabase by lazy {
        LifeInboxDatabase.build(applicationContext)
    }

    val collectionRepository: CollectionRepository by lazy {
        CollectionRepository(database.collectionDao())
    }

    val linkRepository: LinkRepository by lazy {
        LinkRepository(database.linkDao(), collectionRepository)
    }

    val petConfigRepository: PetConfigRepository by lazy {
        PetConfigRepository(database.petConfigDao())
    }

    val petConfig: StateFlow<PetConfig> by lazy {
        petConfigRepository.observeConfig().stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = PetConfig.Default
        )
    }
}
