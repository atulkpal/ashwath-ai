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

---

## EPIC-2: Android Engine Integration ✅ (Complete)

**Goal**: Android app embeds and communicates with the Go engine via JNI loopback.

### Stories
- [x] E2.1: Implement JNI bridge in Go (`engine/cmd/libashwath`)
- [x] E2.2: Implement `nativeStartServer` to launch in-process gRPC
- [x] E2.3: Automated Gradle build for Go shared library (`.so`)
- [x] E2.4: gRPC client in Kotlin SDK (`EngineGrpcClient`, `ClientInferenceEngine`)
- [x] E2.5: `EmbeddedInferenceEngine` for managing native server lifecycle
- [x] E2.6: Connect ChatViewModel to real engine via loopback
- [x] E2.7: ServiceLocator wiring for EMBEDDED mode
- [x] E2.8: Verified end-to-stream pipeline in Android UI
- [x] E2.9: Establish Engineering Charter (`GUILD.md`)

---

## EPIC-3: Real Inference

**Goal**: Engine runs real AI models with full model lifecycle management.

### Phase A: Engine Foundation & Android Integration ✅ (Complete)

**Stories**:
- [x] E3.A1: Wire backend selection through mobile + bridge + JNI + SDK
- [x] E3.A2: Model download pipeline with registry persistence (downloads.go, models.go, registry.go)
- [x] E3.A3: RemoveModel RPC — full model lifecycle (install, list, remove)
- [x] E3.A4: Real benchmark implementation for performance measurement
- [x] E3.A5: Proto drift fix — sync SDK proto with engine proto
- [x] E3.A6: Extend EngineGrpcClient with listModels, installModel, removeModel, modelId parameter
- [x] E3.A7: GrpcModelRepository implementing ModelRepository via gRPC
- [x] E3.A8: ServiceLocator provideModelRepository
- [x] E3.A9: ExploreViewModel and LibraryViewModel real repository wiring
- [x] E3.A10: ChatViewModel modelId passing
- [x] E3.A11: SDK protobuf deps api visibility
- [x] E3.A12: Tests for gRPC client, ViewModels, ClientInferenceEngine
- [x] E3.A13: Android build fixed (armeabi-v7a removed)
- [x] E3.A14: Web client merged into main
- [x] E3.A15: All worktrees synchronized with main

### Phase B: Real Models 🔜 (Next)

**Stories**:
- [ ] E3.B1: llama.cpp Go bindings for on-device inference
- [ ] E3.B2: Model download from GitHub Releases / HuggingFace
- [ ] E3.B3: Streaming token generation with real models
- [ ] E3.B4: Performance benchmarking with real models
- [ ] E3.B5: Model management UI in Android (Explore + Library screens)

### Phase C: Platform Hardening 🔜 (Future)

**Stories**:
- [ ] E3.C1: Model download progress streaming
- [ ] E3.C2: App lifecycle management (pause/resume engine)
- [ ] E3.C3: Error recovery and reconnection
- [ ] E3.C4: Granular JNI error codes from Go

---

## EPIC-4: RAG & Knowledge 🔜 (Future)

**Goal**: Users can ingest local documents and query them via chat.

### Stories
- [ ] E4.1: SQLite-based vector store
- [ ] E4.2: Document parser (PDF, text, markdown)
- [ ] E4.3: Embedding generation
- [ ] E4.4: Retrieval-augmented generation pipeline
- [ ] E4.5: Knowledge management UI in Android

---

## EPIC-5: Voice & Vision 🔜 (Future)

**Goal**: Multi-modal AI capabilities.

### Stories
- [ ] E5.1: STT integration
- [ ] E5.2: TTS integration
- [ ] E5.3: Image description
- [ ] E5.4: Voice chat UI in Android
- [ ] E5.5: Camera integration in Android

---

## EPIC-6: Web Frontend 🔄 (Merged to main — Active Development)

**Goal**: TypeScript/React web frontend communicates with the Go engine via gRPC-Web and Ashwath AI Runtime.

### Completed
- [x] E6.1: Web project scaffold (Vite + React + TypeScript)
- [x] E6.2: Design system port (Synthetic Noir → Tailwind/shadcn)
- [x] E6.3: Chat UI components (input, message, conversation list, model selector, parameter panel)
- [x] E6.4: Application shell with navigation (sidebar, top bar, status bar)
- [x] E6.5: Engine SDK foundation (RuntimeClient, EngineClient, status monitoring, transport)
- [x] E6.6: Merged into main

### Remaining Stories (In Progress)
- [ ] E6.7: gRPC-Web client in TypeScript SDK
- [ ] E6.8: Engine connection management (WebSocket / gRPC-Web)
- [ ] E6.9: Model browser UI
- [ ] E6.10: Progressive Web App support
- [ ] E6.11: Responsive layout (mobile + desktop)

### Notes
- The Kotlin SDK serves as the reference implementation for gRPC client patterns.
- Web frontend was previously deferred until EPIC 3 API stabilization; now merged and actively developed.
- gRPC-Web requires the Ashwath AI Runtime (not direct browser→engine).
