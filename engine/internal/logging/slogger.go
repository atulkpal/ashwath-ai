package logging

import (
	"context"
	"io"
	"log/slog"
	"os"
	"strings"
)

type slogLogger struct {
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
	opts := &slog.HandlerOptions{
		Level: l,
	}
	return &slogLogger{
		inner: slog.New(slog.NewTextHandler(w, opts)),
	}
}

func NewDefault() Logger {
	return New(os.Stdout, "info")
}

func (s *slogLogger) Debug(msg string, args ...any) {
	s.inner.Debug(msg, args...)
}

func (s *slogLogger) Info(msg string, args ...any) {
	s.inner.Info(msg, args...)
}

func (s *slogLogger) Warn(msg string, args ...any) {
	s.inner.Warn(msg, args...)
}

func (s *slogLogger) Error(msg string, args ...any) {
	s.inner.Error(msg, args...)
}

func (s *slogLogger) With(args ...any) Logger {
	return &slogLogger{inner: s.inner.With(args...)}
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
