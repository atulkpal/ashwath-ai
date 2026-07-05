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
        ModelSource("phi-4-mini", "Phi-4 Mini", listOf(
            "https://huggingface.co/bartowski/microsoft_Phi-4-mini-instruct-GGUF/resolve/main/microsoft_Phi-4-mini-instruct-Q4_K_M.gguf",
        ), "microsoft_Phi-4-mini-instruct-Q4_K_M.gguf", 2_100_000_000L),
        ModelSource("phi-3.1-mini", "Phi-3.1 Mini", listOf(
            "https://huggingface.co/bartowski/Phi-3.1-mini-4k-instruct-GGUF/resolve/main/Phi-3.1-mini-4k-instruct-Q4_K_M.gguf",
        ), "Phi-3.1-mini-4k-instruct-Q4_K_M.gguf", 2_500_000_000L),
        ModelSource("llama-3.2-3b", "Llama 3.2 3B", listOf(
            "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
        ), "Llama-3.2-3B-Instruct-Q4_K_M.gguf", 2_500_000_000L),
        ModelSource("qwen-2.5-3b", "Qwen 2.5 3B", listOf(
            "https://huggingface.co/bartowski/Qwen2.5-3B-Instruct-GGUF/resolve/main/Qwen2.5-3B-Instruct-Q4_K_M.gguf",
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

    private fun modelDir(id: String): File? {
        val src = sourceFor(id) ?: return null
        return File(modelsDir, id).also { it.mkdirs() }
    }

    private fun downloadedFile(id: String): File? {
        val src = sourceFor(id) ?: return null
        val dir = modelDir(id) ?: return null
        val file = File(dir, src.filename)
        return if (file.exists()) file else null
    }

    private fun installedStateFile(): File = File(modelsDir, ".installed.json")

    private fun markInstalled(id: String, installed: Boolean) {
        try {
            modelsDir.mkdirs()
            val state = mutableMapOf<String, Boolean>()
            if (installedStateFile().exists()) {
                val text = installedStateFile().readText()
                val json = org.json.JSONObject(text)
                for (key in json.keys()) state[key] = json.getBoolean(key)
            }
            state[id] = installed
            installedStateFile().writeText(org.json.JSONObject(state.toMap()).toString(2))
        } catch (_: Exception) {}
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
            ?.filter { it.isDirectory }
            ?.flatMap { dir ->
                dir.listFiles()?.filter { it.name.endsWith(".gguf") }?.map { file ->
                    val matched = sources.firstOrNull { it.filename == file.name }
                    ModelInfo(
                        id = matched?.id ?: "local:${dir.name}",
                        name = matched?.name ?: dir.name,
                        provider = "Local",
                        description = "Downloaded model",
                        size = formatSize(file.length()),
                        parameters = "",
                        tags = emptyList(),
                        isInstalled = true,
                    )
                } ?: emptyList()
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
        val dir = modelDir(id) ?: throw Exception("Cannot create model directory")
        val dest = File(dir, src.filename)
        dir.mkdirs()

        for (url in src.sources) {
            try {
                downloader.download(url, dest).collect { }
                markInstalled(id, true)
                return
            } catch (_: Exception) { continue }
        }
        throw Exception("All download sources failed for $id")
    }

    override suspend fun deleteModel(id: String) {
        downloadedFile(id)?.let { file ->
            file.delete()
            file.parentFile?.delete()
            markInstalled(id, false)
        }
    }

    override fun downloadProgress(modelId: String): Flow<Float> = flow {
        val src = sourceFor(modelId) ?: return@flow
        val dir = modelDir(modelId) ?: return@flow
        val dest = File(dir, src.filename)

        if (dest.exists()) { emit(1f); return@flow }
        dir.mkdirs()

        for (url in src.sources) {
            val tempFile = File(dir, dest.name + ".tmp")
            if (tempFile.exists()) tempFile.delete()

            try {
                val conn = URL(url).openConnection() as java.net.HttpURLConnection
                conn.setRequestProperty("User-Agent", "AshwathAI/0.1 (Android)")
                val token = com.ashwathai.ashwathai.core.HfTokenProvider.getToken()
                if (token.isNotBlank()) conn.setRequestProperty("Authorization", "Bearer $token")
                conn.connectTimeout = 30000
                conn.readTimeout = 120000
                conn.instanceFollowRedirects = true
                conn.connect()

                val code = conn.responseCode
                if (code == 401) {
                    throw Exception("Auth required. Set HF token in Settings")
                }
                if (code != 200) throw Exception("HTTP $code")

                val total = conn.contentLengthLong
                val input = conn.inputStream
                val output = java.io.FileOutputStream(tempFile)
                val buf = ByteArray(32 * 1024)
                var downloaded = 0L

                while (true) {
                    val read = input.read(buf)
                    if (read < 0) break
                    output.write(buf, 0, read)
                    downloaded += read
                    val t = if (total > 0) total else downloaded
                    emit(downloaded.toFloat() / t)
                }

                input.close()
                output.close()
                conn.disconnect()

                if (tempFile.exists() && tempFile.length() > 0) {
                    tempFile.renameTo(dest)
                    markInstalled(modelId, true)
                    emit(1f)
                    return@flow
                }
            } catch (_: Exception) {
                tempFile.delete()
                continue
            }
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
