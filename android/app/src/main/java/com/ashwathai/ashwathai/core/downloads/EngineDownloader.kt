package com.ashwathai.ashwathai.core.downloads

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

class EngineDownloader(private val httpClient: HttpClient) {
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    suspend fun downloadFile(url: String, destFile: File) {
        try {
            _downloadState.value = DownloadState.Downloading(0f)

            val response: HttpResponse = httpClient.get(url) {
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null && contentLength > 0) {
                        _downloadState.value = DownloadState.Downloading(bytesSentTotal.toFloat() / contentLength)
                    }
                }
            }

            if (response.status.isSuccess()) {
                val channel: ByteReadChannel = response.bodyAsChannel()
                destFile.parentFile?.mkdirs()

                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (!channel.isClosedForRead) {
                        val read = channel.readAvailable(buffer)
                        if (read > 0) {
                            output.write(buffer, 0, read)
                        }
                        if (read == -1) break
                    }
                }
                _downloadState.value = DownloadState.Complete
            } else {
                _downloadState.value = DownloadState.Failed("HTTP error: ${response.status}")
            }
        } catch (e: Exception) {
            _downloadState.value = DownloadState.Failed(e.message ?: "Unknown error")
        }
    }
}
