package com.ashwathai.ashwathai.data.repository

import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockModelRepository : ModelRepository {
    private val mockModels = listOf(
        ModelInfo(
            id = "gemma-3-4b",
            name = "Gemma 3 4B",
            provider = "Google",
            description = "Lightweight, state-of-the-art open model from Google.",
            size = "2.8 GB",
            parameters = "4B",
            tags = listOf("General", "Efficient"),
            isInstalled = true
        ),
        ModelInfo(
            id = "phi-4-mini",
            name = "Phi-4 Mini",
            provider = "Microsoft",
            description = "Extremely capable small language model.",
            size = "2.1 GB",
            parameters = "3.8B",
            tags = listOf("Reasoning", "Coding"),
            isInstalled = false
        ),
        ModelInfo(
            id = "llama-3.2-3b",
            name = "Llama 3.2 3B",
            provider = "Meta",
            description = "Optimized for mobile and edge devices.",
            size = "2.5 GB",
            parameters = "3B",
            tags = listOf("Balanced", "Chat"),
            isInstalled = true
        ),
        ModelInfo(
            id = "qwen-2.5-3b",
            name = "Qwen 2.5 3B",
            provider = "Alibaba",
            description = "High performance multilingual model.",
            size = "3.1 GB",
            parameters = "3B",
            tags = listOf("Multilingual"),
            isInstalled = false
        )
    )

    override fun getRecommendedModels(): Flow<List<ModelInfo>> = flowOf(mockModels)

    override fun getInstalledModels(): Flow<List<ModelInfo>> = flowOf(mockModels.filter { it.isInstalled })

    override fun getModel(id: String): Flow<ModelInfo?> = flowOf(mockModels.find { it.id == id })

    override suspend fun downloadModel(id: String) {
        // Mock download
    }

    override suspend fun deleteModel(id: String) {
        // Mock delete
    }

    override fun downloadProgress(modelId: String): Flow<Float> = flowOf(1f)
}
