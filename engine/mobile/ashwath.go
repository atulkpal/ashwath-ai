// Package mobile provides a gomobile-bindable API for the Ashwath Engine.
// This is the single entry point for all mobile platforms (Android, iOS).
// It adapts runtime.Engine (channel-based) to callback-based streaming.
package mobile

import (
	"context"
	"errors"
	"sync"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"github.com/ashwathai/ashwath-engine/internal/runtime/llama"
)

// EngineConfig is passed to NewEngine. All fields are optional; zero values use defaults.
type EngineConfig struct {
	EngineType string // "mock" (default) or "llama"
	ModelPath  string
	DataDir    string
	LlamaBin   string // path to llama-server binary (empty = search PATH)
}

// TokenCallback is implemented by the platform (Android/iOS) to receive tokens.
//
// gomobile generates matching Java/ObjC interfaces:
//
//	Android: public interface TokenCallback { void onToken(String text, boolean done); }
//	iOS:     @protocol TokenCallback <NSObject> - (void)onToken:(NSString*)text done:(BOOL)done; @end
type TokenCallback interface {
	OnToken(text string, done bool)
}

// AshwathEngine is the top-level engine handle. Create one via NewEngine, then
// call Initialize before any other method.
type AshwathEngine struct {
	mu     sync.Mutex
	engine runtime.Engine
}

// NewEngine creates an uninitialized engine handle. Call Initialize when ready.
func NewEngine() *AshwathEngine {
	return &AshwathEngine{}
}

// Initialize starts the engine with the given config.
// Returns an error if already initialized or if the backend fails to start.
//
// EngineType selects the backend: "mock" (default) or "llama".
// For "llama", ModelPath must point to a GGUF file and LlamaBin may optionally
// specify the llama-server binary (empty = search PATH).
func (e *AshwathEngine) Initialize(config *EngineConfig) error {
	e.mu.Lock()
	defer e.mu.Unlock()

	if e.engine != nil {
		return errors.New("engine already initialized")
	}

	engineType := "mock"
	var modelPath, llamaBin string
	if config != nil {
		engineType = config.EngineType
		modelPath = config.ModelPath
		llamaBin = config.LlamaBin
	}

	opts := runtime.Options{ModelPath: modelPath}

	var eng runtime.Engine
	switch engineType {
	case "llama":
		if modelPath == "" {
			return errors.New("ModelPath required when EngineType is llama")
		}
		eng = llama.New(llamaBin)
		opts.ModelPath = modelPath
	default:
		eng = runtime.NewMock()
	}

	if err := eng.Initialize(context.Background(), opts); err != nil {
		return err
	}
	e.engine = eng
	return nil
}

// IsRunning returns true if the engine is initialized and ready.
func (e *AshwathEngine) IsRunning() bool {
	e.mu.Lock()
	defer e.mu.Unlock()
	return e.engine != nil
}

// Generate starts streaming a completion for the given prompt.
// Tokens are delivered to cb.OnToken. The last call has done=true.
// Returns an error if not initialized or generation fails to start.
func (e *AshwathEngine) Generate(
	prompt string,
	maxTokens int,
	temperature float64,
	topK int,
	topP float64,
	cb TokenCallback,
) error {
	e.mu.Lock()
	eng := e.engine
	e.mu.Unlock()

	if eng == nil {
		return errors.New("engine not initialized")
	}

	if cb == nil {
		return errors.New("token callback must not be nil")
	}

	req := runtime.Request{
		Prompt:      prompt,
		MaxTokens:   maxTokens,
		Temperature: float32(temperature),
		TopK:        topK,
		TopP:        float32(topP),
	}

	ch, err := eng.Generate(context.Background(), req)
	if err != nil {
		return err
	}

	go func() {
		for r := range ch {
			if r.Error != nil {
				cb.OnToken(r.Error.Error(), true)
				return
			}
			cb.OnToken(r.Text, r.Done)
		}
	}()
	return nil
}

// Cancel stops the current generation (not yet implemented for mock backend).
func (e *AshwathEngine) Cancel() error {
	return nil
}

// Shutdown stops the engine and releases resources.
func (e *AshwathEngine) Shutdown() {
	e.mu.Lock()
	defer e.mu.Unlock()
	if e.engine != nil {
		_ = e.engine.Stop(context.Background())
		e.engine = nil
	}
}
