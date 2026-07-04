//go:build !android

// Command libashwath produces libashwath_engine.so (or .dylib/.dll) for
// platforms that embed the engine via C ABI (desktop, iOS).
//
// Build for Android:
//
//	CGO_ENABLED=1 GOOS=android GOARCH=arm64 CC=<NDK clang> \
//	  go build -buildmode=c-shared -o libashwath_engine.so ./cmd/libashwath/
//
// On Android the JNI entry points in bridge_jni.go (+android build tag)
// are compiled alongside these plain C exports.
package main

/*
#include <stdlib.h>

typedef void (*TokenCallbackFn)(const char* text, int done, void* userdata);

static void call_token_callback(TokenCallbackFn cb, const char* text, int done, void* userdata) {
    cb(text, done, userdata);
}

// ── Lifecycle ──────────────────────────────────────────────────────────
int  engine_init      (const char* model_path, const char* data_dir);
void engine_shutdown  (void);
int  engine_is_running(void);

// ── Generation ─────────────────────────────────────────────────────────
//
// Tokens are delivered to the callback from a goroutine.
// The last invocation has done == 1.
int engine_generate(
    const char*      prompt,
    int              max_tokens,
    float            temperature,
    int              top_k,
    float            top_p,
    TokenCallbackFn  callback,
    void*            userdata);

int engine_cancel(void);
*/
import "C"
import (
	"context"
	"unsafe"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

var eng runtime.Engine

//export engineInit
func engineInit(cModelPath, cDataDir *C.char) C.int {
	modelPath := C.GoString(cModelPath)
	eng = runtime.NewMock()
	opts := runtime.Options{ModelPath: modelPath}
	if err := eng.Initialize(context.Background(), opts); err != nil {
		return 0
	}
	return 1
}

//export engineShutdown
func engineShutdown() {
	if eng != nil {
		_ = eng.Stop(context.Background())
		eng = nil
	}
}

//export engineIsRunning
func engineIsRunning() C.int {
	if eng != nil {
		return 1
	}
	return 0
}

//export engineGenerate
func engineGenerate(
	cPrompt *C.char,
	cMaxTokens C.int,
	cTemperature C.float,
	cTopK C.int,
	cTopP C.float,
	cb C.TokenCallbackFn,
	userdata unsafe.Pointer,
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
			C.call_token_callback(cb, text, C.int(done), userdata)
			C.free(unsafe.Pointer(text))
		}
	}()
	return 1
}

//export engineCancel
func engineCancel() C.int {
	return 1
}

func main() {}
