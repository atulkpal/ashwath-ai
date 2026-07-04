# Engine API

## Communication Protocol

Frontends communicate with the Go engine via **gRPC** over localhost.

Depending on the platform, the engine runs in one of two modes:
- **Daemon Mode (Desktop)**: The engine runs as a standalone child process. The API port is dynamically assigned and passed to the engine via command-line arguments.
- **Embedded Mode (Android)**: The engine is loaded as a shared library (`.so`) and starts a gRPC server in-process.

## Service Definition

See `engine/api/proto/service.proto` for the full protobuf schema.

### Core RPCs

| RPC | Description |
|---|---|
| `Generate` | Stream text generation from a model |
| `ListModels` | List available and installed models |
| `InstallModel` | Download and install a model |
| `GetDeviceInfo` | Query device hardware capabilities |
| `Shutdown` | Gracefully stop the engine |

## Lifecycle

### Android (Embedded)
1. App loads `libashwath_engine.so` via JNI.
2. App calls `nativeStartServer(port, dataDir)`.
3. Go engine starts a gRPC server in a background goroutine.
4. App connects to `localhost:port`.

### Desktop (Daemon)
1. Frontend launches `ashwathd --port <port> --data-dir <path>` as a child process.
2. Frontend connects to `localhost:port`.

## Versioning

The engine API is versioned via the gRPC service package name.
Breaking changes increment the package version (e.g., `ashwath.v2`).
