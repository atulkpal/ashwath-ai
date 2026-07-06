package agent

import (
	"context"
	"testing"
	"time"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

func TestAgentEmptyPrompt(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	ch, err := a.Run(ctx, RunRequest{Prompt: "", MaxTokens: 10})
	if err != nil {
		t.Fatalf("Run failed: %v", err)
	}
	for range ch {
	}

	if a.memory.Len() == 0 {
		t.Error("memory should have messages even with empty prompt")
	}
}

func TestAgentMultipleRuns(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)

	for i := 0; i < 3; i++ {
		ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		ch, err := a.Run(ctx, RunRequest{Prompt: "test", MaxTokens: 5})
		if err != nil {
			cancel()
			t.Fatalf("Run %d failed: %v", i, err)
		}
		for range ch {
		}
		cancel()
	}

	if a.memory.Len() != 6 {
		t.Errorf("expected 6 messages (3 user + 3 assistant), got %d", a.memory.Len())
	}
}

func TestAgentOptions(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng,
		WithSystemPrompt("Custom system prompt"),
	)

	if a.system != "Custom system prompt" {
		t.Errorf("expected custom system prompt, got %q", a.system)
	}
}

func TestAgentRunThenResetThenRun(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	ch, _ := a.Run(ctx, RunRequest{Prompt: "first", MaxTokens: 5})
	for range ch {
	}
	cancel()

	if a.memory.Len() == 0 {
		t.Error("memory should not be empty after first run")
	}

	a.Reset()

	if a.memory.Len() != 0 {
		t.Error("memory should be empty after reset")
	}

	ctx2, cancel2 := context.WithTimeout(context.Background(), 10*time.Second)
	ch2, err := a.Run(ctx2, RunRequest{Prompt: "second", MaxTokens: 5})
	if err != nil {
		cancel2()
		t.Fatalf("Run after reset failed: %v", err)
	}
	for range ch2 {
	}
	cancel2()
}
