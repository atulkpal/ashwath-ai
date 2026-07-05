package com.ashwathai.sdk.jni

class AshwathBridge {
    companion object {
        var isLoaded = false
            private set

        const val ERR_OK = 0
        const val ERR_ENGINE_NIL = 1
        const val ERR_INIT_FAILED = 2
        const val ERR_GENERATE_FAILED = 3
        const val ERR_CANCEL_FAILED = 4
        const val ERR_ENGINE_BUSY = 5
        const val ERR_INVALID_ARGS = 6
        const val ERR_NOT_IMPLEMENTED = 7

        fun errorMessage(code: Int): String = when (code) {
            ERR_OK -> "OK"
            ERR_ENGINE_NIL -> "Engine not initialized"
            ERR_INIT_FAILED -> "Engine initialization failed"
            ERR_GENERATE_FAILED -> "Generation failed"
            ERR_CANCEL_FAILED -> "Cancel failed"
            ERR_ENGINE_BUSY -> "Engine is busy"
            ERR_INVALID_ARGS -> "Invalid arguments"
            ERR_NOT_IMPLEMENTED -> "Not implemented"
            else -> "Unknown error code: $code"
        }

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
