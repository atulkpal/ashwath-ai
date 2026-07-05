package com.ashwathai.ashwathai.features.explore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.ashwathai.ashwathai.app.components.*
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.JetBrainsMonoFontFamily
import com.ashwathai.ashwathai.app.theme.OnSurfaceVariant
import com.ashwathai.ashwathai.app.theme.SurfaceTier1
import com.ashwathai.ashwathai.app.theme.SurfaceTier2
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.features.explore.events.ExploreEvent
import com.ashwathai.ashwathai.features.explore.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AshwathTopBar(
                title = "Explore",
                subtitle = "Discover local AI models"
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
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
                        text = "RECOMMENDED FOR YOU",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        ),
                        color = CyanPrimary.copy(alpha = 0.8f)
                    )
                }

                items(state.featuredModels) { model ->
                    ModelExploreCard(
                        model = model,
                        onDownloadClick = { viewModel.onEvent(ExploreEvent.DownloadModel(model.id)) }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .height(2.dp),
                    color = CyanPrimary,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    AshwathTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = "Search models...",
    )
}

@Composable
fun DeviceRecommendationCard() {
    AshwathCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Memory,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "OPTIMIZED FOR YOUR DEVICE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = CyanPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Snapdragon 8 Gen 3 detected. 4-8B models recommended.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
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
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            AshwathChip(
                text = category.uppercase(),
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun ModelExploreCard(model: ModelInfo, onDownloadClick: () -> Unit) {
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
                        text = "BY ${model.provider.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        color = OnSurfaceVariant,
                    )
                }

                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceTier2)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetadataTag(label = "SIZE", value = model.size)
                    MetadataTag(label = "PARAMS", value = model.parameters)
                }

                if (model.tags.isNotEmpty()) {
                    Text(
                        text = model.tags.first().uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetadataTag(label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceTier2)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp
            ),
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}
