package com.ashwathai.ashwathai.features.chat.events

sealed class ChatEvent {
    data class InputChanged(val text: String) : ChatEvent()
    object SendMessage : ChatEvent()
    object ClearChat : ChatEvent()
}
