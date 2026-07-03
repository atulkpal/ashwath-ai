// Package rag implements Retrieval-Augmented Generation.
package rag

import "context"

type Retriever interface {
	Retrieve(ctx context.Context, query string, topK int) ([]Chunk, error)
}

type Chunk struct {
	Text     string
	Score    float64
	Document string
	Metadata map[string]string
}

type Generator interface {
	Generate(ctx context.Context, prompt string, context []Chunk) (string, error)
}
