package config

import (
	"encoding/json"
	"os"
	"path/filepath"
	"strconv"
)

const (
	DefaultDataDir   = "./data"
	DefaultModelsDir = "./data/models"
	DefaultLogDir    = "./data/logs"
	DefaultLogLevel  = "info"
	DefaultPort      = 50051
	DefaultMaxRAM    = "4GB"
	DefaultDevice    = "auto"
)

func Load() (*Config, error) {
	cfg := defaultConfig()

	if err := loadFromFile(cfg); err != nil && !os.IsNotExist(err) {
		return nil, err
	}

	loadFromEnv(cfg)

	return cfg, nil
}

func Save(cfg *Config) error {
	path := filepath.Join(cfg.DataDir, "config.json")
	if err := os.MkdirAll(cfg.DataDir, 0755); err != nil {
		return err
	}
	f, err := os.Create(path)
	if err != nil {
		return err
	}
	defer f.Close()
	enc := json.NewEncoder(f)
	enc.SetIndent("", "  ")
	return enc.Encode(cfg)
}

func defaultConfig() *Config {
	return &Config{
		DataDir:   DefaultDataDir,
		ModelsDir: DefaultModelsDir,
		LogDir:    DefaultLogDir,
		LogLevel:  DefaultLogLevel,
		Port:      DefaultPort,
		MaxRAM:    DefaultMaxRAM,
		Device:    DefaultDevice,
	}
}

func loadFromFile(cfg *Config) error {
	path := filepath.Join(cfg.DataDir, "config.json")
	f, err := os.Open(path)
	if err != nil {
		return err
	}
	defer f.Close()
	return json.NewDecoder(f).Decode(cfg)
}

func loadFromEnv(cfg *Config) {
	if v := os.Getenv("ASHWATH_DATA_DIR"); v != "" {
		cfg.DataDir = v
	}
	if v := os.Getenv("ASHWATH_MODELS_DIR"); v != "" {
		cfg.ModelsDir = v
	}
	if v := os.Getenv("ASHWATH_LOG_DIR"); v != "" {
		cfg.LogDir = v
	}
	if v := os.Getenv("ASHWATH_LOG_LEVEL"); v != "" {
		cfg.LogLevel = v
	}
	if v := os.Getenv("ASHWATH_PORT"); v != "" {
		if port, err := strconv.Atoi(v); err == nil {
			cfg.Port = port
		}
	}
	if v := os.Getenv("ASHWATH_MAX_RAM"); v != "" {
		cfg.MaxRAM = v
	}
	if v := os.Getenv("ASHWATH_DEVICE"); v != "" {
		cfg.Device = v
	}
}

func (c *Config) Validate() error {
	if c.Port <= 0 || c.Port > 65535 {
		c.Port = DefaultPort
	}
	return nil
}
