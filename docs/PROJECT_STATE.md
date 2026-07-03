# Ashwath.AI Project State

This document provides a real-time operational dashboard for the Ashwath.AI repository.

---

### Meta Information
- **Project Version**: 0.1.0
- **Current Milestone**: EPIC 2: Android Engine Integration
- **Last Updated**: 2024-03-07

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Platform** | Stable | Platform Team |
| **Android Client** | Integration | Android Client Team |
| **Web Client** | Planned | Web Client Team |
| **Research Lab** | Experimental | Research Lab |
| **Documentation** | Active | Shared |

---

### Repository Health

- **Build Status**: ✅ Passing (Android & SDK)
- **Test Status**: ✅ Passing (SDK & Installer unit tests)
- **Known Issues**: 
  - SELinux may restrict execution on some physical devices (Fixed with chmod 755).
  - Ktor 3 migration requires careful header handling.
- **Technical Debt**:
  - `EngineProcessManager` needs a more robust health check (polling port 50051).
  - Manual JNI/gRPC bridge in SDK needs more comprehensive error mapping.

---

### Current Work

- **Platform Team**: Stabilizing gRPC API and preparing for llama.cpp integration (EPIC 3).
- **Android Client Team**: Finalizing engine downloader UI and testing process lifecycle.
- **Web Client Team**: Initial project scaffolding and gRPC-Web exploration.
- **Research Lab**: Evaluating quantization techniques for mobile NPUs.

---

### Recently Completed
- [x] Implemented Kotlin SDK with real gRPC stubs.
- [x] Added `EngineInstaller` with SHA-256 verification.
- [x] Resolved "Permission Denied" (error 13) for engine execution on Android.
- [x] Integrated `ChatViewModel` with streaming gRPC responses.
- [x] Established Engineering Charter (`GUILD.md`).

---

### Next Priorities
1. **llama.cpp bindings**: Move from mock inference to real local models.
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
