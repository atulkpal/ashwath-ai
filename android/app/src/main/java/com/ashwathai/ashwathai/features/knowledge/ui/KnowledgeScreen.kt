package com.ashwathai.ashwathai.features.knowledge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.components.AshwathCard
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.SurfaceTier1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen() {
    val mockKnowledge = listOf(
        KnowledgeItem("AI Research", "Folder", Icons.Default.Folder),
        KnowledgeItem("User Manual.pdf", "PDF", Icons.Default.PictureAsPdf),
        KnowledgeItem("Project Notes", "Note", Icons.Default.Description),
        KnowledgeItem("Diagram.png", "Image", Icons.Default.Image),
        KnowledgeItem("Code Snippets", "Folder", Icons.Default.Folder),
        KnowledgeItem("Meeting_01.mp3", "Audio", Icons.Default.AudioFile)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Knowledge Base", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = { /* Mock */ }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = CyanPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            Text("Your indexed sources for RAG", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockKnowledge) { item ->
                    KnowledgeCard(item)
                }
            }
        }
    }
}

data class KnowledgeItem(val name: String, val type: String, val icon: ImageVector)

@Composable
fun KnowledgeCard(item: KnowledgeItem) {
    AshwathCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(item.icon, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.name, style = MaterialTheme.typography.bodyMedium, color = Color.White, maxLines = 1)
            Text(item.type, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
