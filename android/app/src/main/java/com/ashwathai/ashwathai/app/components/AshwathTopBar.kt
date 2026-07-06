package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.OnSurfaceVariant
import com.ashwathai.ashwathai.app.theme.PureBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshwathTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitleColor: Color = CyanPrimary,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {
    Surface(
        color = PureBlack,
        modifier = modifier
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                ),
                                color = subtitleColor
                            )
                        }
                    }
                },
                navigationIcon = navigationIcon,
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureBlack,
                    titleContentColor = Color.White,
                    actionIconContentColor = OnSurfaceVariant,
                    navigationIconContentColor = Color.White
                )
            )
            HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
        }
    }
}
