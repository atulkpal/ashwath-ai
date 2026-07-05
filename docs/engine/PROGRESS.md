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

## Phase D: Runtime Providers (not started)

## Phase E: Stabilization (not started)

## Test Summary
```
internal/bus:     5 tests ✓
internal/plugins: 7 tests ✓
internal/models: 14 tests ✓
internal/agent:  30 tests ✓ (memory 8 + context 6 + toolpipe 7 + agent 8 + combined 1)
```
