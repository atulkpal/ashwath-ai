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

Ashwath.AI uses a modular, engine-client architecture:
- **Go Engine**: The "brain" of the platform. On Android, it's embedded as a native library via JNI. On other platforms, it runs as a local daemon.
- **gRPC API**: The engine and frontend communicate over a local loopback gRPC connection. This ensures frontend code remains lightweight and platform-agnostic.
- **Gradle Integration**: On Android, the Go engine is automatically compiled and bundled during the Gradle build process.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) and [docs/engineering/gradle-go-integration.md](docs/engineering/gradle-go-integration.md) for details.

## License

Apache 2.0
