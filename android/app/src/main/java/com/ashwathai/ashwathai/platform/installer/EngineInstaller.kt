package com.ashwathai.ashwathai.platform.installer

import android.content.Context
import com.ashwathai.ashwathai.core.downloads.ChecksumVerifier
import com.ashwathai.ashwathai.core.downloads.DownloadState
import com.ashwathai.ashwathai.core.downloads.EngineDownloader
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class EngineInstaller(
    private val context: Context,
    private val downloader: EngineDownloader,
    private val checksumVerifier: ChecksumVerifier,
    private val httpClient: HttpClient
) {
    val downloadState: StateFlow<DownloadState> = downloader.downloadState

    suspend fun install(abiOverride: String? = null): Result<Unit> {
        val abi = sanitizeAbi(abiOverride ?: android.os.Build.SUPPORTED_ABIS?.firstOrNull() ?: "arm64-v8a")
        val engineFile = File(context.filesDir, "bin/ashwathd")
        val checksumsFile = File(context.cacheDir, "checksums.txt")

        // 0. Start progress
        downloader.updateState(DownloadState.Downloading(0f))

        // 1. Download checksums
        val checksumsUrl = "https://github.com/atulkpal/ashwath-ai/releases/latest/download/checksums.txt"
        try {
            val response = httpClient.get(checksumsUrl)
            val checksumsContent = response.bodyAsText()

            // 2. Download engine binary (arm64-v8a is the only supported ABI)
            val binaryName = "ashwathd-$abi"
            val downloadUrl = "https://github.com/atulkpal/ashwath-ai/releases/latest/download/$binaryName"
            downloader.downloadFile(downloadUrl, engineFile)

            if (downloader.downloadState.value is DownloadState.Complete) {
                // 3. Verify checksum
                downloader.updateState(DownloadState.Verifying)
                val expectedChecksum = checksumVerifier.parseChecksums(checksumsContent, binaryName)
                if (expectedChecksum != null && checksumVerifier.verifyChecksum(engineFile, expectedChecksum)) {
                    // 4. Mark as executable
                    engineFile.setExecutable(true)
                    return Result.success(Unit)
                } else {
                    return Result.failure(Exception("Checksum verification failed"))
                }
            } else {
                return Result.failure(Exception("Download failed"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun sanitizeAbi(abi: String): String {
        return when {
            abi.startsWith("arm64") || abi == "arm64-v8a" -> "arm64-v8a"
            else -> "arm64-v8a"   // only arm64-v8a binaries are published
        }
    }

    fun isInstalled(): Boolean {
        val engineFile = File(context.filesDir, "bin/ashwathd")
        return engineFile.exists() && engineFile.canExecute()
    }
}
