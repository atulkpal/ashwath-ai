package plugins

import "context"

type Plugin interface {
	Name() string
	Version() string
	Init(ctx context.Context) error
}

type ToolPlugin interface {
	Plugin
	Execute(ctx context.Context, args map[string]any) (any, error)
	Schema() ToolSchema
}

type ToolSchema struct {
	Name        string                     `json:"name"`
	Description string                     `json:"description"`
	Parameters  map[string]ParameterSchema `json:"parameters"`
}

type ParameterSchema struct {
	Type        string   `json:"type"`
	Description string   `json:"description"`
	Required    bool     `json:"required"`
	Enum        []string `json:"enum,omitempty"`
}

type Manager interface {
	Load(path string) (Plugin, error)
	List() []Plugin
	Unload(name string) error
	RegisterBuiltin(name string, factory func() Plugin)
}
