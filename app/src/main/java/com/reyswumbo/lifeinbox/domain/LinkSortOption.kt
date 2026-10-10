package com.reyswumbo.lifeinbox.domain

import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity

enum class LinkSortOption(val label: String) {
    Newest("Newest first"),
    Oldest("Oldest first"),
    TitleAZ("Title A-Z"),
    DomainAZ("Domain A-Z");

    fun sort(links: List<LinkEntity>): List<LinkEntity> = when (this) {
        Newest -> links.sortedByDescending { it.createdAt }
        Oldest -> links.sortedBy { it.createdAt }
        TitleAZ -> links.sortedBy { it.title.lowercase() }
        DomainAZ -> links.sortedWith(
            compareBy({ it.domain.lowercase() }, { it.title.lowercase() })
        )
    }
}
