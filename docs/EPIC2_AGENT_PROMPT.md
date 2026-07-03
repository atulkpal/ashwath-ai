# EPIC 2: Android Engine Integration — Agent Brief

## Context

You are implementing the Android side of Ashwath.AI's engine integration.
The Go Engine (EPIC 1, in parallel) is being built by another agent.

**Communication contract**: gRPC, defined in `engine/api/proto/service.proto`
**Engine distribution**: GitHub Releases (cross-compiled binary for Android arm64)
**Architecture**: Android app downloads the engine binary on first launch → starts as child process → communicates over localhost gRPC

**YOU DO NOT NEED the engine binary to be built.** Build everything against the gRPC proto contract. The final end-to-end test happens when both EPICs are complete.

---

## Existing Android Code (already works)

### Architecture (Clean Architecture + MVVM)
```
android/app/src/main/java/com/ashwathai/ashwathai/
├── MainActivity.kt
├── app/                          # Orchestration, navigation, theme
│   ├── AshwathApplication.kt
│   ├── MainScreen.kt
│   ├── components/
│   ├── navigation/
│   └── theme/
├── domain/                       # Pure Kotlin, no Android deps
│   ├── models/
│   │   ├── ChatMessage.kt
│   │   └── ModelInfo.kt
│   └── repository/
│       └── ModelRepository.kt    # Interface
├── data/repository/
│   └── MockModelRepository.kt    # Currently used
├── features/                     # Feature modules
│   ├── chat/    (events, state, ui, viewmodel)
│   ├── explore/ (events, state, ui, viewmodel)
│   ├── knowledge/ (ui only)
│   ├── library/ (events, state, ui, viewmodel)
│   ├── onboarding/ (ui only)
│   └── settings/ (ui only)
├── runtime/
│   └── api/
│       └── InferenceEngine.kt    # KEY INTERFACE — implement this
├── platform/
│   └── capability/
│       └── DeviceCapabilityProvider.kt
├── core/                         # EMPTY directories ready for you
│   ├── downloads/
│   ├── device/
│   ├── network/
│   ├── permissions/
│   ├── preferences/
│   ├── storage/
│   └── utils/
├── platform/                     # EMPTY directories
│   ├── installer/
│   ├── updater/
│   ├── analyzer/
│   └── benchmark/
└── di/                           # EMPTY — for dependency injection
```

### Key Interface — `runtime/api/InferenceEngine.kt`
```kotlin
interface InferenceEngine {
    val name: String
    val version: String
    suspend fun initialize(): Result<Unit>
    suspend fun generate(prompt: String, options: GenerationOptions): Flow<InferenceResult>
    suspend fun stop()
}
```
This is where your gRPC client plugs in.

### ChatViewModel (needs modification)
Currently uses a hardcoded `mockResponse()`. You will inject the `InferenceEngine` interface and use it for real generation.

### Build System
- Kotlin 2.2.10, Compose BOM 2026.02.01, AGP 9.2.1
- No gRPC or Ktor dependencies yet — you will add them to `gradle/libs.versions.toml` and `app/build.gradle.kts`

---

## Your Tasks (in dependency order)

### TASK 1: Add gRPC & Ktor dependencies
**Files**: `android/gradle/libs.versions.toml`, `android/app/build.gradle.kts`

Add:
- `io.grpc:grpc-okhttp:1.68.x`
- `io.grpc:grpc-stub:1.68.x`
- `io.grpc:grpc-protobuf-lite:1.68.x`
- `io.ktor:ktor-client-core:3.1.x` (for GitHub API calls)
- `io.ktor:ktor-client-okhttp:3.1.x`
- `javax.annotation:javax.annotation-api:1.3.2`

### TASK 2: Generate Kotlin gRPC stubs from proto
**Files**: `sdk/kotlin/` (new), with reference from `android/app/.../runtime/api/`

Copy or generate `AshwathEngineGrpc.kt`, `ServiceProto.kt` from `engine/api/proto/service.proto` using protoc.

Create a gRPC client class in `sdk/kotlin/src/main/kotlin/com/ashwathai/sdk/`:
- `EngineGrpcClient.kt` — wraps the gRPC stub, manages channel lifecycle
- `ClientInferenceEngine.kt` — implements `InferenceEngine` interface from `runtime/api/InferenceEngine.kt`
  - `initialize()` → start gRPC channel, verify connection
  - `generate()` → call `Generate` RPC, return streaming `Flow<InferenceResult>`
  - `stop()` → call `Shutdown` RPC, close channel

### TASK 3: Engine binary downloader
**Files**: `android/app/.../core/downloads/`

- `EngineDownloader.kt` — calls GitHub Releases API (latest release), downloads binary for the device's ABI (arm64-v8a / x86_64)
- `DownloadState` — sealed class: Idle, Downloading(progress), Verifying, Complete, Failed(error)
- Use Ktor client for the GitHub API + OkHttp for the binary download

### TASK 4: Checksum verification
**Files**: `android/app/.../core/downloads/`

- Download `checksums.txt` from GitHub Releases
- Verify the binary SHA-256 matches before installation
- Handle verification failure (delete corrupted binary, retry)

### TASK 5: Engine installer & process manager
**Files**: `android/app/.../platform/installer/`

- `EngineInstaller.kt` — coordinates: check versions → download → verify → extract (if needed) → mark installed
- `EngineProcessManager.kt` — manages the engine process lifecycle:
  - `start(dataDir, port)` → allocate port, launch `ashwathd`, wait for gRPC health check
  - `stop()` → call `Shutdown` RPC, force-kill if no response in 5s
  - `isRunning()` → check if process is alive
  - Handle crash → restart with backoff

### TASK 6: DI wiring
**Files**: `android/app/.../di/`

Create a simple manual DI or use Hilt:
- Provide `InferenceEngine` → returns `ClientInferenceEngine` (or `MockInferenceEngine`)
- Provide `EngineInstaller` and `EngineProcessManager` as singletons
- Provide `DeviceCapabilityProvider` → real implementation

### TASK 7: ChatViewModel → real engine
**Files**: `android/app/.../features/chat/viewmodel/ChatViewModel.kt`

Modify `ChatViewModel`:
- Accept `InferenceEngine` as constructor parameter (injected)
- On `SendMessage` → call `engine.generate()` instead of `mockResponse()`
- Handle streaming: each `InferenceResult.Partial` updates the last message text
- Handle errors: show error state in chat

### TASK 8: UI states for engine lifecycle
**Files**: `android/app/.../features/chat/ui/ChatScreen.kt`

Add UI for:
- **Not installed** → show "Download Engine" button with progress
- **Installing** → show progress bar + "Installing Ashwath Engine..."
- **Starting** → show "Starting engine..."
- **Connected** → normal chat UI (already exists)
- **Error/Crash** → show "Engine crashed" with restart button

---

## Testing Strategy

1. **Unit tests**: `EngineDownloader`, `EngineProcessManager`, gRPC client — all testable with mocks
2. **UI tests**: ChatScreen states with `ComposeTestRule` — use `MockInferenceEngine`
3. **Integration**: Manual — build engine binary, install on device, verify full flow

---

## DO NOT touch

- `engine/` directory — another agent is building the Go engine
- Any files outside `android/` and `sdk/kotlin/`
- The proto file (`engine/api/proto/service.proto`) — read-only contract

## Files you will create/modify

Please list every file you create or modify at the end so agents can track overlaps.
