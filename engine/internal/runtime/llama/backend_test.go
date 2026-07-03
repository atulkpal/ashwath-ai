package llama

import (
	"context"
	"testing"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

func TestName(t *testing.T) {
	e := New("")
	if e.Name() != backendName {
		t.Errorf("Name() = %s, want %s", e.Name(), backendName)
	}
}

func TestInitializeMissingBinary(t *testing.T) {
	e := New("/nonexistent/llama-server")
	err := e.Initialize(context.Background(), runtime.Options{ModelPath: "/tmp/test.gguf"})
	if err == nil {
		t.Fatal("expected error for missing binary, got nil")
	}
}

func TestInitializeMissingModel(t *testing.T) {
	e := New("")
	err := e.Initialize(context.Background(), runtime.Options{ModelPath: ""})
	if err == nil {
		t.Fatal("expected error for empty model path, got nil")
	}
}

func TestStopWithoutInit(t *testing.T) {
	e := New("")
	if err := e.Stop(context.Background()); err != nil {
		t.Errorf("Stop() on uninitialized engine: %v", err)
	}
}

func TestServerPort(t *testing.T) {
	e := New("")
	if e.ServerPort() != defaultPort {
		t.Errorf("ServerPort() = %d, want %d", e.ServerPort(), defaultPort)
	}
}

func TestNewWithCustomBin(t *testing.T) {
	e := New("/custom/path/llama-server")
	if e.llamaBin != "/custom/path/llama-server" {
		t.Errorf("llamaBin = %s, want /custom/path/llama-server", e.llamaBin)
	}
}
