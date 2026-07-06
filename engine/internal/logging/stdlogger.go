//go:build !cgo

package logging

import (
	"io"
	"log/slog"
	"os"
	"strings"
)

type stdLogger struct {
	inner *slog.Logger
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
	opts := &slog.HandlerOptions{Level: l}
	return &stdLogger{
		inner: slog.New(slog.NewTextHandler(w, opts)),
	}
}

func NewDefault() Logger {
	return New(os.Stdout, "info")
}

func (s *stdLogger) Debug(msg string, args ...any) { s.inner.Debug(msg, args...) }
func (s *stdLogger) Info(msg string, args ...any)  { s.inner.Info(msg, args...) }
func (s *stdLogger) Warn(msg string, args ...any)  { s.inner.Warn(msg, args...) }
func (s *stdLogger) Error(msg string, args ...any) { s.inner.Error(msg, args...) }

func (s *stdLogger) With(args ...any) Logger {
	return &stdLogger{inner: s.inner.With(args...)}
}
