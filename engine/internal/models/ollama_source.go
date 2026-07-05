package models

import (
	"encoding/json"
	"fmt"
	"os"
	"os/user"
	"path/filepath"
	"runtime"
	"strings"
)

type ollamaManifest struct {
	Config struct {
		Digest string `json:"digest"`
	} `json:"config"`
	Layers []struct {
		Digest string `json:"digest"`
		Size   int64  `json:"size"`
	} `json:"layers"`
}

type OllamaSource struct {
	models []Model
}

func NewOllamaSource() Source {
	s := &OllamaSource{}
	s.scan()
	return s
}

func (s *OllamaSource) List() ([]Model, error) {
	result := make([]Model, len(s.models))
	copy(result, s.models)
	return result, nil
}

func (s *OllamaSource) Get(id string) (*Model, error) {
	for _, m := range s.models {
		if m.ID == id {
			return &m, nil
		}
	}
	return nil, nil
}

func (s *OllamaSource) scan() {
	baseDir := ollamaBaseDir()
	if baseDir == "" {
		return
	}

	manifestsDir := filepath.Join(baseDir, "manifests", "registry.ollama.ai", "library")
	entries, err := os.ReadDir(manifestsDir)
	if err != nil {
		return
	}

	for _, entry := range entries {
		if !entry.IsDir() {
			continue
		}
		modelName := entry.Name()
		tagsDir := filepath.Join(manifestsDir, modelName)
		tags, err := os.ReadDir(tagsDir)
		if err != nil {
			continue
		}
		for _, tag := range tags {
			if tag.IsDir() {
				continue
			}
			tagName := tag.Name()
			manifest := s.readManifest(filepath.Join(tagsDir, tagName))
			if manifest == nil {
				continue
			}

			var totalSize int64
			for _, layer := range manifest.Layers {
				totalSize += layer.Size
			}

			blobPath := s.resolveBlob(baseDir, manifest.Config.Digest)
			installed := blobPath != "" && fileExists(blobPath)

			id := fmt.Sprintf("%s:%s", modelName, tagName)
			s.models = append(s.models, Model{
				ID:          id,
				Name:        fmt.Sprintf("%s (%s)", modelName, tagName),
				Provider:    "Ollama",
				Description: fmt.Sprintf("Ollama model: %s:%s", modelName, tagName),
				SizeBytes:   totalSize,
				Parameters:  "",
				Tags:        []string{"ollama", modelName},
				Installed:   installed,
				DownloadURL: "",
				Filename:    blobPath,
			})
		}
	}

	s.scanGGUFModels(baseDir)
}

func (s *OllamaSource) scanGGUFModels(baseDir string) {
	blobsDir := filepath.Join(baseDir, "blobs")
	entries, err := os.ReadDir(blobsDir)
	if err != nil {
		return
	}

	for _, entry := range entries {
		if entry.IsDir() {
			continue
		}
		name := entry.Name()
		if !strings.HasPrefix(name, "sha256-") {
			continue
		}
		blobPath := filepath.Join(blobsDir, name)
		if !isGGUFFile(blobPath) {
			continue
		}

		info, err := entry.Info()
		if err != nil {
			continue
		}

		shortName := strings.TrimPrefix(name, "sha256-")
		if len(shortName) > 12 {
			shortName = shortName[:12]
		}

		alreadyExists := false
		for _, m := range s.models {
			if m.Filename == blobPath {
				alreadyExists = true
				break
			}
		}
		if alreadyExists {
			continue
		}

		s.models = append(s.models, Model{
			ID:          "ollama-blob-" + shortName,
			Name:        fmt.Sprintf("Ollama Model (%s...)", shortName),
			Provider:    "Ollama",
			Description: "GGUF model from Ollama blobs directory",
			SizeBytes:   info.Size(),
			Parameters:  "",
			Tags:        []string{"ollama", "gguf"},
			Installed:   true,
			DownloadURL: "",
			Filename:    blobPath,
		})
	}
}

func (s *OllamaSource) readManifest(path string) *ollamaManifest {
	data, err := os.ReadFile(path)
	if err != nil {
		return nil
	}
	var m ollamaManifest
	if err := json.Unmarshal(data, &m); err != nil {
		return nil
	}
	return &m
}

func (s *OllamaSource) resolveBlob(baseDir, digest string) string {
	parts := strings.SplitN(digest, ":", 2)
	if len(parts) != 2 {
		return ""
	}
	return filepath.Join(baseDir, "blobs", parts[0]+"-"+parts[1])
}

func ollamaBaseDir() string {
	if dir := os.Getenv("OLLAMA_MODELS"); dir != "" {
		return dir
	}
	home := homeDir()
	if home == "" {
		return ""
	}
	candidates := []string{
		filepath.Join(home, ".ollama", "models"),
	}
	if runtime.GOOS == "windows" {
		candidates = append(candidates,
			filepath.Join(home, ".ollama", "models"),
		)
	}
	for _, dir := range candidates {
		if dirExists(dir) {
			return dir
		}
	}
	return ""
}

func homeDir() string {
	if u, err := user.Current(); err == nil {
		return u.HomeDir
	}
	if h := os.Getenv("HOME"); h != "" {
		return h
	}
	if h := os.Getenv("USERPROFILE"); h != "" {
		return h
	}
	return ""
}

func dirExists(path string) bool {
	info, err := os.Stat(path)
	return err == nil && info.IsDir()
}

func fileExists(path string) bool {
	_, err := os.Stat(path)
	return err == nil
}

func isGGUFFile(path string) bool {
	data := make([]byte, 4)
	f, err := os.Open(path)
	if err != nil {
		return false
	}
	defer f.Close()
	n, err := f.Read(data)
	if err != nil || n < 4 {
		return false
	}
	return string(data) == "GGUF"
}
