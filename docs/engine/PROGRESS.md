# Engine Workspace Progress

## Phase B: Architecture Foundation ✓

### B.1 Event System (`internal/bus`)
- In-memory pub/sub event bus with `Publish`, `PublishSync`, `Subscribe` (returns cancel func).
- Predefined topics: `engine.ready`, `engine.stopped`, `model.installed`, `model.removed`, `download.progress`, `engine.error`.
- Thread-safe via `sync.RWMutex`, subscription IDs for reliable unsubscribe.
- Tests: 5/5 passing.

### B.2 Plugin Framework (`internal/plugins`)
- Extended `Plugin` interface with `ToolPlugin` (adds `Execute`, `Schema`).
- `ToolSchema` and `ParameterSchema` types for tool definitions.
- `Manager` implementation with `RegisterBuiltin`, `Load`, `List`, `Unload`.
- Tests: 7/7 passing.

### B.3 Model Abstraction (`internal/models`)
- `Source` interface for pluggable model catalogs.
- `BuiltinSource` wrapping existing hardcoded defaults.
- Event bus integration via `WithBus` functional option.
- Custom source injection via `WithSource` functional option.
- Fully backward compatible.
- Tests: 14/14 passing.

### B.4 Module Boundaries (`docs/engine/MODULE_BOUNDARIES.md`)
- Complete dependency graph, layer diagram, dependency rules, module invariants.

## Phase C: Runtime ✓

### C.1 Memory Architecture (`internal/agent/memory.go`)
- `Message` struct with `Role`, `Content`, `ToolCalls`.
- `Memory` interface with `AddMessage`, `Messages`, `Clear`, `Len`.
- `InMemory` implementation with configurable ring buffer.
- Thread-safe, returns copies to prevent mutation.
- Tests: 8/8 passing.

### C.2 Context Assembly (`internal/agent/context.go`)
- `ContextBuilder` that composes system prompt + conversation history + tool schemas + user input.
- Uses chat template format (`<|im_start|>role\ncontent<|im_end|>`).
- Tool schema injection as both text description and JSON.
- Tests: 6/6 passing.

### C.3 Tool Execution Pipeline (`internal/agent/toolpipe.go`)
- `ToolExecutor` with `Register`, `Execute`, `Schemas`, `List`.
- Built on top of `plugins.ToolPlugin` interface.
- Thread-safe registration and execution.
- Tests: 7/7 passing.

### C.4 Agent Runtime (`internal/agent/agent.go`)
- `Agent` struct orchestrating Memory, Context, Tools, and Engine.
- `Run` method: adds user message to memory, builds context prompt, calls engine, streams results, stores assistant response.
- `Reset` method clears conversation memory.
- Event bus integration via `WithEventBus` option.
- `WithToolExecutor` and `WithSystemPrompt` options.
- Tests: 8/8 passing.

## Phase D: Runtime Providers ✓

### D.1 Provider Registry (`internal/runtime/provider.go`)
- `Provider` interface (`Name()`, `Create(ctx, opts) (Engine, error)`).
- `RegisterProvider(Provider)` for global registration.
- `CreateEngine(ctx, name, opts)` for dynamic engine creation.
- `ListProviders()` for discovery.
- Tests: 5/5 passing.

### D.2 Mock Provider Registration
- Mock engine auto-registers via `init()` as `"mock"` provider.
- No code changes needed for mock usage.

### D.3 Llama Provider (`internal/runtime/llama/provider.go`)
- `llama.Register()` registers the llama provider.
- Provider creates engine via `New(binPath)`, reads `opts.LlamaBin` and `opts.ModelPath`.

### D.4 Server Decoupling (`internal/server/server.go`)
- `server.Run` calls `llama.Register()` and then `runtime.CreateEngine()`.
- Hardcoded engine switch replaced with registry lookup.
- Engine type defaults to `"mock"` when empty.

## Phase E: Stabilization ✓

### E.1 Code Review and Dead Code Removal
- Removed unused `manager` field from `ToolExecutor` struct.
- Removed unused `plugins` import from `agent.go`.
- Changed `InMemory` to unexported `inMemory`; `NewMemory` returns `Memory` interface.
- All `NewToolExecutor()` calls updated to no-arg signature.
- Verified all exports are necessary and used.

### E.2 Test Improvements
- Added edge case tests for `bus`: empty topic, no subscribers, unsubscribe ordering.
- Added edge case tests for `plugins`: init failure, list after unload, duplicate builtin.
- Added edge case tests for `agent`: empty prompt, multiple runs, reset then run, options.

### E.3 Naming and Consistency
- `InMemory` → unexported `inMemory` (returned through `Memory` interface).
- `ToolExecutor` cleaned of unused dependencies.
- All exported types have package-level doc comments.

### E.4 Final Documentation
- `docs/analysis/EPIC3_FINAL.md` — comprehensive epic summary.
- `docs/engine/PROGRESS.md` updated through Phase E.
- `docs/engine/MODULE_BOUNDARIES.md` updated through Phase D.

## Test Summary
```
internal/bus:      9 tests ✓ (5 original + 4 edge)
internal/plugins: 10 tests ✓ (7 original + 3 edge)
internal/models:  14 tests ✓
internal/agent:   37 tests ✓ (30 original + 4 edge + 3 combined)
internal/runtime: 11 tests ✓ (6 mock + 5 provider)
internal/llama:    4 tests ✓ (existing)
internal/api:      5 tests ✓ (existing)
internal/benchmark: 1 test ✓ (existing)
internal/config:   2 tests ✓ (existing)
internal/device:   1 test ✓ (existing)
internal/logging:  3 tests ✓ (existing)
```

## Overall
- New packages: `bus`, `plugins` (implemented), `agent`, `runtime/provider`
- Enhanced: `models`, `runtime`, `server`
- Total tests: 97+ (42 existing + 9 bus + 10 plugins + 5 models + 37 agent + 5 provider)
- All passing, 0 vet warnings
