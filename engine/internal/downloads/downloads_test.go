package downloads

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"net/http"
	"net/http/httptest"
	"os"
	"path/filepath"
	"sync"
	"sync/atomic"
	"testing"
)

func TestNewManager(t *testing.T) {
	m := NewManager()
	if m == nil {
		t.Fatal("NewManager() returned nil")
	}
}

func TestDownloadSuccess(t *testing.T) {
	content := []byte("hello world download test")
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write(content)
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "model.bin")

	var progress []Progress
	var mu sync.Mutex

	err := m.Download(context.Background(), srv.URL, dest, func(p Progress) {
		mu.Lock()
		progress = append(progress, p)
		mu.Unlock()
	})
	if err != nil {
		t.Fatalf("Download failed: %v", err)
	}

	data, err := os.ReadFile(dest)
	if err != nil {
		t.Fatalf("read dest: %v", err)
	}
	if string(data) != string(content) {
		t.Errorf("got %q, want %q", string(data), string(content))
	}

	mu.Lock()
	if len(progress) == 0 {
		t.Error("expected at least one progress callback")
	}
	if progress[0].Status != "connecting" {
		t.Errorf("first progress status = %q, want %q", progress[0].Status, "connecting")
	}
	last := progress[len(progress)-1]
	if last.Status != "complete" {
		t.Errorf("last progress status = %q, want %q", last.Status, "complete")
	}
	if last.BytesDownloaded != int64(len(content)) {
		t.Errorf("downloaded %d bytes, want %d", last.BytesDownloaded, len(content))
	}
	mu.Unlock()
}

func TestDownloadCreatesParentDirs(t *testing.T) {
	content := []byte("nested dir test")
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write(content)
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "sub", "deep", "model.bin")

	err := m.Download(context.Background(), srv.URL, dest, nil)
	if err != nil {
		t.Fatalf("Download failed: %v", err)
	}

	if _, err := os.Stat(dest); os.IsNotExist(err) {
		t.Error("destination file was not created")
	}
}

func TestDownloadHTTPError(t *testing.T) {
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusNotFound)
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "model.bin")

	err := m.Download(context.Background(), srv.URL, dest, nil)
	if err == nil {
		t.Fatal("expected error for HTTP 404")
	}
}

func TestDownloadContextCancellation(t *testing.T) {
	ctx, cancel := context.WithCancel(context.Background())
	cancel()

	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("data"))
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "model.bin")

	err := m.Download(ctx, srv.URL, dest, nil)
	if err == nil {
		t.Fatal("expected cancellation error")
	}
}

func TestDownloadCancelDuringTransfer(t *testing.T) {
	ctx, cancel := context.WithCancel(context.Background())

	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		for i := 0; i < 100; i++ {
			w.Write([]byte("blocking data for cancellation test\n"))
		}
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "model.bin")

	cancel()

	err := m.Download(ctx, srv.URL, dest, func(p Progress) {
		if p.Status == "downloading" {
		}
	})
	if err == nil {
		t.Fatal("expected error after cancellation")
	}
}

func TestVerifyMatch(t *testing.T) {
	content := []byte("checksum verification data")
	h := sha256.Sum256(content)
	checksum := hex.EncodeToString(h[:])

	tmp := filepath.Join(t.TempDir(), "model.bin")
	os.WriteFile(tmp, content, 0644)

	m := NewManager()
	if !m.Verify(tmp, checksum) {
		t.Error("Verify returned false for matching checksum")
	}
}

func TestVerifyMismatch(t *testing.T) {
	content := []byte("original content")
	wrongChecksum := "0000000000000000000000000000000000000000000000000000000000000000"

	tmp := filepath.Join(t.TempDir(), "model.bin")
	os.WriteFile(tmp, content, 0644)

	m := NewManager()
	if m.Verify(tmp, wrongChecksum) {
		t.Error("Verify returned true for mismatching checksum")
	}
}

func TestVerifyEmptyChecksum(t *testing.T) {
	content := []byte("any content")
	tmp := filepath.Join(t.TempDir(), "model.bin")
	os.WriteFile(tmp, content, 0644)

	m := NewManager()
	if !m.Verify(tmp, "") {
		t.Error("Verify returned false for empty checksum")
	}
}

func TestVerifyNonexistentFile(t *testing.T) {
	m := NewManager()
	tmp := filepath.Join(t.TempDir(), "nonexistent.bin")
	if m.Verify(tmp, "anything") {
		t.Error("Verify returned true for nonexistent file")
	}
}

func TestCancel(t *testing.T) {
	m := NewManager()
	m.Cancel("nonexistent")
}

func TestDownloadProgressBytes(t *testing.T) {
	content := make([]byte, 65536)
	for i := range content {
		content[i] = byte(i % 256)
	}

	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Length", "65536")
		w.Write(content)
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "model.bin")

	var totalDownloaded atomic.Int64
	err := m.Download(context.Background(), srv.URL, dest, func(p Progress) {
		if p.Status == "downloading" {
			totalDownloaded.Store(p.BytesDownloaded)
		}
	})
	if err != nil {
		t.Fatalf("Download failed: %v", err)
	}
	if totalDownloaded.Load() != 65536 {
		t.Errorf("totalDownloaded = %d, want 65536", totalDownloaded.Load())
	}
}

func TestConcurrentDownloads(t *testing.T) {
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("concurrent content"))
	}))
	defer srv.Close()

	m := NewManager()
	var wg sync.WaitGroup

	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func(idx int) {
			defer wg.Done()
			dest := filepath.Join(t.TempDir(), "concurrent", "file.bin")
			err := m.Download(context.Background(), srv.URL, dest, nil)
			if err != nil {
				t.Errorf("concurrent download %d failed: %v", idx, err)
			}
		}(i)
	}
	wg.Wait()
}

func TestDownloadEmptyContent(t *testing.T) {
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte{})
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "empty.bin")

	err := m.Download(context.Background(), srv.URL, dest, nil)
	if err != nil {
		t.Fatalf("Download failed: %v", err)
	}

	data, err := os.ReadFile(dest)
	if err != nil {
		t.Fatalf("read dest: %v", err)
	}
	if len(data) != 0 {
		t.Errorf("expected empty file, got %d bytes", len(data))
	}
}

func TestCancelIdempotent(t *testing.T) {
	m := NewManager()
	m.Cancel("nonexistent-id")
}

func TestDownloadNoCallback(t *testing.T) {
	content := []byte("no callback test")
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Write(content)
	}))
	defer srv.Close()

	m := NewManager()
	dest := filepath.Join(t.TempDir(), "model.bin")

	err := m.Download(context.Background(), srv.URL, dest, nil)
	if err != nil {
		t.Fatalf("Download failed: %v", err)
	}

	data, err := os.ReadFile(dest)
	if err != nil {
		t.Fatalf("read dest: %v", err)
	}
	if string(data) != string(content) {
		t.Errorf("got %q, want %q", string(data), string(content))
	}
}
