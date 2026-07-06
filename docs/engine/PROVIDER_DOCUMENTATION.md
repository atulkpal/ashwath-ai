# Provider System — How to Write a New Runtime Provider

The Go engine uses a **provider registry** to decouple inference backends from the server code. Adding a new backend requires implementing two interfaces and registering the provider.

---

## Architecture

```
server.Run()
  ├── llama.Register()           // explicit provider registration
  └── runtime.CreateEngine()     // factory lookup
        └── provider.Create()    // returns Engine interface
              └── engine.Initialize()
```

---

## Two Interfaces to Implement

### 1. `runtime.Provider` (factory)

```go
type Provider interface {
    Name() string
    Create(ctx context.Context, opts Options) (Engine, error)
}
```

- `Name()` returns a unique identifier (e.g. `"llama"`, `"mock"`).
- `Create()` receives `Options` and returns a ready-to-use `Engine`.

### 2. `runtime.Engine` (backend)

```go
type Engine interface {
    Name() string
    Initialize(ctx context.Context, opts Options) error
    Generate(ctx context.Context, req Request) (<-chan Result, error)
    Stop(ctx context.Context) error
}
```

- `Initialize()` — set up the backend (load model, start subprocess, etc.).
- `Generate()` — accept a prompt, return a streaming channel of tokens.
- `Stop()` — tear down the backend.

---

## Key Types

```go
type Options struct {
    ModelPath  string   // path to .gguf model file
    Device     string   // "cpu", "cuda", etc.
    BinaryPath string   // path to backend binary (e.g. llama-server)
}

type Request struct {
    Prompt      string
    MaxTokens   int
    Temperature float32
    TopK        int
    TopP        float32
}

type Result struct {
    Text  string
    Done  bool
    Error error
}
```

---

## Step-by-Step: Adding a New Provider

### 1. Create the package

```sh
mkdir engine/internal/runtime/openvino
```

### 2. Implement the provider

```go
// engine/internal/runtime/openvino/provider.go
package openvino

import (
    "context"
    "fmt"
    "github.com/ashwathai/ashwath-engine/internal/runtime"
)

const providerName = "openvino"

type openvinoProvider struct{}

func (p *openvinoProvider) Name() string { return providerName }

func (p *openvinoProvider) Create(ctx context.Context, opts runtime.Options) (runtime.Engine, error) {
    if opts.ModelPath == "" {
        return nil, fmt.Errorf("openvino: model path is required")
    }
    eng := &engine{binPath: opts.BinaryPath}
    if err := eng.Initialize(ctx, opts); err != nil {
        return nil, fmt.Errorf("openvino: initialize: %w", err)
    }
    return eng, nil
}

func init() {
    runtime.RegisterProvider(&openvinoProvider{})
}
```

### 3. Implement the engine backend

```go
// engine/internal/runtime/openvino/engine.go
package openvino

import (
    "context"
    "github.com/ashwathai/ashwath-engine/internal/runtime"
)

type engine struct {
    binPath string
}

func (e *engine) Name() string { return "openvino" }

func (e *engine) Initialize(ctx context.Context, opts runtime.Options) error {
    // Load model, start subprocess, etc.
    return nil
}

func (e *engine) Generate(ctx context.Context, req runtime.Request) (<-chan runtime.Result, error) {
    ch := make(chan runtime.Result)
    go func() {
        defer close(ch)
        ch <- runtime.Result{Text: "token", Done: true}
    }()
    return ch, nil
}

func (e *engine) Stop(ctx context.Context) error {
    return nil
}
```

### 4. Wire it into the server

In `engine/internal/server/server.go`, add:

```go
import (
    _ "github.com/ashwathai/ashwath-engine/internal/runtime/openvino"
)
```

The `init()` auto-registration runs at import time. If you prefer explicit registration (like llama), export a `Register()` function instead:

```go
func Register() {
    runtime.RegisterProvider(&openvinoProvider{})
}
```

Then call `openvino.Register()` inside `server.Run()`.

---

## Registration Methods

| Method | When it runs | Use when |
|--------|-------------|----------|
| `init()` auto-registration | Package import | Simpler, less boilerplate |
| Explicit `Register()` call | Server startup | Server controls registration order |

Both work. The llama provider uses both: `init()` ensures the provider is available if imported, and `Register()` gives the server explicit control.

---

## Existing Providers

| Name | Package | File | Registration |
|------|---------|------|-------------|
| `"mock"` | `runtime` (same package) | `engine/internal/runtime/mock.go` | `init()` auto |
| `"llama"` | `runtime/llama` | `engine/internal/runtime/llama/provider.go` | `init()` + explicit `Register()` |

---

## Testing

Mock provider tests: `engine/internal/runtime/mock_test.go`
Provider registry tests: `engine/internal/runtime/provider_test.go`

Write a test that registers your provider, calls `CreateEngine()`, verifies `Generate()` returns tokens, then calls `Stop()`.
