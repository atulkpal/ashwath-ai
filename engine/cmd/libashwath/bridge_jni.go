//go:build android

package main

/*
#include <jni.h>
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
    const char* mp = modelPath ? (*env)->GetStringUTFChars(env, modelPath, NULL) : "";
    const char* dd = dataDir   ? (*env)->GetStringUTFChars(env, dataDir,   NULL) : "";
    jint result = go_init(mp, dd);
    if (modelPath) (*env)->ReleaseStringUTFChars(env, modelPath, mp);
    if (dataDir)   (*env)->ReleaseStringUTFChars(env, dataDir,   dd);
    return result;
}

static void jni_shutdown(void) {
    go_shutdown();
}

static jint jni_generate(JNIEnv* env, jstring prompt,
                         jint maxTokens, jfloat temperature,
                         jint topK, jfloat topP,
                         jobject callback) {
    jni_setup(env, callback);

    const char* pStr = prompt ? (*env)->GetStringUTFChars(env, prompt, NULL) : "";
    jint result = go_generate(pStr, (int)maxTokens, (float)temperature,
                              (int)topK, (float)topP);
    if (prompt) (*env)->ReleaseStringUTFChars(env, prompt, pStr);
    return result;
}

static jint jni_cancel(void) {
    return go_cancel();
}
*/
import "C"
import (
	"context"
	"unsafe"

	"github.com/ashwathai/ashwath-engine/internal/runtime"
)

// Go-exported functions called from C preamble via extern.
// These share the 'eng' variable with bridge.go.

//export go_init
func goInit(cModelPath, cDataDir *C.char) C.int {
	modelPath := C.GoString(cModelPath)
	eng = runtime.NewMock()
	opts := runtime.Options{ModelPath: modelPath}
	if err := eng.Initialize(context.Background(), opts); err != nil {
		return 0
	}
	return 1
}

//export go_shutdown
func goShutdown() {
	if eng != nil {
		_ = eng.Stop(context.Background())
		eng = nil
	}
}

//export go_running
func goRunning() C.int {
	if eng != nil {
		return 1
	}
	return 0
}

//export go_generate
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

//export go_cancel
func goCancel() C.int {
	return 1
}
