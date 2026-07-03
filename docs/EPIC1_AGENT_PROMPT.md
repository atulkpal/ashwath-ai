# EPIC 1: Engine MVP — Agent Brief (Self)

## Context

You are building the Go AI Engine for Ashwath.AI.
The Android client (EPIC 2, in parallel) will consume this engine's gRPC API.

**Communication contract**: gRPC, defined in `engine/api/proto/service.proto`
**Distribution**: Standalone binary, cross-compiled and published via GitHub Releases
**Architecture**: Frontends download the engine binary on first launch → start as child process → communicate over localhost gRPC

The engine does NOT run real AI models yet. This MVP delivers the shell: gRPC server, mock responses, device detection, config, logging.

---

## Existing Engine Structure

```
engine/
├── go.mod
├── cmd/ashwathd/main.go        # Placeholder — prints "starting"
├── internal/
│   ├── runtime/runtime.go      # Engine interface (interface only)
│   ├── config/config.go        # Config struct + Loader interface
│   ├── device/device.go        # Capabilities struct + Detector interface
│   ├── downloads/downloads.go  # Manager interface (not needed for MVP)
│   ├── models/models.go        # Model struct + Registry interface
│   ├── knowledge/knowledge.go  # Interface only (not needed for MVP)
│   ├── rag/rag.go              # Interface only (not needed for MVP)
│   ├── voice/voice.go          # Interface only (not needed for MVP)
│   ├── vision/vision.go        # Interface only (not needed for MVP)
│   ├── benchmark/benchmark.go  # Interface only (not needed for MVP)
│   ├── plugins/plugins.go      # Interface only (not needed for MVP)
│   ├── config/config.go        # Interface only
│   └── logging/logging.go      # Logger interface
├── pkg/api/api.go              # Public API types
├── api/proto/service.proto     # gRPC service definition
├── tests/
└── docs/
```

---

## Tasks (in implementation order)

### TASK 1: Structured logger
**Files**: `engine/internal/logging/`

Implement `logging.Logger` using Go's `log/slog` (stdlib, no external dep).
- `New(level, output)` constructor
- Levels: Debug, Info, Warn, Error
- `With(key, value)` for structured context
- Default to stdout

### TASK 2: Config loader
**Files**: `engine/internal/config/`

Implement `config.Loader`:
- `Load()` → merge env vars + JSON file
- Env: `ASHWATH_DATA_DIR`, `ASHWATH_LOG_LEVEL`, `ASHWATH_PORT`
- JSON: `config.json` in data dir
- Return populated `Config` struct with defaults
- `Save(cfg)` → write JSON

### TASK 3: Device detection
**Files**: `engine/internal/device/`

Implement `device.Detector`:
- `Detect()` → collect OS, arch, CPU cores, RAM
- Cross-platform (Go stdlib: `runtime.GOOS`, `runtime.GOARCH`, etc.)
- RAM detection using platform-specific code (build tags: `linux`, `darwin`, `windows`, `android`)

### TASK 4: Mock inference engine
**Files**: `engine/internal/runtime/mock.go`

Implement `runtime.Engine` that:
- `Generate()` → streams tokens of a canned response word-by-word with 50ms delay
- Returns `Result{Text: word, Done: false}` per word, then `Done: true`
- Configurable response text and delay

### TASK 5: gRPC server
**Files**: `engine/internal/api/grpc.go`, `engine/internal/api/service.go`

Implement the gRPC service defined in `api/proto/service.proto`:
- `Generate` → streaming, backed by mock engine
- `ListModels` → return hardcoded model list matching `pkg/api.ModelInfo`
- `InstallModel` → return "mock installed"
- `GetDeviceInfo` → return from device detector
- `Shutdown` → graceful stop

Dependencies: `google.golang.org/grpc`, `google.golang.org/protobuf`

### TASK 6: Wire main entry point
**Files**: `engine/cmd/ashwathd/main.go`

- Parse flags: `--port`, `--data-dir`, `--log-level`
- Initialize logger, config, device detector, mock engine
- Start gRPC server
- Handle SIGTERM/SIGINT for graceful shutdown
- Print startup banner

### TASK 7: CI release workflow
**Files**: `.github/workflows/release-engine.yml`

- Trigger: tag `engine/v*`
- Cross-compile for: `android/arm64`, `android/amd64`, `linux/arm64`, `linux/amd64`, `darwin/arm64`, `darwin/amd64`, `windows/amd64`
- Generate SHA256 checksums
- Create GitHub Release with all binaries + checksums

---

## Dependencies (to add to go.mod)
- `google.golang.org/grpc`
- `google.golang.org/protobuf`

## Testing
- `go test ./...` must pass after every task
- Manual smoke: `go run ./cmd/ashwathd` + `grpcurl` or a simple Go test client

## DO NOT touch
- `android/` directory
- `sdk/` directory (will be built by EPIC 2 agent)
- Files outside `engine/`, `.github/`, and root build files
