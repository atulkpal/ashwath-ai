// Package voice provides speech-to-text and text-to-speech capabilities.
package voice

import "io"

type STT interface {
	Transcribe(audio io.Reader) (string, error)
}

type TTS interface {
	Synthesize(text string) (io.Reader, error)
}
