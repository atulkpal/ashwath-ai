// Package vision provides image processing and understanding capabilities.
package vision

import "io"

type Processor interface {
	Describe(image io.Reader) (string, error)
	Embed(image io.Reader) ([]float32, error)
}
