package com.reyswumbo.lifeinbox.domain

import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class LinkSortOptionTest {

    private fun link(id: Long, title: String, domain: String, createdAt: Long) = LinkEntity(
        id = id,
        url = "https://$domain/$title",
        originalUrl = "https://$domain/$title",
        title = title,
        domain = domain,
        collectionId = 1L,
        createdAt = createdAt,
        updatedAt = createdAt
    )

    private val links = listOf(
        link(1, "Beta", "zebra.com", 300),
        link(2, "alpha", "apple.com", 100),
        link(3, "Gamma", "mango.com", 200)
    )

    @Test
    fun newestSortsByCreatedAtDescending() {
        assertEquals(
            listOf(1L, 3L, 2L),
            LinkSortOption.Newest.sort(links).map { it.id }
        )
    }

    @Test
    fun oldestSortsByCreatedAtAscending() {
        assertEquals(
            listOf(2L, 3L, 1L),
            LinkSortOption.Oldest.sort(links).map { it.id }
        )
    }

    @Test
    fun titleSortIsCaseInsensitive() {
        assertEquals(
            listOf(2L, 1L, 3L),
            LinkSortOption.TitleAZ.sort(links).map { it.id }
        )
    }

    @Test
    fun domainSortUsesDomainThenTitle() {
        assertEquals(
            listOf(2L, 3L, 1L),
            LinkSortOption.DomainAZ.sort(links).map { it.id }
        )
    }
}
