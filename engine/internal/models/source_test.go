package models

import (
	"os"
	"testing"

	"github.com/ashwathai/ashwath-engine/internal/bus"
	"github.com/ashwathai/ashwath-engine/internal/downloads"
)

func TestBuiltinSourceList(t *testing.T) {
	s := NewBuiltinSource()
	models, err := s.List()
	if err != nil {
		t.Fatalf("List failed: %v", err)
	}
	if len(models) != 4 {
		t.Errorf("expected 4 models, got %d", len(models))
	}
}

func TestBuiltinSourceGet(t *testing.T) {
	s := NewBuiltinSource()
	m, err := s.Get("gemma-3-4b")
	if err != nil {
		t.Fatalf("Get failed: %v", err)
	}
	if m == nil {
		t.Fatal("Get returned nil")
	}
	if m.ID != "gemma-3-4b" {
		t.Errorf("expected gemma-3-4b, got %s", m.ID)
	}
}

func TestBuiltinSourceGetMissing(t *testing.T) {
	s := NewBuiltinSource()
	m, err := s.Get("nonexistent")
	if err != nil {
		t.Fatalf("Get failed: %v", err)
	}
	if m != nil {
		t.Errorf("expected nil for missing model")
	}
}

func TestRegistryWithBusEmitsEvents(t *testing.T) {
	dir, err := os.MkdirTemp("", "ashwath-models-bus-*")
	if err != nil {
		t.Fatal(err)
	}
	defer os.RemoveAll(dir)

	b := bus.New()
	var events []bus.Event
	b.Subscribe(bus.TopicModelInstalled, func(e bus.Event) {
		events = append(events, e)
	})
	b.Subscribe(bus.TopicModelRemoved, func(e bus.Event) {
		events = append(events, e)
	})

	r := NewRegistry(dir, downloads.NewManager(), WithBus(b))

	if err := r.Remove("nonexistent"); err == nil {
		t.Error("expected error removing nonexistent model")
	}

	if len(events) != 0 {
		t.Errorf("expected 0 events, got %d", len(events))
	}
}

func TestRegistryWithSource(t *testing.T) {
	dir, err := os.MkdirTemp("", "ashwath-models-source-*")
	if err != nil {
		t.Fatal(err)
	}
	defer os.RemoveAll(dir)

	customSource := &testSource{
		models: []Model{
			{ID: "custom-model", Name: "Custom", Provider: "Test", Filename: "model.gguf"},
		},
	}

	r := NewRegistry(dir, downloads.NewManager(), WithSource(customSource))

	models, err := r.List()
	if err != nil {
		t.Fatalf("List failed: %v", err)
	}
	if len(models) != 1 {
		t.Errorf("expected 1 model from custom source, got %d", len(models))
	}
	if models[0].ID != "custom-model" {
		t.Errorf("expected custom-model, got %s", models[0].ID)
	}
}

type testSource struct {
	models []Model
}

func (s *testSource) List() ([]Model, error) {
	result := make([]Model, len(s.models))
	copy(result, s.models)
	return result, nil
}

func (s *testSource) Get(id string) (*Model, error) {
	for _, m := range s.models {
		if m.ID == id {
			return &m, nil
		}
	}
	return nil, nil
}
