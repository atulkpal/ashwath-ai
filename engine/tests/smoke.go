// Smoke test for the engine gRPC server.
// Run with: cd engine && go run tests/smoke.go
package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"time"

	"github.com/ashwathai/ashwath-engine/internal/api/pb"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

func main() {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	conn, err := grpc.DialContext(ctx, "localhost:9750",
		grpc.WithTransportCredentials(insecure.NewCredentials()),
		grpc.WithBlock(),
	)
	if err != nil {
		log.Fatalf("Failed to connect: %v", err)
	}
	defer conn.Close()

	client := pb.NewAshwathEngineClient(conn)

	// Test ListModels
	{
		resp, err := client.ListModels(ctx, &pb.Empty{})
		if err != nil {
			log.Fatalf("ListModels failed: %v", err)
		}
		fmt.Printf("PASS ListModels: %d models\n", len(resp.Models))
		for _, m := range resp.Models {
			fmt.Printf("       - %s (%s, %s)\n", m.Name, m.Provider, m.Parameters)
		}
	}

	// Test GetDeviceInfo
	{
		resp, err := client.GetDeviceInfo(ctx, &pb.Empty{})
		if err != nil {
			log.Fatalf("GetDeviceInfo failed: %v", err)
		}
		fmt.Printf("PASS GetDeviceInfo: OS=%s Arch=%s Cores=%d RAM=%.1fGB\n",
			resp.Os, resp.Arch, resp.CpuCores, resp.RamGb)
	}

	// Test Generate (streaming)
	{
		stream, err := client.Generate(ctx, &pb.GenerateRequest{
			Prompt:      "Hello!",
			MaxTokens:   50,
			Temperature: 0.7,
		})
		if err != nil {
			log.Fatalf("Generate failed: %v", err)
		}
		var fullText string
		for {
			resp, err := stream.Recv()
			if err == io.EOF {
				break
			}
			if err != nil {
				log.Fatalf("Generate recv failed: %v", err)
			}
			fullText += resp.Text
		}
		if fullText == "" {
			log.Fatalf("Generate returned empty response")
		}
		fmt.Printf("PASS Generate: %d chars received\n", len(fullText))
		fmt.Printf("       Preview: %s\n", truncate(fullText, 80))
	}

	// Test InstallModel
	{
		resp, err := client.InstallModel(ctx, &pb.InstallRequest{ModelId: "llama-3.2-3b"})
		if err != nil {
			log.Fatalf("InstallModel failed: %v", err)
		}
		fmt.Printf("PASS InstallModel: started=%v msg=%s\n", resp.Started, resp.Message)
	}

	fmt.Println("\nAll smoke tests PASSED!")
}

func truncate(s string, max int) string {
	if len(s) <= max {
		return s
	}
	return s[:max] + "..."
}
