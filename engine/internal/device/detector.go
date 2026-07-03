package device

import (
	"runtime"
	"sync"
)

type detector struct {
	once sync.Once
	caps *Capabilities
}

func New() Detector {
	return &detector{}
}

func (d *detector) Detect() (*Capabilities, error) {
	d.once.Do(func() {
		d.caps = &Capabilities{
			OSType:   runtime.GOOS,
			Arch:     runtime.GOARCH,
			CPUCores: runtime.NumCPU(),
			RAMGB:    detectRAM(),
		}
	})
	return d.caps, nil
}
