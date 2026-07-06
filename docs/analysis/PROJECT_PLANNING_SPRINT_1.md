# Project Planning Sprint 1

**Date:** July 2026
**Author:** Chief Architect
**Engine Status:** v1 Certified, Governance Sprint 1 Complete

---

## Executive Summary

The Engine v1 certification unlocks a fundamental shift: **parallel multi-agent development across independent workstreams.**

Before this sprint, the project evolved linearly — one EPIC at a time, each building on the previous. The Engine, Android, and Web were tightly coupled because the Engine API was unstable.

With Engine v1 certified, the architecture is frozen. Android and Web can now build independently without blocking on Engine changes. Engine work continues in parallel on backend capabilities (Knowledge, Voice/Vision).

This sprint reorganizes the entire project around **five parallel tracks**, each with clear ownership, stable boundaries, and well-defined integration points.

---

## Key Decisions

### 1. EPICs → Epoch Model

Replace the flat EPIC list with a **two-dimensional Epoch + Track model**:

```
                 Track 1       Track 2       Track 3       Track 4       Track 5
                 Engine        Android       Web           Platform      Research
Epoch 1 (Jul)    E4-Stability  E5-App v1     E6-Web v1     E12-Release   E13-Models
Epoch 2 (Aug)    E7-Knowledge  E7-Knowledge  E7-Knowledge  —             —
                  (backend)     (Android UI)  (Web UI)
Epoch 3 (Sep)    E8-Voice/Vision             —             E10-Desktop   —
Epoch 4 (Oct)    —                           —             E11-iOS       —
```

**Why Epochs?**
- Parallel across tracks, sequential within tracks
- Integration points at epoch boundaries
- Each epoch is a planning window (2-3 weeks), not a fixed deadline
- Clear go/no-go at each epoch review
- **Epoch end dates are targets, not commitments.** If a story misses its epoch, it rolls to the next. No deadline pressure on quality.

### 2. Workspace Restructure

Standardize worktrees to match the new track structure:

| Worktree | Branch | Owner | Tracks |
|---|---|---|---|
| `worktrees/platform` | `feature/platform` | Engine Agent | Engine, SDK, backend |
| `worktrees/android` | `feature/android-client` | Android Agent | Android app |
| `worktrees/web` | `feature/web-client` | Web Agent | Web frontend |
| `worktrees/research` | `feature/research` | Research Agent | Model research |
| `worktrees/release` | `feature/release` | Chief Architect | CI/CD, release engineering |
| `worktrees/marketing` | `feature/marketing` | Marketing Agent (future) | Website, community, marketing |

### 3. Ownership Matrix

| Subsystem | Owner | Documents Owned |
|---|---|---|
| **Engine** | Engine Agent | ENGINE_API.md, ENGINE_CLIENT_CONTRACT.md, JNI_ARCHITECTURE.md, docs/engine/*, MODULE_BOUNDARIES.md |
| **Android** | Android Agent | ANDROID_ARCHITECTURE.md, docs/engineering/gradle-go-integration.md |
| **Web** | Web Agent | WEB_ARCHITECTURE.md |
| **Design** | Design Team | DESIGN_SYSTEM.md, design/* |
| **SDK (Kotlin)** | Android Agent | sdk/kotlin/* |
| **SDK (Go/TS/Swift)** | Engine Agent | sdk/go/*, sdk/typescript/*, sdk/swift/* |
| **Release Eng.** | Chief Architect | CHANGELOG.md, .github/workflows/* |
| **Documentation** | Chief Architect | AGENT.md, DOCUMENTATION_GOVERNANCE.md, REPOSITORY_MANIFEST.md, ROADMAP.md, EPICS.md, PROJECT_STATE.md, GUILD.md, DECISIONS.md, VISION.md, ARCHITECTURE.md, PLATFORM_GUIDE.md, PLATFORM_RULES.md, docs/analysis/* |
| **Research** | Research Agent | research/* |
| **Marketing** (Future) | Marketing Agent | marketing/*, website, community |

---

## Revised EPIC Structure

### COMPLETED (Historical)

| EPIC | Description | Proof |
|---|---|---|
| **EPIC 1** | Engine MVP — gRPC server, mock inference, config, device, models, CI | All 8 stories ✅ |
| **EPIC 2** | Android Engine Integration — JNI, embedded .so, Kotlin SDK, ChatViewModel | All 9 stories ✅ |
| **EPIC 3** | Engine Architecture — Event bus, plugins, agent runtime, providers, stabilization | All 5 phases (A-E) ✅ |
| **EPIC G1** | Governance Sprint 1 — Cleanup, rename, governance, manifest, certification | ✅ Tagged `arch-engine-v1` |

### EPOCH 1 — Client Polish (July 2026)

These three tracks run in parallel. No track blocks another.

#### EPIC 4: Engine Stability (E4-ENG)

**Owner:** Engine Agent
**Dependencies:** None (Engine v1 certified)
**Risk:** Low — maintenance work on stable codebase

**Stories:**
- [ ] E4.1: Downloads package test coverage (156 lines, 0 tests)
- [ ] E4.2: Download progress streaming via event bus (wire `TopicDownloadProgress`)
- [ ] E4.3: llama.cpp binary bundling in release pipeline
- [ ] E4.4: JNI error code granularity (numeric error codes from Go → Android)
- [ ] E4.5: Engine benchmark CI (nightly benchmark runs)
- [ ] E4.6: Provider documentation (how to write a new runtime provider)

**Definition of Done:** Downloads tested, progress events flowing, llama binary in releases, JNI errors granular.

#### EPIC 5: Android App v1 (E5-AND)

**Owner:** Android Agent
**Dependencies:** EPIC 3, Engine v1 (stable gRPC API)
**Risk:** Medium — Stitch theme conflict is a known blocker

**Stories:**
- [ ] E5.1: Resolve Stitch/Google Fonts theme conflict (`assembleDebug` passing)
- [ ] E5.2: Connect Explore and Library screens to wired ViewModels
- [ ] E5.3: Model download progress UI (progress bar during install)
- [ ] E5.4: Handle engine not installed / offline state properly
- [ ] E5.5: App lifecycle management (pause/stop engine when backgrounded)
- [ ] E5.6: Instrumented tests for download → install → connect flow
- [ ] E5.7: Fix deprecation warnings (icons, statusBar, use import)
- [ ] E5.8: Android CI reliability improvements

**Definition of Done:** `assembleDebug` green, all screens connected, instrumented tests pass, no deprecation warnings.

#### EPIC 6: Web Frontend v1 (E6-WEB)

**Owner:** Web Agent
**Dependencies:** EPIC 3, Engine v1 (stable gRPC API via Runtime)
**Risk:** Low — scaffold complete, engine SDK in progress

**Stories:**
- [ ] E6.1: gRPC-Web client in TypeScript SDK
- [ ] E6.2: Engine connection management (status: connected/disconnected/reconnecting)
- [ ] E6.3: Model browser UI (list installed models, install new)
- [ ] E6.4: Chat UI end-to-end (sends prompt → receives stream → displays messages)
- [ ] E6.5: Progressive Web App support (service worker, offline fallback)
- [ ] E6.6: Responsive layout (mobile + desktop breakpoints)

**Definition of Done:** End-to-end chat works, PWA installable, responsive on mobile/desktop.

---

### EPOCH 2 — Knowledge (August 2026)

#### EPIC 7: Knowledge & RAG (E7-KNOW)

**Backend Owner:** Engine Agent
**Android UI Owner:** Android Agent
**Web UI Owner:** Web Agent
**Dependencies:** EPIC 4 (Engine Stability — downloads need to be tested)

**Architecture Decisions:**
- Backend implements RAG pipeline (ingest → chunk → embed → store → retrieve → generate)
- Clients consume via new gRPC RPCs (SearchDocuments, IngestDocument, etc.)
- Vector store is engine-side (SQLite with vector extension or file-based)
- Each client implements its own knowledge management UI

**Backend Stories:**
- [ ] E7.B1: Vector store implementation (SQLite-based or similar)
- [ ] E7.B2: Document parser (PDF, text, markdown)
- [ ] E7.B3: Embedding generation (ONNX Runtime or llama.cpp embeddings API)
- [ ] E7.B4: Retrieval pipeline (search → rerank → augment → generate)
- [ ] E7.B5: New gRPC RPCs for knowledge operations
- [ ] E7.B6: Knowledge backend tests

**Android Stories:**
- [ ] E7.A1: Knowledge management UI (document list, upload, search)
- [ ] E7.A2: Document upload UI (file picker, progress)
- [ ] E7.A3: In-chat RAG retrieval indicator

**Web Stories:**
- [ ] E7.W1: Knowledge management UI (document list, upload, search)
- [ ] E7.W2: In-chat RAG retrieval indicator

**Definition of Done:** End-to-end RAG works on at least one client. User uploads document → searches → gets augmented response.

---

### EPOCH 3 — Intelligence (September 2026)

#### EPIC 8: Voice & Vision (E8-MODAL)

**Backend Owner:** Engine Agent
**Android UI Owner:** Android Agent
**Dependencies:** EPIC 7 (optional — can be developed in parallel with different models)

**Backend Stories:**
- [ ] E8.B1: Speech-to-Text integration (whisper.cpp or similar)
- [ ] E8.B2: Text-to-Speech integration
- [ ] E8.B3: Image description (multimodal model support)
- [ ] E8.B4: New gRPC RPCs for voice/vision operations

**Android Stories:**
- [ ] E8.A1: Voice input UI (microphone button → STT → generate)
- [ ] E8.A2: Voice output UI (TTS playback)
- [ ] E8.A3: Camera integration → image description

**Definition of Done:** Voice chat works on Android. Image description works on Android.

---

### EPOCH 4 — Platform Expansion (October 2026+)

#### EPIC 10: Desktop App (E10-DESKTOP)

**Owner:** Engine Agent
**Dependencies:** EPIC 4 (Engine Stability — binary bundling)

**Stories:**
- [ ] E10.1: Tauri app scaffold (or native framework)
- [ ] E10.2: Engine bundling (ashwathd included in installer)
- [ ] E10.3: Chat UI (native or web-based)
- [ ] E10.4: System tray integration

#### EPIC 11: iOS App (E11-IOS)

**Owner:** Engine Agent (future: iOS Agent)
**Dependencies:** EPIC 3 (stable gRPC API), EPIC 2 patterns (JNI → Swift equivalent)

**Stories:**
- [ ] E11.1: Swift SDK with gRPC client
- [ ] E11.2: Engine embedding (XCFramework or process-based)
- [ ] E11.3: Chat UI (SwiftUI)
- [ ] E11.4: Knowledge UI

#### EPIC 12: Release Engineering (E12-REL)

**Owner:** Chief Architect
**Dependencies:** None
**Priority:** Begin in Epoch 1 alongside client tracks

**Stories:**
- [ ] E12.1: Semantic versioning policy (documented)
- [ ] E12.2: Engine binary release automation (existing `release-engine.yml` — audit + improve)
- [ ] E12.3: Android release pipeline (signed APK/AAB)
- [ ] E12.4: Web deployment pipeline (static hosting)
- [ ] E12.5: Changelog generation automation
- [ ] E12.6: Integration test gate (all client tests must pass before engine release)

---

### ONGOING — No Fixed Epoch

#### EPIC 13: Model Research (E13-RESEARCH)

**Owner:** Research Agent
**Dependencies:** None (parallel to everything)

**Stories:**
- [ ] E13.1: GGUF quantization benchmark (q4, q5, q8 across architectures)
- [ ] E13.2: Mobile NPU feasibility study (Qualcomm, Apple, Samsung)
- [ ] E13.3: Model evaluation harness (perplexity, latency, memory)
- [ ] E13.4: Alternative runtime research (ONNX, TensorFlow Lite, ExecuTorch)

---

## Revised Roadmap

| Timeline | Engine Track | Android Track | Web Track | Platform Track | Research Track |
|---|---|---|---|---|---|
| **Jul 2026** | E4: Stability | E5: App v1 | E6: Web v1 | E12: Release | E13: Models |
| **Aug 2026** | E7: Knowledge BE | E7: Knowledge UI | E7: Knowledge UI | E12 cont. | E13 cont. |
| **Sep 2026** | E8: Voice/Vision BE | E8: Voice/Vision UI | — | E10: Desktop | E13 cont. |
| **Oct 2026** | E9: Plugins v2 | E9: Plugins config | — | E11: iOS | E13 cont. |
| **Nov 2026+** | Public v1.0 | Public v1.0 | Public v1.0 | Public v1.0 | — |

---

## Dependency Graph

```
EPIC 4 (Engine Stability)
  ├── no dependencies (Engine v1 certified)
  ├── required by: EPIC 7 (needs tested downloads)
  ├── required by: EPIC 10 (needs binary bundling)
  └── required by: EPIC 12 (needs release pipeline improvements)

EPIC 5 (Android App v1)
  ├── depends on: EPIC 3 (stable gRPC API)
  ├── depends on: Engine v1 (frozen API)
  └── no engine dependencies — fully parallel

EPIC 6 (Web Frontend v1)
  ├── depends on: EPIC 3 (stable gRPC API via Runtime)
  ├── depends on: Engine v1 (frozen API)
  └── no engine dependencies — fully parallel

EPIC 7 (Knowledge & RAG)
  ├── depends on: EPIC 4 (tested downloads)
  ├── depends on: Engine v1 (frozen API)
  └── Android/Web UIs are parallel sub-tracks

EPIC 8 (Voice & Vision)
  ├── depends on: Engine v1 (frozen API)
  └── can be parallel with EPIC 7

EPIC 10 (Desktop)
  ├── depends on: EPIC 4 (binary bundling)
  └── depends on: Engine v1

EPIC 11 (iOS)
  ├── depends on: EPIC 2 patterns (JNI → Swift)
  └── depends on: Engine v1

EPIC 12 (Release Engineering)
  ├── no dependencies
  └── enables all other tracks

EPIC 13 (Research)
  └── no dependencies — fully parallel
```

---

## Workspace Recommendations

| Action | Path | Current | Recommendation |
|---|---|---|---|
| **Keep** | `feature/platform` | ✅ Exists | Rename setup script to match |
| **Keep** | `feature/android-client` | ✅ Exists | Update setup script |
| **Keep** | `feature/web-client` | ✅ Exists | Update setup script |
| **Add** | `feature/research` | ❌ Missing | New — Research Lab worktree |
| **Add** | `feature/release` | ❌ Missing | New — CI/CD, release engineering |
| **Remove** | `feature/android-installer` | ⚠️ In script | Rename to `feature/android-client` |
| **Remove** | `feature/ios-frontend` | ⚠️ In script | Replace with `feature/ios` when active |
| **Remove** | `feature/desktop-frontend` | ⚠️ In script | Replace with `feature/desktop` when active |

**Worktree directory convention:** `worktrees/<track-name>` matching branch `feature/<track-name>`.

---

## Current Blockers & Risks

| Blocker | Track | Severity | Resolution |
|---|---|---|---|
| Stitch/Google Fonts theme conflict | E5-AND | HIGH | E5.1 — first priority for Android Agent |
| Downloads package has 0 tests | E4-ENG | MEDIUM | E4.1 — first priority for Engine Agent |
| llama.cpp binary not in CI artifacts | E4-ENG, E10-DESKTOP | MEDIUM | E4.3 — bundle in release pipeline |
| JNI error codes are not granular | E5-AND | LOW | E4.4 — nice to have |
| Download progress not streamed | E5-AND, E6-WEB | MEDIUM | E4.2 — needed for UI progress bars |
| TypeScript SDK is stub only | E6-WEB | MEDIUM | E6.1 — first priority for Web Agent |

---

## Execution Order

### Week 1 (immediate)
| Agent | Task |
|---|---|
| **Engine Agent** | E4.1 (Downloads tests), E4.3 (llama binary bundling) |
| **Android Agent** | E5.1 (Stitch fix — unlock `assembleDebug`) |
| **Web Agent** | E6.1 (gRPC-Web TS SDK), E6.2 (connection management) |
| **Chief Architect** | E12.1 (semver policy), update setup-worktrees.ps1 |

### Week 2
| Agent | Task |
|---|---|
| **Engine Agent** | E4.2 (download progress via bus), E4.4 (JNI error codes) |
| **Android Agent** | E5.2 (Explore/Library screens), E5.3 (download progress UI) |
| **Web Agent** | E6.3 (model browser), E6.4 (chat end-to-end) |

### Week 3
| Agent | Task |
|---|---|
| **Engine Agent** | E4.5 (benchmark CI), E4.6 (provider docs) |
| **Android Agent** | E5.4 (offline state), E5.5 (lifecycle), E5.6 (instrumented tests) |
| **Web Agent** | E6.5 (PWA), E6.6 (responsive layout) |
| **Chief Architect** | E12.2-E12.5 (release pipeline improvements) |

### Epoch Review (end of Week 3)
- All three tracks present at review
- Go/no-go for Epoch 2 (Knowledge)

---

## Risks

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Stitch fix takes >1 week | Medium | High — blocks all Android UI | Escalate to Chief Architect; consider workaround |
| Downloads tests reveal bugs | Medium | Medium — could delay E4 | Fix immediately; E4.1 is first priority |
| gRPC-Web has compatibility issues | Low | Medium — blocks Web E2E | Use Runtime as fallback; document constraints |
| Multiple agents modify engine simultaneously | Low | High — merge conflicts | Single Writer Principle; only Engine Agent modifies `engine/internal/` |
| Epoch scope creep | Medium | Medium — missed deadlines | Strict Definition of Done; unpushed stories deferred to next epoch |

---

## Final Recommendations

1. **Adopt the Epoch + Track model.** It enables true parallel development while maintaining coordination at epoch boundaries.
2. **Standardize worktree branches.** Update `setup-worktrees.ps1` to match the AGENT.md convention.
3. **Begin all three Epoch 1 tracks simultaneously.** Engine, Android, and Web are now decoupled.
4. **Assign EPIC ownership explicitly.** Every EPIC has exactly one owner.
5. **Schedule epoch reviews every 3 weeks.** All tracks present at the same review.
6. **Do not begin Epoch 2 (Knowledge) until Epoch 1 is reviewed.** The Knowledge backend depends on Engine Stability improvements.
