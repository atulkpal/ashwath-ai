package com.ashwathai.ashwathai.features.explore.events

sealed class ExploreEvent {
    object Refresh : ExploreEvent()
    data class Search(val query: String) : ExploreEvent()
    data class SelectCategory(val category: String) : ExploreEvent()
    data class DownloadModel(val modelId: String) : ExploreEvent()
}
