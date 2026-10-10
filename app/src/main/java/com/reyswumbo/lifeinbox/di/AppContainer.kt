package com.reyswumbo.lifeinbox.di

import android.content.Context
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import com.reyswumbo.lifeinbox.data.repository.CollectionRepository
import com.reyswumbo.lifeinbox.data.repository.LinkRepository

class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    private val database: LifeInboxDatabase by lazy {
        LifeInboxDatabase.build(applicationContext)
    }

    val collectionRepository: CollectionRepository by lazy {
        CollectionRepository(database.collectionDao())
    }

    val linkRepository: LinkRepository by lazy {
        LinkRepository(database.linkDao(), collectionRepository)
    }
}
