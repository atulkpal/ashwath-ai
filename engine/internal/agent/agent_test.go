package agent

import (
	"context"
	"strings"
	"testing"
	"time"

	"github.com/ashwathai/ashwath-engine/internal/bus"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

func TestNewAgent(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)
	if a == nil {
		t.Fatal("New returned nil")
	}
}

func TestAgentRun(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng, WithSystemPrompt("You are a test assistant."))

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	ch, err := a.Run(ctx, RunRequest{Prompt: "Hello", MaxTokens: 50})
	if err != nil {
		t.Fatalf("Run failed: %v", err)
	}

	var results []RunResult
	for r := range ch {
		results = append(results, r)
	}

	if len(results) == 0 {
		t.Fatal("no results received")
	}

	last := results[len(results)-1]
	if !last.Done {
		t.Error("last result should have Done=true")
	}
}

func TestAgentMemory(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	ch, err := a.Run(ctx, RunRequest{Prompt: "First message", MaxTokens: 10})
	if err != nil {
		t.Fatalf("Run failed: %v", err)
	}
	for range ch {
	}

	if a.memory.Len() != 2 {
		t.Errorf("expected 2 messages (user + assistant), got %d", a.memory.Len())
	}

	msgs := a.memory.Messages()
	if msgs[0].Role != RoleUser || !strings.Contains(msgs[0].Content, "First message") {
		t.Errorf("first message should be user input")
	}
	if msgs[1].Role != RoleAssistant {
		t.Errorf("second message should be assistant response")
	}
}

func TestAgentReset(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	ch, _ := a.Run(ctx, RunRequest{Prompt: "test", MaxTokens: 5})
	for range ch {
	}

	a.Reset()
	if a.memory.Len() != 0 {
		t.Errorf("expected 0 messages after reset, got %d", a.memory.Len())
	}
}

func TestAgentWithEventBus(t *testing.T) {
	b := bus.New()
	eng := runtime.NewMock()
	a := New(eng, WithEventBus(b))

	var errors []bus.Event
	b.Subscribe(bus.TopicError, func(e bus.Event) {
		errors = append(errors, e)
	})

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	ch, err := a.Run(ctx, RunRequest{Prompt: "Hello", MaxTokens: 5})
	if err != nil {
		t.Fatalf("Run failed: %v", err)
	}
	for range ch {
	}

	if len(errors) != 0 {
		t.Errorf("expected 0 errors, got %d", len(errors))
	}
}

func TestAgentWithToolExecutor(t *testing.T) {
	eng := runtime.NewMock()
	ex := NewToolExecutor()
	a := New(eng, WithToolExecutor(ex))

	if a.executor != ex {
		t.Error("executor should be the one passed in")
	}
}

func TestAgentRunCancellation(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)
	ctx, cancel := context.WithCancel(context.Background())
	cancel()

	_, err := a.Run(ctx, RunRequest{Prompt: "test", MaxTokens: 5})
	if err != nil {
		return
	}
}

func TestAgentFullTextInMemory(t *testing.T) {
	eng := runtime.NewMock()
	a := New(eng)

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	ch, _ := a.Run(ctx, RunRequest{Prompt: "What is AI?", MaxTokens: 100})
	for range ch {
	}

	msgs := a.memory.Messages()
	if len(msgs) < 2 {
		t.Fatal("expected at least 2 messages")
	}

	response := msgs[len(msgs)-1]
	if response.Role != RoleAssistant {
		t.Errorf("last message should be assistant, got %s", response.Role)
	}
	if len(response.Content) == 0 {
		t.Error("assistant response should not be empty")
	}
}
