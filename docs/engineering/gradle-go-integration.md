# Automatic Gradle Integration for Go Builds

This document describes how the Go-based engine is integrated into the Android build pipeline.

## Overview

The Ashwath AI engine is written in Go and compiled as a shared library (`.so`) using `c-shared` build mode. To ensure the engine is always up-to-date and to simplify the developer workflow, we integrate the Go build process directly into Gradle.

## Architecture

1.  **Go Source**: Located in the `/engine` directory at the project root.
2.  **JNI Bridge**: Entry points defined in `engine/cmd/libashwath/`.
3.  **Gradle Tasks**: Custom tasks in `android/app/build.gradle.kts` (or a separate plugin) that invoke the Go toolchain.
4.  **Artifacts**: The resulting `.so` files are placed in `android/app/src/main/jniLibs/<ABI>/`.

## Gradle Integration Details

The integration handles cross-compilation by mapping Android ABIs to Go architectures and configuring the NDK's Clang as the C compiler for CGO.

### Target Mapping

| Android ABI | GOOS | GOARCH | NDK Clang Target |
| :--- | :--- | :--- | :--- |
| `arm64-v8a` | `android` | `arm64` | `aarch64-linux-android24` |
| `x86_64` | `android` | `amd64` | `x86_64-linux-android24` |
| `armeabi-v7a` | `android` | `arm` | `armv7a-linux-androideabi24` |

### Requirements

*   **Go SDK**: Installed and available in the system PATH.
*   **Android NDK**: Installed via SDK Manager (path usually detected automatically by Gradle).
*   **Environment Variables**: `ANDROID_NDK_HOME` (optional, Gradle can typically find it via `sdk.dir` in `local.properties`).

### Implementation

The integration is implemented in `android/app/go-engine.gradle.kts` and applied in the app's `build.gradle.kts`.

**android/app/go-engine.gradle.kts**:
```kotlin
import org.apache.tools.ant.taskdefs.condition.Os
import java.util.Locale

val goEngineTasks = tasks.register("buildGoEngine") {
    group = "build"
    description = "Builds the Go engine shared library for all target ABIs"
}

val targetAbis = listOf("arm64-v8a", "x86_64")
val goSourceDir = file("${project.rootDir}/../engine")
val jniLibsDir = file("${projectDir}/src/main/jniLibs")

targetAbis.forEach { abi ->
    val capitalizedAbi = abi.split("-", "_").joinToString("") { it.capitalize() }
    val taskName = "buildGoEngine$capitalizedAbi"
    
    val buildTask = tasks.register<Exec>(taskName) {
        val (goArch, clangPrefix) = when (abi) {
            "arm64-v8a" -> "arm64" to "aarch64-linux-android24"
            "x86_64" -> "amd64" to "x86_64-linux-android24"
            else -> throw GradleException("Unsupported ABI: $abi")
        }

        val android = project.extensions.getByType<com.android.build.gradle.BaseExtension>()
        val ndkDir = android.ndkDirectory ?: throw GradleException("NDK not found")
        val hostTag = if (Os.isFamily(Os.FAMILY_WINDOWS)) "windows-x86_64" else "linux-x86_64"
        val extension = if (Os.isFamily(Os.FAMILY_WINDOWS)) ".cmd" else ""
        val ccPath = file("$ndkDir/toolchains/llvm/prebuilt/$hostTag/bin/$clangPrefix-clang$extension")

        inputs.dir(goSourceDir)
        outputs.file(file("$jniLibsDir/$abi/libashwath_engine.so"))

        workingDir(goSourceDir)
        executable("go")
        args("build", "-buildmode=c-shared", "-o", outputs.files.singleFile.absolutePath, "./cmd/libashwath/")

        environment("CGO_ENABLED", "1")
        environment("GOOS", "android")
        environment("GOARCH", goArch)
        environment("CC", ccPath.absolutePath)
        
        doFirst {
            if (!file("$jniLibsDir/$abi").exists()) file("$jniLibsDir/$abi").mkdirs()
        }
    }
    goEngineTasks.configure { dependsOn(buildTask) }
}

project.afterEvaluate {
    tasks.named("preBuild") { dependsOn(goEngineTasks) }
}
```

**android/app/build.gradle.kts**:
```kotlin
apply(from = "go-engine.gradle.kts")
```

## Benefits

*   **Reproducibility**: Ensures all developers and CI use the same build flags.
*   **Convenience**: Running `./gradlew assembleDebug` automatically rebuilds the Go engine if source files changed.
*   **ABI Safety**: Automatically builds for all required architectures.
