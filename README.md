# Ashwath.AI

Offline-first AI platform. Private. Modular. Cross-platform.

## Overview

Ashwath.AI is an offline-first AI platform that runs entirely on-device.
The engine is written in Go. The Android frontend is built with Kotlin & Jetpack Compose.
Future frontends will include iOS, Desktop, and Web — all powered by the same Go engine.

## Repository Structure

```
├── engine/        Go AI Engine (the product)
├── android/       Android frontend (standalone, Jetpack Compose)
├── ios/           iOS frontend (future)
├── desktop/       Desktop frontend (future)
├── web/           Web frontend (future)
├── sdk/           Client SDKs for engine API
├── docs/          Platform documentation
├── design/        Shared design assets
├── scripts/       Build and CI scripts
├── tools/         Development tools
└── examples/      Usage examples
```

## Quick Start

### Android

```bash
cd android
./gradlew assembleDebug
```

Open `android/` in Android Studio for development.

### Engine

```bash
cd engine
go build ./cmd/ashwathd
```

## Architecture

The Android app is standalone — it downloads the Go engine binary on first launch via GitHub Releases.
Communication happens over a local gRPC API. The Android app never contains AI business logic.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for details.

## License

Apache 2.0
