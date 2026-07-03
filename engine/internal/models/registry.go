package models

type registry struct {
	models []Model
}

func NewRegistry() Registry {
	return &registry{
		models: defaultModels(),
	}
}

func (r *registry) List() ([]Model, error) {
	return r.models, nil
}

func (r *registry) Get(id string) (*Model, error) {
	for _, m := range r.models {
		if m.ID == id {
			return &m, nil
		}
	}
	return nil, nil
}

func (r *registry) Install(id string) error {
	return nil
}

func (r *registry) Remove(id string) error {
	return nil
}

func defaultModels() []Model {
	return []Model{
		{
			ID:          "gemma-3-4b",
			Name:        "Gemma 3 4B",
			Provider:    "Google",
			Description: "Lightweight, state-of-the-art open model from Google.",
			SizeBytes:   2_800_000_000,
			Parameters:  "4B",
			Tags:        []string{"General", "Efficient"},
			Installed:   true,
		},
		{
			ID:          "phi-4-mini",
			Name:        "Phi-4 Mini",
			Provider:    "Microsoft",
			Description: "Extremely capable small language model.",
			SizeBytes:   2_100_000_000,
			Parameters:  "3.8B",
			Tags:        []string{"Reasoning", "Coding"},
			Installed:   false,
		},
		{
			ID:          "llama-3.2-3b",
			Name:        "Llama 3.2 3B",
			Provider:    "Meta",
			Description: "Optimized for mobile and edge devices.",
			SizeBytes:   2_500_000_000,
			Parameters:  "3B",
			Tags:        []string{"Balanced", "Chat"},
			Installed:   true,
		},
		{
			ID:          "qwen-2.5-3b",
			Name:        "Qwen 2.5 3B",
			Provider:    "Alibaba",
			Description: "High performance multilingual model.",
			SizeBytes:   3_100_000_000,
			Parameters:  "3B",
			Tags:        []string{"Multilingual"},
			Installed:   false,
		},
	}
}
