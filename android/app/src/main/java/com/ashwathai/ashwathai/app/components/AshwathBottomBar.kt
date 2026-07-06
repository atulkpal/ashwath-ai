package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ashwathai.ashwathai.app.navigation.Screen
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.PureBlack
import com.ashwathai.ashwathai.app.theme.SurfaceTier1

@Composable
fun AshwathBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceTier1,
        contentColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screen.label,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    screen.label?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 0.5.sp,
                            fontSize = 9.sp
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PureBlack,
                    selectedTextColor = CyanPrimary,
                    indicatorColor = CyanPrimary,
                    unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                    unselectedTextColor = Color.Gray.copy(alpha = 0.6f)
                )
            )
        }
    }
}
