package com.ashwathai.ashwathai.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.SurfaceTier1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var isOledEnabled by remember { mutableStateOf(true) }
    var isDevMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
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
                    description = "0.1.0-alpha (Open Source)",
                    icon = Icons.Default.Info
                )
            }
        }
    }
}

@Composable
fun SettingHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = CyanPrimary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingItem(title: String, description: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { /* Mock */ },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyanPrimary,
                    checkedTrackColor = CyanPrimary.copy(alpha = 0.5f)
                )
            )
        }
    }
}
