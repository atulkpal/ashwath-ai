package com.ashwathai.ashwathai.app.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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
        contentColor = Color.White
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
                            contentDescription = screen.label
                        )
                    }
                },
                label = {
                    screen.label?.let {
                        Text(text = it)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PureBlack,
                    selectedTextColor = CyanPrimary,
                    indicatorColor = CyanPrimary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
