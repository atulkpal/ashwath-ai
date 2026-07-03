package com.ashwathai.ashwathai.core.downloads

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class EngineDownloader(private val httpClient: HttpClient) {
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    fun updateState(state: DownloadState) {
        _downloadState.value = state
    }

    suspend fun downloadFile(url: String, destFile: File) = withContext(Dispatchers.IO) {
        try {
            _downloadState.value = DownloadState.Downloading(0f)

            val response: HttpResponse = httpClient.get(url)
            if (response.status.value in 200..299) {
                destFile.parentFile?.mkdirs()
                val channel: ByteReadChannel = response.bodyAsChannel()
                val totalBytes = response.contentLength() ?: -1L
                var bytesRead = 0L

                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        val read = channel.readAvailable(buffer)
                        if (read <= 0) break
                        output.write(buffer, 0, read)
                        bytesRead += read
                        if (totalBytes > 0) {
                            _downloadState.value = DownloadState.Downloading(bytesRead.toFloat() / totalBytes)
                        }
                    }
                }
                _downloadState.value = DownloadState.Complete
            } else {
                _downloadState.value = DownloadState.Failed("Server returned ${response.status}")
            }
        } catch (e: Exception) {
            _downloadState.value = DownloadState.Failed(e.message ?: "Download error")
        }
    }
}
