# Ashwath AI Project State

**Operational Dashboard** — Last Updated: 2026-07-05

---

### Meta Information
- **Project Version**: 0.2.0
- **Current Milestone**: EPOCH 1 — Client Polish (Engine Stability + Android App v1 + Web Frontend v1)
- **Last Updated**: 2026-07-05

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Engine** | v1 Certified, Stable | Engine Agent |
| **Android Client** | Stable (needs polish) | Android Agent |
| **Web Client** | Active Development | Web Agent |
| **Research Lab** | Experimental | Research Agent |
| **Marketing** | Future | Marketing Agent |
| **Documentation** | Governed | Chief Architect |

---

### Repository Health

- **Build Status**: ✅ Passing (Engine `go build`, Engine `go vet`)
- **Test Status**: ✅ Passing (Engine: 97+ tests, Android/SDK: 10+ tests)
- **Known Issues**:
  - Stitch/Google Fonts theme conflict blocks Android `assembleDebug`.
  - Downloads package has 0 tests (156 untested lines).
  - Download progress events not streamed via bus.
  - JNI error codes are not granular.
- **Technical Debt**:
  - `internal/downloads` — no test coverage.
  - llama.cpp binary not bundled in CI artifacts.
  - TypeScript SDK is stub only.

---

### Current Work

**Epoch 1 — Client Polish (July 2026)**

| Track | EPIC | Owner | Status |
|---|---|---|---|
| Engine | E4: Engine Stability | Engine Agent | Not started |
| Android | E5: Android App v1 | Android Agent | Not started |
| Web | E6: Web Frontend v1 | Web Agent | Not started |
| Platform | E12: Release Engineering | Chief Architect | Not started |
| Research | E13: Model Research | Research Agent | Not started |

---

### Completed Milestones

- [x] **EPIC 1: Engine MVP** — gRPC server, config, device detection, mock inference, model registry, logging, CI.
- [x] **EPIC 2: Android Engine Integration** — JNI bridge, .so, Kotlin SDK, ChatViewModel, ServiceLocator.
- [x] **EPIC 3: Engine Architecture Foundation** — All 5 phases (A–E): backend, bus, plugins, agent, providers, stabilization.
- [x] **EPIC G1: Governance Sprint 1** — Cleanup, rename, AGENT.md, DOCUMENTATION_GOVERNANCE.md, manifest, certification.
- [x] **Engine v1 Certification** — `arch-engine-v1` tag. All blocking issues resolved. Ready for parallel development.

### Next Priorities (Epoch 1)

1. **E4 (Engine)**: Downloads tests, progress streaming, llama bundling, JNI codes, benchmark CI.
2. **E5 (Android)**: Stitch fix, screen wiring, progress UI, offline state, lifecycle, tests.
3. **E6 (Web)**: gRPC-Web SDK, connection management, model browser, chat E2E, PWA, responsive.
4. **E12 (Release)**: Semver policy, pipeline audit, Android/web deploy, changelog gen.
5. **E13 (Research)**: Quant benchmarks, NPU study, eval harness.

---

### Important Decisions

- **ADR-001**: gRPC over localhost for engine communication.
- **ADR-002**: Multi-worktree monorepo for parallel development.
- **ADR-003**: Protobuf Lite for mobile binary size.
- **ADR-004**: Worktree-based development — `main` is integration branch.
- **ADR-005**: Web uses Ashwath AI Runtime + gRPC-Web architecture.
- **ADR-006**: Epoch + Track planning model — parallel tracks, epoch reviews every 3 weeks.
- **ADR-007**: Single Writer Principle — every file has exactly one owner.

---

### Repository Layout

| Directory | Purpose | Owner |
|---|---|---|
| `/android` | Android application (Compose + MVVM) | Android Agent |
| `/engine` | Go engine (gRPC server, agent, providers) | Engine Agent |
| `/sdk` | Cross-platform SDKs (Kotlin, Go, TS, Swift) | Engine Agent / Android Agent |
| `/web` | Web frontend (React + Vite + TypeScript) | Web Agent |
| `/research` | Model research, benchmark data | Research Agent |
| `/docs` | Platform documentation | Chief Architect |
| `/scripts` | Tooling, worktree setup | Chief Architect |
| `/design` | Synthetic Noir design system | Design Team |

---

### Notes for Contributors

- `main` is the integration branch — never commit directly.
- Feature worktrees: `feature/platform`, `feature/android-client`, `feature/web-client`, `feature/research`, `feature/release`.
- Each worktree is a separate directory under `worktrees/`.
- Documentation changes require Chief Architect approval (Single Writer Principle).
- See `AGENT.md` for full repository constitution and workflow rules.
