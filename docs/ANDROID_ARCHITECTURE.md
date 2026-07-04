# Android Client Architecture

## Purpose

This document describes the architecture of the Ashwath AI **Android Client** (`android/`) — the flagship mobile frontend for the Ashwath AI platform.

It covers package structure, dependency injection, engine integration, the repository layer, ViewModel patterns, UI architecture, testing strategy, and build pipeline.

For the **overall platform architecture** (engine, SDK, multi-client communication), see [`ARCHITECTURE.md`](ARCHITECTURE.md).

---

## Design Philosophy

- **Thin Client**: The Android app contains zero AI business logic. All inference, model management, and intelligence are delegated to the Go Engine.
- **Engine Owns Business Logic**: The engine is the single source of truth for models, conversations, memory, and knowledge.
- **Android Owns Presentation & Platform Integration**: Android is responsible for Compose UI, navigation, theming, platform APIs (camera, mic, storage), and local preferences.
- **Repository Pattern**: Domain-layer interfaces define data contracts. Data-layer implementations (gRPC, mock, cached) are swappable without touching ViewModels.
- **MVVM**: ViewModels manage UI state via `StateFlow`. Composables observe state and render. Events flow downward as function calls, state flows upward as observable streams.
- **Separation of Concerns**: `domain/` has zero Android imports. `data/` knows about gRPC but not Compose. `features/` owns UI and user workflows but never business logic.

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Compose UI                           │
│  Screens · Components · Navigation · Theme             │
│  (features/*/ui/)                                       │
└──────────────────────┬──────────────────────────────────┘
                       │ StateFlow / Events
┌──────────────────────▼──────────────────────────────────┐
│                    ViewModels                           │
│  State management · Event handling · No business logic │
│  (features/*/viewmodel/)                                │
└──────────────────────┬──────────────────────────────────┘
                       │ suspend fun / Flow
┌──────────────────────▼──────────────────────────────────┐
│                   Repositories                          │
│  ModelRepository · (future: ConversationRepository)     │
│  (data/repository/)                                     │
└──────────────────────┬──────────────────────────────────┘
                       │ suspend fun / Flow
┌──────────────────────▼──────────────────────────────────┐
│                      SDK                                │
│  EngineGrpcClient · ClientInferenceEngine               │
│  (sdk/kotlin/ — Gradle subproject :sdk)                 │
└──────────────────────┬──────────────────────────────────┘
                       │ gRPC (localhost)
┌──────────────────────▼──────────────────────────────────┐
│              InferenceEngine Runtime                    │
│  EmbeddedInferenceEngine (JNI .so)                      │
│  or ClientInferenceEngine (TCP to daemon)               │
└──────────────────────┬──────────────────────────────────┘
                       │ gRPC / JNI
┌──────────────────────▼──────────────────────────────────┐
│               Ashwath Engine (Go)                       │
│  Model registry · Downloader · Inference · Benchmark    │
│  (engine/)                                              │
└─────────────────────────────────────────────────────────┘
```

---

## Package Structure

```
android/app/src/main/java/com/ashwathai/ashwathai/
├── app/                       Application shell
│   ├── AshwathApplication.kt  Application class, ServiceLocator.init()
│   ├── MainScreen.kt          Root composable
│   ├── components/            Shared UI components
│   ├── navigation/            NavHost + Screen sealed class
│   └── theme/                 Material 3 + Synthetic Noir theming
├── core/                      Platform-level utilities
│   ├── downloads/             HTTP download with progress tracking
│   └── engine/                Engine binary management (checksum, extraction)
├── data/                      Repository implementations
│   └── repository/            GrpcModelRepository (mock fallback)
├── di/                        Dependency injection
│   ├── ServiceLocator.kt      Manual DI root
│   └── EngineConfig.kt        Runtime mode selection
├── domain/                    Pure Kotlin business layer
│   ├── models/                ModelInfo, (future: Conversation, Message)
│   └── repository/            ModelRepository interface
├── features/                  Feature modules
│   ├── chat/                  Chat workspace
│   ├── explore/               Model discovery and download
│   ├── knowledge/             Knowledge base management (future)
│   ├── library/               Installed model management
│   ├── onboarding/            First-launch flow
│   └── settings/              App configuration
├── platform/                  Android-specific capabilities
│   └── capability/            Feature detection (NPU, GPU, etc.)
└── MainActivity.kt            Single-activity entry point
```

### Package Responsibilities

| Package | Responsibility |
|---------|----------------|
| `app/` | Application lifecycle, single Activity, root navigation, shared theming |
| `core/` | Engine binary download, integrity verification, platform utilities |
| `data/` | Implements domain repository interfaces via gRPC (or mock) |
| `di/` | Manual service locator providing `InferenceEngine` and `ModelRepository` |
| `domain/` | Pure Kotlin interfaces and models — zero Android/SDK imports |
| `features/` | Self-contained feature modules — each owns its UI, ViewModel, state, and events |
| `platform/` | Android hardware capability detection |

### SDK Module (`sdk/kotlin/`)

The SDK is a Gradle subproject (`:sdk`) at `sdk/kotlin/`, included via `settings.gradle.kts`:

```
sdk/kotlin/src/main/kotlin/com/ashwathai/
├── ashwathai/runtime/api/       InferenceEngine interface
│   ├── InferenceEngine.kt       Interface + GenerationOptions + InferenceResult
├── sdk/                         Engine client implementations
│   ├── ClientInferenceEngine.kt gRPC-based inference engine
│   ├── EmbeddedInferenceEngine.kt JNI lifecycle + gRPC fallback
│   ├── EngineGrpcClient.kt      Low-level gRPC stub wrapper
│   ├── EngineJniAdapter.kt      JNI native function declarations
│   └── jni/                     JNI bridge helpers
```

The SDK is published as a JAR via `./gradlew :sdk:jar`. It depends on `grpc-okhttp`, `grpc-protobuf-lite`, and `protobuf-kotlin-lite`.

---

## Dependency Injection

Ashwath AI uses **manual dependency injection** via `ServiceLocator`, a singleton object that provides the `InferenceEngine` and `ModelRepository` to ViewModels.

```kotlin
// di/ServiceLocator.kt — simplified
object ServiceLocator {
    fun init(context: Context)

    fun provideInferenceEngine(): InferenceEngine
    fun provideModelRepository(): ModelRepository
}
```

### Runtime Modes (`EngineConfig.kt`)

| Mode | When | Transport |
|------|------|-----------|
| `EMBEDDED` | Production Android | JNI (`libashwath_engine.so`) → in-process gRPC server |
| `DEVELOPMENT` | Emulator / dev | TCP gRPC to host machine |
| `LOCAL_DAEMON` | Deprecated on Android | Was: child process gRPC — removed for security |

The current default is `EMBEDDED`, which loads the Go engine as a shared library via JNI and starts an in-process gRPC server on `127.0.0.1:50051`.

### Future Migration Path

As the app grows, `ServiceLocator` may be replaced with a standard DI framework. Candidates:
- **Hilt**: Official Android DI, integrates with ViewModel, Navigation, and WorkManager.
- **Koin**: Lighter weight, Kotlin-native, no annotation processing.

Migration would proceed incrementally: wire Hilt within `app/` first, then expand to `features/`, keeping `sdk/` framework-agnostic.

---

## Engine Integration

### Embedded Engine (Production)

On physical Android devices, the Go engine is compiled as `libashwath_engine.so` (via CGO's `c-shared` mode) and placed in `jniLibs/arm64-v8a/` and `jniLibs/x86_64/`.

```
App Process
  ┌────────────────────────────────┐
  │  EmbeddedInferenceEngine       │
  │    ├── nativeStartServer()     │  ← JNI call into .so
  │    └── gRPC client → localhost │  ← loopback connection
  └────────────────────────────────┘
```

`EmbeddedInferenceEngine` calls `nativeStartServer(port, dataDir)` via JNI, which bootstraps the Go runtime and starts a gRPC goroutine. The Kotlin gRPC client then connects to `127.0.0.1:{port}` as if connecting to a remote server.

### gRPC Runtime (Development)

On the emulator or when testing against a host machine, `ClientInferenceEngine` connects to `EngineGrpcClient` which opens a TCP gRPC channel to a configurable host:port. This mode requires a running `ashwathd` binary on the host.

### Runtime Selection

`ServiceLocator.provideInferenceEngine()` selects the runtime based on `EngineConfig.mode`:
- `EMBEDDED` → wraps `ClientInferenceEngine` inside `EmbeddedInferenceEngine`
- `DEVELOPMENT` → plain `ClientInferenceEngine`
- `LOCAL_DAEMON` → `TODO()` — security-deprecated

### Backend Selection

During EPIC 3 Phase A, backend selection was wired through the full stack:
- **Go** (`mobile/ashwath.go`): Backend type (mock vs. real) configured via engine config
- **JNI bridge** (`bridge.go`, `bridge_jni.go`): Forwards backend type to C exports
- **Kotlin SDK** (`EngineJniAdapter.kt`, `AshwathBridge.kt`): Reads backend type and configures `EngineGrpcClient` accordingly

### Model Management

Models are managed entirely by the Go engine. The Android client:
- **Lists models** via `EngineGrpcClient.listModels()` → `GrpcModelRepository.getRecommendedModels()` / `getInstalledModels()`
- **Downloads models** via `EngineGrpcClient.installModel()` → `GrpcModelRepository.downloadModel()`
- **Removes models** via `EngineGrpcClient.removeModel()` → `GrpcModelRepository.deleteModel()`

The domain `ModelInfo` object is populated from proto `ModelInfo` (id, name, provider, size, parameters, tags, installed state).

### JNI Bridge

See [`JNI_ARCHITECTURE.md`](JNI_ARCHITECTURE.md) for the complete JNI integration design, including:
- `bridge.go` C API exports (init, shutdown, generate with callback, cancel)
- `AshwathBridge.kt` — Kotlin `external fun` declarations
- Build pipeline (`CGO_ENABLED=1`, NDK Clang, c-shared output)

---

## Repository Layer

### ModelRepository (Implemented)

```kotlin
interface ModelRepository {
    fun getRecommendedModels(): Flow<List<ModelInfo>>
    fun getInstalledModels(): Flow<List<ModelInfo>>
    fun getModel(id: String): Flow<ModelInfo?>
    suspend fun downloadModel(id: String)
    suspend fun deleteModel(id: String)
}
```

Implementation:
- **`GrpcModelRepository`**: delegates to `EngineGrpcClient.listModels()`, `installModel()`, `removeModel()`. Maps proto `ModelInfo` → domain `ModelInfo`.
- **`MockModelRepository`**: returns hardcoded model lists. Used when gRPC is unavailable.

### ConversationRepository (Future)

Planned for EPIC 4 (Conversation & Memory):
```kotlin
interface ConversationRepository {
    fun getConversations(): Flow<List<Conversation>>
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun createConversation(title: String): Conversation
    suspend fun deleteConversation(id: String)
    suspend fun clearConversation(id: String)
}
```

### Additional Repositories (Planned)

- **KnowledgeRepository**: Document ingestion, search, and retrieval for RAG
- **SettingsRepository**: User preferences (theme, active model, generation parameters)
- **PluginRepository**: Community plugin discovery and management

---

## ViewModel Guidelines

### Responsibilities
- Own UI state via `MutableStateFlow` / `StateFlow`
- Expose event handlers (`fun onEvent(event: FeatureEvent)`)
- Launch coroutines in `viewModelScope`
- Call repository methods (never SDK or gRPC directly)
- Map repository results to UI state

### Constraints
- **No business logic**: ViewModels never implement AI logic, never call the engine directly, never construct gRPC requests manually
- **No Android framework dependencies**: ViewModels import only `androidx.lifecycle.ViewModel` + `viewModelScope`
- **Testable by construction**: ViewModels accept repository + dispatcher as constructor parameters with defaults

### Pattern

```kotlin
class ExploreViewModel(
    private val modelRepository: ModelRepository = ServiceLocator.provideModelRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.DownloadModel -> downloadModel(event.modelId)
        }
    }

    private fun downloadModel(modelId: String) {
        viewModelScope.launch {
            withContext(ioDispatcher) { modelRepository.downloadModel(modelId) }
            loadModels()
        }
    }
}
```

### Testing

ViewModels are tested with `FakeModelRepository` (in-memory implementation) and `StandardTestDispatcher`:

```kotlin
@Test
fun `downloadModel calls repository`() = runTest(testDispatcher) {
    viewModel.onEvent(ExploreEvent.DownloadModel("model-a"))
    advanceUntilIdle()
    assertTrue("model-a" in fakeRepo.downloadedIds)
}
```

---

## UI Layer

### Jetpack Compose + Material 3
- All UI is built with Jetpack Compose and Material 3 components.
- Theming follows the **Synthetic Noir** design system (see `design/` directory).

### Navigation
- Single-activity architecture (`MainActivity.kt`)
- Navigation via Jetpack Navigation Compose (`AshwathNavHost.kt`)
- `Screen` sealed class defines all routes

### State-Driven
- Composables observe `StateFlow` from ViewModels via `collectAsState()`
- No mutable state in Composables — all state flows downward from ViewModels
- Events flow upward via lambda callbacks

### Feature Modules
Each feature under `features/` follows a consistent structure:

```
features/{name}/
├── events/       {Name}Event sealed class
├── state/        {Name}State data class
├── ui/           Compose screens and components
└── viewmodel/    {Name}ViewModel
```

---

## Testing Strategy

### Unit Tests (Present)
- **SDK**: `ClientInferenceEngineTest` (response mapping), `EngineGrpcClientTest` (RPC mocking)
- **ViewModels**: `ExploreViewModelTest`, `LibraryViewModelTest` with `FakeModelRepository`
- **Engine (Go)**: 42+ tests across 8 packages

### ViewModel Testing Pattern

ViewModels use constructor-injected dependencies with defaults, enabling test-only fake implementations:

```kotlin
// Production
ExploreViewModel()

// Test
ExploreViewModel(fakeRepo, testDispatcher)
```

The `FakeModelRepository` tracks `downloadedIds` and `deletedIds` for assertion, and exposes `MutableStateFlow` for controlled emission.

### Compose UI Tests (Planned)
- `@ComposeTestRule` for screen-level component verification
- Testing Library patterns for event simulation

### Integration Tests (Planned)
- Download → install → connect flow on emulator
- End-to-end ChatViewModel → EngineGrpcClient pipeline

### Instrumentation Tests (Future)
- Real device tests with embedded engine `.so`
- Runtime mode switching between EMBEDDED and DEVELOPMENT

---

## Build & Runtime

### Gradle
- Standalone Gradle project in `android/`
- Kotlin DSL (`build.gradle.kts`)
- Single module app with SDK as subproject (`:sdk`)

### Native Engine Binaries
- Go engine compiled to `.so` via Gradle custom tasks (`buildGoEngineArm64V8a`, `buildGoEngineX8664`)
- Targets: `arm64-v8a` (device), `x86_64` (emulator)
- Output: `android/app/src/main/jniLibs/{abi}/libashwath_engine.so`
- Triggered automatically during `preBuild`

### Proto Generation
- Proto source: `sdk/kotlin/src/main/proto/ashwathai/v1/engine.proto` (synced from `engine/api/proto/service.proto`)
- Regenerate via: `./gradlew :sdk:generateProto`
- Produces Kotlin gRPC stubs in `sdk/kotlin/build/generated/`
- Uses `protobuf-lite` for minimal APK footprint

### SDK Module
- Included as `project(":sdk")` in `settings.gradle.kts`
- Published as standalone JAR via `./gradlew :sdk:jar`
- Dependencies: gRPC-okhttp, protobuf-kotlin-lite, coroutines

### JNI Integration
- `System.loadLibrary("ashwath_engine")` in `AshwathBridge` companion object
- Go exports JNI-named functions via `//export Java_...` directives
- NDK Clang cross-compiler configured automatically from `local.properties`

For detailed build commands, see [`PLATFORM_GUIDE.md`](PLATFORM_GUIDE.md).

---

## Relationship to Other Documents

| Document | Relationship |
|----------|-------------|
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Platform-level architecture — engine, SDK layer, multi-client communication. This document describes Android specifically. |
| [`ENGINE_API.md`](ENGINE_API.md) | gRPC RPC definitions (Generate, ListModels, InstallModel, RemoveModel, GetDeviceInfo, Shutdown). Android is the primary consumer. |
| [`JNI_ARCHITECTURE.md`](JNI_ARCHITECTURE.md) | JNI bridge design — C API, Kotlin JNI adapter, build pipeline, ownership boundaries. Android-specific platform integration. |
| [`PLATFORM_GUIDE.md`](PLATFORM_GUIDE.md) | Build commands, CI/CD, coding standards, testing philosophy across all platforms. |
| [`ENGINE_CLIENT_CONTRACT.md`](ENGINE_CLIENT_CONTRACT.md) | Contract between engine and all clients. Android adheres to this contract. |
| [`PROJECT_STATE.md`](PROJECT_STATE.md) | Live operational dashboard — milestones, priorities, known issues. |
| [`PLATFORM_RULES.md`](PLATFORM_RULES.md) | Invariant rules governing all platforms. Android must comply with all 12 rules. |
| [`DOCUMENTATION_GOVERNANCE.md`](DOCUMENTATION_GOVERNANCE.md) | Documentation ownership and maintenance policy across all workspaces. |

---

## Future Roadmap

### Short Term (EPIC 3 Phase B / Android Polish)
- [ ] Resolve Stitch/Google Fonts theme conflict for full UI compile
- [ ] Wire ExploreScreen and LibraryScreen to ViewModels
- [ ] Model download progress UI (streaming RPC)
- [ ] Handle engine-offline and not-installed states
- [ ] Instrumented tests for download → install → connect

### Medium Term (EPIC 3 Phase C)
- [ ] Model download progress streaming
- [ ] App lifecycle management (pause/resume engine)
- [ ] Error recovery and reconnection
- [ ] Granular JNI error codes from Go

### Long Term
- [ ] **Voice**: STT/TTS integration for voice chat
- [ ] **Offline-First**: Full functionality without network (models cached locally)
- [ ] **Accessibility**: Content descriptions, TalkBack, scaling, reduced motion
- [ ] **Performance**: Startup time optimization, APK size reduction, Compose rendering benchmarks
- [ ] **Wear OS**: Companion app for quick queries and voice input (future)
- [ ] **Android Auto**: Optimized driving-mode interface for hands-free AI (future)
