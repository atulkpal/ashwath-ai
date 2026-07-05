package com.ashwathai.ashwathai.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashwathai.ashwathai.di.ServiceLocator
import com.ashwathai.ashwathai.features.chat.ui.ChatScreen
import com.ashwathai.ashwathai.features.download.ui.DownloadScreen
import com.ashwathai.ashwathai.features.explore.ui.ExploreScreen
import com.ashwathai.ashwathai.features.knowledge.ui.KnowledgeScreen
import com.ashwathai.ashwathai.features.library.ui.LibraryScreen
import com.ashwathai.ashwathai.features.onboarding.ui.OnboardingScreen
import com.ashwathai.ashwathai.features.settings.ui.SettingsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AshwathNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var hasChecked by remember { mutableStateOf(false) }
    var hasModels by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val repo = ServiceLocator.provideModelRepository()
        repo.getInstalledModels().collect { models ->
            hasModels = models.isNotEmpty()
            hasChecked = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
        modifier = modifier,
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    if (hasChecked && hasModels) {
                        navController.navigate(Screen.Chat.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Download.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(Screen.Download.route) {
            DownloadScreen(
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Download.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Explore.route) { ExploreScreen() }
        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateToDownload = {
                    navController.navigate(Screen.Download.route)
                },
            )
        }
        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateToExplore = { navController.navigate(Screen.Explore.route) },
            )
        }
        composable(Screen.Knowledge.route) { KnowledgeScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
