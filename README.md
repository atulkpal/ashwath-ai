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
├── web/           Web frontend (React + Vite + TypeScript, active)
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

Ashwath.AI uses a modular, engine-client architecture:
- **Unified Go Engine**: A single high-performance AI core written in Go.
- **Platform-Specific Runtime**: 
  - **Android**: Embedded as a native library (`.so`) via JNI for security (W^X compliance) and performance.
  - **Desktop**: Runs as a standalone local daemon.
- **Shared gRPC/Protobuf API**: All clients communicate with the engine over a local loopback gRPC connection, ensuring a consistent developer experience across all frontends.
- **Automated Build**: Android builds automatically compile the Go engine from source using a custom Gradle integration.
- **Downloadable Models**: While the engine is bundled, AI models (GGUF) are downloaded on-demand to the device's local storage.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) and [docs/engineering/gradle-go-integration.md](docs/engineering/gradle-go-integration.md) for detailed technical specifications.

## License

Apache 2.0
