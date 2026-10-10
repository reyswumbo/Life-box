package com.reyswumbo.lifeinbox.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Home("home", "Home", Icons.Filled.Home),
    Saved("saved", "Saved", Icons.Filled.Bookmark),
    PetLab("pet_lab", "Pet Lab", Icons.Filled.Science)
}
