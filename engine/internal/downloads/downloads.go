// Package downloads manages model downloads with progress tracking and checksum verification.
package downloads

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"sync"
)

type Progress struct {
	BytesDownloaded int64
	TotalBytes      int64
	Status          string
}

type ProgressCallback func(Progress)

type Manager struct {
	client *http.Client
	active map[string]context.CancelFunc
	mu     sync.Mutex
}

func NewManager() *Manager {
	return &Manager{
		client: &http.Client{},
		active: make(map[string]context.CancelFunc),
	}
}

func (m *Manager) Download(ctx context.Context, url, dest string, cb ProgressCallback) error {
	if err := os.MkdirAll(filepath.Dir(dest), 0755); err != nil {
		return fmt.Errorf("create download dir: %w", err)
	}

	ctx, cancel := context.WithCancel(ctx)
	m.mu.Lock()
	m.active[dest] = cancel
	m.mu.Unlock()

	defer func() {
		m.mu.Lock()
		delete(m.active, dest)
		m.mu.Unlock()
		cancel()
	}()

	if cb != nil {
		cb(Progress{Status: "connecting"})
	}

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return fmt.Errorf("create request: %w", err)
	}

	resp, err := m.client.Do(req)
	if err != nil {
		return fmt.Errorf("http get: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("http %d: %s", resp.StatusCode, resp.Status)
	}

	totalBytes := resp.ContentLength
	var downloaded int64

	tmpDest := dest + ".tmp"
	f, err := os.Create(tmpDest)
	if err != nil {
		return fmt.Errorf("create tmp file: %w", err)
	}
	defer f.Close()

	buf := make([]byte, 32*1024)
	for {
		select {
		case <-ctx.Done():
			f.Close()
			os.Remove(tmpDest)
			return ctx.Err()
		default:
		}

		n, readErr := resp.Body.Read(buf)
		if n > 0 {
			if _, writeErr := f.Write(buf[:n]); writeErr != nil {
				return fmt.Errorf("write: %w", writeErr)
			}
			downloaded += int64(n)
			if cb != nil && totalBytes > 0 {
				cb(Progress{
					BytesDownloaded: downloaded,
					TotalBytes:      totalBytes,
					Status:          "downloading",
				})
			}
		}
		if readErr == io.EOF {
			break
		}
		if readErr != nil {
			return fmt.Errorf("read: %w", readErr)
		}
	}

	f.Close()

	if err := os.Rename(tmpDest, dest); err != nil {
		return fmt.Errorf("rename: %w", err)
	}

	if cb != nil {
		cb(Progress{
			BytesDownloaded: downloaded,
			TotalBytes:      downloaded,
			Status:          "complete",
		})
	}

	return nil
}

func (m *Manager) Verify(path, expectedChecksum string) bool {
	if expectedChecksum == "" {
		return true
	}
	f, err := os.Open(path)
	if err != nil {
		return false
	}
	defer f.Close()

	h := sha256.New()
	if _, err := io.Copy(h, f); err != nil {
		return false
	}
	got := hex.EncodeToString(h.Sum(nil))
	return got == expectedChecksum
}

func (m *Manager) Cancel(id string) {
	m.mu.Lock()
	defer m.mu.Unlock()
	if cancel, ok := m.active[id]; ok {
		cancel()
	}
}
