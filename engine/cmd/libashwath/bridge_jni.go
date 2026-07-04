//go:build android

package main

/*
#include <stdlib.h>

extern void jni_on_token(const char* text, int done);
*/
import "C"
import (
	"context"
	"fmt"
	"sync"
	"unsafe"

	"github.com/ashwathai/ashwath-engine/internal/config"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"github.com/ashwathai/ashwath-engine/internal/server"
)

// Go-exported functions called from C.

var (
	eng          runtime.Engine
	serverCancel context.CancelFunc
	serverMu     sync.Mutex
)

//export goInit
func goInit(cModelPath, cDataDir *C.char) C.int {
	modelPath := C.GoString(cModelPath)
	eng = runtime.NewMock()
	opts := runtime.Options{ModelPath: modelPath}
	if err := eng.Initialize(context.Background(), opts); err != nil {
		return 0
	}
	return 1
}

//export goShutdown
func goShutdown() {
	serverMu.Lock()
	if serverCancel != nil {
		serverCancel()
		serverCancel = nil
	}
	serverMu.Unlock()

	if eng != nil {
		_ = eng.Stop(context.Background())
		eng = nil
	}
}

//export goRunning
func goRunning() C.int {
	if eng != nil {
		return 1
	}
	return 0
}

//export goGenerate
func goGenerate(
	cPrompt *C.char,
	cMaxTokens C.int,
	cTemperature C.float,
	cTopK C.int,
	cTopP C.float,
) C.int {
	if eng == nil {
		return 0
	}
	prompt := C.GoString(cPrompt)
	req := runtime.Request{
		Prompt:      prompt,
		MaxTokens:   int(cMaxTokens),
		Temperature: float32(cTemperature),
		TopK:        int(cTopK),
		TopP:        float32(cTopP),
	}
	ch, err := eng.Generate(context.Background(), req)
	if err != nil {
		return 0
	}
	go func() {
		for r := range ch {
			text := C.CString(r.Text)
			done := 0
			if r.Done || r.Error != nil {
				done = 1
			}
			C.jni_on_token(text, C.int(done))
			C.free(unsafe.Pointer(text))
		}
	}()
	return 1
}

//export goCancel
func goCancel() C.int {
	return 1
}

//export goStartServer
func goStartServer(port C.int, cDataDir *C.char) C.int {
	serverMu.Lock()
	defer serverMu.Unlock()

	if serverCancel != nil {
		return 1 // Already running
	}

	dataDir := C.GoString(cDataDir)
	cfg := &config.Config{
		Port:     int(port),
		DataDir:  dataDir,
		LogLevel: "info",
	}

	opts := server.Options{
		EngineType: "mock", // Default to mock for now
	}

	ctx, cancel := context.WithCancel(context.Background())
	serverCancel = cancel

	go func() {
		if err := server.Run(ctx, cfg, opts); err != nil {
			fmt.Printf("Embedded server error: %v\n", err)
		}
		serverMu.Lock()
		serverCancel = nil
		serverMu.Unlock()
	}()

	return 1
}

func main() {}
