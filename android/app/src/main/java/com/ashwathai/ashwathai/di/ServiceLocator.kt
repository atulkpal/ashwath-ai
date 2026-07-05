package com.ashwathai.ashwathai.di

import android.content.Context
import com.ashwathai.ashwathai.data.repository.GrpcModelRepository
import com.ashwathai.ashwathai.domain.repository.ModelRepository
import java.io.File
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.sdk.ClientInferenceEngine
import com.ashwathai.sdk.EmbeddedInferenceEngine
import com.ashwathai.sdk.EngineGrpcClient

object ServiceLocator {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val engineConfig by lazy { EngineConfigProvider.get() }

    private val grpcClient by lazy {
        EngineGrpcClient(engineConfig.host, engineConfig.port)
    }

    fun provideInferenceEngine(): InferenceEngine {
        val modelsDir = File(applicationContext.filesDir, "models")
        val hasModel = modelsDir.exists() && modelsDir.listFiles()?.any { it.name.endsWith(".gguf") } == true
        val engineType = if (hasModel) "llama" else "mock"

        return when (engineConfig.mode) {
            EngineMode.DEVELOPMENT -> ClientInferenceEngine(grpcClient)
            EngineMode.EMBEDDED -> {
                EmbeddedInferenceEngine(
                    clientEngine = ClientInferenceEngine(grpcClient),
                    port = engineConfig.port,
                    dataDir = applicationContext.filesDir,
                    engineType = engineType,
                )
            }
            EngineMode.LOCAL_DAEMON -> {
                TODO("Local daemon (process-based) deprecated on Android for security")
            }
        }
    }

    fun provideModelRepository(): ModelRepository {
        return GrpcModelRepository(
            grpcClient = grpcClient,
            modelsDir = File(applicationContext.filesDir, "models"),
        )
    }
}
