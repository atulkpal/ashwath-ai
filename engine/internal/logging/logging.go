// Package logging provides structured logging for the engine.
package logging

type Level int

const (
	Debug Level = iota
	Info
	Warn
	Error
)

type Logger interface {
	Debug(msg string, args ...any)
	Info(msg string, args ...any)
	Warn(msg string, args ...any)
	Error(msg string, args ...any)
	With(args ...any) Logger
}
