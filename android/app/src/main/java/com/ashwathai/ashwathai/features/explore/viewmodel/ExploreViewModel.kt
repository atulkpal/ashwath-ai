package com.ashwathai.ashwathai.features.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.di.ServiceLocator
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.ashwathai.features.explore.events.ExploreEvent
import com.ashwathai.ashwathai.features.explore.state.ExploreState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ExploreViewModel(
    private val modelRepository: ModelRepository = ServiceLocator.provideModelRepository(),
    private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state = _state.asStateFlow()

    init {
        loadModels()
    }

    fun loadModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                modelRepository.getRecommendedModels().collect { models ->
                    _state.update { it.copy(featuredModels = models, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.Refresh -> loadModels()
            is ExploreEvent.Search -> _state.update { it.copy(searchQuery = event.query) }
            is ExploreEvent.SelectCategory -> _state.update { it.copy(selectedCategory = event.category) }
            is ExploreEvent.DownloadModel -> downloadModel(event.modelId)
        }
    }

    private fun downloadModel(modelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            try {
                withContext(ioDispatcher) {
                    modelRepository.downloadModel(modelId)
                }
                loadModels()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Download failed: ${e.message}") }
            }
        }
    }
}
