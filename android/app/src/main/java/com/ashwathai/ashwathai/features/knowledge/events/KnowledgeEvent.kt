package com.ashwathai.ashwathai.features.knowledge.events

sealed class KnowledgeEvent {
    data class AddSource(val path: String) : KnowledgeEvent()
    data class RemoveSource(val id: String) : KnowledgeEvent()
    object Refresh : KnowledgeEvent()
}
