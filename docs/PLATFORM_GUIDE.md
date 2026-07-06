# Platform Guide

> **Repository layout**: See [ARCHITECTURE.md](ARCHITECTURE.md) for canonical architecture diagram and [README.md](../README.md) for top-level directory overview.
>
> This guide focuses on development workflow and build procedures.

## Repository Layout

```
AshwathAI/
├── engine/            Go AI Engine (ashwathd binary, libashwath .so)
├── android/           Android frontend (Jetpack Compose, standalone Gradle project)
├── web/               Web frontend (React + Vite + TypeScript, active)
├── sdk/               Client SDKs for engine API
│   ├── kotlin/        Kotlin SDK (active MVP, Gradle subproject for Android)
│   ├── typescript/    TypeScript SDK (active, gRPC-Web for web frontend)
│   ├── go/            Go SDK (scaffold)
│   └── swift/         Swift SDK (scaffold)
├── docs/              Platform documentation
├── design/            Shared design assets (Synthetic Noir)
├── scripts/           Build & CI scripts
├── .github/           GitHub Actions workflows
│   └── workflows/
│       ├── engine-ci.yml              Engine lint + test + build
│       ├── android-ci.yml             Android CI pipeline
│       ├── web-ci.yml                 Web CI (pnpm install + lint + build)
│       ├── integration-gate.yml       Proto change gate
│       ├── engine-consistency.yml     Cross-branch engine/sdk drift check
│       └── release-engine.yml         Cross-compile + publish to GitHub Releases
```

## Development Workflow

### Android (app + SDK)
```bash
cd android
./gradlew assembleDebug          # Build debug APK + SDK jar
./gradlew :sdk:jar                # Build SDK jar only
./gradlew :sdk:generateProto      # Regenerate gRPC stubs (when proto changes)
./gradlew :sdk:test               # Run SDK unit tests
./gradlew test                    # Run all unit tests
```

Open `android/` in Android Studio for UI development.

### Engine
```bash
cd engine
go build ./cmd/ashwathd          # Build binary
go test -count=1 ./...            # Run all tests (100+ tests)
go vet ./...                      # Static analysis
go run tests/smoke.go             # Manual smoke test (requires running server)
```

Run the server:
```bash
ashwathd --port 9750 --log-level debug --engine mock
```

### Web
```bash
cd web
pnpm install                      # Install dependencies
pnpm lint                         # ESLint check
pnpm build                        # Production build
pnpm dev                          # Dev server with HMR
```

### SDK (standalone)
```bash
cd sdk/kotlin
# The SDK is built as a Gradle subproject via android/:
cd android && ./gradlew :sdk:build
# For publishing, see sdk/kotlin/README.md
```

### Cross-platform
```bash
make build-android    # Build Android app
make build-engine     # Build Go engine
make test-all         # Run all tests
```

## CI/CD

| Workflow | Trigger | Actions |
|----------|---------|---------|
| Engine CI | `engine/` changes | `go vet`, `go test`, `go build` |
| Android CI | `android/` + `sdk/kotlin/` changes | APK build, lint checks |
| Web CI | `web/` + `sdk/typescript/` changes | `pnpm install`, `pnpm lint`, `pnpm build` |
| Integration Gate | `engine/api/proto/` changes | Build engine, run tests, build Android SDK |
| Engine Consistency | Post-push to `main` | Warn if feature branches have diverged engine/sdk code |
| Engine Release | `engine/v*` tags | Cross-compile for 7 targets, publish to GitHub Releases |

### Engine Release Targets
- `linux/amd64` — Linux x86_64
- `linux/arm64` — Linux ARM (Raspberry Pi, servers)
- `darwin/amd64` — Intel macOS
- `darwin/arm64` — Apple Silicon macOS
- `windows/amd64` — Windows x86_64
- `android/arm64` — Android ARM64 devices

## Coding Standards

- **Go**: Follow standard `gofmt` conventions. Use `internal/` for private packages. Error wrapping with `%w`.
- **Kotlin/Android**: Follow Kotlin coding conventions. Clean Architecture layers. Jetpack Compose for UI. Material 3 with Synthetic Noir design system.
- **TypeScript/React**: Follow project conventions (see `web/`). Tailwind CSS + shadcn/ui.
- **Swift/TypeScript SDK**: To be defined when those frontends are implemented.

## Making Changes

1. **Each platform directory is independent** — changes to `android/` don't affect `engine/` and vice versa.
2. **The API contract** (`engine/api/proto/service.proto`) is the shared dependency between engine and frontends. The SDK maintains a synced copy at `sdk/kotlin/src/main/proto/ashwathai/v1/engine.proto`.
3. **When changing the API**: Update the proto file first, then regenerate SDK stubs for Kotlin and TypeScript.
4. **Engine uses protobuf codec** (migrated from JSON codec during EPIC 2).

## Testing Philosophy

- **Engine**: Unit tests for all `internal/` packages. In-memory gRPC integration tests (`bufconn`). Manual smoke test for end-to-end. 100+ tests across 10+ packages.
- **Android**: Unit tests for ViewModels, SDK client (10+ tests). Instrumented tests for download/install flow (planned).
- **Web**: Unit tests with Vitest. Component tests with Testing Library. E2E with Playwright (future).

## Versioning

- **Engine API**: Versioned via gRPC service package name. Breaking changes increment the package version (e.g., `ashwath.v2`).
- **Engine binary**: Semantic versioning via Git tags (`engine/v1.0.0`).
- **Android app**: Versioned via `versionCode` and `versionName` in build.gradle.kts.
