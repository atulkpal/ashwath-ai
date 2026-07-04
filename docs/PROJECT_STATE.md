# Ashwath AI Project State

This document provides a real-time operational dashboard for the Ashwath AI repository.

---

### Meta Information
<<<<<<< HEAD
- **Project Version**: 0.1.1
- **Current Milestone**: EPIC 3: Real Inference (llama.cpp)
- **Last Updated**: 2024-03-21
=======
- **Project Version**: 0.1.0
- **Current Milestone**: Sprint W3A – Engine Integration Architecture
- **Last Updated**: 2026-07-04
>>>>>>> feature/web-client

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Platform** | Stable | Platform Team |
<<<<<<< HEAD
| **Android Client** | Stable | Android Client Team |
| **Web Client** | Planned | Web Client Team |
=======
| **Android Client** | Integration | Android Client Team |
| **Web Client** | Active Development | Web Client Team |
>>>>>>> feature/web-client
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

<<<<<<< HEAD
- **Platform Team**: Initializing llama.cpp Go bindings (EPIC 3).
- **Android Client Team**: Enhancing Chat UI with real model metadata.
- **Web Client Team**: gRPC-Web exploration and WASM build path.
- **Research Lab**: Evaluating GGUF quantization performance.
=======
- **Platform Team**: Defining the web engine integration architecture and preparing the shared API surface for Sprint W3A.
- **Android Client Team**: Continuing engine integration validation while cross-platform contracts are finalized.
- **Web Client Team**: Advancing the web client ↔ Go engine integration architecture, focused on transport, streaming, lifecycle, and SDK boundaries.
- **Research Lab**: Evaluating quantization techniques for mobile NPUs.
>>>>>>> feature/web-client

---

### Sprint Milestones
- [x] Sprint W0 — Design System v1.0 completed.
- [x] Sprint W1 — Web Application Shell completed.
- [x] Sprint W2 — Chat Workspace completed.

---

### Recently Completed
- [x] Migrated Android to **Embedded Go Runtime** (.so) via JNI.
- [x] Automated Go Engine build within the Gradle pipeline.
- [x] Established shared gRPC loopback architecture for all platforms.
- [x] Verified end-to-stream pipeline in the Chat UI.
- [x] Implemented Kotlin SDK with real gRPC stubs.
- [x] Established Engineering Charter (`GUILD.md`).
- [x] Delivered the foundational design language and web UI primitives for Sprint W0.
- [x] Delivered the web application shell and core navigation framework for Sprint W1.
- [x] Delivered the chat workspace experience and conversation input flow for Sprint W2.

---

### Next Priorities
1. **Web ↔ Engine Integration Architecture**: Finalize the transport layer, streaming model, engine lifecycle, SDK boundary, and migration path for Sprint W3A.
2. **llama.cpp bindings**: Move from mock inference to real local models.
3. **Model Management**: UI for downloading and switching between specific models.
4. **App Lifecycle**: Pausing/Stopping the engine process when app is backgrounded.

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
