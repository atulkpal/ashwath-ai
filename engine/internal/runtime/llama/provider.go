package llama

import (
	"context"
	"fmt"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

const providerName = "llama"

type llamaProvider struct{}

func (p *llamaProvider) Name() string { return providerName }

func (p *llamaProvider) Create(ctx context.Context, opts runtime.Options) (runtime.Engine, error) {
	if opts.ModelPath == "" {
		return nil, fmt.Errorf("llama: model path is required")
	}
	eng := New(opts.LlamaBin)
	if err := eng.Initialize(ctx, opts); err != nil {
		return nil, fmt.Errorf("llama: initialize: %w", err)
	}
	return eng, nil
}

func Register() {
	runtime.RegisterProvider(&llamaProvider{})
}
