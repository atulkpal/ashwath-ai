package com.ashwathai.ashwathai.features.download.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.di.ServiceLocator
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.ashwathai.features.download.state.DownloadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel(
    private val modelRepository: ModelRepository = ServiceLocator.provideModelRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(DownloadState())
    val state = _state.asStateFlow()

    init {
        loadModels()
    }

    fun loadModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                modelRepository.getRecommendedModels().collect { models ->
                    _state.update { it.copy(availableModels = models, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun downloadModel(modelId: String) {
        if (_state.value.downloadingModelId != null) return

        viewModelScope.launch {
            _state.update { it.copy(downloadingModelId = modelId, downloadProgress = 0f, error = null) }
            try {
                withContext(Dispatchers.IO) {
                    modelRepository.downloadModel(modelId)
                }
                modelRepository.downloadProgress(modelId).collect { progress ->
                    _state.update { it.copy(downloadProgress = progress) }
                    if (progress >= 1f) {
                        _state.update { it.copy(
                            downloadingModelId = null,
                            downloadProgress = 0f,
                            isComplete = true,
                        ) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    downloadingModelId = null,
                    downloadProgress = 0f,
                    error = "Download failed: ${e.message}",
                ) }
            }
        }
    }
}
