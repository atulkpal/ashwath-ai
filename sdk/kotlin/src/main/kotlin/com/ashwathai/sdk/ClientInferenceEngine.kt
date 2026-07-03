package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.ashwathai.runtime.api.InferenceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ClientInferenceEngine(private val grpcClient: EngineGrpcClient) : InferenceEngine {
    override val name: String = "Ashwath Engine"
    override val version: String = "0.1.0"

    override suspend fun initialize(): Result<Unit> {
        return grpcClient.connect()
    }

    override suspend fun generate(prompt: String, options: GenerationOptions): Flow<InferenceResult> = flow {
        grpcClient.generate(prompt, options).collect { response ->
            if (response.done) {
                emit(InferenceResult.Success(response.text))
            } else {
                emit(InferenceResult.Partial(response.text))
            }
        }
    }

    override suspend fun stop() {
        grpcClient.shutdown()
    }
}
