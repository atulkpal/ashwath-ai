package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.ashwathai.runtime.api.InferenceResult
import com.ashwathai.sdk.generated.GenerateResponse
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class ClientInferenceEngineTest {
    private val mockGrpcClient = mockk<EngineGrpcClient>()
    private val engine = ClientInferenceEngine(mockGrpcClient)

    @Test
    fun `generate maps partial responses correctly`() = runTest {
        val response = GenerateResponse.newBuilder()
            .setText("Hello")
            .setDone(false)
            .build()

        every { mockGrpcClient.generate(any(), any()) } returns flowOf(response)

        engine.generate("Hi", GenerationOptions()).collect { result ->
            assertEquals(InferenceResult.Partial("Hello"), result)
        }
    }

    @Test
    fun `generate maps final response correctly`() = runTest {
        val response = GenerateResponse.newBuilder()
            .setText("Final")
            .setDone(true)
            .build()

        every { mockGrpcClient.generate(any(), any()) } returns flowOf(response)

        engine.generate("Hi", GenerationOptions()).collect { result ->
            assertEquals(InferenceResult.Success("Final"), result)
        }
    }
}
