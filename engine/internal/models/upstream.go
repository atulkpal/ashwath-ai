package models

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

const defaultIndexURL = "https://raw.githubusercontent.com/atulkpal/ashwath-ai/main/models/index.json"

type ModelIndex struct {
	Version int           `json:"version"`
	Updated string        `json:"updated"`
	Models  []IndexEntry  `json:"models"`
}

type IndexEntry struct {
	ID               string   `json:"id"`
	Name             string   `json:"name"`
	Provider         string   `json:"provider"`
	Description      string   `json:"description"`
	SizeBytes        int64    `json:"sizeBytes"`
	Parameters       string   `json:"parameters"`
	Tags             []string `json:"tags"`
	MinRamGB         float64  `json:"minRamGB"`
	RecommendedRamGB float64  `json:"recommendedRamGB"`
	Capabilities     []string `json:"capabilities"`
	SpeedClass       string   `json:"speedClass"`
	Sources          []string `json:"sources"`
	Filename         string   `json:"filename"`
}

func FetchModelIndex(url string) (*ModelIndex, error) {
	if url == "" {
		url = defaultIndexURL
	}

	client := &http.Client{}
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, fmt.Errorf("create request: %w", err)
	}
	req.Header.Set("User-Agent", "AshwathAI/0.1")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("http get: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("http %d", resp.StatusCode)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("read body: %w", err)
	}

	var index ModelIndex
	if err := json.Unmarshal(body, &index); err != nil {
		return nil, fmt.Errorf("parse json: %w", err)
	}

	return &index, nil
}

func LoadCachedIndex(cachePath string) (*ModelIndex, error) {
	f, err := os.Open(cachePath)
	if err != nil {
		return nil, err
	}
	defer f.Close()

	var index ModelIndex
	if err := json.NewDecoder(f).Decode(&index); err != nil {
		return nil, err
	}
	return &index, nil
}

func SaveCachedIndex(cachePath string, index *ModelIndex) error {
	if err := os.MkdirAll(filepath.Dir(cachePath), 0755); err != nil {
		return err
	}
	f, err := os.Create(cachePath)
	if err != nil {
		return err
	}
	defer f.Close()
	enc := json.NewEncoder(f)
	enc.SetIndent("", "  ")
	return enc.Encode(index)
}

func IndexToModels(index *ModelIndex) []Model {
	models := make([]Model, 0, len(index.Models))
	for _, e := range index.Models {
		downloadURL := ""
		if len(e.Sources) > 0 {
			downloadURL = e.Sources[0]
		}
		models = append(models, Model{
			ID:               e.ID,
			Name:             e.Name,
			Provider:         e.Provider,
			Description:      e.Description,
			SizeBytes:        e.SizeBytes,
			Parameters:       e.Parameters,
			Tags:             e.Tags,
			Installed:        false,
			DownloadURL:      downloadURL,
			Filename:         e.Filename,
			MinRamGB:         e.MinRamGB,
			RecommendedRamGB: e.RecommendedRamGB,
			Capabilities:     e.Capabilities,
			SpeedClass:       SpeedClass(e.SpeedClass),
		})
	}
	return models
}

func CatalogFromUpstream(url, cachePath string) []Model {
	index, err := FetchModelIndex(url)
	if err == nil && index != nil {
		if cachePath != "" {
			SaveCachedIndex(cachePath, index)
		}
		return IndexToModels(index)
	}

	if cachePath != "" {
		if cached, err := LoadCachedIndex(cachePath); err == nil && cached != nil {
			return IndexToModels(cached)
		}
	}

	return catalogModels()
}
