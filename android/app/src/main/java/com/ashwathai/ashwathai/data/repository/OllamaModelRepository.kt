package com.ashwathai.ashwathai.data.repository

import com.ashwathai.ashwathai.data.ollama.OllamaClient
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class OllamaModelRepository(
    private val ollamaClient: OllamaClient = OllamaClient(),
    private val modelsDir: File = File(""),
) : ModelRepository {

    private fun ollamaToModelInfo(m: OllamaClient.OllamaModel): ModelInfo {
        val parts = m.name.split(":")
        val name = parts.getOrElse(0) { m.name }
        val tag = parts.getOrElse(1) { "latest" }
        return ModelInfo(
            id = "ollama:$name:$tag",
            name = m.name,
            provider = "Ollama",
            description = "Model from Ollama library",
            size = formatSize(m.size),
            parameters = "",
            tags = listOf("ollama", name),
            isInstalled = false,
        )
    }

    override fun getRecommendedModels(): Flow<List<ModelInfo>> = flow {
        val models = mutableListOf<ModelInfo>()

        try {
            ollamaClient.listModels().collect { ollamaModels ->
                models.addAll(ollamaModels.map { ollamaToModelInfo(it) })
            }
        } catch (_: Exception) {
            // Ollama unavailable — try built-in catalog
        }

        if (models.isEmpty()) {
            catalogModels().forEach { models.add(it) }
        }
        emit(models)
    }

    override fun getInstalledModels(): Flow<List<ModelInfo>> = flow {
        val scanned = modelsDir.listFiles()
            ?.filter { it.name.endsWith(".gguf") }
            ?.map { file ->
                ModelInfo(
                    id = "local:${file.nameWithoutExtension}",
                    name = file.nameWithoutExtension,
                    provider = "Local",
                    description = "Downloaded model",
                    size = formatSize(file.length()),
                    parameters = "",
                    tags = emptyList(),
                    isInstalled = true,
                )
            } ?: emptyList()
        emit(scanned)
    }

    override fun getModel(id: String): Flow<ModelInfo?> = flow {
        emit(null)
    }

    override suspend fun downloadModel(id: String) {
        val parts = id.removePrefix("ollama:").split(":")
        val name = parts.getOrElse(0) { id }
        val tag = parts.getOrElse(1) { "latest" }
        ollamaClient.pullModel("$name:$tag").collect {
            if (it.status == "success") {
                val modelPath = "$modelsDir/$name-$tag.gguf"
                // The model is stored by Ollama, not directly accessible
            }
        }
    }

    override suspend fun deleteModel(id: String) {}

    override fun downloadProgress(modelId: String): Flow<Float> = flow {
        val parts = modelId.removePrefix("ollama:").split(":")
        val name = parts.getOrElse(0) { modelId }
        val tag = parts.getOrElse(1) { "latest" }

        ollamaClient.pullModel("$name:$tag").collect { progress ->
            emit(progress.fraction)
        }
    }

    private fun catalogModels(): List<ModelInfo> = listOf(
        ModelInfo("phi-4-mini", "Phi-4 Mini", "Microsoft", "Small capable model", "2.1 GB", "3.8B", emptyList()),
        ModelInfo("qwen-2.5-3b", "Qwen 2.5 3B", "Alibaba", "Multilingual model", "3.1 GB", "3B", emptyList()),
    )

    private fun formatSize(bytes: Long): String = when {
        bytes <= 0 -> "Unknown"
        bytes < 1_000_000_000 -> "${bytes / 1_000_000}MB"
        else -> String.format("%.1fGB", bytes / 1_000_000_000.0)
    }
}
