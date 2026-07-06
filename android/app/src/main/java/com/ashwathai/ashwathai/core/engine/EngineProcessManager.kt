package com.ashwathai.ashwathai.core.engine

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class EngineProcessManager(private val context: Context) {
    private var process: Process? = null

    val engineBinary: File
        get() = File(context.codeCacheDir, "libashwathd.so")

    suspend fun start(port: Int): Result<Unit> = withContext(Dispatchers.IO) {
        if (isAlive()) {
            return@withContext Result.success(Unit)
        }

        if (!engineBinary.exists()) {
            return@withContext Result.failure(Exception("Engine binary not found at ${engineBinary.absolutePath}"))
        }

        try {
            // Test if we can run ANY process
            val testPb = ProcessBuilder("ls", "-l", engineBinary.parent!!)
            val testP = testPb.start()
            val output = testP.inputStream.bufferedReader().readText()
            Log.i(TAG, "Test ls output: $output")

            // Ensure binary is executable via shell chmod
            val chmodProcess = Runtime.getRuntime().exec(arrayOf("chmod", "775", engineBinary.absolutePath))
            chmodProcess.waitFor()

            Log.i(TAG, "Binary stats: exists=${engineBinary.exists()}, canExec=${engineBinary.canExecute()}, length=${engineBinary.length()}")

            val pb = ProcessBuilder(
                engineBinary.absolutePath,
                "--port", port.toString(),
                "--data-dir", context.filesDir.absolutePath
            )
            pb.directory(context.filesDir)
            pb.redirectErrorStream(true)

            Log.i(TAG, "Starting engine: ${pb.command()}")
            process = pb.start()

            // Monitor output in a separate thread
            Thread {
                try {
                    process?.inputStream?.bufferedReader()?.useLines { lines ->
                        lines.forEach { Log.d(TAG, "[STDOUT] $it") }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Stdout reader failed", e)
                }
            }.start()

            Thread {
                try {
                    process?.errorStream?.bufferedReader()?.useLines { lines ->
                        lines.forEach { Log.e(TAG, "[STDERR] $it") }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Stderr reader failed", e)
                }
            }.start()

            Result.success(Unit)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start engine", e)
            Result.failure(e)
        }
    }

    fun stop() {
        process?.destroy()
        process = null
        Log.i(TAG, "Engine stopped")
    }

    fun isAlive(): Boolean {
        return try {
            process?.exitValue()
            false
        } catch (e: IllegalThreadStateException) {
            true
        } ?: false
    }

    companion object {
        private const val TAG = "EngineProcessManager"
    }
}
