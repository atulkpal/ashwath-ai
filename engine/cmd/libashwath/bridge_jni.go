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
	"net"
	"os"
	"path/filepath"
	"sync"
	"time"
	"unsafe"

	"github.com/ashwathai/ashwath-engine/internal/config"
	"github.com/ashwathai/ashwath-engine/internal/downloads"
	"github.com/ashwathai/ashwath-engine/internal/models"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"github.com/ashwathai/ashwath-engine/internal/runtime/llama"
	"github.com/ashwathai/ashwath-engine/internal/server"
)

var (
	eng          runtime.Engine
	serverCancel context.CancelFunc
	serverMu     sync.Mutex
)

//export goInit
func goInit(cEngineType, cModelPath, cLlamaBin *C.char) C.int {
	engineType := C.GoString(cEngineType)
	modelPath := C.GoString(cModelPath)
	llamaBin := C.GoString(cLlamaBin)
	opts := runtime.Options{}
	switch engineType {
	case "mock":
		eng = runtime.NewMock()
	case "llama":
		fallthrough
	default:
		if modelPath == "" {
			return ErrInitFailed
		}
		eng = llama.New(llamaBin)
		opts.ModelPath = modelPath
	}
	if err := eng.Initialize(context.Background(), opts); err != nil {
		eng = nil
		return ErrInitFailed
	}
	return ErrOK
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
		return ErrOK
	}
	return ErrEngineNil
}

//export goGenerate
func goGenerate(cPrompt *C.char, cMaxTokens C.int, cTemperature C.float, cTopK C.int, cTopP C.float) C.int {
	if eng == nil {
		return ErrEngineNil
	}
	if cPrompt == nil || C.GoString(cPrompt) == "" {
		return ErrInvalidArgs
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
		return ErrGenerateFailed
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
	return ErrOK
}

//export goCancel
func goCancel() C.int {
	if eng == nil {
		return ErrEngineNil
	}
	return ErrOK
}

//export goStartServer
func goStartServer(port C.int, cDataDir *C.char, cEngineType *C.char) C.int {
	serverMu.Lock()
	defer serverMu.Unlock()

	if serverCancel != nil {
		return ErrOK
	}

	dataDir := C.GoString(cDataDir)
	engineType := C.GoString(cEngineType)

	if engineType == "" {
		engineType = "llama"
	}

	llama.Register()

	var modelPath string
	if engineType == "llama" {
		modelsDir := filepath.Join(dataDir, "models")
		reg := models.NewRegistry(modelsDir, downloads.NewManager())

		mdls, _ := reg.List()
		for _, m := range mdls {
			if m.Installed {
				modelPath = filepath.Join(modelsDir, m.ID, m.Filename)
				break
			}
		}

		if modelPath == "" && len(mdls) > 0 {
			m := mdls[0]
			if err := reg.Install(m.ID); err == nil {
				installed, err := reg.Get(m.ID)
				if err == nil && installed != nil {
					modelPath = filepath.Join(modelsDir, installed.ID, installed.Filename)
				}
			}
		}
	}

	cfg := &config.Config{
		Port:     int(port),
		DataDir:  dataDir,
		LogLevel: "info",
	}

	opts := server.Options{
		EngineType: engineType,
		ModelPath:  modelPath,
	}

	ctx, cancel := context.WithCancel(context.Background())
	serverCancel = cancel

	serverReady := make(chan error, 1)
	go func() {
		err := server.Run(ctx, cfg, opts)
		serverReady <- err
		serverMu.Lock()
		serverCancel = nil
		serverMu.Unlock()
	}()

	addr := fmt.Sprintf("127.0.0.1:%d", int(port))
	deadline := time.Now().Add(60 * time.Second)
	for time.Now().Before(deadline) {
		select {
		case err := <-serverReady:
			fmt.Fprintf(os.Stderr, "Embedded server exited early: %v\n", err)
			return ErrInitFailed
		default:
		}
		conn, err := net.DialTimeout("tcp", addr, 500*time.Millisecond)
		if err == nil {
			conn.Close()
			return ErrOK
		}
		time.Sleep(500 * time.Millisecond)
	}

	return ErrInitFailed
}

func main() {}
