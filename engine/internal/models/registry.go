package models

import (
	"context"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"

	"github.com/ashwathai/ashwath-engine/internal/bus"
	"github.com/ashwathai/ashwath-engine/internal/downloads"
)

const installedStateFile = ".installed.json"

type registry struct {
	modelsDir  string
	models     []Model
	downloader *downloads.Manager
	source     Source
	eventBus   bus.Bus
}

func NewRegistry(modelsDir string, downloader *downloads.Manager, opts ...RegistryOption) Registry {
	r := &registry{
		modelsDir:  modelsDir,
		downloader: downloader,
		source:     NewBuiltinSource(),
	}
	for _, opt := range opts {
		opt(r)
	}
	r.loadFromSource()
	r.scanInstalled()
	return r
}

func (r *registry) ModelsDir() string { return r.modelsDir }

func (r *registry) List() ([]Model, error) {
	result := make([]Model, len(r.models))
	copy(result, r.models)
	return result, nil
}

func (r *registry) Get(id string) (*Model, error) {
	for _, m := range r.models {
		if m.ID == id {
			return &m, nil
		}
	}
	return nil, fmt.Errorf("model %q not found", id)
}

func (r *registry) Install(id string) error {
	m, err := r.Get(id)
	if err != nil {
		return err
	}
	if m.Installed {
		return nil
	}
	if m.DownloadURL == "" {
		return fmt.Errorf("model %q has no download URL configured", id)
	}

	modelDir := filepath.Join(r.modelsDir, m.ID)
	if err := os.MkdirAll(modelDir, 0755); err != nil {
		return fmt.Errorf("create model dir: %w", err)
	}

	dest := filepath.Join(modelDir, m.Filename)

	if err := r.downloader.Download(context.Background(), m.DownloadURL, dest, nil); err != nil {
		os.RemoveAll(modelDir)
		return fmt.Errorf("download %s: %w", m.ID, err)
	}

	if m.ChecksumSHA256 != "" {
		if !r.downloader.Verify(dest, m.ChecksumSHA256) {
			os.RemoveAll(modelDir)
			return fmt.Errorf("checksum mismatch for %s", m.ID)
		}
	}

	for i := range r.models {
		if r.models[i].ID == id {
			r.models[i].Installed = true
			break
		}
	}

	if err := r.saveState(); err != nil {
		return err
	}

	r.emitEvent(bus.TopicModelInstalled, map[string]string{"id": id})
	return nil
}

func (r *registry) Remove(id string) error {
	m, err := r.Get(id)
	if err != nil {
		return err
	}
	if !m.Installed {
		return nil
	}

	modelDir := filepath.Join(r.modelsDir, m.ID)
	if err := os.RemoveAll(modelDir); err != nil {
		return fmt.Errorf("remove model dir: %w", err)
	}

	for i := range r.models {
		if r.models[i].ID == id {
			r.models[i].Installed = false
			break
		}
	}

	if err := r.saveState(); err != nil {
		return err
	}

	r.emitEvent(bus.TopicModelRemoved, map[string]string{"id": id})
	return nil
}

func (r *registry) loadFromSource() {
	models, err := r.source.List()
	if err != nil {
		return
	}
	r.models = models
}

func (r *registry) scanInstalled() {
	state, err := r.loadState()
	if err != nil {
		return
	}
	for i := range r.models {
		id := r.models[i].ID
		r.models[i].Installed = state[id]
	}
}

func (r *registry) emitEvent(topic string, payload any) {
	if r.eventBus != nil {
		r.eventBus.Publish(topic, payload)
	}
}

type installedState map[string]bool

func (r *registry) statePath() string {
	return filepath.Join(r.modelsDir, installedStateFile)
}

func (r *registry) loadState() (installedState, error) {
	path := r.statePath()
	f, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer f.Close()
	var state installedState
	if err := json.NewDecoder(f).Decode(&state); err != nil {
		return nil, err
	}
	return state, nil
}

func (r *registry) saveState() error {
	state := make(installedState)
	for _, m := range r.models {
		state[m.ID] = m.Installed
	}
	if err := os.MkdirAll(r.modelsDir, 0755); err != nil {
		return err
	}
	path := r.statePath()
	f, err := os.Create(path)
	if err != nil {
		return err
	}
	defer f.Close()
	enc := json.NewEncoder(f)
	enc.SetIndent("", "  ")
	return enc.Encode(state)
}

func defaultModels() []Model {
	return []Model{
		{
			ID:             "gemma-3-4b",
			Name:           "Gemma 3 4B",
			Provider:       "Google",
			Description:    "Lightweight, state-of-the-art open model from Google.",
			SizeBytes:      2_800_000_000,
			Parameters:     "4B",
			Tags:           []string{"General", "Efficient"},
			DownloadURL:    "https://huggingface.co/google/gemma-3-4b-it-gguf/resolve/main/gemma-3-4b-it-Q4_K_M.gguf",
			Filename:       "model.gguf",
			ChecksumSHA256: "",
		},
		{
			ID:             "phi-4-mini",
			Name:           "Phi-4 Mini",
			Provider:       "Microsoft",
			Description:    "Extremely capable small language model.",
			SizeBytes:      2_100_000_000,
			Parameters:     "3.8B",
			Tags:           []string{"Reasoning", "Coding"},
			DownloadURL:    "https://huggingface.co/microsoft/Phi-4-mini-instruct-gguf/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf",
			Filename:       "model.gguf",
			ChecksumSHA256: "",
		},
		{
			ID:             "llama-3.2-3b",
			Name:           "Llama 3.2 3B",
			Provider:       "Meta",
			Description:    "Optimized for mobile and edge devices.",
			SizeBytes:      2_500_000_000,
			Parameters:     "3B",
			Tags:           []string{"Balanced", "Chat"},
			DownloadURL:    "https://huggingface.co/meta-llama/Llama-3.2-3B-Instruct-gguf/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
			Filename:       "model.gguf",
			ChecksumSHA256: "",
		},
		{
			ID:             "qwen-2.5-3b",
			Name:           "Qwen 2.5 3B",
			Provider:       "Alibaba",
			Description:    "High performance multilingual model.",
			SizeBytes:      3_100_000_000,
			Parameters:     "3B",
			Tags:           []string{"Multilingual"},
			DownloadURL:    "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-gguf/resolve/main/Qwen2.5-3B-Instruct-Q4_K_M.gguf",
			Filename:       "model.gguf",
			ChecksumSHA256: "",
		},
	}
}
