package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.sdk.generated.AshwathEngineGrpcKt
import com.ashwathai.sdk.generated.GenerateResponse
import com.ashwathai.sdk.generated.generateRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class EngineGrpcClient(private val host: String, private val port: Int) {
    private var channel: ManagedChannel? = null

    suspend fun connect(retries: Int = 3): Result<Unit> {
        var lastException: Exception? = null
        repeat(retries) { attempt ->
            try {
                channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .build()
                // Simple health check could go here
                return Result.success(Unit)
            } catch (e: Exception) {
                lastException = e
                kotlinx.coroutines.delay(1000L * (attempt + 1))
            }
        }
        return Result.failure(lastException ?: Exception("Failed to connect after $retries attempts"))
    }

    fun generate(prompt: String, options: GenerationOptions): Flow<GenerateResponse> {
        val currentChannel = channel ?: throw IllegalStateException("Channel not connected")
        val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(currentChannel)

        val request = generateRequest {
            this.prompt = prompt
            this.temperature = options.temperature
            this.topK = options.topK
            this.topP = options.topP
            this.maxTokens = options.maxTokens
        }

        return stub.generate(request)
    }

    suspend fun shutdown() {
        channel?.shutdown()?.awaitTermination(5, TimeUnit.SECONDS)
        channel = null
    }
}
