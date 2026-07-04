package com.ashwathai.sdk

import com.ashwathai.ashwathai.runtime.api.GenerationOptions
import com.ashwathai.sdk.generated.GenerateResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EngineGrpcClientTest {
    private val client = mockk<EngineGrpcClient>()

    @Test
    fun `generate passes modelId to server`() = runTest {
        val response = GenerateResponse.newBuilder()
            .setText("result")
            .setDone(true)
            .build()

        every { client.generate("test", any(), "my-model") } returns flowOf(response)

        val result = client.generate("test", GenerationOptions(), "my-model").first()
        assertEquals("result", result.text)
    }

    @Test
    fun `listModels returns model list`() = runTest {
        val result = Result.success(
            com.ashwathai.sdk.generated.ModelList.newBuilder()
                .addModels(com.ashwathai.sdk.generated.ModelInfo.newBuilder()
                    .setId("test-model")
                    .setName("Test Model")
                    .build())
                .build()
        )

        coEvery { client.listModels() } returns result

        val models = client.listModels()
        assertTrue(models.isSuccess)
        assertEquals("test-model", models.getOrThrow().modelsList[0].id)
    }

    @Test
    fun `installModel delegates correctly`() = runTest {
        coEvery { client.installModel("my-model") } returns Result.success(Unit)

        val result = client.installModel("my-model")
        assertTrue(result.isSuccess)
        coVerify { client.installModel("my-model") }
    }

    @Test
    fun `removeModel delegates correctly`() = runTest {
        coEvery { client.removeModel("my-model") } returns Result.success(Unit)

        val result = client.removeModel("my-model")
        assertTrue(result.isSuccess)
        coVerify { client.removeModel("my-model") }
    }
}
