package device

import (
	"runtime"
	"testing"
)

func TestDetect(t *testing.T) {
	d := New()
	caps, err := d.Detect()
	if err != nil {
		t.Fatalf("Detect failed: %v", err)
	}
	if caps.OSType != runtime.GOOS {
		t.Errorf("OS = %s, want %s", caps.OSType, runtime.GOOS)
	}
	if caps.Arch != runtime.GOARCH {
		t.Errorf("Arch = %s, want %s", caps.Arch, runtime.GOARCH)
	}
	if caps.CPUCores <= 0 {
		t.Errorf("CPUCores = %d, want > 0", caps.CPUCores)
	}
}

func TestDetectReturnsCached(t *testing.T) {
	d := New()
	caps1, _ := d.Detect()
	caps2, _ := d.Detect()
	if caps1 != caps2 {
		t.Error("Detect should return cached result on second call")
	}
}

func TestDetectRAMLinux(t *testing.T) {
	ram := detectRAMLinux()
	if runtime.GOOS == "linux" {
		if ram <= 0 {
			t.Errorf("RAM on linux = %f, want > 0", ram)
		}
	} else {
		if ram != 0 {
			t.Errorf("RAM on non-linux should be 0, got %f", ram)
		}
	}
}
