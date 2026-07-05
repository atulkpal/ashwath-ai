# Ashwath AI Project State

**Operational Dashboard** — Last Updated: 2026-07-04

---

### Meta Information
- **Project Version**: 0.2.0
- **Current Milestone**: GOVERNANCE SPRINT 1 — Repository Stabilization (cleanup, rename, documentation governance)
- **Last Updated**: 2026-07-04

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Platform (Engine + SDK)** | Stable | Platform Team |
| **Android Client** | Stable | Android Client Team |
| **Web Client** | Active Development | Web Client Team |
| **Research Lab** | Experimental | Research Lab |
| **Documentation** | Active | Shared |

---

### Repository Health

- **Build Status**: ✅ Passing (Android `assembleDebug`, Engine `go build`)
- **Test Status**: ✅ Passing (Engine: 42+ tests, Android/SDK: 10+ tests)
- **Known Issues**:
  - SELinux restrictions on physical devices bypassed via Embedded Runtime.
  - Automatic Go build requires Go SDK in system PATH.
  - Stitch/Google Fonts theme conflict in Android Compose (separate workspace).
- **Technical Debt**:
  - `EmbeddedInferenceEngine` JNI bridge needs granular error codes from Go.
  - Model download progress streaming is unary (no streaming progress RPC yet).
  - Android UI compile depends on Stitch fix for full `assembleDebug` success.

---

### Current Work

- **Governance Sprint 1**: Repository stabilization — cleanup, rename, documentation governance, consistency audit, repository manifest.
- **Platform Team**: EPIC 3 complete (all phases A–E). Awaiting next EPIC.
- **Android Client Team**: Awaiting UI compile fix (Stitch); model management ViewModels wired and tested.
- **Web Client Team**: Continuing engine SDK and runtime foundation for browser-based interaction.
- **Research Lab**: Evaluating GGUF quantization performance and mobile NPU targets.

---

### Completed Milestones

- [x] **EPIC 1: Engine MVP** — gRPC server, config, device detection, mock inference, model registry, logging, CI release pipeline.
- [x] **EPIC 2: Android Engine Integration** — JNI bridge, embedded .so, Gradle build, Kotlin SDK gRPC client, ChatViewModel wired, ServiceLocator.
- [x] **EPIC 3 Phase A: Engine Foundation & Android Integration** — Backend selection, model download/registry persistence, RemoveModel RPC, real benchmark implementation, proto drift fix, Android build fixed, Web merged into main, all worktrees synchronized.
- [x] **EPIC 3 Phase B: Architecture Foundation** — Event System (internal/bus), Plugin Framework (internal/plugins), Model Abstraction Layer (internal/models), Module Boundaries documented.
- [x] **EPIC 3 Phase C: Runtime** — Memory Architecture, Context Assembly, Tool Execution Pipeline, Agent Runtime (internal/agent).
- [x] **EPIC 3 Phase D: Runtime Providers** — Provider Registry, Mock provider, Llama provider, Server decoupling.
- [x] **EPIC 3 Phase E: Stabilization** — Dead code removal, edge case tests, naming consistency, final documentation.

### Next Priorities

1. **GOVERNANCE SPRINT 1**: Finalize repository manifest, commit all documentation changes.
2. **Android UI Fix**: Resolve Stitch/Google Fonts conflict for full UI compile.
3. **Model Management UI**: Connect Explore and Library screens to wired ViewModels.
4. **App Lifecycle**: Pause/stop engine when app is backgrounded.

---

### Important Decisions

- **ADR-001**: Adoption of gRPC over localhost for engine communication.
- **ADR-002**: Multi-worktree monorepo structure for parallel development.
- **ADR-003**: Selection of Protobuf Lite for mobile binary size optimization.
- **ADR-004**: Worktree-based development — `main` is integration branch; all implementation in feature worktrees.
- **ADR-005**: Web Client uses Ashwath AI Runtime + gRPC-Web architecture (not direct engine access).

---

### Repository Layout

- `/android`: Flagship Android application (Compose + MVVM).
- `/engine`: Go-based AI execution core.
- `/sdk`: Cross-platform language bindings (Kotlin, TS, Go).
- `/web`: Browser-based frontend (React + Vite + TypeScript).
- `/research`: Experimental models and lab scripts.
- `/docs`: Global platform documentation and Architecture.
- `/scripts`: Tooling for repository setup and CI/CD.
- `/design`: Shared design assets and Synthetic Noir design system.

---

### Notes for Contributors

- `main` is the integration branch — never commit directly to it.
- All implementation belongs in feature worktrees (`feature/platform`, `feature/android-client`, `feature/web-client`).
- Merge latest `main` into feature branches before debugging.
- Documentation lives in `docs/`; workspace-specific progress in `docs/analysis/`.
- Respect the **Engineering Charter** in `docs/GUILD.md`.
