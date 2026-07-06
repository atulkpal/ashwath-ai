package plugins

import (
	"context"
	"fmt"
	"sync"
)

type manager struct {
	mu       sync.RWMutex
	plugins  map[string]Plugin
	builtins map[string]func() Plugin
}

func NewManager() Manager {
	return &manager{
		plugins:  make(map[string]Plugin),
		builtins: make(map[string]func() Plugin),
	}
}

func (m *manager) RegisterBuiltin(name string, factory func() Plugin) {
	m.mu.Lock()
	m.builtins[name] = factory
	m.mu.Unlock()
}

func (m *manager) Load(path string) (Plugin, error) {
	m.mu.Lock()
	defer m.mu.Unlock()

	if _, exists := m.plugins[path]; exists {
		return nil, fmt.Errorf("plugin %q already loaded", path)
	}

	factory, ok := m.builtins[path]
	if !ok {
		return nil, fmt.Errorf("plugin %q not found", path)
	}

	p := factory()
	if err := p.Init(context.Background()); err != nil {
		return nil, fmt.Errorf("init plugin %q: %w", path, err)
	}
	m.plugins[p.Name()] = p
	return p, nil
}

func (m *manager) List() []Plugin {
	m.mu.RLock()
	defer m.mu.RUnlock()
	result := make([]Plugin, 0, len(m.plugins))
	for _, p := range m.plugins {
		result = append(result, p)
	}
	return result
}

func (m *manager) Unload(name string) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	if _, exists := m.plugins[name]; !exists {
		return fmt.Errorf("plugin %q not loaded", name)
	}
	delete(m.plugins, name)
	return nil
}
