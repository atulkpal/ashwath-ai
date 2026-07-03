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
│  │              Go Engine (ashwathd)                     │    │
│  │  Runtime │ Models │ Config │ Device │ Logging        │    │
│  │  RAG │ Voice │ Vision │ Knowledge │ Plugins          │    │
│  └───────────────────────┬──────────────────────────────┘    │
│                           │ GitHub Releases                   │
│                    Downloaded on first launch                 │
└──────────────────────────────────────────────────────────────┘
```

## Engine Architecture

The Go engine (`engine/`) is a standalone binary distributed via GitHub Releases. It exposes a gRPC API on localhost with a dynamically assigned port. The engine uses a JSON codec for gRPC during development (MVP), with a switch to generated protobuf stubs planned before production.

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
- **`core/`**: Engine download, checksum verification
- **`platform/`**: Engine installer, process manager
- **`sdk/`** (via Gradle subproject `:sdk`): Shared inference client (`ClientInferenceEngine`, `EngineGrpcClient`)

The Android app builds independently. It contains zero AI business logic — all inference is delegated to the Go engine over gRPC.

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

1. Frontend launches → checks if Go engine binary is installed
2. If not installed → download from GitHub Releases → verify SHA256 checksum → extract
3. Launch engine as child process (`ashwathd --port <port> --data-dir <path>`)
4. Connect via gRPC on localhost
5. All AI operations → gRPC calls to engine (ListModels, Generate, InstallModel, etc.)
6. Shutdown → frontend calls `Shutdown` RPC → terminates engine process

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
