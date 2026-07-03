package com.ashwathai.sdk.jni

class AshwathBridge {
    companion object {
        init {
            System.loadLibrary("ashwath_engine")
        }
    }

    external fun nativeInit(modelPath: String?, dataDir: String?): Int

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
}
