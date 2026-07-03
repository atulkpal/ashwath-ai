package com.ashwathai.ashwathai.platform.installer

import android.content.Context
import android.util.Log
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
        val engineFile = getEngineFile()
        val checksumsFile = File(context.cacheDir, "checksums.txt")

        downloader.updateState(DownloadState.Downloading(0f))

        // 1. Download checksums
        val checksumsUrl = "https://github.com/atulkpal/ashwath-ai/releases/latest/download/checksums.txt"
        try {
            val response = httpClient.get(checksumsUrl)
            val checksumsContent = response.bodyAsText()

            // 2. Download engine binary
            val binaryName = "ashwathd-$abi"
            val downloadUrl = "https://github.com/atulkpal/ashwath-ai/releases/latest/download/$binaryName"
            downloader.downloadFile(downloadUrl, engineFile)

            if (downloader.downloadState.value is DownloadState.Complete) {
                downloader.updateState(DownloadState.Verifying)
                val expectedChecksum = checksumVerifier.parseChecksums(checksumsContent, binaryName)
                if (expectedChecksum != null && checksumVerifier.verifyChecksum(engineFile, expectedChecksum)) {
                    // 3. Grant execute permission
                    val permissionResult = grantExecutePermission(engineFile)
                    if (permissionResult.isFailure) {
                        return permissionResult
                    }
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

    private fun grantExecutePermission(file: File): Result<Unit> {
        val path = file.absolutePath

        if (file.setExecutable(true, false)) {
            Log.i("EngineInstaller", "setExecutable(true, false) succeeded for $path")
            return Result.success(Unit)
        }

        // setExecutable failed — try shell chmod
        try {
            val proc = Runtime.getRuntime().exec("chmod 755 $path")
            proc.waitFor()
            if (proc.exitValue() == 0) {
                Log.i("EngineInstaller", "chmod 755 succeeded for $path")
                return Result.success(Unit)
            }
            Log.w("EngineInstaller", "chmod 755 exited ${proc.exitValue()} for $path")
        } catch (e: Exception) {
            Log.w("EngineInstaller", "chmod 755 failed", e)
        }

        // Both failed — try restoring SELinux context
        try {
            Runtime.getRuntime().exec("restorecon $path").waitFor()
            if (file.setExecutable(true, false)) {
                Log.i("EngineInstaller", "restorecon + setExecutable succeeded for $path")
                return Result.success(Unit)
            }
        } catch (_: Exception) {}

        return Result.failure(Exception(
            "Cannot make engine executable. " +
            "Android sandbox may block native binary execution on this device. " +
            "Path: $path, exists: ${file.exists()}, " +
            "canRead: ${file.canRead()}, canWrite: ${file.canWrite()}, " +
            "canExecute_before: ${file.canExecute()}"
        ))
    }

    fun getEngineFile(): File {
        return File(context.cacheDir, "ashwathd")
    }

    private fun sanitizeAbi(abi: String): String {
        return when {
            abi.startsWith("arm64") || abi == "arm64-v8a" -> "arm64-v8a"
            else -> "arm64-v8a"
        }
    }

    fun isInstalled(): Boolean {
        val file = getEngineFile()
        return file.exists() && file.canExecute()
    }
}
