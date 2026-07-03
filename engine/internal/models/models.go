// Package models manages the model registry and lifecycle.
package models

type Model struct {
	ID          string
	Name        string
	Provider    string
	Description string
	SizeBytes   int64
	Parameters  string
	Tags        []string
	Installed   bool
}

type Registry interface {
	List() ([]Model, error)
	Get(id string) (*Model, error)
	Install(id string) error
	Remove(id string) error
}
