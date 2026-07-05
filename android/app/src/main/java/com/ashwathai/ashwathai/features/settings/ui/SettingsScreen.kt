package com.ashwathai.ashwathai.features.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashwathai.ashwathai.app.components.AshwathTopBar
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.JetBrainsMonoFontFamily
import com.ashwathai.ashwathai.app.theme.OnSurfaceVariant
import com.ashwathai.ashwathai.app.theme.PureBlack
import com.ashwathai.ashwathai.app.theme.SurfaceTier1
import com.ashwathai.ashwathai.core.HfTokenProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var isOledEnabled by remember { mutableStateOf(true) }
    var isDevMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AshwathTopBar(
                title = "Settings",
                subtitle = "App Configuration"
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { SettingHeader("System") }
            item {
                SettingToggleItem(
                    title = "Pure Black (OLED)",
                    description = "Force #000000 backgrounds to save battery",
                    icon = Icons.Default.DarkMode,
                    checked = isOledEnabled,
                    onCheckedChange = { isOledEnabled = it }
                )
            }
            item {
                SettingItem(
                    title = "Inference Engine",
                    description = "Current: llama.cpp (Vulkan)",
                    icon = Icons.Default.PrecisionManufacturing
                )
            }

            item { SettingHeader("Models & Data") }
            item {
                SettingItem(
                    title = "Model Storage",
                    description = "8.4 GB used of 128 GB",
                    icon = Icons.Default.Storage
                )
            }
            item {
                SettingItem(
                    title = "Knowledge Base Indexing",
                    description = "Automatic indexing enabled",
                    icon = Icons.Default.Search
                )
            }

            item { SettingHeader("Hugging Face") }
            item {
                var hfToken by remember { mutableStateOf(HfTokenProvider.getToken()) }
                var showToken by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent,
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            "Hugging Face Access Token",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (hfToken.isNotBlank()) "Token set (${hfToken.take(8)}...)" else "Required for model downloads",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = hfToken,
                                onValueChange = {
                                    hfToken = it
                                    HfTokenProvider.setToken(it)
                                },
                                placeholder = { Text("hf_...", color = Color.Gray) },
                                visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = Color.White,
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = SurfaceTier1,
                                    cursorColor = CyanPrimary,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                ),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { showToken = !showToken }) {
                                Icon(
                                    if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = Color.Gray,
                                )
                            }
                        }
                    }
                }
            }

            item { SettingHeader("Advanced") }
            item {
                SettingToggleItem(
                    title = "Developer Mode",
                    description = "Show hardware telemetry and logs",
                    icon = Icons.Default.Code,
                    checked = isDevMode,
                    onCheckedChange = { isDevMode = it }
                )
            }
            item {
                SettingItem(
                    title = "Benchmark Device",
                    description = "Test your device's AI performance",
                    icon = Icons.Default.Speed
                )
            }

            item { SettingHeader("About") }
            item {
                SettingItem(
                    title = "Ashwath AI Version",
                    description = "0.2.0 (Stable)",
                    icon = Icons.Default.Info,
                    isTechnical = true
                )
            }

            item { Spacer(modifier = Modifier.height(48.dp)) }
        }
    }
}

@Composable
fun SettingHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        ),
        color = CyanPrimary.copy(alpha = 0.8f),
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    icon: ImageVector,
    isTechnical: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Mock */ },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
                Text(
                    description,
                    style = if (isTechnical) {
                        MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            letterSpacing = 0.5.sp
                        )
                    } else {
                        MaterialTheme.typography.bodySmall
                    },
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureBlack,
                    checkedTrackColor = CyanPrimary,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = SurfaceTier1,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}
