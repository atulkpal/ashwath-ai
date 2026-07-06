package com.ashwathai.ashwathai.core.engine

import android.content.Context
import android.os.Build
import android.util.Log
import com.ashwathai.ashwathai.core.downloads.DownloadState
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class EngineInstaller(
    private val context: Context,
    private val httpClient: HttpClient = HttpClient(OkHttp)
) {
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    val engineBinary: File
        get() = File(context.codeCacheDir, "libashwathd.so")

    suspend fun isInstalled(): Boolean = withContext(Dispatchers.IO) {
        engineBinary.exists() && engineBinary.canExecute()
    }

    suspend fun install(): Result<Unit> = withContext(Dispatchers.IO) {
        if (isInstalled()) {
            return@withContext Result.success(Unit)
        }

        val abi = getSupportedAbi()
        // Correcting the repo URL based on the project settings
        val downloadUrl = "https://github.com/atulkpal/ashwath-ai/releases/download/engine/v0.1.0/ashwathd-$abi"

        Log.i(TAG, "Installing engine for ABI: $abi from $downloadUrl")

        try {
            _downloadState.value = DownloadState.Downloading(0f)

            val response = httpClient.get(downloadUrl) {
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null && contentLength > 0) {
                        val progress = bytesSentTotal.toFloat() / contentLength.toFloat()
                        _downloadState.value = DownloadState.Downloading(progress)
                    }
                }
            }

            if (response.status.value !in 200..299) {
                throw Exception("Failed to download engine: ${response.status}")
            }

            val bodyChannel = response.bodyAsChannel()
            val tempFile = File(context.cacheDir, "ashwathd_temp")

            FileOutputStream(tempFile).use { output ->
                bodyChannel.copyTo(output)
            }

            if (tempFile.renameTo(engineBinary)) {
                engineBinary.setExecutable(true)
                _downloadState.value = DownloadState.Complete
                Log.i(TAG, "Engine installed successfully at ${engineBinary.absolutePath}")
                Result.success(Unit)
            } else {
                throw Exception("Failed to move binary to destination")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Installation failed", e)
            _downloadState.value = DownloadState.Failed(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    private fun getSupportedAbi(): String {
        // Mapping Android ABIs to GitHub Release asset names
        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"
        return when {
            abi.contains("arm64") -> "arm64-v8a"
            abi.contains("x86_64") -> "linux-x64" // Emulator uses linux-x64 binary
            abi.contains("armeabi-v7") -> "armeabi-v7a"
            else -> abi
        }
    }

    companion object {
        private const val TAG = "EngineInstaller"
    }
}
