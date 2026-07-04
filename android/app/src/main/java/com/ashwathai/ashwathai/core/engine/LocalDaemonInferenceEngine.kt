package com.ashwathai.ashwathai.core.engine

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.ashwathai.runtime.api.InferenceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class LocalDaemonInferenceEngine(
    private val installer: EngineInstaller,
    private val processManager: EngineProcessManager,
    private val clientEngine: InferenceEngine,
    private val port: Int
) : InferenceEngine {
    override val name: String get() = clientEngine.name
    override val version: String get() = clientEngine.version

    override suspend fun initialize(): Result<Unit> {
        // 1. Ensure installed
        if (!installer.isInstalled()) {
            installer.install().onFailure { return Result.failure(it) }
        }

        // 2. Start process
        processManager.start(port).onFailure { return Result.failure(it) }

        // 3. Connect via gRPC
        return clientEngine.initialize()
    }

    override suspend fun generate(prompt: String, options: GenerationOptions): Flow<InferenceResult> = flow {
        if (!processManager.isAlive()) {
            emit(InferenceResult.Error("Engine process died"))
            return@flow
        }
        emitAll(clientEngine.generate(prompt, options))
    }

    override suspend fun stop() {
        clientEngine.stop()
        processManager.stop()
    }
}
