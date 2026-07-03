// Package device detects hardware capabilities for optimal model selection.
package device

type Capabilities struct {
	RAMGB      float64
	CPUCores   int
	HasNPU     bool
	HasGPU     bool
	GPUVendor  string
	OSType     string
	Arch       string
}

type Detector interface {
	Detect() (*Capabilities, error)
}
