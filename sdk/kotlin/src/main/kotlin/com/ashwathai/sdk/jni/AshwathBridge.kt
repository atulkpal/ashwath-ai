package com.ashwathai.sdk.jni

class AshwathBridge {
    companion object {
        var isLoaded = false
            private set

        init {
            try {
                System.loadLibrary("ashwath_engine")
                isLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                // Library not found, will handle gracefully in adapter
            }
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
