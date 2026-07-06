package logging

import (
	"context"
	"fmt"
	"io"
	"log/slog"
	"os"
	"runtime"
	"strings"
)

/*
#cgo android LDFLAGS: -llog
#include <android/log.h>
#include <stdlib.h>

static void android_log(int level, const char* msg) {
    __android_log_print(level, "AshwathEngine", "%s", msg);
}
*/
import "C"
import "unsafe"

type slogLogger struct {
	inner     *slog.Logger
	isAndroid bool
}

func New(w io.Writer, levelStr string) Logger {
	var l slog.Level
	switch strings.ToLower(levelStr) {
	case "debug":
		l = slog.LevelDebug
	case "info":
		l = slog.LevelInfo
	case "warn":
		l = slog.LevelWarn
	case "error":
		l = slog.LevelError
	default:
		l = slog.LevelInfo
	}
	opts := &slog.HandlerOptions{
		Level: l,
	}
	return &slogLogger{
		inner:     slog.New(slog.NewTextHandler(w, opts)),
		isAndroid: runtime.GOOS == "android",
	}
}

func (s *slogLogger) logAndroid(level C.int, msg string, args ...any) {
	fullMsg := msg
	if len(args) > 0 {
		fullMsg = fmt.Sprintf(msg+" %v", args...)
	}
	cMsg := C.CString(fullMsg)
	defer C.free(unsafe.Pointer(cMsg))
	C.android_log(level, cMsg)
}

func (s *slogLogger) Debug(msg string, args ...any) {
	if s.isAndroid {
		s.logAndroid(3, msg, args...)
	}
	s.inner.Debug(msg, args...)
}

func (s *slogLogger) Info(msg string, args ...any) {
	if s.isAndroid {
		s.logAndroid(4, msg, args...)
	}
	s.inner.Info(msg, args...)
}

func (s *slogLogger) Warn(msg string, args ...any) {
	if s.isAndroid {
		s.logAndroid(5, msg, args...)
	}
	s.inner.Warn(msg, args...)
}

func (s *slogLogger) Error(msg string, args ...any) {
	if s.isAndroid {
		s.logAndroid(6, msg, args...)
	}
	s.inner.Error(msg, args...)
}

func (s *slogLogger) With(args ...any) Logger {
	return &slogLogger{
		inner:     s.inner.With(args...),
		isAndroid: s.isAndroid,
	}
}

func NewDefault() Logger {
	return New(os.Stdout, "info")
}

func (s *slogLogger) Enabled(ctx context.Context, level slog.Level) bool {
	return s.inner.Enabled(ctx, level)
}

func (s *slogLogger) Handle(ctx context.Context, record slog.Record) error {
	return s.inner.Handler().Handle(ctx, record)
}

func (s *slogLogger) WithAttrs(attrs []slog.Attr) slog.Handler {
	return s.inner.Handler().WithAttrs(attrs)
}

func (s *slogLogger) WithGroup(name string) slog.Handler {
	return s.inner.Handler().WithGroup(name)
}
