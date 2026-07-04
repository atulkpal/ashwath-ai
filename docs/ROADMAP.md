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
- [x] All 6 gRPC RPCs implemented (Generate, ListModels, InstallModel, RemoveModel, GetDeviceInfo, Shutdown)
- [x] Engine binary release pipeline (GitHub Actions, 7 cross-compile targets)
- [x] Go CI workflow (lint, test, build)
- [x] Unit test suite: 42+ tests across 8 packages
- [x] In-memory gRPC integration tests for all RPCs

## Phase 3: Android Engine Integration ✅ (Complete)
- [x] Kotlin SDK module with gRPC client infrastructure
- [x] JNI bridge for embedded Go engine (.so)
- [x] Automated Gradle build for Go shared library (arm64-v8a, x86_64)
- [x] EmbeddedInferenceEngine for managing native server lifecycle
- [x] ServiceLocator wiring SDK to app (EMBEDDED and DEVELOPMENT modes)
- [x] ChatViewModel connected to InferenceEngine interface
- [x] Generate real gRPC stubs from proto file
- [x] Wire EngineGrpcClient.generate() to real stub calls
- [x] Backend selection wiring (mobile -> bridge -> JNI -> SDK)
- [x] Model download pipeline with registry persistence
- [x] Full model lifecycle (ListModels, InstallModel, RemoveModel)
- [x] Real benchmark implementation
- [x] Proto synchronization between engine and SDK
- [x] GrpcModelRepository (domain repository via gRPC)
- [x] ExploreViewModel and LibraryViewModel wired to real models
- [x] SDK protobuf deps visibility fix
- [x] Test suite for SDK and ViewModels
- [x] Android build cleanup (armeabi-v7a removed)

## Phase 3b: Android Polish (In Progress)
- [ ] Handle engine not installed / offline state properly
- [ ] Add instrumented tests for download → install → connect flow
- [ ] Resolve Stitch/Google Fonts theme conflict
- [ ] Connect UI screens to wired ViewModels (Explore, Library)
- [ ] Model download progress UI
- [ ] Fix deprecation warnings (icons, statusBar, use import)

## Phase 4: Real Inference (Next)
- [ ] llama.cpp Go bindings
- [ ] Model download from GitHub Releases / HuggingFace
- [ ] Streaming token generation with real models
- [ ] Basic chat flow end-to-end (Android app ↔ Go engine)
- [ ] Performance benchmarking with real models

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
- [ ] Plugin system
- [ ] Community model hub
