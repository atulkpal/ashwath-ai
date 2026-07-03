package llama

import (
	"bufio"
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"os/exec"
	"strings"
	"sync"
	"time"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

const (
	backendName    = "llama"
	defaultPort    = 18080
	startTimeout   = 30 * time.Second
	shutdownGrace  = 5 * time.Second
	healthInterval = 200 * time.Millisecond
)

type Engine struct {
	mu          sync.Mutex
	cmd         *exec.Cmd
	serverPort  int
	modelPath   string
	llamaBin    string
	httpClient  *http.Client
	initialized bool
}

func New(binPath string) *Engine {
	return &Engine{
		llamaBin:   binPath,
		serverPort: defaultPort,
		httpClient: &http.Client{Timeout: 10 * time.Second},
	}
}

func (e *Engine) Name() string { return backendName }

func (e *Engine) Initialize(ctx context.Context, opts runtime.Options) error {
	e.mu.Lock()
	defer e.mu.Unlock()

	if e.initialized {
		return nil
	}

	if opts.ModelPath == "" {
		return fmt.Errorf("llama: model path required")
	}
	e.modelPath = opts.ModelPath

	if e.llamaBin == "" {
		var err error
		e.llamaBin, err = exec.LookPath("llama-server")
		if err != nil {
			return fmt.Errorf("llama: llama-server not found in PATH; install llama.cpp or set --llama-bin")
		}
	}

	args := []string{
		"--model", e.modelPath,
		"--host", "127.0.0.1",
		"--port", fmt.Sprintf("%d", e.serverPort),
		"--threads", cpuThreadCount(),
		"--no-display-prompt",
	}

	e.cmd = exec.CommandContext(ctx, e.llamaBin, args...)
	if err := e.cmd.Start(); err != nil {
		return fmt.Errorf("llama: start server: %w", err)
	}

	if err := e.waitForHealth(ctx); err != nil {
		e.cmd.Process.Kill()
		e.cmd.Wait()
		return fmt.Errorf("llama: health check: %w", err)
	}

	e.initialized = true
	return nil
}

func (e *Engine) Generate(ctx context.Context, req runtime.Request) (<-chan runtime.Result, error) {
	ch := make(chan runtime.Result)

	body := map[string]any{
		"prompt":       req.Prompt,
		"n_predict":    req.MaxTokens,
		"temperature":  req.Temperature,
		"top_k":        req.TopK,
		"top_p":        req.TopP,
		"stream":       true,
		"cache_prompt": true,
		"stop":         []string{"</s>", "<|im_end|>"},
	}

	payload, err := json.Marshal(body)
	if err != nil {
		return nil, fmt.Errorf("llama: marshal: %w", err)
	}

	go func() {
		defer close(ch)

		url := fmt.Sprintf("http://127.0.0.1:%d/completion", e.serverPort)
		hreq, err := http.NewRequestWithContext(ctx, http.MethodPost, url, bytes.NewReader(payload))
		if err != nil {
			ch <- runtime.Result{Error: fmt.Errorf("llama: create request: %w", err)}
			return
		}
		hreq.Header.Set("Content-Type", "application/json")

		resp, err := e.httpClient.Do(hreq)
		if err != nil {
			ch <- runtime.Result{Error: fmt.Errorf("llama: http: %w", err)}
			return
		}
		defer resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			b, _ := io.ReadAll(resp.Body)
			ch <- runtime.Result{Error: fmt.Errorf("llama: server %d: %s", resp.StatusCode, string(b))}
			return
		}

		scanner := bufio.NewScanner(resp.Body)
		for scanner.Scan() {
			line := scanner.Text()
			if !strings.HasPrefix(line, "data: ") {
				continue
			}

			data := strings.TrimPrefix(line, "data: ")
			if data == "[DONE]" {
				ch <- runtime.Result{Text: "", Done: true}
				return
			}

			var event struct {
				Content string `json:"content"`
				Stop    bool   `json:"stop"`
			}
			if err := json.Unmarshal([]byte(data), &event); err != nil {
				continue
			}

			ch <- runtime.Result{Text: event.Content, Done: event.Stop}
		}

		if err := scanner.Err(); err != nil {
			ch <- runtime.Result{Error: fmt.Errorf("llama: read stream: %w", err)}
		}
	}()

	return ch, nil
}

func (e *Engine) Stop(ctx context.Context) error {
	e.mu.Lock()
	defer e.mu.Unlock()

	if !e.initialized || e.cmd == nil {
		return nil
	}

	e.cmd.Process.Signal(os.Interrupt)

	done := make(chan struct{})
	go func() {
		e.cmd.Wait()
		close(done)
	}()

	select {
	case <-done:
	case <-time.After(shutdownGrace):
		e.cmd.Process.Kill()
		<-done
	}

	e.initialized = false
	return nil
}

func (e *Engine) ServerPort() int { return e.serverPort }

func (e *Engine) waitForHealth(ctx context.Context) error {
	url := fmt.Sprintf("http://127.0.0.1:%d/health", e.serverPort)
	timeout := time.After(startTimeout)

	for {
		select {
		case <-ctx.Done():
			return ctx.Err()
		case <-timeout:
			return fmt.Errorf("server not ready within %v", startTimeout)
		case <-time.After(healthInterval):
			resp, err := e.httpClient.Get(url)
			if err == nil {
				resp.Body.Close()
				if resp.StatusCode == http.StatusOK {
					return nil
				}
			}
		}
	}
}

func cpuThreadCount() string {
	return "0"
}
