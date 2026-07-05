package agent

import (
	"context"
	"fmt"
	"sync"

	"github.com/ashwathai/ashwath-engine/internal/bus"
	"github.com/ashwathai/ashwath-engine/internal/plugins"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

type Agent struct {
	mu        sync.Mutex
	engine    runtime.Engine
	memory    Memory
	executor  *ToolExecutor
	context   *ContextBuilder
	eventBus  bus.Bus
	system    string
}

type AgentOption func(*Agent)

func WithEventBus(b bus.Bus) AgentOption {
	return func(a *Agent) {
		a.eventBus = b
	}
}

func WithSystemPrompt(prompt string) AgentOption {
	return func(a *Agent) {
		a.system = prompt
		a.context = NewContextBuilder(prompt)
	}
}

func WithToolExecutor(ex *ToolExecutor) AgentOption {
	return func(a *Agent) {
		a.executor = ex
	}
}

func New(eng runtime.Engine, opts ...AgentOption) *Agent {
	a := &Agent{
		engine:   eng,
		memory:   NewMemory(Config{MaxMessages: 100}),
		executor: NewToolExecutor(plugins.NewManager()),
		context:  NewContextBuilder(""),
	}
	for _, opt := range opts {
		opt(a)
	}
	return a
}

type RunRequest struct {
	Prompt       string
	MaxTokens    int
	Temperature  float32
	TopK         int
	TopP         float32
}

type RunResult struct {
	Text  string
	Done  bool
	Error error
}

func (a *Agent) Run(ctx context.Context, req RunRequest) (<-chan RunResult, error) {
	a.mu.Lock()
	defer a.mu.Unlock()

	if err := a.memory.AddMessage(Message{Role: RoleUser, Content: req.Prompt}); err != nil {
		return nil, fmt.Errorf("add user message: %w", err)
	}

	prompt := a.context.Build(BuildInput{
		UserInput:   req.Prompt,
		History:     a.memory.Messages(),
		ToolSchemas: a.executor.Schemas(),
	})

	rpcReq := runtime.Request{
		Prompt:      prompt,
		MaxTokens:   req.MaxTokens,
		Temperature: req.Temperature,
		TopK:        req.TopK,
		TopP:        req.TopP,
	}

	ch, err := a.engine.Generate(ctx, rpcReq)
	if err != nil {
		return nil, fmt.Errorf("generate: %w", err)
	}

	resultCh := make(chan RunResult)
	go func() {
		defer close(resultCh)
		var fullText string
		for res := range ch {
			if res.Error != nil {
				a.emitError(res.Error)
				resultCh <- RunResult{Error: res.Error}
				return
			}
			fullText += res.Text
			resultCh <- RunResult{Text: res.Text, Done: res.Done}
		}

		a.memory.AddMessage(Message{Role: RoleAssistant, Content: fullText})
	}()

	return resultCh, nil
}

func (a *Agent) Memory() Memory {
	return a.memory
}

func (a *Agent) Executor() *ToolExecutor {
	return a.executor
}

func (a *Agent) Reset() {
	a.mu.Lock()
	defer a.mu.Unlock()
	a.memory.Clear()
}

func (a *Agent) emitError(err error) {
	if a.eventBus != nil {
		a.eventBus.Publish(bus.TopicError, map[string]string{
			"error": err.Error(),
		})
	}
}
