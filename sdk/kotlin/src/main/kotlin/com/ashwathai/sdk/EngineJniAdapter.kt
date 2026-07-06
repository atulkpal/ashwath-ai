package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.ashwathai.runtime.api.InferenceResult
import com.ashwathai.sdk.jni.AshwathBridge
import com.ashwathai.sdk.jni.TokenCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EngineJniAdapter(
    private val bridge: AshwathBridge = AshwathBridge(),
    private val engineType: String = "llama",
    private val modelPath: String? = null,
    private val llamaBin: String? = null,
) : InferenceEngine {
    override val name: String = "Ashwath Engine"
    override val version: String = "0.1.0"

    override suspend fun initialize(): Result<Unit> {
        if (!AshwathBridge.isLoaded) {
            return Result.success(Unit)
        }
        val result = bridge.nativeInit(engineType, modelPath, llamaBin)
        return if (result == 1) Result.success(Unit)
        else Result.failure(Exception("Engine initialization failed (code=$result)"))
    }

    override suspend fun generate(
        prompt: String,
        options: GenerationOptions,
    ): Flow<InferenceResult> = callbackFlow {
        if (!AshwathBridge.isLoaded) {
            trySend(InferenceResult.Partial("Native engine library is not available. "))
            trySend(InferenceResult.Success("Native engine library is not available. Falling back to stub mode."))
            close()
            return@callbackFlow
        }

        val callback = object : TokenCallback {
            override fun onToken(text: String?, done: Boolean) {
                if (done) {
                    if (text != null && text.startsWith("Error: ")) {
                        trySend(InferenceResult.Error(text.removePrefix("Error: ")))
                    } else {
                        trySend(InferenceResult.Success(text ?: ""))
                    }
                    close()
                } else {
                    trySend(InferenceResult.Partial(text ?: ""))
                }
            }
        }

        val result = bridge.nativeGenerate(
            prompt,
            options.maxTokens,
            options.temperature,
            options.topK,
            options.topP,
            callback,
        )

        if (result != 1) {
            trySend(InferenceResult.Error("Generation failed (code=$result)"))
            close()
        }

        awaitClose {
            if (AshwathBridge.isLoaded) {
                bridge.nativeCancel()
            }
        }
    }

    override suspend fun stop() {
        if (AshwathBridge.isLoaded) {
            bridge.nativeShutdown()
        }
    }
}
