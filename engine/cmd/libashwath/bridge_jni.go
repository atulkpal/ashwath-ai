//go:build android

package main

/*
#include <stdlib.h>

// ── JNI helpers callable from Go ────────────────────────────────────────

static JavaVM* jvm             = NULL;
static jobject g_callback      = NULL;
static jmethodID g_onToken_mid = NULL;

// Called from Go (via C.jni_setup) to store the JavaVM and callback.
void jni_setup(JNIEnv* env, jobject callback) {
    if (jvm == NULL) {
        (*env)->GetJavaVM(env, &jvm);
    }
    if (g_callback != NULL) {
        (*env)->DeleteGlobalRef(env, g_callback);
    }
    g_callback = (*env)->NewGlobalRef(env, callback);

    jclass cls = (*env)->GetObjectClass(env, callback);
    g_onToken_mid = (*env)->GetMethodID(env, cls, "onToken",
                                        "(Ljava/lang/String;Z)V");
    (*env)->DeleteLocalRef(env, cls);
}

// Called from a Go goroutine to deliver a token to Java.
void jni_on_token(const char* text, int done) {
    if (jvm == NULL || g_callback == NULL || g_onToken_mid == NULL) return;

    JNIEnv* env;
    jint attach = (*jvm)->AttachCurrentThread(jvm, &env, NULL);
    if (attach != JNI_OK) return;

    jstring jText = (*env)->NewStringUTF(env, text);
    if (jText == NULL) {
        if (done) (*jvm)->DetachCurrentThread(jvm);
        return;
    }

    (*env)->CallVoidMethod(env, g_callback, g_onToken_mid, jText, done);
    (*env)->DeleteLocalRef(env, jText);

    if (done) {
        (*env)->DeleteGlobalRef(env, g_callback);
        g_callback = NULL;
        (*jvm)->DetachCurrentThread(jvm);
    }
}

void jni_cleanup() {
    g_callback = NULL; // caller must have already deleted global ref
    g_onToken_mid = NULL;
}

// ── JNI entry points ────────────────────────────────────────────────────

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeInit(
    JNIEnv* env, jobject thiz, jstring modelPath, jstring dataDir) {
    (void)thiz;
    return jni_init(env, modelPath, dataDir);
}

JNIEXPORT void JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeShutdown(
    JNIEnv* env, jobject thiz) {
    (void)env; (void)thiz;
    jni_shutdown();
}

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeGenerate(
    JNIEnv* env, jobject thiz, jstring prompt,
    jint maxTokens, jfloat temperature, jint topK, jfloat topP,
    jobject callback) {
    (void)thiz;
    return jni_generate(env, prompt, maxTokens, temperature, topK, topP, callback);
}

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeCancel(
    JNIEnv* env, jobject thiz) {
    (void)env; (void)thiz;
    return jni_cancel();
}

// ── Internal C wrappers (called by JNI entry points) ────────────────────
// These forward to Go-exported functions via extern.
// (Declared here rather than as //export on the Go side so we keep all
//  JNI boilerplate in C where it's readable.)

extern int  go_init(const char* model_path, const char* data_dir);
extern void go_shutdown(void);
extern int  go_running(void);
extern int  go_generate(const char* prompt, int max_tokens,
                        float temperature, int top_k, float top_p);
extern int  go_cancel(void);

static jint jni_init(JNIEnv* env, jstring modelPath, jstring dataDir) {
    if (modelPath == NULL || dataDir == NULL) return ErrInvalidArgs;
    const char* mp = (*env)->GetStringUTFChars(env, modelPath, NULL);
    const char* dd = (*env)->GetStringUTFChars(env, dataDir,   NULL);
    jint result = go_init(mp, dd);
    (*env)->ReleaseStringUTFChars(env, modelPath, mp);
    (*env)->ReleaseStringUTFChars(env, dataDir,   dd);
    return result;
}

static void jni_shutdown(void) {
    go_shutdown();
}

static jint jni_generate(JNIEnv* env, jstring prompt,
                         jint maxTokens, jfloat temperature,
                         jint topK, jfloat topP,
                         jobject callback) {
    if (prompt == NULL) return ErrInvalidArgs;
    jni_setup(env, callback);

    const char* pStr = (*env)->GetStringUTFChars(env, prompt, NULL);
    jint result = go_generate(pStr, (int)maxTokens, (float)temperature,
                              (int)topK, (float)topP);
    (*env)->ReleaseStringUTFChars(env, prompt, pStr);
    return result;
}

static jint jni_cancel(void) {
    return go_cancel();
}
>>>>>>> b251e92 (E4.4: JNI error code granularity (8 error codes))
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
