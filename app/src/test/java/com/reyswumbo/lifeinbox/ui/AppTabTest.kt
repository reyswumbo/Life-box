package com.reyswumbo.lifeinbox.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppTabTest {

    @Test
    fun tabs_haveUniqueRoutes() {
        val routes = AppTab.entries.map { it.route }
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun tabs_containsThreePrimaryTabs() {
        assertEquals(3, AppTab.entries.size)
        assertEquals(
            listOf("home", "saved", "pet_lab"),
            AppTab.entries.map { it.route }
        )
    }

    @Test
    fun tabs_labelsAreNonEmpty() {
        assertTrue(AppTab.entries.all { it.label.isNotBlank() })
    }
}
