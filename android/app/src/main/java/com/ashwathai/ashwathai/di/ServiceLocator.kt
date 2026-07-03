package com.ashwathai.ashwathai.di

import android.content.Context
import com.ashwathai.ashwathai.core.downloads.ChecksumVerifier
import com.ashwathai.ashwathai.core.downloads.EngineDownloader
import com.ashwathai.ashwathai.platform.installer.EngineInstaller
import com.ashwathai.ashwathai.platform.installer.EngineProcessManager
import com.ashwathai.ashwathai.runtime.api.InferenceEngine
import com.ashwathai.sdk.ClientInferenceEngine
import com.ashwathai.sdk.EngineGrpcClient
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

object ServiceLocator {
    private var httpClient: HttpClient? = null

    fun provideHttpClient(): HttpClient {
        return httpClient ?: HttpClient(OkHttp).also { httpClient = it }
    }

    fun provideEngineDownloader(): EngineDownloader {
        return EngineDownloader(provideHttpClient())
    }

    fun provideChecksumVerifier(): ChecksumVerifier {
        return ChecksumVerifier()
    }

    fun provideEngineInstaller(context: Context): EngineInstaller {
        return EngineInstaller(
            context,
            provideEngineDownloader(),
            provideChecksumVerifier(),
            provideHttpClient()
        )
    }

    fun provideEngineProcessManager(context: Context): EngineProcessManager {
        return EngineProcessManager(context)
    }

    fun provideInferenceEngine(): InferenceEngine {
        val grpcClient = EngineGrpcClient("127.0.0.1", 50051)
        return ClientInferenceEngine(grpcClient)
    }
}
