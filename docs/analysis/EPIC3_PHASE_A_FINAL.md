# EPIC 3 Phase A — Engine Foundation & Android Integration

**Status**: Complete ✅
**Date**: 2026-07-04
**Commits**: A1 (92c822a), A2 (fd5386b), A3 (b4263bc), A4 (2381a4f), A5 (fd7deee), Web merge (94fb2df), Android integration docs

---

## 1. Objectives

EPIC 3 Phase A had the following objectives:

1. **Engine Foundation Stabilization**: Complete the engine's model management lifecycle (download, registry persistence, removal).
2. **Android Integration**: Wire real engine RPCs (not mocks) into the Android Kotlin layer (gRPC client, repository, ViewModels).
3. **Cross-Platform Synchronization**: Sync SDK protos with engine protos. Merge the Web client into main. Clean up all worktrees.
4. **Build & Test Verification**: Ensure Android builds cleanly, all engine tests pass, and the Kotlin SDK test suite is comprehensive.

---

## 2. Completed Work

### A1: Backend Selection Wiring
- **Files**: bridge.go, bridge_jni.c, bridge_jni.go, mobile/ashwath.go, EmbeddedInferenceEngine.kt, EngineJniAdapter.kt, AshwathBridge.kt
- **What**: Wired backend selection (mock vs. real) through the full stack: mobile Go package → JNI bridge → Kotlin SDK.
- **Impact**: Android can now switch between DEVELOPMENT (gRPC client) and EMBEDDED (JNI .so) modes.

### A2: Model Download & Registry Persistence
- **Files**: downloads.go, models.go, registry.go, registry_test.go, server.go, service.go, service_test.go, stdlogger.go
- **What**: Implemented model download pipeline, registry persistence (installed state survives restarts), and server wiring.
- **Impact**: Models can be downloaded and persist across engine restarts.

### A3: RemoveModel RPC
- **Files**: service.proto, service.pb.go, service_grpc.pb.go, service.go (RemoveModel handler), service_test.go
- **What**: Added RemoveModel RPC to complete the model lifecycle (ListModels, InstallModel, RemoveModel).
- **Impact**: Full CRUD for models via gRPC.

### A4: Real Benchmark Implementation
- **Files**: benchmark.go, benchmark_test.go
- **What**: Implemented performance benchmarking for the engine (not mock-based).
- **Impact**: Performance can be measured and tracked over time.

### A5: Proto Drift Fix & Build Cleanup
- **Files**: SDK proto synced, build.gradle.kts (armeabi-v7a removed)
- **What**: Synced SDK Kotlin proto with engine proto (added RemoveModel). Removed dead `armeabi-v7a` Android build variant.
- **Impact**: Proto files are consistent across engine and SDK. Android build is cleaner.

### Android Integration (Post-A5)

| Component | Change |
|-----------|--------|
| `EngineGrpcClient.kt` | Extended with `listModels()`, `installModel()`, `removeModel()`, `modelId` parameter on `generate()` |
| `ClientInferenceEngine.kt` | Forwards `options.modelId` to gRPC call |
| `InferenceEngine.kt` | Added `modelId` field to `GenerationOptions` |
| `build.gradle.kts` (SDK) | Protobuf deps changed from `implementation` to `api` for transitive visibility |
| `ServiceLocator.kt` | Added `provideModelRepository()` returning `GrpcModelRepository` |
| `GrpcModelRepository.kt` | New file — `ModelRepository` impl mapping proto→domain types |
| `ExploreViewModel.kt` | Constructor-injected `modelRepository` + `ioDispatcher`; real download flow |
| `LibraryViewModel.kt` | Constructor-injected `modelRepository` + `ioDispatcher`; real delete flow |
| `ChatViewModel.kt` | Passes `activeModelName` as `modelId` in `GenerateRequest` |

### Testing
- **Engine**: 42+ tests across 8 packages
- **Android SDK**: 6 unit tests (ClientInferenceEngine, EngineGrpcClient)
- **Android App**: 9 unit tests (ExploreViewModel, LibraryViewModel)
- **Android Build**: `gradlew test` — all 16+ tests passing, 0 warnings

### Web Client Merge
- Full React + Vite + TypeScript web client merged into `main` (commit 94fb2df)
- Includes: application shell, chat UI components, engine SDK foundation (RuntimeClient, EngineClient, transport, status monitoring)
- Design system documentation (Synthetic Noir v1.0) added to `design/`

---

## 3. Architectural Decisions

### Decision 1: ViewModel Constructor Injection
- ViewModels accept `ModelRepository` and `ioDispatcher` as constructor parameters with defaults from `ServiceLocator`.
- Tests can inject `FakeModelRepository` and `StandardTestDispatcher` without changing production code.

### Decision 2: SDK Protobuf Deps as `api`
- Changed from `implementation` to `api` so the Android app module can reference proto-generated types transitively.
- Cleaner than duplicating protobuf dependencies in the app module.

### Decision 3: gRPC Client Returns `Result<T>`
- `listModels()` returns `Result<ModelList>`, `installModel()`/`removeModel()` return `Result<Unit>`.
- Repository maps failures to domain exceptions. Enables clean error handling in ViewModels.

### Decision 4: No Streaming Download Progress
- `InstallModel` RPC is unary (blocking). The UI shows a spinner during download.
- Streaming progress left for a future RPC upgrade (Phase B or C).

### Decision 5: Worktree-Based Development
- `main` is the integration branch only. All implementation in feature worktrees.
- This decision was formalized in AGENT.md and PROJECT_STATE.md during this phase.

---

## 4. Repository Status

### Branch Status
- `main` — Integration branch, all commits verified
- `feature/platform` — Clean (merged)
- `feature/android-client` — Clean (merged)
- `feature/web-client` — Clean (merged)
- `research/lab` — Not created yet

### Worktree Status
- All worktrees synchronized with `main`
- No stale branches remaining

### Build Status
| Target | Status |
|--------|--------|
| Go engine (`go build`) | ✅ Passing |
| Go tests (`go test ./...`) | ✅ 42+ tests |
| Android (`gradlew test`) | ✅ 16+ tests |
| Android (`gradlew assembleDebug`) | ✅ Passing (with Go engine .so build) |

### Known Issues
- Stitch/Google Fonts theme conflict prevents clean UI compile in some configurations (being handled in separate workspace)
- Model download progress is unary (no streaming)
- JNI error codes from Go are not granular
- EmbeddedInferenceEngine's `nativeStartServer` signature may differ from its caller in some configurations

---

## 5. Lessons Learned

### What Worked Well
1. **Incremental commits (A1-A5)**: Small, focused commits made code review and debugging straightforward.
2. **Proto-first approach**: Syncing the SDK proto with engine proto early prevented contract drift.
3. **Constructor injection for testability**: Adding `ioDispatcher` parameter alongside `modelRepository` made coroutine testing trivial.
4. **Worktree isolation**: Feature worktrees prevented cross-team interference during parallel development.

### What Could Be Improved
1. **gRPC streaming for downloads**: The unary InstallModel RPC blocks the client during downloads. A streaming progress RPC would improve UX.
2. **JNI error granularity**: The Go engine returns generic error codes via JNI. Specific error codes would improve Android-side error handling.
3. **UI compile dependency**: The Stitch/Google Fonts issue should have been resolved before starting Phase A.
4. **Proto generation automation**: SDK proto regeneration still requires a manual `gradlew :sdk:generateProto` step.

---

## 6. Remaining Roadmap

### EPIC 3 Phase B (Next)
- llama.cpp Go bindings for on-device inference
- Real model download from HuggingFace / GitHub Releases
- Streaming token generation with real models
- Performance benchmarking with real models
- Model management UI in Android (Explore + Library screens)

### EPIC 3 Phase C (Future)
- Model download progress streaming
- App lifecycle management (pause/resume engine)
- Error recovery and reconnection
- Granular JNI error codes from Go

### Phase 3b: Android Polish
- Handle engine not installed / offline state properly
- Add instrumented tests for download → install → connect flow
- Resolve Stitch/Google Fonts theme conflict
- Connect UI screens to wired ViewModels
- Model download progress UI

---

## 7. Recommended Next Steps

1. **Resolve the Stitch/Google Fonts conflict** — This is the primary blocker for Android UI development.
2. **Implement llama.cpp Go bindings** — Start EPIC 3 Phase B with the core inference integration.
3. **Wire ExploreScreen and LibraryScreen** — Connect the existing UI composables to the wired ViewModels.
4. **Add streaming download progress** — Enhance the InstallModel RPC to support streaming progress updates.
5. **Publish Kotlin SDK artifact** — Make the SDK available as a standalone artifact for external consumers.

---

## 8. Key Contributors

- **Platform Team (OpenCode)**: Engine foundation (A1-A5), proto sync, SDK changes, repository consolidation, documentation.
- **Web Client Team**: Web project scaffold, engine SDK, chat UI, merged into main.
- **Android Client Team (Gemini)**: ViewModel wiring, repository implementation, test suite, build fixes.
