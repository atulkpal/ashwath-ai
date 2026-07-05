package com.ashwathai.ashwathai.data.repository

import com.ashwathai.ashwathai.data.download.DirectModelDownloader
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import com.ashwathai.sdk.EngineGrpcClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CopyOnWriteArrayList

class GrpcModelRepository(
    private val grpcClient: EngineGrpcClient,
    private val modelsDir: File = File(""),
    private val downloader: DirectModelDownloader = DirectModelDownloader(),
) : ModelRepository {

    private val upstreamUrl = "https://raw.githubusercontent.com/atulkpal/ashwath-ai/main/models/index.json"
    private val cacheFile: File get() = File(modelsDir.parentFile, "model-index.json")

    private data class ModelSource(val id: String, val name: String, val sources: List<String>, val filename: String, val sizeBytes: Long)

    private val hardcodedSources = listOf(
        ModelSource("gemma-3-4b", "Gemma 3 4B", listOf(
            "https://huggingface.co/google/gemma-3-4b-it-gguf/resolve/main/gemma-3-4b-it-Q4_K_M.gguf",
        ), "gemma-3-4b-it-Q4_K_M.gguf", 2_800_000_000L),
        ModelSource("phi-4-mini", "Phi-4 Mini", listOf(
            "https://huggingface.co/microsoft/Phi-4-mini-instruct-gguf/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf",
        ), "Phi-4-mini-instruct-Q4_K_M.gguf", 2_100_000_000L),
        ModelSource("llama-3.2-3b", "Llama 3.2 3B", listOf(
            "https://huggingface.co/meta-llama/Llama-3.2-3B-Instruct-gguf/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        ), "Llama-3.2-3B-Instruct-Q4_K_M.gguf", 2_500_000_000L),
        ModelSource("qwen-2.5-3b", "Qwen 2.5 3B", listOf(
            "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-gguf/resolve/main/Qwen2.5-3B-Instruct-Q4_K_M.gguf",
        ), "Qwen2.5-3B-Instruct-Q4_K_M.gguf", 3_100_000_000L),
    )

    private val sources = CopyOnWriteArrayList(hardcodedSources)

    init {
        refreshUpstream()
    }

    private fun refreshUpstream() {
        try {
            val url = URL(upstreamUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", "AshwathAI/0.1")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            if (conn.responseCode == 200) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).readText()
                val parsed = parseUpstream(body)
                if (parsed.isNotEmpty()) {
                    sources.clear()
                    sources.addAll(parsed)
                    cacheFile.parentFile?.mkdirs()
                    cacheFile.writeText(body)
                }
            } else if (cacheFile.exists()) {
                val cached = parseUpstream(cacheFile.readText())
                if (cached.isNotEmpty()) {
                    sources.clear()
                    sources.addAll(cached)
                }
            }
            conn.disconnect()
        } catch (_: Exception) {
            if (cacheFile.exists()) {
                try {
                    val cached = parseUpstream(cacheFile.readText())
                    if (cached.isNotEmpty()) {
                        sources.clear()
                        sources.addAll(cached)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun parseUpstream(json: String): List<ModelSource> {
        val models = org.json.JSONObject(json).getJSONArray("models")
        val result = mutableListOf<ModelSource>()
        for (i in 0 until models.length()) {
            val m = models.getJSONObject(i)
            val id = m.getString("id")
            val name = m.getString("name")
            val filename = m.getString("filename")
            val sizeBytes = m.optLong("sizeBytes", 0L)
            val srcArr = m.getJSONArray("sources")
            val srcList = mutableListOf<String>()
            for (j in 0 until srcArr.length()) {
                srcList.add(srcArr.getString(j))
            }
            result.add(ModelSource(id, name, srcList, filename, sizeBytes))
        }
        return result
    }

    private fun sourceFor(id: String): ModelSource? = sources.find { it.id == id }

    private fun downloadedFile(id: String): File? {
        val src = sourceFor(id) ?: return null
        val file = File(modelsDir, src.filename)
        return if (file.exists()) file else null
    }

    override fun getRecommendedModels(): Flow<List<ModelInfo>> = flow {
        val result = grpcClient.listModels()
        val gRpcModels = result.map { list -> list.modelsList.map { it.toDomain() } }
            .getOrDefault(emptyList())
        if (gRpcModels.isNotEmpty()) {
            emit(gRpcModels)
        } else {
            emit(sources.map { it.toModelInfo() })
        }
    }

    override fun getInstalledModels(): Flow<List<ModelInfo>> = flow {
        val scanned = modelsDir.listFiles()
            ?.filter { it.name.endsWith(".gguf") }
            ?.map { file ->
                val matched = sources.firstOrNull { it.filename == file.name }
                ModelInfo(
                    id = matched?.id ?: "local:${file.nameWithoutExtension}",
                    name = matched?.name ?: file.nameWithoutExtension,
                    provider = "Local",
                    description = "Downloaded model",
                    size = formatSize(file.length()),
                    parameters = "",
                    tags = emptyList(),
                    isInstalled = true,
                )
            } ?: emptyList()

        val result = grpcClient.listModels()
        val grpcModels = result.map { list ->
            list.modelsList.filter { it.installed }.map { it.toDomain() }
        }.getOrDefault(emptyList())

        emit(scanned + grpcModels)
    }

    override fun getModel(id: String): Flow<ModelInfo?> = flow {
        downloadedFile(id)?.let {
            emit(ModelInfo(id, id, "Local", "Downloaded model", formatSize(it.length()), "", emptyList(), true))
            return@flow
        }
        val result = grpcClient.listModels()
        emit(result.map { list -> list.modelsList.firstOrNull { it.id == id }?.toDomain() }.getOrDefault(null))
    }

    override suspend fun downloadModel(id: String) {
        val src = sourceFor(id) ?: throw IllegalArgumentException("Unknown model: $id")
        val dest = File(modelsDir, src.filename)
        modelsDir.mkdirs()

        for (url in src.sources) {
            try {
                downloader.download(url, dest).collect { }
                return
            } catch (_: Exception) { continue }
        }
        throw Exception("All download sources failed for $id")
    }

    override suspend fun deleteModel(id: String) {
        downloadedFile(id)?.delete()
    }

    override fun downloadProgress(modelId: String): Flow<Float> = flow {
        val src = sourceFor(modelId) ?: return@flow
        val dest = File(modelsDir, src.filename)

        if (dest.exists()) { emit(1f); return@flow }
        modelsDir.mkdirs()

        for (url in src.sources) {
            var done = false
            try {
                downloader.download(url, dest).collect { p ->
                    emit(p.fraction)
                    if (p.fraction >= 1f) { done = true; return@collect }
                }
                if (done || dest.exists()) { if (!done) emit(1f); return@flow }
            } catch (_: Exception) { continue }
        }
        throw Exception("All download sources failed for $modelId")
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)

    private fun com.ashwathai.sdk.generated.ModelInfo.toDomain(): ModelInfo = ModelInfo(
        id = id, name = name, provider = provider, description = "",
        size = formatSize(sizeBytes), parameters = parameters, tags = tagsList, isInstalled = installed,
    )

    private fun ModelSource.toModelInfo() = ModelInfo(
        id = id, name = name, provider = if (id.startsWith("local:")) "Local" else id.split("-").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Unknown",
        description = "", size = formatSize(sizeBytes), parameters = "",
        tags = emptyList(), isInstalled = downloadedFile(id) != null,
    )

    private fun formatSize(bytes: Long): String = when {
        bytes <= 0 -> "Unknown"
        bytes < 1_000_000_000 -> "${bytes / 1_000_000}MB"
        else -> String.format("%.1fGB", bytes / 1_000_000_000.0)
    }
}
