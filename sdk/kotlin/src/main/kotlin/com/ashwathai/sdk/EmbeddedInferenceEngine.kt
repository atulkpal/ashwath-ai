package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.ashwathai.runtime.api.InferenceResult
import com.ashwathai.sdk.jni.AshwathBridge
import kotlinx.coroutines.flow.Flow
import java.io.File

class EmbeddedInferenceEngine(
    private val clientEngine: InferenceEngine,
    private val port: Int,
    private val dataDir: File,
    private val engineType: String = "mock",
    private val bridge: AshwathBridge = AshwathBridge()
) : InferenceEngine {
    override val name: String get() = clientEngine.name
    override val version: String get() = clientEngine.version

    override suspend fun initialize(): Result<Unit> {
        println("EmbeddedInferenceEngine: Initializing...")
        if (!AshwathBridge.isLoaded) {
            println("EmbeddedInferenceEngine: Bridge NOT loaded")
            return Result.failure(Exception("Native engine library not loaded"))
        }

        // 1. Start in-process gRPC server
        println("EmbeddedInferenceEngine: Starting native server on port $port...")
        val result = bridge.nativeStartServer(port, dataDir.absolutePath, engineType)
        if (result != AshwathBridge.ERR_OK) {
            println("EmbeddedInferenceEngine: Native server failed to start (code=$result)")
            return Result.failure(Exception("Failed to start embedded engine (${AshwathBridge.errorMessage(result)})"))
        }

        // 2. Connect via gRPC (localhost)
        println("EmbeddedInferenceEngine: Connecting to local gRPC server...")
        val clientResult = clientEngine.initialize()
        println("EmbeddedInferenceEngine: Client initialization result: $clientResult")
        return clientResult
    }

    override suspend fun generate(prompt: String, options: GenerationOptions): Flow<InferenceResult> {
        return clientEngine.generate(prompt, options)
    }

    override suspend fun stop() {
        clientEngine.stop()
        if (AshwathBridge.isLoaded) {
            bridge.nativeShutdown()
        }
    }
}
