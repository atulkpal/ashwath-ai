# Engine API

## Communication Protocol

Frontends communicate with the Go engine via **gRPC** over localhost.

The engine runs as a child process managed by the frontend. The API port is dynamically assigned and passed to the engine via command-line arguments.

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

1. Frontend allocates a port
2. Frontend launches `ashwathd --port <port> --data-dir <path>`
3. Frontend establishes gRPC connection
4. Engine serves requests until `Shutdown` or process kill
5. Frontend terminates engine process on exit

## Versioning

The engine API is versioned via the gRPC service package name.
Breaking changes increment the package version (e.g., `ashwath.v2`).
