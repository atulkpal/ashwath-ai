package com.ashwathai.ashwathai.features.chat.state

import com.ashwathai.ashwathai.domain.models.ChatMessage

sealed class EngineStatus {
    object NotInstalled : EngineStatus()
    data class Installing(val progress: Float) : EngineStatus()
    object Starting : EngineStatus()
    object Connected : EngineStatus()
    data class Error(val message: String) : EngineStatus()
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isTyping: Boolean = false,
    val activeModelName: String = "Ashwath Engine",
    val engineStatus: EngineStatus = EngineStatus.NotInstalled
)
