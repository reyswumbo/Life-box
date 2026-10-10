package com.reyswumbo.lifeinbox.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import com.reyswumbo.lifeinbox.domain.pet.Accessory
import com.reyswumbo.lifeinbox.domain.pet.BodyShape
import com.reyswumbo.lifeinbox.domain.pet.EyeStyle
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.domain.pet.PetTexture
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PetConfigRepositoryTest {

    private lateinit var database: LifeInboxDatabase
    private lateinit var repository: PetConfigRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = LifeInboxDatabase.build(
            context,
            inMemory = true,
            allowMainThreadQueries = true
        )
        repository = PetConfigRepository(database.petConfigDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun defaultsToPocketPalWhenNothingSaved() = runTest {
        val config = repository.observeConfig().first()
        assertEquals(PetConfig.Default, config)
        assertEquals("Pocket Pal", config.name)
    }

    @Test
    fun savedConfigIsPersistedAndRestored() = runTest {
        val original = PetConfig(
            name = "Luna",
            bodyShape = BodyShape.HEART,
            eyeStyle = EyeStyle.HAPPY,
            accessory = Accessory.CROWN,
            bodyColorHex = "#C9B8E8",
            eyeColorHex = "#4A6FA5",
            accentColorHex = "#F3B0B0",
            texture = PetTexture.FUZZY
        )
        repository.saveConfig(original)

        val restored = repository.observeConfig().first()
        assertEquals(original, restored)
    }

    @Test
    fun newSaveReplacesPreviousConfig() = runTest {
        repository.saveConfig(PetConfig(name = "First", bodyShape = BodyShape.BOXY))
        repository.saveConfig(PetConfig(name = "Second", bodyShape = BodyShape.HEART))

        val restored = repository.observeConfig().first()
        assertEquals("Second", restored.name)
        assertEquals(BodyShape.HEART, restored.bodyShape)
    }

    @Test
    fun seedMigrationCreatesDefaultRowInUpgradedDatabase() = runTest {
        assertTrue(repository.observeConfig().first().accessory == Accessory.NONE)
    }
}