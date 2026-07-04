// Package benchmark measures model and engine performance on device.
package benchmark

import (
	"context"
	"fmt"
	"runtime"
	"time"

	stubrunt "github.com/ashwathai/ashwath-engine/internal/runtime"
)

type Config struct {
	Prompt    string
	MaxTokens int
	NumRuns   int
}

func DefaultConfig() Config {
	return Config{
		Prompt:    "What is the capital of France?",
		MaxTokens: 128,
		NumRuns:   3,
	}
}

type Result struct {
	ModelID       string
	TokensPerSec  float64
	LatencyMs     float64
	LoadTimeMs    int64
	TotalTokens   int
	MemoryUsageMB float64
	Hardware      string
}

type Runner struct {
	eng    stubrunt.Engine
	config Config
}

func NewRunner(eng stubrunt.Engine, config Config) *Runner {
	return &Runner{eng: eng, config: config}
}

func (r *Runner) Run(ctx context.Context, modelID string) (*Result, error) {
	beforeMem := allocMB()
	hw := fmt.Sprintf("%s/%s", runtime.GOOS, runtime.GOARCH)

	loadStart := time.Now()
	initOpts := stubrunt.Options{
		ModelPath: modelID,
		Device:    "auto",
	}
	if err := r.eng.Initialize(ctx, initOpts); err != nil {
		return nil, fmt.Errorf("init: %w", err)
	}
	loadTime := time.Since(loadStart).Milliseconds()
	defer r.eng.Stop(ctx)

	var totalTokens int
	var totalDuration time.Duration

	for i := 0; i < r.config.NumRuns; i++ {
		req := stubrunt.Request{
			Prompt:     r.config.Prompt,
			MaxTokens:  r.config.MaxTokens,
			Temperature: 0.0,
			TopK:       40,
			TopP:       0.9,
		}

		ch, err := r.eng.Generate(ctx, req)
		if err != nil {
			return nil, fmt.Errorf("generate run %d: %w", i, err)
		}

		runStart := time.Now()
		var runTokens int
		for res := range ch {
			if res.Error != nil {
				return nil, fmt.Errorf("generate error run %d: %w", i, res.Error)
			}
			if res.Done {
				break
			}
			runTokens++
		}
		runDuration := time.Since(runStart)

		totalTokens += runTokens
		totalDuration += runDuration
	}

	if totalTokens == 0 {
		return nil, fmt.Errorf("no tokens generated")
	}

	avgLatency := totalDuration.Seconds() * 1000 / float64(totalTokens)
	tokensPerSec := float64(totalTokens) / totalDuration.Seconds()
	afterMem := allocMB()
	memUsed := afterMem - beforeMem
	if memUsed < 0 {
		memUsed = 0
	}

	return &Result{
		ModelID:       modelID,
		TokensPerSec:  tokensPerSec,
		LatencyMs:     avgLatency,
		LoadTimeMs:    loadTime,
		TotalTokens:   totalTokens,
		MemoryUsageMB: memUsed,
		Hardware:      hw,
	}, nil
}

func allocMB() float64 {
	var m runtime.MemStats
	runtime.ReadMemStats(&m)
	return float64(m.Alloc) / 1024 / 1024
}


