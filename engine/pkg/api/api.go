// Package api defines the public API types for the Ashwath Engine.
// These types are used by SDK clients to communicate with the engine.
package api

type GenerateRequest struct {
	Model       string  `json:"model"`
	Prompt      string  `json:"prompt"`
	MaxTokens   int     `json:"max_tokens"`
	Temperature float32 `json:"temperature"`
	TopK        int     `json:"top_k"`
	TopP        float32 `json:"top_p"`
}

type GenerateResponse struct {
	Text       string `json:"text"`
	TokensUsed int    `json:"tokens_used"`
	Done       bool   `json:"done"`
}

type ModelInfo struct {
	ID          string   `json:"id"`
	Name        string   `json:"name"`
	Provider    string   `json:"provider"`
	SizeBytes   int64    `json:"size_bytes"`
	Parameters  string   `json:"parameters"`
	Tags        []string `json:"tags"`
	Installed   bool     `json:"installed"`
	Downloading bool     `json:"downloading"`
	Progress    float32  `json:"progress"`
}

type DeviceInfo struct {
	RAMGB     float64 `json:"ram_gb"`
	CPUCores  int     `json:"cpu_cores"`
	HasNPU    bool    `json:"has_npu"`
	HasGPU    bool    `json:"has_gpu"`
	GPUVendor string  `json:"gpu_vendor"`
	OS        string  `json:"os"`
	Arch      string  `json:"arch"`
}

type ErrorResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}
