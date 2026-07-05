package com.ashwathai.ashwathai.features.library.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.components.*
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.JetBrainsMonoFontFamily
import com.ashwathai.ashwathai.app.theme.OnSurfaceVariant
import com.ashwathai.ashwathai.app.theme.SurfaceTier1
import com.ashwathai.ashwathai.app.theme.SurfaceTier2
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.features.library.events.LibraryEvent
import com.ashwathai.ashwathai.features.library.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(),
    onNavigateToExplore: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AshwathTopBar(
                title = "Model Library",
                subtitle = if (state.installedModels.isNotEmpty()) "${state.installedModels.size} Models Installed" else null,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(LibraryEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.Gray)
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading && state.installedModels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            } else if (state.installedModels.isEmpty()) {
                EmptyLibraryContent(onExploreClick = onNavigateToExplore)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "INSTALLED MODELS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp
                            ),
                            color = CyanPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = state.installedModels,
                        key = { it.id }
                    ) { model ->
                        InstalledModelCard(
                            model = model,
                            isActive = state.activeModelId == model.id,
                            onToggle = { viewModel.onEvent(LibraryEvent.ToggleModel(model.id)) },
                            onDelete = { viewModel.onEvent(LibraryEvent.DeleteModel(model.id)) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = SurfaceTier1,
                    contentColor = Color.White,
                    action = {
                        TextButton(onClick = { viewModel.onEvent(LibraryEvent.Refresh) }) {
                            Text("RETRY", color = CyanPrimary)
                        }
                    }
                ) {
                    Text(error)
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
    AshwathCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = model.provider.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        color = OnSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isActive) CyanPrimary.copy(alpha = 0.1f) else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isActive) "Deactivate" else "Activate",
                            tint = if (isActive) CyanPrimary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetadataItem(label = "SIZE", value = model.size)
                    MetadataItem(label = "PARAMS", value = model.parameters)
                }

                if (isActive) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0.4f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .alpha(alpha)
                                .background(CyanPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            color = CyanPrimary
                        )
                    }
                }
            }

            if (isActive) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(CircleShape),
                    color = CyanPrimary,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun MetadataItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 8.sp,
                letterSpacing = 1.sp
            ),
            color = OnSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun EmptyLibraryContent(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanPrimary.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.LibraryBooks,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Library is Empty",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Downloaded models will appear here for management and offline use.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AshwathPrimaryButton(
            text = "EXPLORE MODELS",
            onClick = onExploreClick
        )
    }
}
