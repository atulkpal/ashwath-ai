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
        val abi = abiOverride ?: android.os.Build.SUPPORTED_ABIS?.firstOrNull() ?: "arm64-v8a"
        val engineFile = File(context.filesDir, "bin/ashwathd")
        val checksumsFile = File(context.cacheDir, "checksums.txt")

        // 1. Download checksums
        val checksumsUrl = "https://github.com/ashwathai/ashwath-engine/releases/latest/download/checksums.txt"
        try {
            val response = httpClient.get(checksumsUrl)
            val checksumsContent = response.bodyAsText()

            // 2. Download engine binary
            val binaryName = "ashwathd-$abi"
            val downloadUrl = "https://github.com/ashwathai/ashwath-engine/releases/latest/download/$binaryName"
            downloader.downloadFile(downloadUrl, engineFile)

            if (downloader.downloadState.value is DownloadState.Complete) {
                // 3. Verify checksum
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

    fun isInstalled(): Boolean {
        val engineFile = File(context.filesDir, "bin/ashwathd")
        return engineFile.exists() && engineFile.canExecute()
    }
}
