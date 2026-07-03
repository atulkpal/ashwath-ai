// Package runtime defines the inference engine abstraction.
// All AI model backends (llama.cpp, etc.) implement this interface.
package runtime

import "context"

type Engine interface {
	Name() string
	Initialize(ctx context.Context, opts Options) error
	Generate(ctx context.Context, req Request) (<-chan Result, error)
	Stop(ctx context.Context) error
}

type Options struct {
	ModelPath string
	Device    string
}

type Request struct {
	Prompt     string
	MaxTokens  int
	Temperature float32
	TopK       int
	TopP       float32
}

type Result struct {
	Text  string
	Done  bool
	Error error
}
