package main

import (
	"context"
	"flag"
	"fmt"
	"net"
	"os"
	"os/signal"
	"syscall"

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

func main() {
	port := flag.Int("port", 0, "gRPC server port (overrides ASHWATH_PORT)")
	dataDir := flag.String("data-dir", "", "data directory (overrides ASHWATH_DATA_DIR)")
	logLevel := flag.String("log-level", "", "log level: debug, info, warn, error (overrides ASHWATH_LOG_LEVEL)")
	engineType := flag.String("engine", "mock", "inference engine: mock|llama")
	llamaBin := flag.String("llama-bin", "", "path to llama-server binary (default: search PATH)")
	modelPath := flag.String("model", "", "path to GGUF model file (required for --engine=llama)")
	flag.Parse()

	cfg, err := config.Load()
	if err != nil {
		fmt.Fprintf(os.Stderr, "config load error: %v\n", err)
		os.Exit(1)
	}

	if *port != 0 {
		cfg.Port = *port
	}
	if *dataDir != "" {
		cfg.DataDir = *dataDir
	}
	if *logLevel != "" {
		cfg.LogLevel = *logLevel
	}

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
	switch *engineType {
	case "llama":
		if *modelPath == "" {
			log.Error("--model is required when --engine=llama")
			os.Exit(1)
		}
		eng = llama.New(*llamaBin)
		initOpts.ModelPath = *modelPath
		log.Info("Using llama.cpp backend", "model", *modelPath)
	default:
		eng = runtime.NewMock()
		log.Info("Using mock backend")
	}

	if err := eng.Initialize(context.Background(), initOpts); err != nil {
		log.Error("engine initialization failed", "error", err)
		os.Exit(1)
	}
	log.Info("Engine initialized", "name", eng.Name())

	reg := models.NewRegistry(cfg.ModelsDir, downloads.NewManager(), models.WithOllamaSource())

	sigCh := make(chan os.Signal, 1)
	signal.Notify(sigCh, syscall.SIGINT, syscall.SIGTERM)

	shutdownCh := make(chan struct{})
	api.SetShutdownChannel(shutdownCh)

	grpcServer := grpc.NewServer()
	svc := api.NewEngineService(eng, det, reg, log)
	pb.RegisterAshwathEngineServer(grpcServer, svc)

	addr := fmt.Sprintf(":%d", cfg.Port)
	listener, err := net.Listen("tcp", addr)
	if err != nil {
		log.Error("failed to listen", "addr", addr, "error", err)
		os.Exit(1)
	}

	log.Info("gRPC server listening", "addr", listener.Addr().String())

	go func() {
		select {
		case <-sigCh:
			log.Info("Signal received, shutting down...")
			grpcServer.GracefulStop()
		case <-shutdownCh:
			log.Info("Shutdown requested via gRPC")
			grpcServer.GracefulStop()
		}
	}()

	if err := grpcServer.Serve(listener); err != nil {
		log.Error("gRPC server error", "error", err)
	}

	log.Info("Engine stopped")
}
