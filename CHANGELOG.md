# Changelog

## [0.1.0] - Unreleased

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

### Android (EPIC-2, WIP)
- Kotlin SDK module (`sdk/kotlin/`) as a Gradle subproject.
- SDK interfaces: `InferenceEngine`, `GenerationOptions`, `InferenceResult`.
- SDK implementations: `ClientInferenceEngine`, `EngineGrpcClient`.
- Engine download infrastructure: `EngineDownloader`, `ChecksumVerifier`, `DownloadState`.
- Platform layer: `EngineInstaller`, `EngineProcessManager`.
- Dependency injection: `ServiceLocator`.
- ViewModels: `ChatViewModel` (connected to engine), `LibraryViewModel`.
- Version catalog (`libs.versions.toml`) with gRPC, Ktor, protobuf, Compose dependencies.
- Full app build succeeds (`./gradlew assembleDebug`).

### Fixed
- gRPC `HandlerType` nil-pointer crash in service descriptor registration.
- Android SDK plugin resolution (Kotlin JVM plugin version + classpath conflict).
- Android SDK dependency: replaced `sourceSets` inline source with proper `project(":sdk")`.
- SDK protobuf dependency conflict: use `grpc-protobuf-lite` only, no full `protobuf-java`.
