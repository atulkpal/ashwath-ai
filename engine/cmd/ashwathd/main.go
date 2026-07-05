package main

import (
	"context"
	"flag"
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"github.com/ashwathai/ashwath-engine/internal/config"
	"github.com/ashwathai/ashwath-engine/internal/runtime/llama"
	"github.com/ashwathai/ashwath-engine/internal/server"
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

	llama.Register()

	opts := server.Options{
		EngineType: *engineType,
		LlamaBin:   *llamaBin,
		ModelPath:  *modelPath,
	}

	ctx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	if err := server.Run(ctx, cfg, opts); err != nil {
		fmt.Fprintf(os.Stderr, "server error: %v\n", err)
		os.Exit(1)
	}
}
