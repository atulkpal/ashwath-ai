package com.ashwathai.ashwathai.features.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwathai.ashwathai.domain.models.ChatMessage
import com.ashwathai.ashwathai.domain.models.Sender
import com.ashwathai.ashwathai.features.chat.events.ChatEvent
import com.ashwathai.ashwathai.features.chat.state.ChatState
import com.ashwathai.ashwathai.features.chat.state.EngineStatus
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
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    init {
        initializeEngine()
    }

    private fun initializeEngine() {
        viewModelScope.launch {
            _state.update { it.copy(engineStatus = EngineStatus.Initializing) }
            val result = withContext(Dispatchers.IO) {
                engine.initialize()
            }
            result.onSuccess {
                _state.update { it.copy(engineStatus = EngineStatus.Connected) }
            }.onFailure { e ->
                _state.update {
                    it.copy(engineStatus = EngineStatus.Error(
                        "Engine init failed: ${e.message}"
                    ))
                }
            }
        }
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.InputChanged -> _state.update { it.copy(inputText = event.text) }
            is ChatEvent.SendMessage -> sendMessage()
            is ChatEvent.ClearChat -> _state.update { it.copy(messages = emptyList()) }
            is ChatEvent.RetryEngine -> initializeEngine()
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
            var fullText = ""
            engine.generate(prompt, GenerationOptions()).collect { result ->
                when (result) {
                    is InferenceResult.Partial -> {
                        _state.update { it.copy(isTyping = false) }
                        fullText += result.text
                        updateLastMessage(fullText)
                    }
                    is InferenceResult.Success -> {
                        _state.update { it.copy(isTyping = false) }
                        // If it's a success, ensure we use the accumulated text
                        // or the full text provided (depending on implementation).
                        // In our case, partial results already accumulated most of it.
                        if (result.fullText.isNotEmpty() && result.fullText.length > fullText.length) {
                            fullText = result.fullText
                        }
                        updateLastMessage(fullText)
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
