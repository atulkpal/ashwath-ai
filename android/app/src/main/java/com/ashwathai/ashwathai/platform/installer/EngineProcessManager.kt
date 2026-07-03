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

    fun getEngineFile(): File {
        return File(context.cacheDir, "ashwathd")
    }

    suspend fun start(port: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val engineFile = getEngineFile()
        if (!engineFile.exists()) {
            return@withContext Result.failure(Exception("Engine binary not found at ${engineFile.absolutePath}"))
        }

        Log.i(TAG, String.format(
            "Engine binary: path=%s exists=%b read=%b write=%b exec=%b size=%d",
            engineFile.absolutePath,
            engineFile.exists(),
            engineFile.canRead(),
            engineFile.canWrite(),
            engineFile.canExecute(),
            engineFile.length()
        ))

        try {
            val pb = ProcessBuilder(
                engineFile.absolutePath,
                "--port", port.toString(),
                "--data-dir", context.filesDir.absolutePath
            )
            pb.directory(context.filesDir)
            pb.redirectErrorStream(true)

            process = pb.start()

            delay(1000)

            if (isRunning()) {
                Result.success(Unit)
            } else {
                val exitVal = process?.exitValue()
                Result.failure(Exception("Engine exited immediately with code $exitVal"))
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
