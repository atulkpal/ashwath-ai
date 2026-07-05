package com.ashwathai.ashwathai.data.ollama

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class OllamaClient(private val serverUrl: String = "http://192.168.1.100:11434") {

    data class OllamaModel(
        val name: String,
        val size: Long,
        val digest: String,
    )

    data class PullProgress(
        val status: String,
        val completed: Long,
        val total: Long,
        val fraction: Float,
    )

    fun listModels(): Flow<List<OllamaModel>> = flow {
        val conn = URL("$serverUrl/api/tags").openConnection() as HttpURLConnection
        conn.connectTimeout = 5000
        conn.readTimeout = 10000
        conn.setRequestProperty("Accept", "application/json")

        try {
            conn.connect()
            if (conn.responseCode != 200) {
                throw Exception("Ollama not reachable at $serverUrl")
            }
            val body = BufferedReader(InputStreamReader(conn.inputStream)).readText()
            val json = JSONObject(body)
            val models = json.getJSONArray("models")
            val list = mutableListOf<OllamaModel>()
            for (i in 0 until models.length()) {
                val m = models.getJSONObject(i)
                list.add(OllamaModel(
                    name = m.getString("name"),
                    size = m.optLong("size", 0),
                    digest = m.optString("digest", ""),
                ))
            }
            emit(list)
        } finally {
            conn.disconnect()
        }
    }.flowOn(Dispatchers.IO)

    fun pullModel(modelName: String): Flow<PullProgress> = flow {
        val conn = URL("$serverUrl/api/pull").openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.connectTimeout = 10000
        conn.readTimeout = 0
        conn.setRequestProperty("Content-Type", "application/json")

        try {
            val requestBody = JSONObject().apply {
                put("name", modelName)
                put("stream", true)
            }
            conn.connect()
            OutputStreamWriter(conn.outputStream).use { it.write(requestBody.toString()) }

            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            var line = reader.readLine()
            while (line != null) {
                if (line.isNotEmpty()) {
                    val json = JSONObject(line)
                    val status = json.optString("status", "")
                    val completed = json.optLong("completed", 0)
                    val total = json.optLong("total", 0)
                    val fraction = if (total > 0) completed.toFloat() / total else 0f
                    emit(PullProgress(status, completed, total, fraction))
                    if (status == "success") break
                }
                line = reader.readLine()
            }
        } finally {
            conn.disconnect()
        }
    }.flowOn(Dispatchers.IO)

    fun isReachable(): Boolean = try {
        val conn = URL("$serverUrl/api/tags").openConnection() as HttpURLConnection
        conn.connectTimeout = 3000
        conn.readTimeout = 3000
        conn.connect()
        conn.responseCode == 200
    } catch (_: Exception) {
        false
    }
}
