# Ashwath.AI Project State

This document provides a real-time operational dashboard for the Ashwath.AI repository.

---

### Meta Information
- **Project Version**: 0.2.0
- **Current Milestone**: EPIC 3: Phase B (Architecture Foundation)
- **Last Updated**: 2024-03-24

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Platform** | Stable | Platform Team |
| **Android Client** | Stable | Android Client Team |
| **Web Client** | Planned | Web Client Team |
| **Research Lab** | Experimental | Research Lab |
| **Documentation** | Active | Shared |

---

### Repository Health

- **Build Status**: ✅ Passing (Android, Engine, & SDK)
- **Test Status**: ✅ Passing (SDK & Engine integration tests)
- **Known Issues**: 
  - SELinux restrictions on physical devices bypassed via Embedded Runtime.
  - Automatic Go build requires Go SDK in system PATH.
- **Technical Debt**:
  - `libashwath` JNI bridge needs more granular error codes from Go.
  - Proto definitions should be synchronized across all future frontends.

---

### Current Work

- **Platform Team**: Transitioning to EPIC 3 Phase B (Refining gRPC contracts and SDK abstractions).
- **Android Client Team**: Migrating to the new persistent model registry in the Library UI.
- **Web Client Team**: Prototyping gRPC-Web bridge for browser-based engine access.
- **Research Lab**: Benchmarking Q4_K_M vs Q5_K_M GGUF models on mobile hardware.

---

### Recently Completed
- [x] **EPIC 3 Phase A**: Stabilized Engine Foundation (Real downloads, registry, benchmarks).
- [x] Migrated Android to **Embedded Go Runtime** (.so) via JNI.
- [x] Automated Go Engine build within the Gradle pipeline.
- [x] Established shared gRPC loopback architecture for all platforms.
- [x] Verified end-to-stream pipeline in the Chat UI.
- [x] Implemented Kotlin SDK with real gRPC stubs.
- [x] Established Engineering Charter (`GUILD.md`).

---

### Next Priorities
1. **EPIC 3 Phase B**: Architecture Foundation (Standardizing API contracts and multi-backend support).
2. **Model Management**: UI for downloading and switching between specific models.
3. **App Lifecycle**: Pausing/Stopping the engine process when app is backgrounded.

---

### Important Decisions
- **ADR-001**: Adoption of gRPC over localhost for engine communication.
- **ADR-002**: Multi-worktree monorepo structure for parallel development.
- **ADR-003**: Selection of Protobuf Lite for mobile binary size optimization.

---

### Repository Layout
- `/android`: Flagship Android application (Compose + MVVM).
- `/engine`: Go-based AI execution core.
- `/sdk`: Cross-platform language bindings (Kotlin, TS, Go).
- `/research`: Experimental models and lab scripts.
- `/docs`: Global platform documentation and Architecture.
- `/scripts`: Tooling for repository setup and CI/CD.

---

### Notes for Contributors
- Ensure your worktree is synchronized with `main` before starting.
- Never commit directly to `main`; use `feature/` branches.
- Respect the **Engineering Charter** in `docs/GUILD.md`.
