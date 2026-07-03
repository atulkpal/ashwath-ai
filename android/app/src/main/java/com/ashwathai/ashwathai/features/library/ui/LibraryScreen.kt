package com.ashwathai.ashwathai.features.library.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.components.AshwathCard
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.features.library.events.LibraryEvent
import com.ashwathai.ashwathai.features.library.viewmodel.LibraryViewModel
import com.ashwathai.ashwathai.app.theme.CyanPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model Library", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Installed Models (${state.installedModels.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }

            items(state.installedModels) { model ->
                InstalledModelCard(
                    model = model,
                    isActive = state.activeModelId == model.id,
                    onToggle = { viewModel.onEvent(LibraryEvent.ToggleModel(model.id)) },
                    onDelete = { viewModel.onEvent(LibraryEvent.DeleteModel(model.id)) }
                )
            }
            
            if (state.installedModels.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No models installed yet.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun InstalledModelCard(
    model: ModelInfo,
    isActive: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    AshwathCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(model.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(
                        if (isActive) "Status: Running" else "Status: Ready",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isActive) CyanPrimary else Color.Gray
                    )
                }
                
                Row {
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isActive) "Stop" else "Start",
                            tint = if (isActive) Color.Red else CyanPrimary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxWidth(),
                color = if (isActive) CyanPrimary else Color.DarkGray,
                trackColor = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Size: ${model.size}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("RAM: 3.2 GB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
