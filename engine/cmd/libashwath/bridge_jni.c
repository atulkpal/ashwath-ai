#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>

#define LOG_TAG "AshwathEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// ── JNI helpers ────────────────────────────────────────────────────────

static JavaVM* jvm             = NULL;
static jobject g_callback      = NULL;
static jmethodID g_onToken_mid = NULL;

void jni_setup(JNIEnv* env, jobject callback) {
    LOGI("jni_setup called");
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

void jni_on_token(const char* text, int done) {
    if (jvm == NULL || g_callback == NULL || g_onToken_mid == NULL) return;

    JNIEnv* env;
    jint attach = (*jvm)->AttachCurrentThread(jvm, &env, NULL);
    if (attach != JNI_OK) {
        LOGE("Failed to attach current thread to JVM");
        return;
    }

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

// ── Forward declarations of Go functions ────────────────────────────────

extern int  goInit(const char* engine_type, const char* model_path, const char* llama_bin);
extern void goShutdown(void);
extern int  goRunning(void);
extern int  goGenerate(const char* prompt, int max_tokens,
                        float temperature, int top_k, float top_p);
extern int  goCancel(void);
extern int  goStartServer(int port, const char* data_dir, const char* engine_type);

// ── JNI entry points ────────────────────────────────────────────────────

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeInit(
    JNIEnv* env, jobject thiz,
    jstring engineType, jstring modelPath, jstring llamaBin) {
    LOGI("nativeInit called (type=%s)", engineType ? "set" : "null");
    const char* et = engineType ? (*env)->GetStringUTFChars(env, engineType, NULL) : "mock";
    const char* mp = modelPath ? (*env)->GetStringUTFChars(env, modelPath, NULL) : "";
    const char* lb = llamaBin ? (*env)->GetStringUTFChars(env, llamaBin, NULL) : "";
    jint result = goInit(et, mp, lb);
    if (engineType) (*env)->ReleaseStringUTFChars(env, engineType, et);
    if (modelPath)  (*env)->ReleaseStringUTFChars(env, modelPath, mp);
    if (llamaBin)   (*env)->ReleaseStringUTFChars(env, llamaBin, lb);
    return result;
}

JNIEXPORT void JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeShutdown(
    JNIEnv* env, jobject thiz) {
    LOGI("nativeShutdown called");
    goShutdown();
}

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeGenerate(
    JNIEnv* env, jobject thiz, jstring prompt,
    jint maxTokens, jfloat temperature, jint topK, jfloat topP,
    jobject callback) {
    LOGI("nativeGenerate called");
    jni_setup(env, callback);
    const char* pStr = prompt ? (*env)->GetStringUTFChars(env, prompt, NULL) : "";
    jint result = goGenerate(pStr, (int)maxTokens, (float)temperature,
                              (int)topK, (float)topP);
    if (prompt) (*env)->ReleaseStringUTFChars(env, prompt, pStr);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeCancel(
    JNIEnv* env, jobject thiz) {
    LOGI("nativeCancel called");
    return goCancel();
}

JNIEXPORT jint JNICALL
Java_com_ashwathai_sdk_jni_AshwathBridge_nativeStartServer(
    JNIEnv* env, jobject thiz,
    jint port, jstring dataDir, jstring engineType) {
    LOGI("nativeStartServer called on port %d (type=%s)", port,
         engineType ? "set" : "null");
    const char* dd = dataDir ? (*env)->GetStringUTFChars(env, dataDir, NULL) : "";
    const char* et = engineType ? (*env)->GetStringUTFChars(env, engineType, NULL) : "mock";
    jint result = goStartServer((int)port, dd, et);
    if (dataDir)    (*env)->ReleaseStringUTFChars(env, dataDir, dd);
    if (engineType) (*env)->ReleaseStringUTFChars(env, engineType, et);
    return result;
}
