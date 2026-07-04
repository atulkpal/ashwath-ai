package server

import (
	"context"
	"fmt"
	"net"
	"os"

	"github.com/ashwathai/ashwath-engine/internal/api"
	"github.com/ashwathai/ashwath-engine/internal/api/pb"
	"github.com/ashwathai/ashwath-engine/internal/config"
	"github.com/ashwathai/ashwath-engine/internal/device"
	"github.com/ashwathai/ashwath-engine/internal/downloads"
	"github.com/ashwathai/ashwath-engine/internal/logging"
	"github.com/ashwathai/ashwath-engine/internal/models"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"github.com/ashwathai/ashwath-engine/internal/runtime/llama"
	"google.golang.org/grpc"
)

type Options struct {
	EngineType string
	LlamaBin   string
	ModelPath  string
}

func Run(ctx context.Context, cfg *config.Config, opts Options) error {
	log := logging.New(os.Stdout, cfg.LogLevel)
	log.Info("Ashwath Engine starting",
		"version", "0.1.0",
		"port", cfg.Port,
		"data_dir", cfg.DataDir,
	)

	det := device.New()
	caps, err := det.Detect()
	if err != nil {
		log.Warn("device detection failed", "error", err)
	} else {
		log.Info("Device detected",
			"os", caps.OSType,
			"arch", caps.Arch,
			"cores", caps.CPUCores,
			"ram_gb", fmt.Sprintf("%.1f", caps.RAMGB),
		)
	}

	initOpts := runtime.Options{ModelPath: cfg.ModelsDir}

	var eng runtime.Engine
	switch opts.EngineType {
	case "llama":
		if opts.ModelPath == "" {
			return fmt.Errorf("--model is required when --engine=llama")
		}
		eng = llama.New(opts.LlamaBin)
		initOpts.ModelPath = opts.ModelPath
		log.Info("Using llama.cpp backend", "model", opts.ModelPath)
	default:
		eng = runtime.NewMock()
		log.Info("Using mock backend")
	}

	if err := eng.Initialize(ctx, initOpts); err != nil {
		return fmt.Errorf("engine initialization failed: %w", err)
	}
	log.Info("Engine initialized", "name", eng.Name())

	reg := models.NewRegistry(cfg.ModelsDir, downloads.NewManager())

	shutdownCh := make(chan struct{})
	api.SetShutdownChannel(shutdownCh)

	grpcServer := grpc.NewServer()
	svc := api.NewEngineService(eng, det, reg, log)
	pb.RegisterAshwathEngineServer(grpcServer, svc)

	addr := fmt.Sprintf(":%d", cfg.Port)
	listener, err := net.Listen("tcp", addr)
	if err != nil {
		return fmt.Errorf("failed to listen on %s: %w", addr, err)
	}

	log.Info("gRPC server listening", "addr", listener.Addr().String())

	errCh := make(chan error, 1)
	go func() {
		if err := grpcServer.Serve(listener); err != nil {
			errCh <- fmt.Errorf("gRPC server error: %w", err)
		}
	}()

	select {
	case <-ctx.Done():
		log.Info("Shutdown requested via context, shutting down...")
		grpcServer.GracefulStop()
		return ctx.Err()
	case <-shutdownCh:
		log.Info("Shutdown requested via gRPC, shutting down...")
		grpcServer.GracefulStop()
		return nil
	case err := <-errCh:
		return err
	}
}
