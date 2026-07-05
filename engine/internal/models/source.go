package models

type Source interface {
	List() ([]Model, error)
	Get(id string) (*Model, error)
}

type BuiltinSource struct {
	models []Model
}

func NewBuiltinSource() Source {
	return &BuiltinSource{models: defaultModels()}
}

func (s *BuiltinSource) List() ([]Model, error) {
	result := make([]Model, len(s.models))
	copy(result, s.models)
	return result, nil
}

func (s *BuiltinSource) Get(id string) (*Model, error) {
	for _, m := range s.models {
		if m.ID == id {
			return &m, nil
		}
	}
	return nil, nil
}
