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
                println("EngineGrpcClient: Attempting to connect to $host:$port (attempt ${attempt + 1})...")
                val newChannel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .build()

                // Real Health Check: Try to call an RPC
                val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(newChannel)
                stub.getDeviceInfo(com.ashwathai.sdk.generated.Empty.getDefaultInstance())

                println("EngineGrpcClient: Successfully connected and verified via RPC")
                channel = newChannel
                return Result.success(Unit)
            } catch (e: Exception) {
                println("EngineGrpcClient: Connection attempt failed: ${e.message}")
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
