package com.ashwathai.ashwathai.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String? = null,
    val icon: ImageVector? = null
) {
    object Onboarding : Screen("onboarding")
    object Download : Screen("download")

    object Chat : Screen(
        route = "chat",
        label = "Chat",
        icon = Icons.AutoMirrored.Filled.Chat
    )

    object Library : Screen(
        route = "library",
        label = "Library",
        icon = Icons.AutoMirrored.Filled.LibraryBooks
    )

    object Knowledge : Screen(
        route = "knowledge",
        label = "Knowledge",
        icon = Icons.Default.Collections
    )

    object Explore : Screen(
        route = "explore",
        label = "Explore",
        icon = Icons.Default.Explore
    )

    object Settings : Screen(
        route = "settings",
        label = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        val bottomNavItems = listOf(
            Chat,
            Library,
            Knowledge,
            Explore,
            Settings
        )
    }
}
