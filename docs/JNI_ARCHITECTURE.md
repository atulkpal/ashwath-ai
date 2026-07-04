# Ashwath Engine — JNI Integration Architecture

## Design Objective

Establish the permanent integration layer between the Ashwath Engine (Go) and all
frontend platforms. Android is the reference implementation; Desktop, iOS, and Web
use the same Engine with different platform adapters.

---

## Unified API Strategy

**Ashwath AI uses gRPC as the primary communication protocol across all platforms.**

While Android embeds the Go engine as a shared library (`.so`), it still utilizes the same gRPC service definition. The JNI bridge is responsible for:
1. Bootstrapping the Go runtime.
2. Launching an in-process gRPC server on a loopback interface.
3. Managing the lifecycle (Shutdown/Init) of the native engine.

This approach ensures that the Kotlin SDK (and future SDKs) remain platform-agnostic, using the same gRPC client logic regardless of whether the engine is a local daemon or an embedded library.

---

## Data Flow (Embedded gRPC)

```
┌──────────────────────────────────────────────────────────┐
│  Android App Process                                     │
│                                                          │
│  ┌──────────┐   ┌────────────┐   ┌──────────────────┐   │
│  │ Compose  │──▶│ ViewModels │──▶│  Engine SDK      │   │
│  │   UI     │   │            │   │  (Kotlin)        │   │
│  └──────────┘   └────────────┘   └───────┬──────────┘   │
│                                          │ gRPC (localhost)
│                                  ┌───────▼──────────┐   │
│                                  │  gRPC Server     │   │
│                                  │  (Inside Go .so) │   │
│                                  └───────┬──────────┘   │
│                                          │              │
│  ════════════════════════════════════════╪═══════════════│
│                            JNI Boundary │               │
│  ════════════════════════════════════════╪═══════════════│
│                                          │              │
│                                  ┌───────▼──────────┐   │
│                                  │  libashwath       │   │
│                                  │  engine.so       │   │
│                                  │  (Go → c-shared) │   │
└──────────────────────────────────────────────────────────┘
```

    Desktop / Server                     iOS                     Web
    ┌──────────────┐              ┌──────────────┐       ┌──────────────┐
    │  ashwathd    │              │  C Binding   │       │  WASM Build  │
    │  (gRPC srv)  │              │  (cgo .dylib)│       │  (wasm)      │
    └──────────────┘              └──────────────┘       └──────────────┘
```

---

## Platform Independence

**The Go Engine has zero knowledge of Android, JNI, or Java.**

It exports a plain C API via `cgo`:

```c
// Engine lifecycle
int    engine_init(const char* model_path, const char* data_dir);
void   engine_shutdown(void);
int    engine_is_running(void);

// Model management
int    engine_load_model(const char* model_id);
int    engine_unload_model(const char* model_id);

// Generation (streaming via callback)
typedef void (*engine_token_cb)(const char* text, int done, void* userdata);
int    engine_generate(const char* prompt, int max_tokens,
                       float temperature, int top_k, float top_p,
                       engine_token_cb callback, void* userdata);
int    engine_cancel(void);

// Logging
void   engine_set_log_level(int level);
// level: 0=debug, 1=info, 2=warn, 3=error
```

Each platform provides a thin adapter:

| Platform | Adapter | Mechanism |
|----------|---------|-----------|
| Android  | JNI (C → Java) | `libashwath_engine.so` loaded via `System.loadLibrary()` |
| Desktop  | gRPC client | TCP connection to `ashwathd` binary |
| iOS      | C binding | `libashwath_engine.dylib` via Swift C interop |
| Web      | WASM bind | `ashwathd.wasm` via `syscall/js` |

---

## Engine API (Platform-Independent)

### Lifecycle

```go
// Initialize the engine with a model and data directory.
// Returns error if model loading fails.
Initialize(modelPath string, dataDir string) error

// Shut down the engine, release all resources.
Shutdown() error

// Check if the engine is ready.
IsRunning() bool
```

### Model Management

```go
// Load a model by its registry ID.
LoadModel(modelID string) error

// Unload the currently loaded model.
UnloadModel() error
```

### Generation

```go
// Generate text with streaming tokens via callback.
// The callback is called zero or more times with partial tokens,
// then once with done=true.
Generate(
    prompt    string,
    maxTokens int,
    temp      float64,
    topK      int,
    topP      float64,
    onToken   TokenCallback,  // called on each token
) error

type TokenCallback interface {
    OnToken(text string, done bool)
}
```

### Cancellation

```go
// Cancel an in-progress generation.
Cancel() error
```

---

## JNI Boundary (Android-Specific)

The JNI adapter is intentionally thin — it translates between Kotlin types and
C types, nothing more.

### Go Exports (cgo, JNI-named symbols)

```go
//export Java_com_ashwathai_sdk_jni_AshwathBridge_nativeInit
func nativeInit(env *C.JNIEnv, thiz C.jobject, modelPath, dataDir C.jstring) C.jint

//export Java_com_ashwathai_sdk_jni_AshwathBridge_nativeGenerate
func nativeGenerate(env *C.JNIEnv, thiz C.jobject, prompt C.jstring,
    maxTokens C.jint, temperature C.jfloat, topK C.jint, topP C.jfloat,
    callback C.jobject) C.jint

//export Java_com_ashwathai_sdk_jni_AshwathBridge_nativeCancel
func nativeCancel(env *C.JNIEnv, thiz C.jobject) C.jint

//export Java_com_ashwathai_sdk_jni_AshwathBridge_nativeShutdown
func nativeShutdown(env *C.JNIEnv, thiz C.jobject) void
```

### Kotlin JNI Adapter

```kotlin
// engine/sdk/kotlin/src/main/kotlin/com/ashwathai/sdk/jni/AshwathBridge.kt
class AshwathBridge {
    companion object {
        init { System.loadLibrary("ashwath_engine") }
    }

    external fun nativeInit(modelPath: String?, dataDir: String?): Int
    external fun nativeGenerate(
        prompt: String?, maxTokens: Int, temperature: Float,
        topK: Int, topP: Float, callback: TokenCallback
    ): Int
    external fun nativeCancel(): Int
    external fun nativeShutdown()
}

interface TokenCallback {
    fun onToken(text: String?, done: Boolean)
}
```

### Kotlin Engine SDK (consumed by ViewModels)

```kotlin
// engine/sdk/kotlin/src/main/kotlin/com/ashwathai/sdk/EngineJniAdapter.kt
class EngineJniAdapter(private val bridge: AshwathBridge = AshwathBridge()) {

    fun initialize(modelPath: String, dataDir: String): Result<Unit>
    fun generate(prompt: String, options: Options, onToken: (String, Boolean) -> Unit): Result<Unit>
    fun cancel(): Result<Unit>
    fun shutdown(): Result<Unit>
}
```

---

## Build Pipeline

### Go → Android .so

```makefile
# Requires: ANDROID_NDK_HOME (set by Android Studio / gradle)
# Requires: Go toolchain (go 1.22+)

LIBASHWATH = engine/cmd/libashwath

android-so: ## Build libashwath_engine.so for Android arm64
    $(eval CC = $(ANDROID_NDK_HOME)/toolchains/llvm/prebuilt/$(HOST_TAG)/bin/\
        aarch64-linux-android21-clang)
    cd $(LIBASHWATH) && \
    CGO_ENABLED=1 \
    GOOS=android GOARCH=arm64 \
    CC=$(CC) \
    go build -buildmode=c-shared \
        -o ../../../android/app/src/main/jniLibs/arm64-v8a/libashwath_engine.so
```

The `.so` is placed directly into `android/app/src/main/jniLibs/arm64-v8a/`
so the Android build includes it automatically.

### Android Build

The existing `assembleDebug` picks up the `.so` from `jniLibs/` — no additional
Gradle configuration needed. The `System.loadLibrary("ashwath_engine")` call
in `AshwathBridge` loads it at runtime.

---

## Ownership Boundaries

| Layer | Owns | Responsibility |
|-------|------|---------------|
| Compose UI | `android/app/.../features/**/ui/` | Screens, components, navigation |
| ViewModels | `android/app/.../features/**/viewmodel/` | State management, event handling |
| Engine SDK | `sdk/kotlin/src/.../sdk/` | High-level engine API (Kotlin) |
| JNI Adapter | `sdk/kotlin/src/.../sdk/jni/` | Thin JNI native declarations |
| Go Engine | `engine/internal/runtime/` + `cmd/libashwath/` | All inference logic, backends |
| C API | `engine/cmd/libashwath/bridge.go` | Platform-agnostic C exports |

**Invariant:** Business logic never crosses the JNI boundary into Kotlin.
The Kotlin SDK is purely a consumer — it formats requests and delivers results.

---

## Status

- **Android Reference Implementation**: ✅ Complete. Uses `libashwath_engine.so` with embedded gRPC server.
- **Desktop/Daemon**: ✅ Complete. Uses `ashwathd` standalone binary.
- **iOS/Web**: 🔜 In Roadmap.
