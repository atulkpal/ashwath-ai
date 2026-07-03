// Package config manages engine configuration.
package config

type Config struct {
	DataDir    string
	ModelsDir  string
	LogDir     string
	LogLevel   string
	Port       int
	MaxRAM     string
	Device     string
}

type Loader interface {
	Load() (*Config, error)
	Save(cfg *Config) error
}
