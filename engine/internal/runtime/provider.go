package runtime

import (
	"context"
	"fmt"
	"sync"
)

type Provider interface {
	Name() string
	Create(ctx context.Context, opts Options) (Engine, error)
}

var (
	registry   = map[string]Provider{}
	registryMu sync.RWMutex
)

func RegisterProvider(p Provider) {
	registryMu.Lock()
	defer registryMu.Unlock()
	registry[p.Name()] = p
}

func CreateEngine(ctx context.Context, name string, opts Options) (Engine, error) {
	registryMu.RLock()
	p, ok := registry[name]
	registryMu.RUnlock()

	if !ok {
		return nil, fmt.Errorf("runtime provider %q not registered", name)
	}

	eng, err := p.Create(ctx, opts)
	if err != nil {
		return nil, fmt.Errorf("create %s engine: %w", name, err)
	}

	return eng, nil
}

func ListProviders() []string {
	registryMu.RLock()
	defer registryMu.RUnlock()
	names := make([]string, 0, len(registry))
	for name := range registry {
		names = append(names, name)
	}
	return names
}
