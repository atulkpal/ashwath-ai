package com.ashwathai.ashwathai.di

/**
 * Configuration for the Inference Engine connection.
 */
data class EngineConfig(
    val host: String,
    val port: Int,
    val mode: EngineMode,
)

enum class EngineMode {
    /**
     * Connect to an engine running on the development host (e.g., via Emulator gateway).
     */
    DEVELOPMENT,

    /**
     * Connect to a local engine process running on the Android device.
     */
    LOCAL_DAEMON,

    /**
     * Use the embedded JNI adapter (if available).
     */
    EMBEDDED,
}

object EngineConfigProvider {
    /**
     * Provides the current engine configuration based on build type or environment.
     */
    fun get(): EngineConfig {
        // Architecture Shift: Android uses the Embedded Go runtime (JNI shared library)
        // to comply with modern Android security policies while preserving gRPC.
        return EngineConfig(
            host = "127.0.0.1",
            port = 50051,
            mode = EngineMode.EMBEDDED
        )
    }
}
