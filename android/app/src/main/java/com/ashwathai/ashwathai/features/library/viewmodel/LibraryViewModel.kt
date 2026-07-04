package com.ashwathai.ashwathai.features.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.di.ServiceLocator
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.ashwathai.features.library.events.LibraryEvent
import com.ashwathai.ashwathai.features.library.state.LibraryState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class LibraryViewModel(
    private val modelRepository: ModelRepository = ServiceLocator.provideModelRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    init {
        loadInstalledModels()
    }

    fun loadInstalledModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                modelRepository.getInstalledModels().collect { models ->
                    _state.update { it.copy(installedModels = models, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
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
            is LibraryEvent.DeleteModel -> deleteModel(event.modelId)
            is LibraryEvent.Refresh -> loadInstalledModels()
        }
    }

    private fun deleteModel(modelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            try {
                withContext(ioDispatcher) {
                    modelRepository.deleteModel(modelId)
                }
                loadInstalledModels()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Delete failed: ${e.message}") }
            }
        }
    }
}
