package logging

import (
	"bytes"
	"strings"
	"testing"
)

func TestNewDefault(t *testing.T) {
	l := NewDefault()
	if l == nil {
		t.Fatal("NewDefault returned nil")
	}
}

func TestLogLevels(t *testing.T) {
	tests := []struct {
		level string
		msg   string
	}{
		{"debug", "debug message"},
		{"info", "info message"},
		{"warn", "warn message"},
		{"error", "error message"},
	}

	for _, tt := range tests {
		t.Run(tt.level, func(t *testing.T) {
			var buf bytes.Buffer
			l := New(&buf, tt.level)
			switch tt.level {
			case "debug":
				l.Debug(tt.msg)
			case "info":
				l.Info(tt.msg)
			case "warn":
				l.Warn(tt.msg)
			case "error":
				l.Error(tt.msg)
			}
			if !strings.Contains(buf.String(), tt.msg) {
				t.Errorf("output missing %q: %s", tt.msg, buf.String())
			}
		})
	}
}

func TestLogLevelFiltering(t *testing.T) {
	var buf bytes.Buffer
	l := New(&buf, "error")
	l.Info("should not appear")
	if buf.Len() > 0 {
		t.Errorf("info message should be filtered at error level: %s", buf.String())
	}
}

func TestWith(t *testing.T) {
	var buf bytes.Buffer
	l := New(&buf, "info")
	withL := l.With("component", "test")
	withL.Info("with fields")
	if !strings.Contains(buf.String(), "component") || !strings.Contains(buf.String(), "test") {
		t.Errorf("With fields missing from output: %s", buf.String())
	}
}

func TestInvalidLevelDefaultsToInfo(t *testing.T) {
	var buf bytes.Buffer
	l := New(&buf, "invalid")
	l.Debug("should be filtered")
	if buf.Len() > 0 {
		t.Errorf("debug should be filtered when level defaults to info")
	}
	l.Info("should appear")
	if !strings.Contains(buf.String(), "should appear") {
		t.Errorf("info should appear after invalid level: %s", buf.String())
	}
}
