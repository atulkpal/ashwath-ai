package com.ashwathai.ashwathai.di

import android.content.Context
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
        return when (engineConfig.mode) {
            EngineMode.DEVELOPMENT -> ClientInferenceEngine(grpcClient)
            EngineMode.EMBEDDED -> {
                EmbeddedInferenceEngine(
                    ClientInferenceEngine(grpcClient),
                    engineConfig.port,
                    applicationContext.filesDir
                )
            }
            EngineMode.LOCAL_DAEMON -> {
                TODO("Local daemon (process-based) deprecated on Android for security")
            }
        }
    }
}
