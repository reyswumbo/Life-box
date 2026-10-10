package com.reyswumbo.lifeinbox.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CollectionRepositoryTest {

    private lateinit var database: LifeInboxDatabase
    private lateinit var collectionRepository: CollectionRepository
    private lateinit var linkRepository: LinkRepository
    private var now = 1_000_000L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = LifeInboxDatabase.build(
            context,
            inMemory = true,
            allowMainThreadQueries = true
        )
        collectionRepository = CollectionRepository(database.collectionDao()) { now }
        linkRepository = LinkRepository(database.linkDao(), collectionRepository) { now }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createCollectionAddsIt() = runTest {
        val result = collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")

        assertTrue(result is CollectionResult.Success)
        val collections = collectionRepository.observeCollections().first()
        assertEquals(4, collections.size)
        assertTrue(collections.any { it.name == "Recipes" })
    }

    @Test
    fun createCollectionRejectsDuplicateNameCaseInsensitive() = runTest {
        collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val result = collectionRepository.createCollection("recipes", "#E7EEDD", "folder")

        assertTrue(result is CollectionResult.DuplicateName)
        assertEquals(4, collectionRepository.observeCollections().first().size)
    }

    @Test
    fun createCollectionRejectsBlankName() = runTest {
        val result = collectionRepository.createCollection("   ", "#E7EEDD", "folder")
        assertTrue(result is CollectionResult.Invalid)
    }

    @Test
    fun createCollectionRejectsOverlongName() = runTest {
        val result = collectionRepository.createCollection("x".repeat(41), "#E7EEDD", "folder")
        assertTrue(result is CollectionResult.Invalid)
    }

    @Test
    fun renameCollectionUpdatesNameColorAndIcon() = runTest {
        val created = collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val id = (created as CollectionResult.Success).id

        val result = collectionRepository.renameCollection(id, "Cooking", "#DCEBF0", "book")

        assertTrue(result is CollectionResult.Success)
        val updated = collectionRepository.getById(id)
        assertEquals("Cooking", updated?.name)
        assertEquals("#DCEBF0", updated?.colorHex)
        assertEquals("book", updated?.icon)
    }

    @Test
    fun renameCollectionRejectsClashWithOtherCollection() = runTest {
        collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val created = collectionRepository.createCollection("Trips", "#DCEBF0", "folder")
        val id = (created as CollectionResult.Success).id

        val result = collectionRepository.renameCollection(id, "Recipes", "#DCEBF0", "folder")

        assertTrue(result is CollectionResult.DuplicateName)
    }

    @Test
    fun renameCollectionAllowsKeepingSameName() = runTest {
        val created = collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val id = (created as CollectionResult.Success).id

        val result = collectionRepository.renameCollection(id, "Recipes", "#E7EEDD", "leaf")

        assertTrue(result is CollectionResult.Success)
        assertEquals("#E7EEDD", collectionRepository.getById(id)?.colorHex)
    }

    @Test
    fun deleteCollectionMovesLinksToInboxWithoutDeletingThem() = runTest {
        val created = collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val id = (created as CollectionResult.Success).id
        linkRepository.addLink("https://example.com/a", null, "", id)
        linkRepository.addLink("https://example.com/b", null, "", id)

        val result = collectionRepository.deleteCollection(id)

        assertTrue(result is CollectionResult.Success)
        assertNull(collectionRepository.getById(id))
        val links = linkRepository.observeLinks("").first()
        assertEquals(2, links.size)
        assertTrue(links.all { it.collectionId == LifeInboxDatabase.INBOX_COLLECTION_ID })
    }

    @Test
    fun deleteDefaultCollectionIsRejected() = runTest {
        val result = collectionRepository.deleteCollection(LifeInboxDatabase.INBOX_COLLECTION_ID)

        assertTrue(result is CollectionResult.Invalid)
        assertTrue(collectionRepository.getById(LifeInboxDatabase.INBOX_COLLECTION_ID) != null)
    }

    @Test
    fun deleteUnknownCollectionReturnsError() = runTest {
        val result = collectionRepository.deleteCollection(999L)
        assertTrue(result is CollectionResult.Error)
    }

    @Test
    fun countsReflectStoredLinks() = runTest {
        val created = collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val id = (created as CollectionResult.Success).id
        linkRepository.addLink("https://example.com/a", null, "", id)
        linkRepository.addLink("https://example.com/b", null, "", null)

        val counts = collectionRepository.observeCollectionsWithCount().first()
        assertEquals(1, counts.first { it.collection.id == id }.linkCount)
        assertEquals(
            1,
            counts.first { it.collection.id == LifeInboxDatabase.INBOX_COLLECTION_ID }.linkCount
        )
    }

    @Test
    fun moveLinkChangesCollection() = runTest {
        val created = collectionRepository.createCollection("Recipes", "#F6E0E0", "heart")
        val id = (created as CollectionResult.Success).id
        val added = linkRepository.addLink("https://example.com/move", null, "", null)
        val linkId = (added as SaveLinkResult.Success).id

        val moved = linkRepository.moveLink(linkId, id)

        assertTrue(moved is SaveLinkResult.Success)
        assertEquals(id, linkRepository.observeLinks("").first().single().collectionId)
    }
}
