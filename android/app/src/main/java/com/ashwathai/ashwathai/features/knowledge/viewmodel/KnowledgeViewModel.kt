package com.ashwathai.ashwathai.features.knowledge.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.features.knowledge.events.KnowledgeEvent
import com.ashwathai.ashwathai.features.knowledge.state.KnowledgeState
import com.ashwathai.ashwathai.features.knowledge.state.KnowledgeSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class KnowledgeViewModel : ViewModel() {
    private val _state = MutableStateFlow(KnowledgeState())
    val state = _state.asStateFlow()

    init {
        loadSources()
    }

    private fun loadSources() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Mock data for initial UI polish
            val mockSources = listOf(
                KnowledgeSource(UUID.randomUUID().toString(), "AI_Ethics_Guidelines.pdf", "PDF", Icons.Default.PictureAsPdf, "2.4 MB", "2026-05-01"),
                KnowledgeSource(UUID.randomUUID().toString(), "Project_Omega_Notes", "Note", Icons.Default.Description, "45 KB", "2026-05-02"),
                KnowledgeSource(UUID.randomUUID().toString(), "Research_Data_2025", "Folder", Icons.Default.Folder, "128 MB", "2026-04-20"),
                KnowledgeSource(UUID.randomUUID().toString(), "System_Architecture_V2.png", "Image", Icons.Default.Image, "1.2 MB", "2026-05-03"),
                KnowledgeSource(UUID.randomUUID().toString(), "Voice_Log_001.mp3", "Audio", Icons.Default.AudioFile, "5.6 MB", "2026-05-04")
            )

            _state.update { it.copy(isLoading = false, sources = mockSources) }
        }
    }

    fun onEvent(event: KnowledgeEvent) {
        when (event) {
            is KnowledgeEvent.AddSource -> { /* Mock */ }
            is KnowledgeEvent.RemoveSource -> {
                _state.update { s ->
                    s.copy(sources = s.sources.filter { it.id != event.id })
                }
            }
            is KnowledgeEvent.Refresh -> loadSources()
        }
    }
}
