# Module Boundaries

## Package Dependency Graph (internal only)

```
cmd/ashwathd → server, config
cmd/libashwath → (c-shared; build-tag gated)
mobile → runtime, models, bus, logging
pkg/api → runtime, models, bus, logging

internal/server → api, api/pb, config, device, downloads, logging, models, runtime, runtime/llama
internal/api → api/pb, device, logging, models, runtime
internal/api/pb → (generated protobuf stubs, external only)
internal/models → bus, downloads
internal/runtime → (no internal deps)
internal/runtime/llama → runtime
internal/plugins → (no internal deps)
internal/bus → (no internal deps)
internal/downloads → (no internal deps)
internal/config → (no internal deps)
internal/device → (no internal deps)
internal/logging → (no internal deps)
internal/benchmark → runtime

internal/rag     → (interface scaffold only)
internal/knowledge → (interface scaffold only)
internal/vision  → (interface scaffold only)
internal/voice   → (interface scaffold only)
```

## Layer Diagram

```
┌──────────────────────────────────────┐
│          cmd/ashwathd                │  Entry point
├──────────────────────────────────────┤
│          internal/server             │  Orchestrator
├──────────┬────────────┬──────────────┤
│  api     │  models    │  runtime     │  Core services
│          │  .──bus    │  ├──llama    │
│  device  │  downloads │  └──mock     │
│  logging │  plugins   │              │
│  config  │            │              │
└──────────┴────────────┴──────────────┘
```

## Dependency Rules

1. **Leaf packages** import nothing from the engine (bus, config, device, downloads, logging, plugins, runtime).
2. **Core domain packages** (models, runtime/llama) may import bus and runtime only.
3. **Service layer** (api) imports core domain packages but not server.
4. **Orchestrator** (server) imports everything — it wires the graph.
5. **No circular dependencies** — verified by `go list`.
6. **Plugins** and **bus** are opt-in via functional options; the system works without them.

## Package Responsibilities

| Package | Responsibility | Dependencies (engine) |
|---|---|---|
| `bus` | In-memory pub/sub event bus | none |
| `config` | Configuration loading | none |
| `device` | Hardware capability detection | none |
| `downloads` | HTTP downloads with checksum verification | none |
| `logging` | Structured logging | none |
| `plugins` | Plugin/Manager interface + builtin registry | none |
| `runtime` | `Engine` interface (inference abstraction) | none |
| `runtime/llama` | llama.cpp HTTP backend | runtime |
| `models` | Model registry + Source abstraction | bus, downloads |
| `api` | gRPC service implementation | api/pb, device, logging, models, runtime |
| `server` | Wires everything, starts gRPC server | api, config, device, downloads, logging, models, runtime, runtime/llama |
| `benchmark` | Performance measurement | runtime |
| `rag/knowledge/vision/voice` | Interface scaffolds (future) | none |

## Module Invariants

- engine must never depend on a concrete provider at compile time (enforced by `runtime.Engine` interface).
- Provider selection must be configurable without code changes.
- Event emission must never block the publisher.
- Plugin discovery must support both built-in and external (future).
