package com.ashwathai.ashwathai.platform.installer

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class EngineProcessManager(private val context: Context) {
    private var process: Process? = null
    private val TAG = "EngineProcessManager"

    suspend fun start(port: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val engineFile = File(context.filesDir, "bin/ashwathd")
        if (!engineFile.exists()) {
            return@withContext Result.failure(Exception("Engine not installed"))
        }

        try {
            val pb = ProcessBuilder(
                engineFile.absolutePath,
                "--port", port.toString(),
                "--data-dir", context.filesDir.absolutePath
            )
            pb.directory(context.filesDir)
            pb.redirectErrorStream(true)

            process = pb.start()

            // Wait for gRPC health check or just wait a bit
            // In a real app, we should check if the port is open
            delay(1000)

            if (isRunning()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Process failed to start"))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start engine", e)
            Result.failure(e)
        }
    }

    fun stop() {
        process?.destroy()
        process = null
    }

    fun isRunning(): Boolean {
        return try {
            process?.exitValue()
            false
        } catch (e: IllegalThreadStateException) {
            true
        } ?: false
    }
}
