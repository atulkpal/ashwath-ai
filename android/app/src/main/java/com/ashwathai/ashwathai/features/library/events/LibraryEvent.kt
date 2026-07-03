package com.ashwathai.ashwathai.features.library.events

sealed class LibraryEvent {
    data class ToggleModel(val modelId: String) : LibraryEvent()
    data class DeleteModel(val modelId: String) : LibraryEvent()
    object Refresh : LibraryEvent()
}
