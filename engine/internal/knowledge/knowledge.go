// Package knowledge provides local knowledge base management.
package knowledge

import "context"

type Document struct {
	ID      string
	Title   string
	Content string
	Source  string
}

type Store interface {
	Index(ctx context.Context, docs []Document) error
	Search(ctx context.Context, query string, limit int) ([]Document, error)
	Delete(ctx context.Context, id string) error
}
