# Engine API

## Communication Protocol

Frontends communicate with the Go engine via **gRPC** over localhost.

Depending on the platform, the engine runs in one of two modes:
- **Daemon Mode (Desktop)**: The engine runs as a standalone child process. The API port is dynamically assigned and passed to the engine via command-line arguments.
- **Embedded Mode (Android)**: The engine is loaded as a shared library (`.so`) and starts a gRPC server in-process.
- **Runtime Mode (Web)**: The Ashwath AI Runtime manages the engine process; the browser communicates via gRPC-Web.

## Service Definition

See `engine/api/proto/service.proto` for the full protobuf schema.
SDK protos are synced at `sdk/kotlin/src/main/proto/ashwathai/v1/engine.proto`.

### Core RPCs

| RPC | Description | Status |
|---|---|---|
| `Generate` | Stream text generation from a model | ✅ Implemented |
| `ListModels` | List available and installed models | ✅ Implemented |
| `InstallModel` | Download and install a model | ✅ Implemented |
| `RemoveModel` | Remove an installed model | ✅ Implemented |
| `GetDeviceInfo` | Query device hardware capabilities | ✅ Implemented |
| `Shutdown` | Gracefully stop the engine | ✅ Implemented |

### Request/Response Types

```
GenerateRequest  → stream GenerateResponse
Empty            → ModelList
InstallRequest   → InstallResponse
RemoveRequest    → RemoveResponse
Empty            → DeviceInfo
Empty            → Empty
```

## Lifecycle

### Android (Embedded)
1. App loads `libashwath_engine.so` via JNI.
2. App calls `nativeStartServer(port, dataDir)`.
3. Go engine starts a gRPC server in a background goroutine.
4. App connects to `localhost:port`.
5. All RPCs (Generate, ListModels, InstallModel, RemoveModel, etc.) available.

### Desktop (Daemon)
1. Frontend launches `ashwathd --port <port> --data-dir <path>` as a child process.
2. Frontend connects to `localhost:port`.

### Web (Runtime)
1. Browser connects to Ashwath AI Runtime via gRPC-Web.
2. Runtime manages engine lifecycle (start, health check, shutdown).
3. Runtime forwards requests to engine over native gRPC.

## Versioning

The engine API is versioned via the gRPC service package name.
Breaking changes increment the package version (e.g., `ashwath.v2`).
