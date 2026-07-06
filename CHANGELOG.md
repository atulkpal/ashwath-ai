# Changelog

## [Unreleased]

### Migration (Repository Architecture)
- Archival of 6 stale branches (tagged `archive/*`), branch reconciliation completed.
- EPIC-4 engine features merged: model catalog, upstream index, Ollama detection, JNI error codes, benchmarking, CI workflows.
- Android client v1 merged: real gRPC model repository, engine model management, download flow, design polish.
- Web frontend v1 merged: chat E2E with engine streaming, gRPC-Web SDK, PWA support, model browser.
- Branching model established: Trunk-Based Development + Release Branches.
- CI added: Web CI, Integration Gate (proto changes), Engine Consistency Check.
- PR template added, `release/v0.2.x` branch created, tag convention standardized.
- Worktree policy updated: merge-based sync (no cherry-pick), 2-week lifetime limit.

### Added
- EPIC 3 (Engine Foundation): Architecture Foundation, Runtime, Provider Abstraction, Stabilization.
  - Event System (`internal/bus`): in-memory pub/sub event bus with 6 predefined topics.
  - Plugin Framework (`internal/plugins`): Manager implementation, ToolPlugin extension, builtin registry.
  - Model Abstraction (`internal/models`): Source interface, BuiltinSource, event bus integration.
  - Agent Runtime (`internal/agent`): Memory, Context Assembly, Tool Pipeline, Agent orchestrator.
  - Provider Registry (`internal/runtime/provider.go`): RegisterProvider, CreateEngine, ListProviders.
  - Llama provider registration (`internal/runtime/llama/provider.go`).
  - Server decoupled: hardcoded engine switch replaced with registry lookup.
  - Module Boundaries documented (`docs/engine/MODULE_BOUNDARIES.md`).
- Web Client merged into main (React + Vite + TypeScript, shadcn/ui, engine SDK runtime layer).
- Design system documentation added (Synthetic Noir v1.0 — brand, tokens, components, icons, motion, accessibility, UX principles).

### Changed
- AGENT.md: comprehensive rewrite as repository constitution (18 sections: mission, principles, AI duties, Single Writer Principle, branch/worktree/merge policy, sprint workflow, Definition of Done, audit checklist, session management, engineering oath).
- DOCUMENTATION_GOVERNANCE.md: rewritten with Single Writer Principle, documentation types, creation policy, archive policy.
- PROJECT_STATE.md: updated for EPIC 3 completion, current milestone set to GOVERNANCE SPRINT 1.
- ARCHITECTURE.md: Web Client status updated from Planned to Active Development.
- ENGINE_API.md: added RemoveModel RPC, updated proto file path.
- EPICS.md: EPIC-3 restructured into Phases A/B/C/D/E; all phases marked complete.
- ROADMAP.md: Phase 3 (Android Engine Integration) marked complete.
- DECISIONS.md: Web Frontend status updated; fixed stale SPRINT_W3A reference.
- PLATFORM_GUIDE.md: web/ directory status updated to active; added cross-reference to ARCHITECTURE.md.
- GUILD.md: governance sections trimmed; cross-reference to AGENT.md added.
- SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md: moved to docs/analysis/ as historical record; fixed broken relative links.
- PROJECT_STATE.md: EPIC 3 Phases B–E added to completed milestones.
- android/.gitignore: added jniLibs/ for build artifacts.

### Removed
- docs/EPIC1_AGENT_PROMPT.md, docs/EPIC2_AGENT_PROMPT.md, docs/EPIC2_TODO.md, docs/EPIC3_PHASE_A_FINAL.md (obsolete).
- web/README.md (unused default template).
- engine/internal/api/jsoncodec.go (dead code).
- engine/ashwathd.exe (stale binary).
- docs/CHANGELOG.md (merged into root CHANGELOG.md).

### Added
- docs/REPOSITORY_MANIFEST.md: authoritative directory/file map with ownership, purpose, lifecycle.
- docs/analysis/GOVERNANCE_SPRINT_1_FINAL.md: governance sprint final report.

## [0.1.0] - 2026-03-07

### Added
- Monorepo platform foundation with multi-frontend architecture.
- Android standalone app migrated to `android/`.
- Go AI Engine scaffold under `engine/`.
- Platform documentation (vision, architecture, roadmap, API, decisions, design system).
- SDK scaffolding for Kotlin, Swift, Go, TypeScript.
- Placeholder directories for iOS, Desktop, Web, tools, examples, scripts.

### Engine (EPIC-1)
- gRPC server with JSON codec on dynamic port, 5 RPCs: Generate (streaming), ListModels, InstallModel, GetDeviceInfo, Shutdown.
- Mock inference engine (word-by-word streaming with 50ms delay, context cancellation).
- Config loader: JSON file + 7 environment variables, port validation.
- Device detection: OS, architecture, CPU cores, RAM (Linux `/proc/meminfo`, cross-platform runtime).
- Model registry: 4 curated models (Gemma 3 4B, Phi-4 Mini, Llama 3.2 3B, Qwen 2.5 3B).
- Structured logging via slog with 4 levels (debug, info, warn, error) and key-value fields.
- Unit test suite: 21 tests across 6 packages, including in-memory gRPC integration tests.
- GitHub Actions: engine CI (lint, test, build) + release pipeline (7 cross-compile targets).
- `go vet` clean, all tests passing.

### Android (EPIC-2)
- Kotlin SDK module (`sdk/kotlin/`) as a Gradle subproject.
- SDK interfaces: `InferenceEngine`, `GenerationOptions`, `InferenceResult`.
- SDK implementations: `ClientInferenceEngine`, `EngineGrpcClient`.
- Engine download infrastructure: `EngineDownloader`, `ChecksumVerifier`, `DownloadState`.
- Platform layer: `EngineInstaller`, `EngineProcessManager`.
- Dependency injection: `ServiceLocator`.
- ViewModels: `ChatViewModel` (connected to engine), `LibraryViewModel`.
- Version catalog (`libs.versions.toml`) with gRPC, Ktor, protobuf, Compose dependencies.
- Full app build succeeds (`./gradlew assembleDebug`).

### Engine (EPIC-3, Phase A: Foundation Stabilization)
- Real model installation pipeline with background download worker.
- Persistent model registry (`registry.json`) with install/remove lifecycle management.
- Integration of `llama.cpp` backend wiring with initial `llama-server` process management.
- Download manager with multi-threaded chunked downloads and SHA-256 verification.
- Benchmark implementation for on-device performance evaluation (tokens/sec, memory).
- Refined engine configuration with data directory isolation.
- Verified end-to-end integration between engine and SDK for real model metadata.
- Unit test coverage expanded to 45 tests; `go test` and `go vet` clean.
- Fixed various race conditions in streaming response cancellation.

### Fixed
- gRPC `HandlerType` nil-pointer crash in service descriptor registration.
- Android SDK plugin resolution (Kotlin JVM plugin version + classpath conflict).
- Android SDK dependency: replaced `sourceSets` inline source with proper `project(":sdk")`.
- SDK protobuf dependency conflict: use `grpc-protobuf-lite` only, no full `protobuf-java`.
