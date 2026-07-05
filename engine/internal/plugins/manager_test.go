package plugins

import (
	"context"
	"testing"
)

type testPlugin struct {
	name    string
	version string
}

func (p *testPlugin) Name() string          { return p.name }
func (p *testPlugin) Version() string        { return p.version }
func (p *testPlugin) Init(ctx interface{}) error { return nil }

type testToolPlugin struct {
	testPlugin
}

func (p *testToolPlugin) Execute(ctx context.Context, args map[string]any) (any, error) {
	return "executed", nil
}

func (p *testToolPlugin) Schema() ToolSchema {
	return ToolSchema{
		Name:        p.name,
		Description: "test tool",
		Parameters: map[string]ParameterSchema{
			"input": {Type: "string", Description: "input value", Required: true},
		},
	}
}

func TestNewManager(t *testing.T) {
	m := NewManager()
	if m == nil {
		t.Fatal("NewManager returned nil")
	}
}

func TestRegisterAndLoadBuiltin(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("test-plugin", func() Plugin {
		return &testPlugin{name: "test-plugin", version: "1.0.0"}
	})

	p, err := m.Load("test-plugin")
	if err != nil {
		t.Fatalf("Load failed: %v", err)
	}
	if p.Name() != "test-plugin" {
		t.Errorf("expected name test-plugin, got %s", p.Name())
	}
	if p.Version() != "1.0.0" {
		t.Errorf("expected version 1.0.0, got %s", p.Version())
	}
}

func TestLoadNonExistent(t *testing.T) {
	m := NewManager()
	_, err := m.Load("does-not-exist")
	if err == nil {
		t.Fatal("expected error for non-existent plugin")
	}
}

func TestListPlugins(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("p1", func() Plugin {
		return &testPlugin{name: "p1", version: "1.0.0"}
	})

	plugins := m.List()
	if len(plugins) != 0 {
		t.Errorf("expected 0 plugins before load, got %d", len(plugins))
	}

	m.Load("p1")
	plugins = m.List()
	if len(plugins) != 1 {
		t.Errorf("expected 1 plugin after load, got %d", len(plugins))
	}
}

func TestUnloadPlugin(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("p1", func() Plugin {
		return &testPlugin{name: "p1", version: "1.0.0"}
	})
	m.Load("p1")

	if err := m.Unload("p1"); err != nil {
		t.Fatalf("Unload failed: %v", err)
	}
	if err := m.Unload("p1"); err == nil {
		t.Error("expected error unloading already unloaded plugin")
	}
}

func TestToolPluginInterface(t *testing.T) {
	p := &testToolPlugin{
		testPlugin: testPlugin{name: "tool-plugin", version: "1.0.0"},
	}

	if p.Name() != "tool-plugin" {
		t.Errorf("expected name tool-plugin")
	}

	schema := p.Schema()
	if schema.Name != "tool-plugin" {
		t.Errorf("expected schema name tool-plugin")
	}

	result, err := p.Execute(context.Background(), map[string]any{"input": "test"})
	if err != nil {
		t.Fatalf("Execute failed: %v", err)
	}
	if result != "executed" {
		t.Errorf("expected 'executed', got %v", result)
	}
}

func TestDoubleLoad(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("dup", func() Plugin {
		return &testPlugin{name: "dup", version: "1.0.0"}
	})
	m.Load("dup")
	_, err := m.Load("dup")
	if err == nil {
		t.Error("expected error loading duplicate plugin")
	}
}
