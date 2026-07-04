package com.ashwathai.ashwathai.features.chat.state

import com.ashwathai.ashwathai.domain.models.ChatMessage

sealed class EngineStatus {
    object Initializing : EngineStatus()
    object Connected : EngineStatus()
    object NoModelInstalled : EngineStatus()
    data class Error(val message: String) : EngineStatus()
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isTyping: Boolean = false,
    val activeModelName: String = "Ashwath AI Engine",
    val engineStatus: EngineStatus = EngineStatus.Initializing
)
