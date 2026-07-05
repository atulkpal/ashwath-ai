package agent

import (
	"context"
	"fmt"
	"sync"

	"github.com/ashwathai/ashwath-engine/internal/plugins"
)

type ToolExecutor struct {
	mu    sync.RWMutex
	tools map[string]plugins.ToolPlugin
}

func NewToolExecutor() *ToolExecutor {
	return &ToolExecutor{
		tools: make(map[string]plugins.ToolPlugin),
	}
}

func (e *ToolExecutor) Register(name string, tool plugins.ToolPlugin) error {
	e.mu.Lock()
	defer e.mu.Unlock()
	if _, exists := e.tools[name]; exists {
		return fmt.Errorf("tool %q already registered", name)
	}
	e.tools[name] = tool
	return nil
}

func (e *ToolExecutor) Execute(ctx context.Context, name string, args map[string]any) (any, error) {
	e.mu.RLock()
	tool, ok := e.tools[name]
	e.mu.RUnlock()

	if !ok {
		return nil, fmt.Errorf("tool %q not found", name)
	}

	return tool.Execute(ctx, args)
}

func (e *ToolExecutor) Schemas() []plugins.ToolSchema {
	e.mu.RLock()
	defer e.mu.RUnlock()
	schemas := make([]plugins.ToolSchema, 0, len(e.tools))
	for _, tool := range e.tools {
		schemas = append(schemas, tool.Schema())
	}
	return schemas
}

func (e *ToolExecutor) List() []string {
	e.mu.RLock()
	defer e.mu.RUnlock()
	names := make([]string, 0, len(e.tools))
	for name := range e.tools {
		names = append(names, name)
	}
	return names
}
