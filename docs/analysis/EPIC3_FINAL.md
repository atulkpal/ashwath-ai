# EPIC 3: Engine Foundation — Final Report

**Completed:** July 2026
**Goals:** Architecture Foundation, Runtime, Provider Abstraction, Stabilization

---

## Overview

EPIC 3 transformed the Ashwath Engine from a minimal gRPC server into a layered,
extensible inference platform with clear module boundaries, decoupled provider
registration, an event system, plugin framework, agent runtime, and tool execution
pipeline.

---

## Architecture Changes

### Before EPIC 3
```
cmd/ashwathd → server
server → api, config, device, downloads, logging, models, runtime, runtime/llama (hardcoded switch)
api → models, runtime
runtime → Engine interface + Mock
models → hardcoded defaults, no event emission
plugins → interfaces only (4 lines)
```

### After EPIC 3
```
cmd/ashwathd → server
server → api, config, device, downloads, logging, models, runtime, runtime/llama (registry lookup)
api → models, runtime
runtime → Engine interface + Provider registry (auto-registers Mock)
runtime/llama → exposes Register() for provider registration
models → Source abstraction + event bus integration
plugins → Manager implementation + ToolPlugin extension
agent → Memory, Context Assembly, Tool Pipeline, Agent Runtime
bus → In-memory pub/sub event bus
```

---

## Files Created/Modified

### New Packages (7 packages, 13 files)

| Package | File | Purpose |
|---|---|---|
| `internal/bus` | `bus.go` | In-memory pub/sub event bus |
| `internal/bus` | `topics.go` | Predefined event topic constants |
| `internal/bus` | `bus_test.go` | 5 basic tests + 4 edge tests |
| `internal/plugins` | `manager.go` | Manager implementation with builtin registry |
| `internal/plugins` | `manager_test.go` | 7 tests |
| `internal/plugins` | `manager_edge_test.go` | 3 edge tests |
| `internal/models` | `source.go` | Source interface + BuiltinSource |
| `internal/models` | `source_test.go` | 5 tests |
| `internal/agent` | `memory.go` | Memory interface + inMemory implementation |
| `internal/agent` | `context.go` | ContextBuilder for prompt assembly |
| `internal/agent` | `toolpipe.go` | ToolExecutor for tool plugin execution |
| `internal/agent` | `agent.go` | Agent orchestrating Engine + Memory + Tools |
| `internal/runtime` | `provider.go` | Provider registration + CreateEngine |
| `internal/runtime/llama` | `provider.go` | llama.cpp provider registration |

### Modified Files (4 files)

| File | Changes |
|---|---|
| `internal/runtime/runtime.go` | Added `LlamaBin` field to Options |
| `internal/runtime/mock.go` | Added mockProvider + init() auto-registration |
| `internal/plugins/plugins.go` | Added ToolPlugin, ToolSchema, ParameterSchema; extended Manager |
| `internal/models/models.go` | Added RegistryOption, WithBus, WithSource |
| `internal/models/registry.go` | Refactored to use Source; added event emission |
| `internal/server/server.go` | Replaced switch with llama.Register() + runtime.CreateEngine() |

### Documentation

| File | Purpose |
|---|---|
| `docs/engine/MODULE_BOUNDARIES.md` | Dependency graph, layer diagram, invariants |
| `docs/engine/PROGRESS.md` | Workspace progress tracking |
| `docs/analysis/EPIC3_FINAL.md` | This report |

---

## Architecture Decisions

### 1. Event System: In-memory pub/sub
- **Rationale:** Decouples components without channel coordination complexity.
- **Key design:** Subscription returns cancel func, not channel; thread-safe via RWMutex.

### 2. Plugin Framework: In-process registry
- **Rationale:** Go's `plugin` package is Linux-only; mobile targets need static linking.
- **Key design:** Builtin plugins registered via factory functions; ToolPlugin extends base Plugin.

### 3. Source Abstraction: Pluggable model catalogs
- **Rationale:** Allows future HuggingFace Hub, local JSON, or remote API sources.
- **Key design:** Backward compatible via `NewRegistry(dir, dl)` unchanged; opt-in WithSource/WithBus.

### 4. Agent Runtime: Explicit lifecycle
- **Rationale:** Separates conversation management from raw inference.
- **Key design:** Agent owns Memory, uses Engine for generation, supports tools via ToolExecutor.

### 5. Provider Registry: Global + runtime registration
- **Rationale:** Server imports providers explicitly but selects by string name.
- **Key design:** Providers registered via `RegisterProvider()`, engines created via `CreateEngine(name)`.

---

## Test Coverage

| Package | Tests | What's Tested |
|---|---|---|
| `bus` | 9 | Publish, subscribe, unsubscribe, concurrent, edge cases |
| `plugins` | 10 | Manager lifecycle, ToolPlugin, init failure, duplicates |
| `models` | 14 | Registry, Source, bus events, installed state |
| `agent` | 33 | Memory, context, tools, agent run/reset, edge cases |
| `runtime` | 11 | Mock engine, provider registration, mock+provider tests |
| `runtime/llama` | 4 | Backend initialization, generate, stop, cancellation |
| `api` | 5 | gRPC service, model list, generate, shutdown |
| Other existing | 13 | Benchmark, config, device, logging |
| **Total** | **97+** | All passing, 0 vet warnings |

---

## Key Metrics

- **New Go files:** 19 source + 9 test = 28 files
- **Lines of code added:** ~2,000+
- **New interfaces:** `Source`, `Memory`, `Bus`, `ToolPlugin`, `Provider`
- **New concrete types:** `InMemoryBus`, `Manager`, `BuiltinSource`, `ContextBuilder`
  `ToolExecutor`, `Agent`, `InMemory`
- **Zero new external dependencies** (only stdlib + existing gRPC/protobuf)
- **Backward compatible:** All existing APIs unchanged; only server internal wiring changed

---

## Future Work (EPIC 4+)

- Dynamic model source (HuggingFace Hub API, local JSON catalog)
- External plugin loading (Go plugin, WASM, or subprocess)
- Context window management (token-aware truncation, summarization)
- Tool calling loop (model → tool request → execute → continue)
- Streaming model download progress via event bus
- Android/iOS mobile agent integration
- ONNX Runtime provider
- Service-level tests for full gRPC orchestration
