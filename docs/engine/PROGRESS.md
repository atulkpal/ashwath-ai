# Engine Workspace Progress

## Phase B: Architecture Foundation ✓

### B.1 Event System (`internal/bus`)
- In-memory pub/sub event bus with `Publish`, `PublishSync`, `Subscribe` (returns cancel func).
- Predefined topics: `engine.ready`, `engine.stopped`, `model.installed`, `model.removed`, `download.progress`, `engine.error`.
- Thread-safe via `sync.RWMutex`, subscription IDs for reliable unsubscribe.
- Tests: 5/5 passing (publish/subscribe, unsubscribe, multiple subscribers, different topics, concurrent).

### B.2 Plugin Framework (`internal/plugins`)
- Extended `Plugin` interface with `ToolPlugin` (adds `Execute`, `Schema`).
- `ToolSchema` and `ParameterSchema` types for tool definitions.
- `Manager` implementation with `RegisterBuiltin`, `Load`, `List`, `Unload`.
- In-process plugin registry using factory functions (no external plugin loading yet).
- Tests: 7/7 passing.

### B.3 Model Abstraction (`internal/models`)
- `Source` interface for pluggable model catalogs (`List()`, `Get()`).
- `BuiltinSource` wrapping existing hardcoded defaults.
- Event bus integration via `WithBus` functional option (emits `model.installed`/`model.removed`).
- Custom source injection via `WithSource` functional option.
- Fully backward compatible — `NewRegistry(dir, downloader)` unchanged.
- Tests: 14/14 passing (9 existing + 5 new).

### B.4 Module Boundaries (`docs/engine/MODULE_BOUNDARIES.md`)
- Complete dependency graph (internal packages only).
- Layer diagram showing orchestrator → services → leaf structure.
- Dependency rules: no cycles, leaf packages import nothing, opt-in event/plugin system.
- Module invariants documented.

## Phase C: Runtime (not started)

## Phase D: Runtime Providers (not started)

## Phase E: Stabilization (not started)

## Test Summary
```
internal/bus:     5 tests ✓
internal/plugins: 7 tests ✓
internal/models: 14 tests ✓
```
