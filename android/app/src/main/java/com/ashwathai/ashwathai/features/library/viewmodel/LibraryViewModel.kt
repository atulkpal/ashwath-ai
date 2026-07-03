package com.ashwathai.ashwathai.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.data.repository.MockModelRepository
import com.ashwathai.ashwathai.features.library.events.LibraryEvent
import com.ashwathai.ashwathai.features.library.state.LibraryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val modelRepository = MockModelRepository()

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    init {
        loadInstalledModels()
    }

    private fun loadInstalledModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            modelRepository.getInstalledModels().collect { models ->
                _state.update { it.copy(installedModels = models, isLoading = false) }
            }
        }
    }

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.ToggleModel -> {
                _state.update { 
                    it.copy(activeModelId = if (it.activeModelId == event.modelId) null else event.modelId)
                }
            }
            is LibraryEvent.DeleteModel -> {
                // Mock delete
            }
            is LibraryEvent.Refresh -> loadInstalledModels()
        }
    }
}
