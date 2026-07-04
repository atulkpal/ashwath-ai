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

| Package | Purpose |
|---------|---------|
| `cmd/ashwathd` | Main entry point, wires all services |
| `internal/api` | gRPC service definitions, JSON codec, request/response types |
| `internal/config` | JSON file + env var config loader |
| `internal/device` | Hardware detection (OS, arch, CPU, RAM) |
| `internal/logging` | Structured slog-based logger |
| `internal/models` | Model registry (installed + available) |
| `internal/runtime` | Engine abstraction (mock, future: llama.cpp) |
| `internal/downloads` | Model download manager |
| `internal/rag` | Retrieval-augmented generation (planned) |
| `internal/voice` | STT/TTS (planned) |
| `internal/vision` | Image understanding (planned) |
| `internal/knowledge` | Knowledge base management (planned) |
| `internal/plugins` | Plugin system (planned) |
| `pkg/api` | Public API types for external consumers |

### Testing
- Unit tests: 21 tests across 6 packages.
- In-memory gRPC integration tests using `bufconn` (no external server needed).
- Smoke test in `engine/tests/smoke.go` for manual verification.

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
2. **Model Loading**: Both modes check for installed models in the `data-dir`. If a model is missing, it is downloaded from GitHub Releases / HuggingFace.
3. **Connect via gRPC**: All frontends connect to the local gRPC server (usually on `127.0.0.1:50051`).
4. **AI Operations**: All requests (Generate, ListModels, etc.) are sent as gRPC calls.
5. **Shutdown**: Frontend calls `Shutdown` RPC or `nativeShutdown` (JNI) to gracefully terminate the engine.

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
