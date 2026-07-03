package config

import (
	"os"
	"path/filepath"
	"testing"
)

func TestDefaults(t *testing.T) {
	cfg := defaultConfig()
	if cfg.Port != DefaultPort {
		t.Errorf("port = %d, want %d", cfg.Port, DefaultPort)
	}
	if cfg.LogLevel != DefaultLogLevel {
		t.Errorf("log level = %s, want %s", cfg.LogLevel, DefaultLogLevel)
	}
	if cfg.DataDir != DefaultDataDir {
		t.Errorf("data dir = %s, want %s", cfg.DataDir, DefaultDataDir)
	}
}

func TestLoadFromEnv(t *testing.T) {
	os.Setenv("ASHWATH_PORT", "7777")
	os.Setenv("ASHWATH_LOG_LEVEL", "debug")
	os.Setenv("ASHWATH_DATA_DIR", "/tmp/ashwath-test")
	defer os.Unsetenv("ASHWATH_PORT")
	defer os.Unsetenv("ASHWATH_LOG_LEVEL")
	defer os.Unsetenv("ASHWATH_DATA_DIR")

	cfg := defaultConfig()
	loadFromEnv(cfg)

	if cfg.Port != 7777 {
		t.Errorf("port = %d, want 7777", cfg.Port)
	}
	if cfg.LogLevel != "debug" {
		t.Errorf("log level = %s, want debug", cfg.LogLevel)
	}
	if cfg.DataDir != "/tmp/ashwath-test" {
		t.Errorf("data dir = %s, want /tmp/ashwath-test", cfg.DataDir)
	}
}

func TestLoadFromEnvInvalidPort(t *testing.T) {
	os.Setenv("ASHWATH_PORT", "not-a-number")
	defer os.Unsetenv("ASHWATH_PORT")

	cfg := defaultConfig()
	loadFromEnv(cfg)

	if cfg.Port != DefaultPort {
		t.Errorf("invalid port should be ignored, got %d", cfg.Port)
	}
}

func TestValidatePortFixup(t *testing.T) {
	cfg := &Config{Port: 0}
	cfg.Validate()
	if cfg.Port != DefaultPort {
		t.Errorf("port 0 should reset to %d, got %d", DefaultPort, cfg.Port)
	}

	cfg.Port = 99999
	cfg.Validate()
	if cfg.Port != DefaultPort {
		t.Errorf("port 99999 should reset to %d, got %d", DefaultPort, cfg.Port)
	}

	cfg.Port = 8080
	cfg.Validate()
	if cfg.Port != 8080 {
		t.Errorf("valid port 8080 should be preserved, got %d", cfg.Port)
	}
}

func TestSaveAndLoadFromFile(t *testing.T) {
	dir := t.TempDir()
	cfg := &Config{
		DataDir:   dir,
		ModelsDir: filepath.Join(dir, "models"),
		LogDir:    filepath.Join(dir, "logs"),
		LogLevel:  "debug",
		Port:      9876,
		MaxRAM:    "8GB",
		Device:    "cpu",
	}

	if err := Save(cfg); err != nil {
		t.Fatalf("Save failed: %v", err)
	}

	loaded := &Config{DataDir: dir}
	if err := loadFromFile(loaded); err != nil {
		t.Fatalf("loadFromFile failed: %v", err)
	}
	if loaded.Port != 9876 {
		t.Errorf("loaded port = %d, want 9876", loaded.Port)
	}
	if loaded.LogLevel != "debug" {
		t.Errorf("loaded log level = %s, want debug", loaded.LogLevel)
	}
	if loaded.MaxRAM != "8GB" {
		t.Errorf("loaded max RAM = %s, want 8GB", loaded.MaxRAM)
	}
	if loaded.Device != "cpu" {
		t.Errorf("loaded device = %s, want cpu", loaded.Device)
	}
}
