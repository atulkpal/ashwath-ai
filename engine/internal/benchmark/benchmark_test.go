package benchmark

import (
	"context"
	"testing"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

func TestRunnerWithMock(t *testing.T) {
	eng := runtime.NewMock()
	cfg := DefaultConfig()
	cfg.NumRuns = 1
	cfg.MaxTokens = 10

	r := NewRunner(eng, cfg)
	result, err := r.Run(context.Background(), "test-model")
	if err != nil {
		t.Fatalf("Run failed: %v", err)
	}
	if result == nil {
		t.Fatal("Run returned nil result")
	}
	if result.TotalTokens <= 0 {
		t.Errorf("TotalTokens = %d, want > 0", result.TotalTokens)
	}
	if result.TokensPerSec <= 0 {
		t.Errorf("TokensPerSec = %f, want > 0", result.TokensPerSec)
	}
	if result.LatencyMs <= 0 {
		t.Errorf("LatencyMs = %f, want > 0", result.LatencyMs)
	}
	if result.Hardware == "" {
		t.Error("Hardware should not be empty")
	}
	if result.ModelID != "test-model" {
		t.Errorf("ModelID = %s, want test-model", result.ModelID)
	}
}

func TestDefaultConfig(t *testing.T) {
	cfg := DefaultConfig()
	if cfg.Prompt == "" {
		t.Error("Default prompt should not be empty")
	}
	if cfg.MaxTokens <= 0 {
		t.Errorf("MaxTokens = %d, want > 0", cfg.MaxTokens)
	}
	if cfg.NumRuns <= 0 {
		t.Errorf("NumRuns = %d, want > 0", cfg.NumRuns)
	}
}
