package com.ashwathai.ashwathai.features.download.state

import com.ashwathai.ashwathai.domain.models.ModelInfo

data class DownloadState(
    val isLoading: Boolean = true,
    val availableModels: List<ModelInfo> = emptyList(),
    val downloadingModelId: String? = null,
    val downloadProgress: Float = 0f,
    val isComplete: Boolean = false,
    val error: String? = null,
)
