# Ashwath AI Project State

**Operational Dashboard** — Last Updated: 2026-07-06

---

### Meta Information
- **Project Version**: 0.2.0
- **Branching Model**: Trunk-Based Development + Release Branches
- **Release Branch**: `release/v0.2.x`
- **Current Milestone**: EPOCH 1 — Client Polish (Engine Stability + Android App v1 + Web Frontend v1)

---

### Repository Status

| Subsystem | Status | Primary Maintainer |
| :--- | :--- | :--- |
| **Engine** | v1 Certified, Stable | Engine Agent |
| **Android Client** | Stable (needs polish) | Android Agent |
| **Web Client** | Active Development | Web Agent |
| **Documentation** | Governed | Chief Architect |

---

### Repository Health

- **Build Status**: ✅ Passing (Engine `go build`, Engine `go vet`, Android `buildGoEngine` both ABIs)
- **Test Status**: ✅ Passing (Engine: 100+ tests, Android/SDK: 10+ tests)
- **Known Issues**:
  - Stitch/Google Fonts theme conflict blocks Android `assembleDebug`.
  - TypeScript SDK needs feature parity with Kotlin SDK.
- **Technical Debt**:
  - llama.cpp binary not bundled in CI artifacts.
  - Go SDK (`sdk/go/`) is scaffold only.

---

### Completed Milestones

- [x] **EPIC 1-3, G1**: Engine MVP, Android Integration, Architecture Foundation, Governance Sprint.
- [x] **Engine v1 Certification** — `arch-engine-v1` tag.
- [x] **EPIC-M1: Pre-Migration Audit** — Git bundle backup, stale branches archived, stash recovered, config drift fixed.
- [x] **EPIC-M2: Branch Reconciliation** — `feature/platform`, `feature/android-client`, `feature/web-client` reconciled into `main`. `research/lab` archived.
- [x] **EPIC-M3: Governance Enablement** — PR template, Web CI, Integration Gate, Engine Consistency Check, `release/v0.2.x` branch, tag standardization.
- [x] **EPIC-M4: Documentation Cleanup** — Obsolete docs archived, AGENT.md updated, lockfile regenerated.
- [x] **EPIC-4: Engine Stability** — Downloads tests (83.8%), event bus, llama bundling, JNI codes, benchmark CI, provider docs.
- [x] **EPIC-6: Web v1 (in progress)** — Client-side routing, 5 pages, sidebar navigation, conversation persistence, PWA, responsive layout.

---

### Next Priorities (Epoch 1)

1. **E5 (Android)**: Stitch fix, screen wiring, progress UI, offline state, lifecycle, tests.
2. **E6 (Web)**: gRPC-Web SDK polish, wire real engine status to UI, model browser page.
3. **E12 (Release)**: Semver policy, pipeline audit, Android/web deploy, changelog gen.
4. **E13 (Research)**: Quant benchmarks, NPU study, eval harness.

---

### Important Decisions

- **ADR-001**: gRPC over localhost for engine communication.
- **ADR-003**: Protobuf Lite for mobile binary size.
- **ADR-005**: Web uses Ashwath AI Runtime + gRPC-Web architecture.
- **ADR-006**: Epoch + Track planning model — parallel tracks, epoch reviews every 3 weeks.
- **ADR-007**: Single Writer Principle — every file has exactly one owner.
- **ADR-008**: Trunk-Based Development + Release Branches (replaces worktree branching model).

---

### Repository Layout

| Directory | Purpose | Owner |
|---|---|---|
| `/android` | Android application (Compose + MVVM) | Android Agent |
| `/engine` | Go engine (gRPC server, agent, providers) | Engine Agent |
| `/sdk` | Cross-platform SDKs (Kotlin, Go, TS) | Engine Agent / Android Agent |
| `/web` | Web frontend (React + Vite + TypeScript) | Web Agent |
| `/docs` | Platform documentation | Chief Architect |
| `/scripts` | Tooling, worktree setup | Chief Architect |
| `/design` | Synthetic Noir design system | Design Team |

---

### Notes for Contributors

- All development follows Trunk-Based Development: feature branches off `main`, squash-merge via PR.
- See `AGENT.md` for full repository constitution, branch naming, worktree policy, and merge rules.
- Documentation follows Single Writer Principle — every file has exactly one owner.
- GitHub branch protection on `main` is required (PR, status checks, linear history) — enable via repo Settings.
