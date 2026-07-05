// Package models manages the model registry and lifecycle.
package models

import "github.com/ashwathai/ashwath-engine/internal/bus"

type Model struct {
	ID             string
	Name           string
	Provider       string
	Description    string
	SizeBytes      int64
	Parameters     string
	Tags           []string
	Installed      bool
	DownloadURL    string
	ChecksumSHA256 string
	Filename       string
}

type Registry interface {
	List() ([]Model, error)
	Get(id string) (*Model, error)
	Install(id string) error
	Remove(id string) error
	ModelsDir() string
}

type RegistryOption func(*registry)

func WithBus(b bus.Bus) RegistryOption {
	return func(r *registry) {
		r.eventBus = b
	}
}

func WithSource(s Source) RegistryOption {
	return func(r *registry) {
		r.source = s
	}
}
