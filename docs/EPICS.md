# EPICs

## EPIC-1: Engine MVP ✅ (Complete)

**Goal**: Go engine compiles, starts, and serves a gRPC API with mock responses.

### Stories
- [x] E1.1: Implement gRPC server with service stubs (JSON codec, all 5 RPCs)
- [x] E1.2: Implement config loading from file/env (`config.Load()`, 7 env vars)
- [x] E1.3: Implement device detection (OS, arch, CPU cores, RAM via /proc/meminfo)
- [x] E1.4: Implement mock inference engine (streaming, 50ms word delay, cancellation)
- [x] E1.5: Implement model registry (4 hardcoded models: Gemma, Phi, Llama, Qwen)
- [x] E1.6: Implement structured logging (slog-based, 4 levels, `With` support)
- [x] E1.7: Engine release pipeline in CI (7 targets: linux/amd64/arm64, darwin/amd64/arm64, windows/amd64, android/arm64)
- [x] E1.8: Unit test suite (21 tests across config, logging, device, models, runtime, api)

## EPIC-2: Android Engine Integration (In Progress)

**Goal**: Android app downloads, installs, and communicates with the Go engine.

### Stories
- [x] E2.1: Engine binary downloader with progress (`EngineDownloader`, `DownloadState`)
- [x] E2.2: Checksum verification (`ChecksumVerifier`)
- [x] E2.3: Engine process lifecycle management (`EngineProcessManager`)
- [x] E2.4: gRPC client in Kotlin SDK (`EngineGrpcClient`, `ClientInferenceEngine`)
- [x] E2.5: Connect ChatViewModel to real engine
- [x] E2.6: ServiceLocator wiring SDK to app
- [ ] E2.7: Generate real gRPC stubs from `engine/proto/ashwathai/v1/engine.proto`
- [ ] E2.8: Wire `EngineGrpcClient.generate()` to real stub calls (currently mocked)
- [ ] E2.9: Handle engine not installed / offline state
- [ ] E2.10: Fix deprecation warnings (icons, statusBar, use import)
- [ ] E2.11: Add unit and instrumented tests

## EPIC-3: Real Inference

**Goal**: Engine runs real AI models (llama.cpp).

### Stories
- [ ] E3.1: llama.cpp Go bindings
- [ ] E3.2: Model download from GitHub Releases/HuggingFace
- [ ] E3.3: Streaming token generation
- [ ] E3.4: Model management UI in Android
- [ ] E3.5: Performance benchmarking

## EPIC-4: RAG & Knowledge

**Goal**: Users can ingest local documents and query them via chat.

### Stories
- [ ] E4.1: SQLite-based vector store
- [ ] E4.2: Document parser (PDF, text, markdown)
- [ ] E4.3: Embedding generation
- [ ] E4.4: Retrieval-augmented generation pipeline
- [ ] E4.5: Knowledge management UI in Android

## EPIC-5: Voice & Vision

**Goal**: Multi-modal AI capabilities.

### Stories
- [ ] E5.1: STT integration
- [ ] E5.2: TTS integration
- [ ] E5.3: Image description
- [ ] E5.4: Voice chat UI in Android
- [ ] E5.5: Camera integration in Android

## EPIC-6: Web Frontend (Planned)

**Goal**: TypeScript/React web frontend communicates with the Go engine via gRPC-Web.

### Prerequisites
- Engine API must be stable (post-EPIC-3)
- Proto-generated TypeScript stubs available
- gRPC-Web proxy or envoy in the engine

### Stories
- [ ] E6.1: Web project scaffold (Vite + React + TypeScript)
- [ ] E6.2: gRPC-Web client in TypeScript SDK
- [ ] E6.3: Design system port (Synthetic Noir → CSS/Tailwind)
- [ ] E6.4: Chat UI component
- [ ] E6.5: Engine connection management (WebSocket / gRPC-Web)
- [ ] E6.6: Model browser UI
- [ ] E6.7: Progressive Web App support
- [ ] E6.8: Responsive layout (mobile + desktop)

### Notes
- Web frontend is deferred until the engine API stabilizes with real inference (EPIC-3).
- The Kotlin SDK serves as the reference implementation for gRPC client patterns.
- gRPC-Web requires the engine to support it (either via Envoy proxy or native gRPC-Web in Go).
