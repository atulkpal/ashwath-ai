package com.ashwathai.ashwathai.features.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.data.repository.MockModelRepository
import com.ashwathai.ashwathai.features.explore.events.ExploreEvent
import com.ashwathai.ashwathai.features.explore.state.ExploreState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val modelRepository = MockModelRepository()
    
    private val _state = MutableStateFlow(ExploreState())
    val state = _state.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            modelRepository.getRecommendedModels().collect { models ->
                _state.update { it.copy(featuredModels = models, isLoading = false) }
            }
        }
    }

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.Refresh -> loadModels()
            is ExploreEvent.Search -> _state.update { it.copy(searchQuery = event.query) }
            is ExploreEvent.SelectCategory -> _state.update { it.copy(selectedCategory = event.category) }
            is ExploreEvent.DownloadModel -> {
                // Mock behavior
            }
        }
    }
}
