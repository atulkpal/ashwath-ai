package com.ashwathai.ashwathai.features.library.state

import com.ashwathai.ashwathai.domain.models.ModelInfo

data class LibraryState(
    val isLoading: Boolean = false,
    val installedModels: List<ModelInfo> = emptyList(),
    val activeModelId: String? = "gemma-3-4b",
    val error: String? = null
)
