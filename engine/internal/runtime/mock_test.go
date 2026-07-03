package runtime

import (
	"context"
	"testing"
	"time"
)

func TestMockName(t *testing.T) {
	m := NewMock()
	if m.Name() != mockName {
		t.Errorf("Name() = %s, want %s", m.Name(), mockName)
	}
}

func TestMockInitialize(t *testing.T) {
	m := NewMock()
	if err := m.Initialize(context.Background(), Options{}); err != nil {
		t.Errorf("Initialize failed: %v", err)
	}
}

func TestMockGenerate(t *testing.T) {
	m := NewMock()
	req := Request{
		Prompt:      "Hello",
		MaxTokens:   10,
		Temperature: 0.7,
	}
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	ch, err := m.Generate(ctx, req)
	if err != nil {
		t.Fatalf("Generate failed: %v", err)
	}

	var results []Result
	for r := range ch {
		results = append(results, r)
	}

	if len(results) == 0 {
		t.Fatal("Generate returned no results")
	}

	last := results[len(results)-1]
	if !last.Done {
		t.Error("last result should have Done=true")
	}

	var fullText string
	for _, r := range results {
		fullText += r.Text
	}
	if fullText == "" {
		t.Error("full text should not be empty")
	}
}

func TestMockStop(t *testing.T) {
	m := NewMock()
	if err := m.Stop(context.Background()); err != nil {
		t.Errorf("Stop failed: %v", err)
	}
}

func TestMockGenerateCancellation(t *testing.T) {
	m := NewMock()
	req := Request{Prompt: "test"}

	ctx, cancel := context.WithCancel(context.Background())
	cancel()

	ch, err := m.Generate(ctx, req)
	if err != nil {
		t.Fatalf("Generate failed: %v", err)
	}

	for r := range ch {
		if r.Error != nil {
			return
		}
	}
	t.Error("expected cancellation error, got none")
}
