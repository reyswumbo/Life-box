package com.reyswumbo.lifeinbox.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.reyswumbo.lifeinbox.data.local.LifeInboxDatabase
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
class LinkRepositoryTest {

    private lateinit var database: LifeInboxDatabase
    private lateinit var linkRepository: LinkRepository
    private lateinit var collectionRepository: CollectionRepository
    private var now = 1_000_000L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = LifeInboxDatabase.build(
            context,
            inMemory = true,
            allowMainThreadQueries = true
        )
        val linkDao = database.linkDao()
        collectionRepository = CollectionRepository(database.collectionDao())
        linkRepository = LinkRepository(linkDao, collectionRepository) { now }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun seedsDefaultCollections() = runTest {
        val collections = collectionRepository.observeCollections().first()
        assertEquals(3, collections.size)
        val default = collections.first { it.isDefault }
        assertEquals(LifeInboxDatabase.INBOX_COLLECTION_ID, default.id)
        assertEquals("Inbox", default.name)
        assertTrue(collections.any { it.name == "Ideas" })
        assertTrue(collections.any { it.name == "Inspiration" })
    }

    @Test
    fun addLinkStoresInDefaultInbox() = runTest {
        val result = linkRepository.addLink("https://example.com/a", null, "", null)

        assertTrue(result is SaveLinkResult.Success)
        val links = linkRepository.observeLinks("").first()
        assertEquals(1, links.size)
        assertEquals(LifeInboxDatabase.INBOX_COLLECTION_ID, links.first().collectionId)
        assertEquals("https://example.com/a", links.first().url)
    }

    @Test
    fun addLinkDerivesTitleWhenMissing() = runTest {
        linkRepository.addLink("https://example.com/posts/hello-world", null, "", null)
        val link = linkRepository.observeLinks("").first().single()
        assertEquals("hello world", link.title)
        assertEquals("example.com", link.domain)
    }

    @Test
    fun addLinkUsesProvidedTitle() = runTest {
        linkRepository.addLink("https://example.com/x", "My Title", "note", null)
        val link = linkRepository.observeLinks("").first().single()
        assertEquals("My Title", link.title)
        assertEquals("note", link.notes)
    }

    @Test
    fun addLinkRejectsInvalidUrl() = runTest {
        val result = linkRepository.addLink("not a url at all", null, "", null)
        assertTrue(result is SaveLinkResult.InvalidUrl)
        assertTrue(linkRepository.observeLinks("").first().isEmpty())
    }

    @Test
    fun addLinkDetectsExactDuplicate() = runTest {
        linkRepository.addLink("https://example.com/dup", null, "", null)
        val second = linkRepository.addLink("https://example.com/dup", null, "", null)

        assertTrue(second is SaveLinkResult.Duplicate)
        assertEquals(1, linkRepository.observeLinks("").first().size)
    }

    @Test
    fun addLinkDetectsNormalizedDuplicate() = runTest {
        linkRepository.addLink("https://example.com/Path", null, "", null)
        val second = linkRepository.addLink("HTTPS://Example.com/Path/", null, "", null)

        assertTrue(second is SaveLinkResult.Duplicate)
        assertEquals(1, linkRepository.observeLinks("").first().size)
    }

    @Test
    fun deleteRemovesLink() = runTest {
        val added = linkRepository.addLink("https://example.com/delete", null, "", null)
        val id = (added as SaveLinkResult.Success).id

        assertTrue(linkRepository.deleteLink(id))
        assertTrue(linkRepository.observeLinks("").first().isEmpty())
    }

    @Test
    fun updateChangesTitleNotesAndCollection() = runTest {
        val added = linkRepository.addLink("https://example.com/edit", null, "", null)
        val id = (added as SaveLinkResult.Success).id

        val result = linkRepository.updateLink(
            id = id,
            title = "Updated",
            notes = "new note",
            collectionId = LifeInboxDatabase.IDEAS_COLLECTION_ID
        )

        assertTrue(result is SaveLinkResult.Success)
        val link = linkRepository.observeLinks("").first().single()
        assertEquals("Updated", link.title)
        assertEquals("new note", link.notes)
        assertEquals(LifeInboxDatabase.IDEAS_COLLECTION_ID, link.collectionId)
    }

    @Test
    fun toggleFavoritePersistsFlag() = runTest {
        val added = linkRepository.addLink("https://example.com/fav", null, "", null)
        val id = (added as SaveLinkResult.Success).id

        assertTrue(linkRepository.toggleFavorite(id, true))
        assertTrue(linkRepository.observeLinks("").first().single().isFavorite)

        assertTrue(linkRepository.toggleFavorite(id, false))
        assertTrue(!linkRepository.observeLinks("").first().single().isFavorite)
    }

    @Test
    fun searchMatchesTitleAndDomain() = runTest {
        linkRepository.addLink("https://kotlinlang.org/docs", "Kotlin Docs", "", null)
        linkRepository.addLink("https://example.com/other", "Something Else", "", null)

        val byTitle = linkRepository.observeLinks("docs").first()
        assertEquals(1, byTitle.size)
        assertEquals("Kotlin Docs", byTitle.first().title)

        val byDomain = linkRepository.observeLinks("kotlinlang").first()
        assertEquals(1, byDomain.size)
    }

    @Test
    fun addingLinksKeepsNewestFirstInRawOrder() = runTest {
        now = 100L
        linkRepository.addLink("https://example.com/old", null, "", null)
        now = 200L
        linkRepository.addLink("https://example.com/new", null, "", null)

        val links = linkRepository.observeLinks("").first()
        assertEquals("https://example.com/new", links.first().url)
    }

    @Test
    fun isDuplicateReturnsFalseForInvalidUrl() = runTest {
        assertTrue(!linkRepository.isDuplicate("not a link"))
    }
}
