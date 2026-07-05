package server

import (
	"context"
	"fmt"
	"net"
	"os"
	"path/filepath"

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

	engType := opts.EngineType
	if engType == "" {
		engType = "llama"
	}
	log.Info("Engine type selected", "type", engType)

	downloader := downloads.NewManager()
	reg := models.NewRegistry(cfg.ModelsDir, downloader)

	resolvedModelPath := opts.ModelPath
	if engType == "llama" && resolvedModelPath == "" {
		mdls, err := reg.List()
		if err != nil {
			return fmt.Errorf("list models: %w", err)
		}

		var installedModel *models.Model
		for _, m := range mdls {
			if m.Installed {
				installedModel = &m
				break
			}
		}

		if installedModel == nil && len(mdls) > 0 {
			m := mdls[0]
			log.Info("No installed model found, installing default", "model_id", m.ID)
			if err := reg.Install(m.ID); err != nil {
				return fmt.Errorf("install default model %s: %w", m.ID, err)
			}

			updated, err := reg.Get(m.ID)
			if err != nil {
				return fmt.Errorf("get installed model %s: %w", m.ID, err)
			}
			installedModel = updated
		}

		if installedModel != nil {
			resolvedModelPath = filepath.Join(cfg.ModelsDir, installedModel.ID, installedModel.Filename)
			log.Info("Using installed model", "path", resolvedModelPath)
		}
	}

	llama.Register()

	initOpts := runtime.Options{
		ModelPath:  resolvedModelPath,
		BinaryPath: opts.LlamaBin,
	}

	eng, err := runtime.CreateEngine(ctx, engType, initOpts)
	if err != nil {
		return fmt.Errorf("create engine: %w", err)
	}
	log.Info("Engine initialized", "name", eng.Name(), "type", engType)

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
