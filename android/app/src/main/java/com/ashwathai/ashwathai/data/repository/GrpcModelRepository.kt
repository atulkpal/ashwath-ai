package com.ashwathai.ashwathai.data.repository

import com.ashwathai.ashwathai.data.download.DirectModelDownloader
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.sdk.EngineGrpcClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

class GrpcModelRepository(
    private val grpcClient: EngineGrpcClient,
    private val modelsDir: File = File(""),
    private val downloader: DirectModelDownloader = DirectModelDownloader(),
) : ModelRepository {

    private val knownUrls = mapOf(
        "gemma-3-4b" to "https://huggingface.co/google/gemma-3-4b-it-gguf/resolve/main/gemma-3-4b-it-Q4_K_M.gguf",
        "phi-4-mini" to "https://huggingface.co/microsoft/Phi-4-mini-instruct-gguf/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf",
        "llama-3.2-3b" to "https://huggingface.co/meta-llama/Llama-3.2-3B-Instruct-gguf/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        "qwen-2.5-3b" to "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-gguf/resolve/main/Qwen2.5-3B-Instruct-Q4_K_M.gguf",
    )

    private val modelFilenames = mapOf(
        "gemma-3-4b" to "gemma-3-4b-it-Q4_K_M.gguf",
        "phi-4-mini" to "Phi-4-mini-instruct-Q4_K_M.gguf",
        "llama-3.2-3b" to "Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        "qwen-2.5-3b" to "Qwen2.5-3B-Instruct-Q4_K_M.gguf",
    )

    private fun downloadedModelFile(modelId: String): File? {
        val filename = modelFilenames[modelId] ?: return null
        val file = File(modelsDir, filename)
        return if (file.exists()) file else null
    }

    override fun getRecommendedModels(): Flow<List<ModelInfo>> = flow {
        val result = grpcClient.listModels()
        emit(result.map { list -> list.modelsList.map { it.toDomain() } }
            .getOrDefault(emptyList()))
    }

    override fun getInstalledModels(): Flow<List<ModelInfo>> = flow {
        val scanned = modelsDir.listFiles()
            ?.filter { it.name.endsWith(".gguf") }
            ?.map { file ->
                val matchedId = modelFilenames.entries.firstOrNull { file.name == it.value }?.key
                ModelInfo(
                    id = matchedId ?: "local:${file.nameWithoutExtension}",
                    name = matchedId?.let { id -> knownUrls.keys.firstOrNull { it == id } } ?: file.nameWithoutExtension,
                    provider = "Local",
                    description = "Downloaded model",
                    size = formatSize(file.length()),
                    parameters = "",
                    tags = emptyList(),
                    isInstalled = true,
                )
            } ?: emptyList()

        // Also try gRPC for models installed via engine
        val result = grpcClient.listModels()
        val grpcModels = result.map { list ->
            list.modelsList.filter { it.installed }.map { it.toDomain() }
        }.getOrDefault(emptyList())

        emit(scanned + grpcModels)
    }

    override fun getModel(id: String): Flow<ModelInfo?> = flow {
        val local = downloadedModelFile(id)
        if (local != null) {
            emit(ModelInfo(
                id = id,
                name = id,
                provider = "Local",
                description = "Downloaded model",
                size = formatSize(local.length()),
                parameters = "",
                tags = emptyList(),
                isInstalled = true,
            ))
            return@flow
        }
        val result = grpcClient.listModels()
        emit(result.map { list ->
            list.modelsList.firstOrNull { it.id == id }?.toDomain()
        }.getOrDefault(null))
    }

    override suspend fun downloadModel(id: String) {
        val url = knownUrls[id] ?: throw IllegalArgumentException("Unknown model: $id")
        val filename = modelFilenames[id] ?: throw IllegalArgumentException("Unknown model: $id")
        val dest = File(modelsDir, filename)

        modelsDir.mkdirs()

        withContext(Dispatchers.IO) {
            var lastProgress = -1f
            downloader.download(url, dest).collect { progress ->
                if (progress.fraction >= 1f && lastProgress < 1f) {
                    lastProgress = 1f
                }
            }
        }
    }

    override suspend fun deleteModel(id: String) {
        downloadedModelFile(id)?.delete()
    }

    override fun downloadProgress(modelId: String): Flow<Float> = flow {
        val url = knownUrls[modelId] ?: return@flow
        val filename = modelFilenames[modelId] ?: return@flow
        val dest = File(modelsDir, filename)

        modelsDir.mkdirs()

        var done = false
        downloader.download(url, dest).collect { progress ->
            emit(progress.fraction)
            if (progress.fraction >= 1f) {
                done = true
                return@collect
            }
        }
        if (!done) emit(1f)
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)

    private fun com.ashwathai.sdk.generated.ModelInfo.toDomain(): ModelInfo = ModelInfo(
        id = id,
        name = name,
        provider = provider,
        description = "",
        size = formatSize(sizeBytes),
        parameters = parameters,
        tags = tagsList,
        isInstalled = installed,
    )

    private fun formatSize(bytes: Long): String = when {
        bytes <= 0 -> "Unknown"
        bytes < 1_000_000_000 -> "${bytes / 1_000_000}MB"
        else -> String.format("%.1fGB", bytes / 1_000_000_000.0)
    }
}
