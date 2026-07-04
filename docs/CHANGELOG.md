# Changelog

## [Unreleased]

### Added
- EPIC 3 Phase A: Engine foundation stabilization and Android integration.
  - Engine: backend selection wiring through mobile + bridge + JNI + SDK.
  - Engine: model download pipeline with registry persistence.
  - Engine: RemoveModel RPC for full model lifecycle management.
  - Engine: real benchmark implementation for performance measurement.
  - Engine: proto drift fix — SDK proto synchronized with engine proto.
  - Android: EngineGrpcClient extended with listModels, installModel, removeModel, and modelId parameter.
  - Android: GrpcModelRepository implementing ModelRepository via gRPC client.
  - Android: ServiceLocator wired to provide ModelRepository.
  - Android: ExploreViewModel and LibraryViewModel connected to real repository with error handling.
  - Android: ChatViewModel passes modelId in GenerateRequest.
  - Android: SDK protobuf deps changed from implementation to api for transitive visibility.
  - Android: tests for EngineGrpcClient, ClientInferenceEngine, ExploreViewModel, LibraryViewModel.
  - Android: build fixed (armeabi-v7a dead variant removed, compileSdk 37 handled).
- Web Client merged into main (React + Vite + TypeScript, shadcn/ui, engine SDK runtime layer).
- Design system documentation added (Synthetic Noir v1.0 — brand, tokens, components, icons, motion, accessibility, UX principles).
- Documentation: EPIC 3 Phase A final summary created (docs/analysis/EPIC3_PHASE_A_FINAL.md).

### Changed
- PROJECT_STATE.md: resolved merge conflicts, updated for EPIC 3 Phase A completion.
- ARCHITECTURE.md: Web Client status updated from Planned to Active Development.
- ENGINE_API.md: added RemoveModel RPC, updated proto file path.
- EPICS.md: EPIC-3 restructured into Phases A/B/C; Phase A marked complete.
- ROADMAP.md: Phase 3 (Android Engine Integration) marked complete; Phase 3b created for remaining work.
- DECISIONS.md: "Web Frontend Deferred" updated to reflect active development.
- AGENT.md: added Worktree Rules, Documentation Rules, Merge Rules, and repository consolidation workflow.
- PLATFORM_GUIDE.md: web/ directory status updated to active.
- SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md: status changed from Draft to Complete/Archived.

## [0.1.0] - 2026-03-07
### Added
- Initial project skeleton.
- Synthetic Noir design system documentation.
- Placeholder package structure for features and core services.
