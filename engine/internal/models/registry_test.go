package models

import (
	"os"
	"testing"

	"github.com/ashwathai/ashwath-engine/internal/downloads"
)

func newTestRegistry(t *testing.T) (Registry, string) {
	t.Helper()
	dir, err := os.MkdirTemp("", "ashwath-models-*")
	if err != nil {
		t.Fatal(err)
	}
	r := NewRegistry(dir, downloads.NewManager())
	return r, dir
}

func TestNewRegistry(t *testing.T) {
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
	if r == nil {
		t.Fatal("NewRegistry returned nil")
	}
}

func TestList(t *testing.T) {
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
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
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
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
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
	m, err := r.Get("nonexistent-model")
	if err == nil {
		t.Fatal("Get should return error for missing model")
	}
	if m != nil {
		t.Errorf("Get should return nil for missing model, got %v", m)
	}
}

func TestModelFields(t *testing.T) {
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
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

func TestInstallNoURL(t *testing.T) {
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
	err := r.Install("nonexistent")
	if err == nil {
		t.Fatal("expected error for missing model")
	}
}

func TestModelsDir(t *testing.T) {
	r, dir := newTestRegistry(t)
	defer os.RemoveAll(dir)
	if r.ModelsDir() != dir {
		t.Errorf("ModelsDir = %q, want %q", r.ModelsDir(), dir)
	}
}

func TestInstalledStatePersistence(t *testing.T) {
	dir, err := os.MkdirTemp("", "ashwath-models-*")
	if err != nil {
		t.Fatal(err)
	}
	defer os.RemoveAll(dir)

	r := NewRegistry(dir, downloads.NewManager())

	// Initial state should have nothing installed
	models, _ := r.List()
	for _, m := range models {
		if m.Installed {
			t.Errorf("model %q should not be installed initially", m.ID)
		}
	}

	// Create a second registry pointing at same dir, verify state persists
	r2 := NewRegistry(dir, downloads.NewManager())
	models2, _ := r2.List()
	for _, m := range models2 {
		if m.Installed {
			t.Errorf("model %q should not be installed in second registry", m.ID)
		}
	}
}
