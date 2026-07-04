# Ashwath.AI Roadmap

## Phase 1: Platform Foundation ✅ (Complete)
- [x] Monorepo structure established
- [x] Android standalone project migrated to `android/`
- [x] Go engine scaffold with interface definitions
- [x] Synthetic Noir design system basics
- [x] Navigation and feature skeletons (Chat, Library, Explore, Settings, Knowledge, Onboarding)
- [x] Platform documentation suite (Vision, Architecture, Roadmap, API, Decisions, Design System)
- [x] SDK module scaffolding (Kotlin, Swift, Go, TypeScript)

## Phase 2: Engine MVP ✅ (Complete)
- [x] gRPC server implementation in Go engine with JSON codec
- [x] Mock inference engine returning streaming word-by-word responses
- [x] Config loading from JSON file + environment variables
- [x] Device detection (OS, arch, CPU cores, RAM)
- [x] Model registry with 4 curated models (Gemma, Phi, Llama, Qwen)
- [x] Structured logging via slog
- [x] All 5 gRPC RPCs implemented (Generate, ListModels, InstallModel, GetDeviceInfo, Shutdown)
- [x] Engine binary release pipeline (GitHub Actions, 7 cross-compile targets)
- [x] Go CI workflow (lint, test, build)
- [x] Unit test suite: 21 tests across 6 packages (config, logging, device, models, runtime, api)
- [x] In-memory gRPC integration tests for all RPCs

## Phase 3: Android Engine Integration ✅ (Complete)
- [x] Kotlin SDK module with gRPC client infrastructure
- [x] Engine downloader with progress tracking and checksum verification
- [x] Engine process lifecycle manager (start, stop, health check)
- [x] Android ServiceLocator wiring SDK to app
- [x] ChatViewModel connected to InferenceEngine interface
- [x] Generate real gRPC stubs from proto file
- [x] Wire `EngineGrpcClient.generate()` to real stub calls
- [x] Handle engine not installed / offline state properly
- [x] Add instrumented tests for download → install → connect flow

## Phase 4: Real Inference (Phase A: Foundation Stabilization ✅)
- [x] llama.cpp Go backend wiring
- [x] Real model installation pipeline (Downloads + Verification)
- [x] Persistent model registry (`registry.json`)
- [x] Performance benchmarking implementation
- [ ] Phase B: Architecture Foundation (Next Priority)
- [ ] Streaming token generation with real models (End-to-End)
- [ ] Basic chat flow end-to-end (Android app ↔ Go engine)

## Phase 5: Knowledge & RAG
- [ ] SQLite-based vector store
- [ ] Document parser (PDF, text, markdown)
- [ ] Embedding generation
- [ ] Retrieval-augmented generation pipeline
- [ ] Knowledge management UI in Android

## Phase 6: Voice & Vision
- [ ] STT integration
- [ ] TTS integration
- [ ] Image description
- [ ] Voice chat UI in Android
- [ ] Camera integration in Android

## Phase 7: Platform Expansion
- [ ] iOS frontend (Swift)
- [ ] Desktop frontend (Tauri/TBD)
- [ ] Web frontend (TypeScript/React)
- [ ] Plugin system
- [ ] Community model hub
