# Ashwath AI Engine

The core AI engine powering all Ashwath AI frontends.

Written in Go. Platform-independent. Distributed as a standalone binary.

## Architecture

```
cmd/ashwathd/      Entry point
internal/           Private engine packages
  runtime/           Inference engine abstraction
  downloads/         Model download & verification
  device/            Device capability detection
  models/            Model registry & lifecycle
  knowledge/         Knowledge base management
  rag/               Retrieval-Augmented Generation
  voice/             Speech-to-Text & Text-to-Speech
  vision/            Image processing
  benchmark/         Performance benchmarking
  plugins/           Plugin system
  config/            Engine configuration
  logging/           Structured logging
pkg/api/             Public API types
api/proto/           gRPC/protobuf service definitions
tests/               Integration tests
```

## Build

```bash
go build ./cmd/ashwathd
```

## Design Philosophy

- The engine is the product. UIs are replaceable.
- All business logic lives here, never in frontends.
- Communication with frontends happens over gRPC.
- The engine is downloaded by frontends at runtime via GitHub Releases.
