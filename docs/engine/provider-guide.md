# Provider Guide — How to Write a New Runtime Provider

## Overview

A _provider_ implements the `runtime.Engine` interface and is registered
with the provider registry so the engine server can instantiate it by name.

## 1. Implement the Engine Interface

```go
import "github.com/ashwathai/ashwath-engine/internal/runtime"

type MyProvider struct {}

func (p *MyProvider) Name() string { return "my-provider" }

func (p *MyProvider) Initialize(ctx context.Context, opts runtime.Options) error {
    // Load model, allocate resources, etc.
    return nil
}

func (p *MyProvider) Generate(ctx context.Context, req runtime.Request) (<-chan runtime.Result, error) {
    ch := make(chan runtime.Result)
    go func() {
        defer close(ch)
        // Stream tokens on ch
        ch <- runtime.Result{Text: "hello "}
        ch <- runtime.Result{Text: "world", Done: true}
    }()
    return ch, nil
}

func (p *MyProvider) Stop(ctx context.Context) error {
    // Free resources
    return nil
}
```

## 2. Create a Provider Adapter

Providers live under `engine/internal/runtime/<name>/`.

```go
package myprovider

import "github.com/ashwathai/ashwath-engine/internal/runtime"

type Factory struct {}

func (f *Factory) Name() string { return "my-provider" }

func (f *Factory) Create(ctx context.Context, opts runtime.Options) (runtime.Engine, error) {
    return &MyProvider{}, nil
}
```

## 3. Register the Provider

In `engine/internal/runtime/<name>/register.go`:

```go
package myprovider

import "github.com/ashwathai/ashwath-engine/internal/runtime"

func init() {
    runtime.RegisterProvider(&Factory{})
}
```

Or from `main.go`:

```go
import "github.com/ashwathai/ashwath-engine/internal/runtime/myprovider"

func main() {
    myprovider.Register()
}
```

## 4. Use via Configuration

```json
{
  "engine_type": "my-provider"
}
```

## Engine Interface Reference

```go
type Engine interface {
    Name() string
    Initialize(ctx context.Context, opts Options) error
    Generate(ctx context.Context, req Request) (<-chan Result, error)
    Stop(ctx context.Context) error
}

type Options struct {
    ModelPath  string
    BinaryPath string
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
