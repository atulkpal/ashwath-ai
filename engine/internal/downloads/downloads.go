// Package downloads manages model and engine binary downloads
// with verification and resume support.
package downloads

import "context"

type Manager interface {
	Download(ctx context.Context, url string, dest string) error
	Verify(ctx context.Context, path string, checksum string) bool
	Cancel(id string)
}

type Progress struct {
	BytesDownloaded int64
	TotalBytes      int64
	Status          string
}
