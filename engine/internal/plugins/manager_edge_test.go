package plugins

import (
	"context"
	"errors"
	"testing"
)

type failingPlugin struct {
	name string
}

func (p *failingPlugin) Name() string          { return p.name }
func (p *failingPlugin) Version() string        { return "1.0" }
func (p *failingPlugin) Init(ctx context.Context) error { return errors.New("init failed") }

func TestLoadInitFailure(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("failing", func() Plugin {
		return &failingPlugin{name: "failing"}
	})

	_, err := m.Load("failing")
	if err == nil {
		t.Fatal("expected error for failing init")
	}
}

func TestListAfterUnload(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("p1", func() Plugin {
		return &testPlugin{name: "p1", version: "1.0"}
	})
	m.RegisterBuiltin("p2", func() Plugin {
		return &testPlugin{name: "p2", version: "1.0"}
	})

	m.Load("p1")
	m.Load("p2")
	m.Unload("p1")

	plugins := m.List()
	if len(plugins) != 1 {
		t.Errorf("expected 1 plugin after unload, got %d", len(plugins))
	}
	if plugins[0].Name() != "p2" {
		t.Errorf("expected p2, got %s", plugins[0].Name())
	}
}

func TestRegisterDuplicateBuiltin(t *testing.T) {
	m := NewManager()
	m.RegisterBuiltin("dup", func() Plugin {
		return &testPlugin{name: "dup", version: "1.0"}
	})
	m.RegisterBuiltin("dup", func() Plugin {
		return &testPlugin{name: "dup", version: "2.0"}
	})

	p, err := m.Load("dup")
	if err != nil {
		t.Fatalf("Load failed: %v", err)
	}
	if p.Version() != "2.0" {
		t.Errorf("expected version 2.0 (last registered), got %s", p.Version())
	}
}
