// Package models manages the model registry and lifecycle.
package models

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
	Filename       string // GGUF filename within the model directory
}

type Registry interface {
	List() ([]Model, error)
	Get(id string) (*Model, error)
	Install(id string) error
	Remove(id string) error
	ModelsDir() string
}
