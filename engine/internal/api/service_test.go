package api

import (
	"context"
	"io"
	"net"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/ashwathai/ashwath-engine/internal/api/pb"
	"github.com/ashwathai/ashwath-engine/internal/device"
	"github.com/ashwathai/ashwath-engine/internal/downloads"
	"github.com/ashwathai/ashwath-engine/internal/logging"
	"github.com/ashwathai/ashwath-engine/internal/models"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/test/bufconn"
)

const bufSize = 1024 * 1024

func TestServiceListModels(t *testing.T) {
	srv, lis := newTestServer(t)
	defer srv.GracefulStop()
	go srv.Serve(lis)
	defer srv.Stop()

	conn := dialTest(t, lis)
	defer conn.Close()

	client := pb.NewAshwathEngineClient(conn)
	resp, err := client.ListModels(context.Background(), &pb.Empty{})
	if err != nil {
		t.Fatalf("ListModels failed: %v", err)
	}
	if len(resp.Models) != 4 {
		t.Errorf("got %d models, want 4", len(resp.Models))
	}
}

func TestServiceGetDeviceInfo(t *testing.T) {
	srv, lis := newTestServer(t)
	defer srv.GracefulStop()
	go srv.Serve(lis)
	defer srv.Stop()

	conn := dialTest(t, lis)
	defer conn.Close()

	client := pb.NewAshwathEngineClient(conn)
	resp, err := client.GetDeviceInfo(context.Background(), &pb.Empty{})
	if err != nil {
		t.Fatalf("GetDeviceInfo failed: %v", err)
	}
	if resp.Os == "" {
		t.Error("OS should not be empty")
	}
	if resp.CpuCores <= 0 {
		t.Errorf("CPUCores = %d, want > 0", resp.CpuCores)
	}
}

func TestServiceInstallModelMissing(t *testing.T) {
	srv, lis := newTestServer(t)
	defer srv.GracefulStop()
	go srv.Serve(lis)
	defer srv.Stop()

	conn := dialTest(t, lis)
	defer conn.Close()

	client := pb.NewAshwathEngineClient(conn)
	_, err := client.InstallModel(context.Background(), &pb.InstallRequest{ModelId: "nonexistent"})
	if err == nil {
		t.Fatal("InstallModel should fail for unknown model")
	}
}

func TestServiceGenerateStream(t *testing.T) {
	srv, lis := newTestServer(t)
	defer srv.GracefulStop()
	go srv.Serve(lis)
	defer srv.Stop()

	conn := dialTest(t, lis)
	defer conn.Close()

	client := pb.NewAshwathEngineClient(conn)
	stream, err := client.Generate(context.Background(), &pb.GenerateRequest{Prompt: "Hello", MaxTokens: 50})
	if err != nil {
		t.Fatalf("Generate failed: %v", err)
	}

	var fullText strings.Builder
	for {
		resp, err := stream.Recv()
		if err == io.EOF {
			break
		}
		if err != nil {
			t.Fatalf("Recv failed: %v", err)
		}
		fullText.WriteString(resp.Text)
	}
	if fullText.Len() == 0 {
		t.Fatal("Generate returned no text")
	}
}

func TestServiceShutdown(t *testing.T) {
	srv, lis := newTestServer(t)
	defer srv.GracefulStop()
	go srv.Serve(lis)
	defer srv.Stop()

	conn := dialTest(t, lis)
	defer conn.Close()

	ch := make(chan struct{})
	SetShutdownChannel(ch)

	client := pb.NewAshwathEngineClient(conn)
	_, err := client.Shutdown(context.Background(), &pb.Empty{})
	if err != nil {
		t.Fatalf("Shutdown failed: %v", err)
	}

	select {
	case <-ch:
	case <-time.After(time.Second):
		t.Fatal("Shutdown channel not closed")
	}
}

func TestTruncate(t *testing.T) {
	if s := truncate("hello world", 5); s != "hello..." {
		t.Errorf("truncate 'hello world' to 5 = %s, want 'hello...'", s)
	}
	if s := truncate("hello", 10); s != "hello" {
		t.Errorf("truncate 'hello' to 10 = %s, want 'hello'", s)
	}
	if s := truncate("", 5); s != "" {
		t.Errorf("truncate '' = %s, want ''", s)
	}
}

func newTestServer(t *testing.T) (*grpc.Server, *bufconn.Listener) {
	t.Helper()
	eng := runtime.NewMock()
	det := device.New()
	dir, err := os.MkdirTemp("", "ashwath-models-*")
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { os.RemoveAll(dir) })
	reg := models.NewRegistry(dir, downloads.NewManager())
	log := logging.NewDefault()

	svc := NewEngineService(eng, det, reg, log)
	srv := grpc.NewServer()
	pb.RegisterAshwathEngineServer(srv, svc)

	lis := bufconn.Listen(bufSize)
	return srv, lis
}

func dialTest(t *testing.T, lis *bufconn.Listener) *grpc.ClientConn {
	t.Helper()
	conn, err := grpc.DialContext(context.Background(), "bufnet",
		grpc.WithContextDialer(func(ctx context.Context, s string) (net.Conn, error) {
			return lis.Dial()
		}),
		grpc.WithTransportCredentials(insecure.NewCredentials()),
		grpc.WithBlock(),
	)
	if err != nil {
		t.Fatalf("Failed to dial bufnet: %v", err)
	}
	return conn
}
