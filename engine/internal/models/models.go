package models

import "github.com/ashwathai/ashwath-engine/internal/bus"

type SpeedClass string

const (
	SpeedFast   SpeedClass = "fast"
	SpeedMedium SpeedClass = "medium"
	SpeedSlow   SpeedClass = "slow"
)

type Model struct {
	ID               string
	Name             string
	Provider         string
	Description      string
	SizeBytes        int64
	Parameters       string
	Tags             []string
	Installed        bool
	DownloadURL      string
	ChecksumSHA256   string
	Filename         string
	MinRamGB         float64
	RecommendedRamGB float64
	Capabilities     []string
	SpeedClass       SpeedClass
}

type ScoredModel struct {
	Model
	Score       int
	Recommended bool
	Reason      string
}

type DeviceSpec struct {
	RamGB    float64
	CPUCores int
	HasNPU   bool
	HasGPU   bool
	GPUVendor string
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
	return func(r *registry) { r.eventBus = b }
}

func WithSource(s Source) RegistryOption {
	return func(r *registry) { r.source = s }
}

func WithOllamaSource() RegistryOption {
	return func(r *registry) {
		ollama := NewOllamaSource()
		ollamaModels, _ := ollama.List()
		existing := make(map[string]bool)
		for _, m := range r.models {
			existing[m.ID] = true
		}
		for _, m := range ollamaModels {
			if !existing[m.ID] {
				r.models = append(r.models, m)
				existing[m.ID] = true
			}
		}
	}
}

func mergeSources(base, extra []Model) []Model {
	seen := make(map[string]bool)
	var merged []Model
	for _, m := range base {
		seen[m.ID] = true
		merged = append(merged, m)
	}
	for _, m := range extra {
		if !seen[m.ID] {
			merged = append(merged, m)
		}
	}
	return merged
}
