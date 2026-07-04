# Ashwath.AI Architecture

## Platform Overview

```
┌──────────────────────────────────────────────────────────────┐
│                        Frontends                             │
│  ┌──────────┐ ┌──────┐ ┌──────────┐ ┌───────────────────┐   │
│  │ Android  │ │ iOS  │ │ Desktop  │ │       Web         │   │
│  │ (Kotlin) │ │(Swift)│ │ (Tauri)  │ │ (TS/React+gRPC-Web)│  │
│  └────┬─────┘ └──┬───┘ └────┬─────┘ └────────┬──────────┘   │
│       │          │          │                 │              │
│  ┌────▼──────────▼──────────▼─────────────────▼──────────┐   │
│  │                    SDK Layer                          │   │
│  │  ┌─────────┐ ┌────────┐ ┌────────┐ ┌──────────────┐  │   │
│  │  │ Kotlin  │ │ Swift  │ │   Go   │ │ TypeScript   │  │   │
│  │  │  SDK    │ │  SDK   │ │  SDK   │ │    SDK       │  │   │
│  │  └─────────┘ └────────┘ └────────┘ └──────────────┘  │   │
│  └────────────────────────┬──────────────────────────────┘   │
│                           │ gRPC                             │
├───────────────────────────┼──────────────────────────────────┤
│  ┌───────────────────────▼──────────────────────────────┐    │
│  │              Go Engine (libashwath)                   │    │
│  │  Runtime │ Models │ Config │ Device │ Logging        │    │
│  │  RAG │ Voice │ Vision │ Knowledge │ Plugins          │    │
│  └───────────────────────┬──────────────────────────────┘    │
│                           │ Embedded (Android) / Download     │
│                    Linked at build time (Android)             │
└──────────────────────────────────────────────────────────────┘
```

## Engine Architecture

The Go engine (`engine/`) is the core AI logic of the platform. On most platforms (Desktop), it runs as a standalone binary (`ashwathd`) distributed via GitHub Releases. On Android, to comply with security policies (W^X), it is compiled as a shared library (`libashwath_engine.so`) and embedded directly into the app process.

The engine exposes a gRPC API on localhost (127.0.0.1) using a dynamically assigned port or a fixed port (50051) in embedded mode.

### Engine Packages

| Package | Purpose | Status |
|---------|---------|--------|
| `cmd/ashwathd` | Main entry point, wires all services | ✅ Implemented |
| `internal/api` | gRPC service definitions, JSON codec, request/response types | ✅ Implemented |
| `internal/config` | JSON file + env var config loader | ✅ Implemented |
| `internal/device` | Hardware detection (OS, arch, CPU, RAM) | ✅ Implemented |
| `internal/logging` | Structured slog-based logger | ✅ Implemented |
| `internal/models` | Persistent model registry (`registry.json`) | ✅ Implemented (Phase A) |
| `internal/runtime` | Engine abstraction (llama.cpp and mock backends) | ✅ Implemented (Phase A) |
| `internal/downloads` | Multi-threaded download manager with verification | ✅ Implemented (Phase A) |
| `internal/benchmark` | On-device performance evaluation (tokens/sec, memory) | ✅ Implemented (Phase A) |
| `internal/rag` | Retrieval-augmented generation | 🗂️ Planned |
| `internal/voice` | STT/TTS | 🗂️ Planned |
| `internal/vision` | Image understanding | 🗂️ Planned |
| `internal/knowledge` | Knowledge base management | 🗂️ Planned |
| `internal/plugins` | Plugin system | 🗂️ Planned |
| `pkg/api` | Public API types for external consumers | ✅ Implemented |

### Testing
- Unit tests: 45 tests across 8 packages (including downloads and runtime).
- In-memory gRPC integration tests using `bufconn` (no external server needed).
- Smoke test in `engine/tests/smoke.go` for manual verification.
- `go vet` and `golangci-lint` integrated into CI.

## Android Architecture

The Android app (`android/`) follows Clean Architecture with MVVM:

- **`app/`**: Orchestration, navigation, DI root (`ServiceLocator`)
- **`features/`**: Self-contained features (Chat, Library, Explore, Settings, Knowledge, Onboarding)
- **`domain/`**: Pure Kotlin business logic and repository interfaces
- **`data/`**: Repository implementations
- **`core/`**: Model download, checksum verification
- **`platform/`**: Native bridge management, process control (for non-embedded modes)
- **`sdk/`** (via Gradle subproject `:sdk`): Shared inference client (`ClientInferenceEngine`, `EngineGrpcClient`, `EmbeddedInferenceEngine`)

The Android app builds the Go engine automatically via Gradle custom tasks. It contains zero AI business logic — all inference is delegated to the Go engine over gRPC (loopback).

## SDK Layer

The SDK (`sdk/`) provides language-specific gRPC clients for the engine API:

| SDK | Status | Location |
|-----|--------|----------|
| Kotlin | ✅ Active (MVP) | `sdk/kotlin/` |
| Swift | 🗂️ Scaffold only | `sdk/swift/` |
| Go | 🗂️ Scaffold only | `sdk/go/` |
| TypeScript | 🗂️ Scaffold only | `sdk/typescript/` |

The Kotlin SDK is included in the Android build as a Gradle subproject. It will be published as a standalone artifact for other consumers.

## Communication Pattern

1. **Frontend launches**:
   - **Android (Embedded)**: The app loads `libashwath_engine.so` via JNI. `EmbeddedInferenceEngine` calls `nativeStartServer` to launch the gRPC server within the app process.
   - **Desktop/Others (Daemon)**: The app checks if the `ashwathd` binary is installed. If not, it downloads it from GitHub Releases, verifies the checksum, and launches it as a child process.
2. **Engine Initialization**:
   - The engine loads its configuration and initializes the **Persistent Model Registry**.
   - If a backend (e.g., `llama.cpp`) is requested, the engine wires the specific runtime backend.
3. **Model Management**:
   - The engine exposes `ListModels`, `InstallModel`, and `RemoveModel` RPCs.
   - `InstallModel` triggers the **Download Manager**, which handles multi-threaded GGUF downloads with background verification.
   - The registry state is persisted to `registry.json` in the `data-dir`.
4. **Connect via gRPC**: All frontends connect to the local gRPC server (usually on `127.0.0.1:50051`).
5. **AI Operations**: All requests (Generate, ListModels, etc.) are sent as gRPC calls.
6. **Performance Monitoring**: The engine periodically runs benchmarks (if enabled) to report hardware utilization and inference speed to the frontend via `GetDeviceInfo` or custom telemetry.
7. **Shutdown**: Frontend calls `Shutdown` RPC or `nativeShutdown` (JNI) to gracefully terminate the engine and all child processes (like `llama-server`).

## Future Frontends

### Web (Planned)
- TypeScript/React with gRPC-Web
- Deferred until engine API stabilizes with real inference (EPIC-3)
- Requires gRPC-Web support in the engine (Envoy proxy or native Go gRPC-Web)
- Progressive Web App for mobile and desktop

### iOS (Planned)
- Swift with gRPC Swift library
- Pattern follows Kotlin SDK
- Requires engine binary for arm64 macOS (already in release pipeline)

### Desktop (Planned)
- Tauri (Rust) or Electron
- Bundles engine binary directly (no download needed)
- Full offline experience
