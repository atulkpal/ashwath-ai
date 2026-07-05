package benchmark

import (
	"context"
	"testing"
	"time"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

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
