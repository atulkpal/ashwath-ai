package benchmark

import (
	"context"
	"testing"
	"time"

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

func BenchmarkMockGenerate(b *testing.B) {
	eng := runtime.NewMock()
	if err := eng.Initialize(context.Background(), runtime.Options{}); err != nil {
		b.Fatalf("Initialize: %v", err)
	}
	defer eng.Stop(context.Background())

	req := runtime.Request{
		Prompt:      "Explain the transformer architecture in detail.",
		MaxTokens:   256,
		Temperature: 0.7,
		TopK:        40,
		TopP:        0.9,
	}

	b.ResetTimer()

	for i := 0; i < b.N; i++ {
		b.StopTimer()
		ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
		b.StartTimer()

		ch, err := eng.Generate(ctx, req)
		if err != nil {
			b.Fatalf("Generate: %v", err)
		}

		var last runtime.Result
		for r := range ch {
			_ = r.Text
			last = r
		}
		cancel()

		if !last.Done {
			b.Error("generation did not complete")
		}
	}
}

func BenchmarkMockGenerateShortPrompt(b *testing.B) {
	eng := runtime.NewMock()
	if err := eng.Initialize(context.Background(), runtime.Options{}); err != nil {
		b.Fatalf("Initialize: %v", err)
	}
	defer eng.Stop(context.Background())

	req := runtime.Request{
		Prompt:      "Hello",
		MaxTokens:   64,
		Temperature: 0.5,
	}

	b.ResetTimer()

	for i := 0; i < b.N; i++ {
		ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		ch, err := eng.Generate(ctx, req)
		if err != nil {
			b.Fatalf("Generate: %v", err)
		}
		for r := range ch {
			_ = r.Text
		}
		cancel()
	}
}

func BenchmarkMockGenerateCancellation(b *testing.B) {
	eng := runtime.NewMock()
	if err := eng.Initialize(context.Background(), runtime.Options{}); err != nil {
		b.Fatalf("Initialize: %v", err)
	}
	defer eng.Stop(context.Background())

	req := runtime.Request{
		Prompt:      "Generate a long text about machine learning.",
		MaxTokens:   10000,
	}

	b.ResetTimer()

	for i := 0; i < b.N; i++ {
		ctx, cancel := context.WithCancel(context.Background())
		ch, err := eng.Generate(ctx, req)
		if err != nil {
			b.Fatalf("Generate: %v", err)
		}

		read := 0
		for r := range ch {
			_ = r.Text
			read++
			if read >= 5 {
				break
			}
		}
		cancel()
	}
}
