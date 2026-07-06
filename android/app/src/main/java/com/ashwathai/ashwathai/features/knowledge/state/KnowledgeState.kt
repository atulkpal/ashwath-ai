package com.ashwathai.ashwathai.features.knowledge.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class KnowledgeSource(
    val id: String,
    val name: String,
    val type: String,
    val icon: ImageVector,
    val size: String,
    val dateAdded: String,
    val status: SourceStatus = SourceStatus.Indexed
)

sealed class SourceStatus {
    object Indexing : SourceStatus()
    object Indexed : SourceStatus()
    data class Error(val message: String) : SourceStatus()
}

data class KnowledgeState(
    val isLoading: Boolean = false,
    val sources: List<KnowledgeSource> = emptyList(),
    val error: String? = null
)
