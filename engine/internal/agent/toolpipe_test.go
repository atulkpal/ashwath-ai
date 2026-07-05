package agent

import (
	"context"
	"errors"
	"testing"

	"github.com/ashwathai/ashwath-engine/internal/plugins"
)

type mockToolPlugin struct {
	name    string
	version string
	execute func(ctx context.Context, args map[string]any) (any, error)
	schema  func() plugins.ToolSchema
}

func (m *mockToolPlugin) Name() string          { return m.name }
func (m *mockToolPlugin) Version() string        { return m.version }
func (m *mockToolPlugin) Init(ctx context.Context) error { return nil }
func (m *mockToolPlugin) Execute(ctx context.Context, args map[string]any) (any, error) {
	if m.execute != nil {
		return m.execute(ctx, args)
	}
	return nil, nil
}
func (m *mockToolPlugin) Schema() plugins.ToolSchema {
	if m.schema != nil {
		return m.schema()
	}
	return plugins.ToolSchema{Name: m.name}
}

func TestNewToolExecutor(t *testing.T) {
	e := NewToolExecutor()
	if e == nil {
		t.Fatal("NewToolExecutor returned nil")
	}
}

func TestRegisterAndExecute(t *testing.T) {
	e := NewToolExecutor()

	tool := &mockToolPlugin{
		name:    "test-tool",
		version: "1.0",
		execute: func(ctx context.Context, args map[string]any) (any, error) {
			return "result", nil
		},
	}

	if err := e.Register("test-tool", tool); err != nil {
		t.Fatalf("Register failed: %v", err)
	}

	result, err := e.Execute(context.Background(), "test-tool", map[string]any{"input": "test"})
	if err != nil {
		t.Fatalf("Execute failed: %v", err)
	}
	if result != "result" {
		t.Errorf("expected 'result', got %v", result)
	}
}

func TestExecuteUnknownTool(t *testing.T) {
	e := NewToolExecutor()

	_, err := e.Execute(context.Background(), "unknown", nil)
	if err == nil {
		t.Fatal("expected error for unknown tool")
	}
}

func TestDuplicateRegistration(t *testing.T) {
	e := NewToolExecutor()

	tool := &mockToolPlugin{name: "dup-tool"}
	if err := e.Register("dup-tool", tool); err != nil {
		t.Fatalf("first Register failed: %v", err)
	}
	if err := e.Register("dup-tool", tool); err == nil {
		t.Error("expected error for duplicate registration")
	}
}

func TestListTools(t *testing.T) {
	e := NewToolExecutor()

	e.Register("a", &mockToolPlugin{name: "a"})
	e.Register("b", &mockToolPlugin{name: "b"})

	tools := e.List()
	if len(tools) != 2 {
		t.Errorf("expected 2 tools, got %d", len(tools))
	}
}

func TestSchemas(t *testing.T) {
	e := NewToolExecutor()

	e.Register("weather", &mockToolPlugin{
		name: "weather",
		schema: func() plugins.ToolSchema {
			return plugins.ToolSchema{
				Name:        "weather",
				Description: "Get weather",
				Parameters: map[string]plugins.ParameterSchema{
					"city": {Type: "string", Description: "City name", Required: true},
				},
			}
		},
	})

	schemas := e.Schemas()
	if len(schemas) != 1 {
		t.Fatalf("expected 1 schema, got %d", len(schemas))
	}
	if schemas[0].Name != "weather" {
		t.Errorf("expected weather schema, got %s", schemas[0].Name)
	}
}

func TestExecuteError(t *testing.T) {
	e := NewToolExecutor()

	tool := &mockToolPlugin{
		name: "error-tool",
		execute: func(ctx context.Context, args map[string]any) (any, error) {
			return nil, errors.New("tool failed")
		},
	}

	e.Register("error-tool", tool)
	_, err := e.Execute(context.Background(), "error-tool", nil)
	if err == nil || err.Error() != "tool failed" {
		t.Errorf("expected 'tool failed', got %v", err)
	}
}
