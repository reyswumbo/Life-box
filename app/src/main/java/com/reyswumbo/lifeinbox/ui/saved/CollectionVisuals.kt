package com.reyswumbo.lifeinbox.ui.saved

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

object CollectionVisuals {

    val palette: List<String> = listOf(
        "#E7EEDD",
        "#F3E9B9",
        "#EFEDF6",
        "#F7F4E8",
        "#DCEBF0",
        "#F6E0E0",
        "#E4E9F7"
    )

    val iconKeys: List<String> = listOf(
        "inbox", "idea", "spark", "book", "star", "heart", "folder", "tag", "leaf"
    )

    fun iconFor(key: String): ImageVector = when (key) {
        "inbox" -> Icons.Filled.Inbox
        "idea" -> Icons.Filled.Lightbulb
        "spark" -> Icons.Filled.AutoAwesome
        "book" -> Icons.Filled.MenuBook
        "star" -> Icons.Filled.Star
        "heart" -> Icons.Filled.Favorite
        "folder" -> Icons.Filled.Folder
        "tag" -> Icons.Filled.Label
        "leaf" -> Icons.Filled.Eco
        else -> Icons.Filled.Folder
    }

    fun colorFromHex(hex: String): Color = try {
        val cleaned = hex.removePrefix("#")
        val rgb = cleaned.toLong(16)
        Color(0xFF000000L or rgb)
    } catch (e: Exception) {
        LifeInboxColors.PaleSage
    }
}
