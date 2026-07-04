package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.PureBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshwathTopBar(
    title: String,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary
                    )
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PureBlack,
            titleContentColor = Color.White,
            actionIconContentColor = Color.Gray,
            navigationIconContentColor = Color.White
        )
    )
}
