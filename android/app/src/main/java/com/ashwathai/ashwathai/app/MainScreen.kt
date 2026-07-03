package com.ashwathai.ashwathai.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ashwathai.ashwathai.app.components.AshwathBottomBar
import com.ashwathai.ashwathai.app.navigation.AshwathNavHost

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = { AshwathBottomBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AshwathNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
