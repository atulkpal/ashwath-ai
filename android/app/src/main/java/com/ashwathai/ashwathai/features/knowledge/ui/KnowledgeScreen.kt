package com.ashwathai.ashwathai.features.knowledge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.components.*
import com.ashwathai.ashwathai.app.theme.*
import com.ashwathai.ashwathai.features.knowledge.events.KnowledgeEvent
import com.ashwathai.ashwathai.features.knowledge.state.KnowledgeSource
import com.ashwathai.ashwathai.features.knowledge.viewmodel.KnowledgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    viewModel: KnowledgeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AshwathTopBar(
                title = "Knowledge Base",
                subtitle = if (state.sources.isNotEmpty()) "${state.sources.size} Sources Indexed" else null,
                actions = {
                    IconButton(onClick = { /* TODO: Add Source */ }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Source", tint = CyanPrimary)
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
            if (state.isLoading && state.sources.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            } else if (state.sources.isEmpty()) {
                EmptyKnowledgeContent()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "INDEXED SOURCES",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanPrimary,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = state.sources,
                        key = { it.id }
                    ) { source ->
                        KnowledgeSourceCard(
                            source = source,
                            onRemove = { viewModel.onEvent(KnowledgeEvent.RemoveSource(source.id)) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun KnowledgeSourceCard(
    source: KnowledgeSource,
    onRemove: () -> Unit
) {
    AshwathCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceTier2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = source.icon,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = source.type,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = source.size,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Remove",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyKnowledgeContent() {
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
                Icons.Default.AutoAwesomeMotion,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Knowledge Base",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Index local documents and folders to enhance AI context with private data.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AshwathPrimaryButton(
            text = "ADD SOURCE",
            onClick = { /* TODO */ }
        )
    }
}
