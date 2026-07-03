package device

import (
	"os"
	"runtime"
	"strconv"
	"strings"
)

func detectRAM() float64 {
	switch runtime.GOOS {
	case "linux", "android":
		return detectRAMLinux()
	default:
		return 0
	}
}

func detectRAMLinux() float64 {
	data, err := os.ReadFile("/proc/meminfo")
	if err != nil {
		return 0
	}
	for _, line := range strings.Split(string(data), "\n") {
		if strings.HasPrefix(line, "MemTotal:") {
			fields := strings.Fields(line)
			if len(fields) >= 2 {
				kb, err := strconv.ParseFloat(fields[1], 64)
				if err == nil {
					return kb / (1024 * 1024)
				}
			}
		}
	}
	return 0
}
