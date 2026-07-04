# Ashwath AI Project State

This document provides a real-time operational dashboard for the Ashwath AI repository.

---

### Meta Information
- **Project Version**: 0.1.0
- **Current Milestone**: Sprint W3 – Web Client ↔ Go Engine Integration Planning
- **Last Updated**: 2026-07-04

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Platform** | Stable | Platform Team |
| **Android Client** | Integration | Android Client Team |
| **Web Client** | Active Development | Web Client Team |
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

- **Platform Team**: Defining the web engine integration contract and preparing the shared API surface for Sprint W3.
- **Android Client Team**: Continuing engine integration validation while cross-platform contracts are finalized.
- **Web Client Team**: Advancing the web client ↔ Go engine integration path, focused on transport, state flow, and UX alignment.
- **Research Lab**: Evaluating quantization techniques for mobile NPUs.

---

### Sprint Milestones
- [x] Sprint W0 — Design System v1.0 completed.
- [x] Sprint W1 — Web Application Shell completed.
- [x] Sprint W2 — Chat Workspace completed.

---

### Recently Completed
- [x] Implemented Kotlin SDK with real gRPC stubs.
- [x] Added `EngineInstaller` with SHA-256 verification.
- [x] Resolved "Permission Denied" (error 13) for engine execution on Android.
- [x] Integrated `ChatViewModel` with streaming gRPC responses.
- [x] Established Engineering Charter (`GUILD.md`).
- [x] Delivered the foundational design language and web UI primitives for Sprint W0.
- [x] Delivered the web application shell and core navigation framework for Sprint W1.
- [x] Delivered the chat workspace experience and conversation input flow for Sprint W2.

---

### Next Priorities
1. **Web ↔ Engine Integration Planning**: Define the client/engine contract, transport strategy, and state synchronization approach for Sprint W3.
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
