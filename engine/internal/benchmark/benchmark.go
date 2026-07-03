// Package benchmark measures model and engine performance on device.
package benchmark

type Result struct {
	ModelID        string
	TokensPerSec   float64
	MemoryUsageMB  float64
	LoadTimeMs     int64
	Hardware       string
}

type Runner interface {
	Run(modelID string) (*Result, error)
}
