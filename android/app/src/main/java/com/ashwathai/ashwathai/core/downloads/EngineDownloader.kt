package com.ashwathai.ashwathai.core.downloads

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
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

            httpClient.prepareGet(url) {
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null && contentLength > 0) {
                        _downloadState.value = DownloadState.Downloading(bytesSentTotal.toFloat() / contentLength)
                    }
                }
            }.execute { response ->
                if (response.status.isSuccess()) {
                    val channel: ByteReadChannel = response.bodyAsChannel()
                    destFile.parentFile?.mkdirs()

                    FileOutputStream(destFile).use { output ->
                        val buffer = ByteArray(8192)
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
            }
        } catch (e: Exception) {
            _downloadState.value = DownloadState.Failed(e.message ?: "Unknown error")
        }
    }
}
