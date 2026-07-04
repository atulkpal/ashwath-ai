package com.ashwathai.sdk.jni

class AshwathBridge {
    companion object {
        var isLoaded = false
            private set

        init {
            try {
                System.loadLibrary("ashwath_engine")
                isLoaded = true
                println("AshwathBridge: Successfully loaded native library")
            } catch (e: UnsatisfiedLinkError) {
                println("AshwathBridge: Failed to load native library: ${e.message}")
            }
        }
    }

    external fun nativeInit(
        engineType: String?,
        modelPath: String?,
        llamaBin: String?,
    ): Int

    external fun nativeShutdown()

    external fun nativeGenerate(
        prompt: String?,
        maxTokens: Int,
        temperature: Float,
        topK: Int,
        topP: Float,
        callback: TokenCallback
    ): Int

    external fun nativeCancel(): Int

    external fun nativeStartServer(
        port: Int,
        dataDir: String?,
        engineType: String?,
    ): Int
}
