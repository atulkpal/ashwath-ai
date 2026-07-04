package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.sdk.generated.AshwathEngineGrpcKt
import com.ashwathai.sdk.generated.Empty
import com.ashwathai.sdk.generated.GenerateResponse
import com.ashwathai.sdk.generated.generateRequest
import com.ashwathai.sdk.generated.installRequest
import com.ashwathai.sdk.generated.removeRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.delay
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

                val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(newChannel)
                stub.getDeviceInfo(Empty.getDefaultInstance())

                println("EngineGrpcClient: Successfully connected and verified via RPC")
                channel = newChannel
                return Result.success(Unit)
            } catch (e: Exception) {
                println("EngineGrpcClient: Connection attempt failed: ${e.message}")
                lastException = e
                delay(1000L * (attempt + 1))
            }
        }
        return Result.failure(lastException ?: Exception("Failed to connect after $retries attempts"))
    }

    fun generate(prompt: String, options: GenerationOptions, modelId: String = ""): Flow<GenerateResponse> {
        val currentChannel = channel ?: throw IllegalStateException("Channel not connected")
        val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(currentChannel)

        val request = generateRequest {
            this.model = modelId
            this.prompt = prompt
            this.temperature = options.temperature
            this.topK = options.topK
            this.topP = options.topP
            this.maxTokens = options.maxTokens
        }

        return stub.generate(request)
    }

    suspend fun listModels(): Result<com.ashwathai.sdk.generated.ModelList> {
        return try {
            val currentChannel = channel ?: throw IllegalStateException("Channel not connected")
            val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(currentChannel)
            Result.success(stub.listModels(Empty.getDefaultInstance()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun installModel(modelId: String): Result<Unit> {
        return try {
            val currentChannel = channel ?: throw IllegalStateException("Channel not connected")
            val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(currentChannel)
            stub.installModel(installRequest { this.modelId = modelId })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeModel(modelId: String): Result<Unit> {
        return try {
            val currentChannel = channel ?: throw IllegalStateException("Channel not connected")
            val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(currentChannel)
            stub.removeModel(removeRequest { this.modelId = modelId })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun shutdown() {
        channel?.shutdown()?.awaitTermination(5, TimeUnit.SECONDS)
        channel = null
    }
}
