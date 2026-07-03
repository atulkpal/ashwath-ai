package com.ashwathai.ashwathai.features.explore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.components.AshwathCard
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.features.explore.events.ExploreEvent
import com.ashwathai.ashwathai.features.explore.viewmodel.ExploreViewModel
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.SurfaceTier2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Models", style = MaterialTheme.typography.headlineMedium) },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(ExploreEvent.Search(it)) }
                )
            }

            item {
                DeviceRecommendationCard()
            }

            item {
                CategorySection(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { viewModel.onEvent(ExploreEvent.SelectCategory(it)) }
                )
            }

            item {
                Text(
                    text = "Recommended for you",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            items(state.featuredModels) { model ->
                ModelExploreCard(
                    model = model,
                    onDownloadClick = { viewModel.onEvent(ExploreEvent.DownloadModel(model.id)) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search models...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanPrimary,
            unfocusedBorderColor = Color.DarkGray,
            focusedContainerColor = SurfaceTier2,
            unfocusedContainerColor = SurfaceTier2,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
fun DeviceRecommendationCard() {
    AshwathCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Memory,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Optimized for your device",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    "Snapdragon 8 Gen 3 detected. 4-8B models recommended.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onCategorySelected(category) },
                color = if (isSelected) CyanPrimary else SurfaceTier2,
                contentColor = if (isSelected) Color.Black else Color.White
            ) {
                Text(
                    text = category.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ModelExploreCard(model: ModelInfo, onDownloadClick: () -> Unit) {
    AshwathCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "by ${model.provider}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanPrimary
                    )
                }
                IconButton(onClick = onDownloadClick) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModelTag(text = model.size)
                ModelTag(text = model.parameters)
                model.tags.take(1).forEach { ModelTag(text = it) }
            }
        }
    }
}

@Composable
fun ModelTag(text: String) {
    Box(
        modifier = Modifier
            .background(Color.DarkGray, RoundedCornerShape(2.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontSize = 9.sp
        )
    }
}
