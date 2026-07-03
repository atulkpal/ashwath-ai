package com.ashwathai.ashwathai.domain.models

data class ModelInfo(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val size: String,
    val parameters: String,
    val tags: List<String>,
    val isInstalled: Boolean = false,
    val isDownloading: Boolean = false,
    val progress: Float = 0f
)
