package com.ashwathai.ashwathai.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashwathai.ashwathai.features.chat.ui.ChatScreen
import com.ashwathai.ashwathai.features.explore.ui.ExploreScreen
import com.ashwathai.ashwathai.features.knowledge.ui.KnowledgeScreen
import com.ashwathai.ashwathai.features.library.ui.LibraryScreen
import com.ashwathai.ashwathai.features.onboarding.ui.OnboardingScreen
import com.ashwathai.ashwathai.features.settings.ui.SettingsScreen

@Composable
fun AshwathNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Explore.route,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) { OnboardingScreen() }
        composable(Screen.Explore.route) { ExploreScreen() }
        composable(Screen.Chat.route) { ChatScreen() }
        composable(Screen.Library.route) { LibraryScreen() }
        composable(Screen.Knowledge.route) { KnowledgeScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
