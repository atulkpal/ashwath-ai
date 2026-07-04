# Architectural Decisions

## [2026-07-03] Monorepo Platform Architecture
- **Decision**: Restructure repository as a monorepo with platform root, standalone Android project, and Go engine.
- **Reasoning**: Platform longevity requires decoupling frontends from business logic. One shared engine powers all clients.
- **Decision**: Android project lives in `android/` as self-contained Gradle project.
- **Reasoning**: Avoids build system conflicts with Go tooling. Android Studio opens `android/` directly.
- **Decision**: Go engine uses `internal/` package convention for private packages.
- **Reasoning**: Go compiler enforces encapsulation. Prevents external consumers from importing engine internals.

## [2026-07-03] JSON Codec for MVP gRPC
- **Decision**: Use a custom JSON codec for gRPC instead of protobuf code generation.
- **Reasoning**: Avoids `protoc` dependency during early development. The proto file remains the API contract. JSON codec enables rapid iteration without regenerating stubs.
- **Trade-off**: No type safety or schema enforcement at the wire level. Must switch to generated stubs before production.

## [2026-07-03] SDK Module as Gradle Subproject
- **Decision**: The Kotlin SDK (`sdk/kotlin/`) is included as a Gradle subproject inside `android/settings.gradle.kts` with `project(":sdk").projectDir = file("../sdk/kotlin")`.
- **Reasoning**: Single `./gradlew` invocation builds both app and SDK. SDK consumers outside the monorepo can publish it as a standalone artifact.
- **Decision**: App depends on SDK via `implementation(project(":sdk"))`, not source set inlining.
- **Reasoning**: Proper project dependency ensures SDK compiles independently and its JAR is consumed by the app.

## [2026-07-03] Engine Distribution Model
- **Decision**: Engine is distributed as a standalone binary via GitHub Releases. Android downloads it on first launch.
- **Reasoning**: Keeps Android APK small (<100MB). Enables engine updates without Play Store submission. Users can also run the engine standalone.

## [2026-07-03] gRPC-Protobuf-Lite for SDK
- **Decision**: SDK uses `grpc-protobuf-lite` instead of full `grpc-protobuf`.
- **Reasoning**: Lite variant avoids duplicate class conflicts with Android's built-in protobuf-javalite. Reduces SDK footprint.

## [2026-07-04] Web Frontend Merged into Main
- **Decision**: Web frontend (TypeScript/React) project is now merged into main and actively developed.
- **Rationale**: The engine API is stable enough (EPIC 2 and EPIC 3 Phase A complete) for parallel web development. The Web Client follows the Ashwath AI Runtime + gRPC-Web architecture described in `docs/SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md`.
- **Status**: Active development. Engine SDK and chat UI components established. Merged into main on 2026-07-04.

## [2026-03-07] Initial Architecture Selection
- **Decision**: Use Package-based modularization within `:app` initially.
- **Reasoning**: Faster development velocity while maintaining Clean Architecture boundaries.
- **Decision**: Kotlin DSL for Gradle.
- **Reasoning**: Consistency with modern Android standards.
