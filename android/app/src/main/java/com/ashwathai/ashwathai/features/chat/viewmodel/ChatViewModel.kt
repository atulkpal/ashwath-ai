package com.ashwathai.ashwathai.features.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.core.downloads.DownloadState
import com.ashwathai.ashwathai.domain.models.ChatMessage
import com.ashwathai.ashwathai.domain.models.Sender
import com.ashwathai.ashwathai.features.chat.events.ChatEvent
import com.ashwathai.ashwathai.features.chat.state.ChatState
import com.ashwathai.ashwathai.features.chat.state.EngineStatus
import com.ashwathai.ashwathai.platform.installer.EngineInstaller
import com.ashwathai.ashwathai.platform.installer.EngineProcessManager
import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.ashwathai.runtime.api.InferenceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(
    private val engine: InferenceEngine,
    private val installer: EngineInstaller,
    private val processManager: EngineProcessManager
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    init {
        checkEngineStatus()
        observeDownloadProgress()
    }

    private fun checkEngineStatus() {
        if (installer.isInstalled()) {
            if (processManager.isRunning()) {
                _state.update { it.copy(engineStatus = EngineStatus.Connected) }
            } else {
                _state.update { it.copy(engineStatus = EngineStatus.Starting) }
                startEngine()
            }
        } else {
            _state.update { it.copy(engineStatus = EngineStatus.NotInstalled) }
        }
    }

    private fun observeDownloadProgress() {
        viewModelScope.launch {
            installer.downloadState.collect { downloadState ->
                when (downloadState) {
                    is DownloadState.Downloading -> {
                        _state.update { it.copy(engineStatus = EngineStatus.Installing(downloadState.progress)) }
                    }
                    is DownloadState.Failed -> {
                        _state.update { it.copy(engineStatus = EngineStatus.Error(downloadState.error)) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.InputChanged -> _state.update { it.copy(inputText = event.text) }
            is ChatEvent.SendMessage -> sendMessage()
            is ChatEvent.ClearChat -> _state.update { it.copy(messages = emptyList()) }
            // New events can be added here, e.g., DownloadEngine, StartEngine
        }
    }

    fun downloadEngine() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                installer.install()
            }
            result.onSuccess {
                startEngine()
            }.onFailure { e ->
                _state.update { it.copy(engineStatus = EngineStatus.Error(e.message ?: "Installation failed")) }
            }
        }
    }

    private fun startEngine() {
        viewModelScope.launch {
            _state.update { it.copy(engineStatus = EngineStatus.Starting) }
            processManager.start(50051).onSuccess {
                engine.initialize().onSuccess {
                    _state.update { it.copy(engineStatus = EngineStatus.Connected) }
                }.onFailure { e ->
                    _state.update { it.copy(engineStatus = EngineStatus.Error("gRPC connect failed: ${e.message}")) }
                }
            }.onFailure { e ->
                _state.update { it.copy(engineStatus = EngineStatus.Error("Process start failed: ${e.message}")) }
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText
        if (text.isBlank()) return

        val userMessage = ChatMessage(text = text, sender = Sender.USER)
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isTyping = true
            )
        }

        viewModelScope.launch {
            generateResponse(text)
        }
    }

    private suspend fun generateResponse(prompt: String) {
        val aiMessage = ChatMessage(text = "", sender = Sender.AI)
        _state.update { it.copy(messages = it.messages + aiMessage) }

        try {
            engine.generate(prompt, GenerationOptions()).collect { result ->
                when (result) {
                    is InferenceResult.Partial -> {
                        _state.update { it.copy(isTyping = false) }
                        updateLastMessage(result.text)
                    }
                    is InferenceResult.Success -> {
                        _state.update { it.copy(isTyping = false) }
                        updateLastMessage(result.fullText)
                    }
                    is InferenceResult.Error -> {
                        _state.update { it.copy(isTyping = false) }
                        updateLastMessage("Error: ${result.message}")
                    }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(isTyping = false) }
            updateLastMessage("Error: ${e.message}")
        }
    }

    private fun updateLastMessage(text: String) {
        _state.update { s ->
            val newList = s.messages.toMutableList()
            if (newList.isNotEmpty()) {
                val last = newList.last()
                newList[newList.size - 1] = last.copy(text = text)
            }
            s.copy(messages = newList)
        }
    }
}
