package com.ashwathai.ashwathai.runtime.api

import kotlinx.coroutines.flow.Flow

interface InferenceEngine {
    val name: String
    val version: String

    suspend fun initialize(): Result<Unit>
    suspend fun generate(prompt: String, options: GenerationOptions): Flow<InferenceResult>
    suspend fun stop()
}

data class GenerationOptions(
    val temperature: Float = 0.7f,
    val topK: Int = 40,
    val topP: Float = 0.9f,
    val maxTokens: Int = 512
)

sealed class InferenceResult {
    data class Partial(val text: String) : InferenceResult()
    data class Success(val fullText: String) : InferenceResult()
    data class Error(val message: String) : InferenceResult()
}
