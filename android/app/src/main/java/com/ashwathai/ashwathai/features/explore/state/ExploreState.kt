package com.ashwathai.ashwathai.features.explore.state

import com.ashwathai.ashwathai.domain.models.ModelInfo

data class ExploreState(
    val isLoading: Boolean = false,
    val featuredModels: List<ModelInfo> = emptyList(),
    val categories: List<String> = listOf("General", "Coding", "Creative", "Reasoning", "Vision"),
    val selectedCategory: String = "General",
    val searchQuery: String = "",
    val error: String? = null
)
