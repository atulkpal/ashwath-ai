package models

import (
	"testing"
)

func TestNewRegistry(t *testing.T) {
	r := NewRegistry()
	if r == nil {
		t.Fatal("NewRegistry returned nil")
	}
}

func TestList(t *testing.T) {
	r := NewRegistry()
	models, err := r.List()
	if err != nil {
		t.Fatalf("List failed: %v", err)
	}
	if len(models) == 0 {
		t.Fatal("List returned empty")
	}
	if len(models) != 4 {
		t.Errorf("List returned %d models, want 4", len(models))
	}
}

func TestGetExisting(t *testing.T) {
	r := NewRegistry()
	m, err := r.Get("llama-3.2-3b")
	if err != nil {
		t.Fatalf("Get failed: %v", err)
	}
	if m == nil {
		t.Fatal("Get returned nil for existing model")
	}
	if m.Name != "Llama 3.2 3B" {
		t.Errorf("Name = %s, want Llama 3.2 3B", m.Name)
	}
	if m.Provider != "Meta" {
		t.Errorf("Provider = %s, want Meta", m.Provider)
	}
}

func TestGetMissing(t *testing.T) {
	r := NewRegistry()
	m, err := r.Get("nonexistent-model")
	if err != nil {
		t.Fatalf("Get failed: %v", err)
	}
	if m != nil {
		t.Errorf("Get should return nil for missing model, got %v", m)
	}
}

func TestInstallAndRemove(t *testing.T) {
	r := NewRegistry()
	if err := r.Install("llama-3.2-3b"); err != nil {
		t.Errorf("Install failed: %v", err)
	}
	if err := r.Remove("llama-3.2-3b"); err != nil {
		t.Errorf("Remove failed: %v", err)
	}
}

func TestModelFields(t *testing.T) {
	r := NewRegistry()
	list, _ := r.List()

	gemma := list[0]
	if gemma.ID != "gemma-3-4b" {
		t.Errorf("first model ID = %s, want gemma-3-4b", gemma.ID)
	}
	if gemma.SizeBytes <= 0 {
		t.Errorf("SizeBytes should be > 0, got %d", gemma.SizeBytes)
	}
	if len(gemma.Tags) == 0 {
		t.Error("Tags should not be empty")
	}
}

func TestDefaultModels(t *testing.T) {
	models := defaultModels()
	if len(models) != 4 {
		t.Errorf("defaultModels returned %d, want 4", len(models))
	}

	ids := make(map[string]bool)
	for _, m := range models {
		if ids[m.ID] {
			t.Errorf("duplicate model ID: %s", m.ID)
		}
		ids[m.ID] = true
		if m.Name == "" {
			t.Errorf("model %s has empty Name", m.ID)
		}
		if m.Provider == "" {
			t.Errorf("model %s has empty Provider", m.ID)
		}
	}
}
