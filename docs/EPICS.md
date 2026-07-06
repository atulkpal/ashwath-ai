# EPICs

> **Planning model:** Epochs (time-boxed parallel tracks) with EPICs as units of work.
> See `docs/analysis/PROJECT_PLANNING_SPRINT_1.md` for the full planning context.

---

## Foundation (Complete)

### EPIC-1: Engine MVP ✅

**Goal**: Go engine compiles, starts, and serves a gRPC API with mock responses.

**Owner:** Engine Team

- [x] E1.1: Implement gRPC server with service stubs (JSON codec, all 5 RPCs)
- [x] E1.2: Implement config loading from file/env
- [x] E1.3: Implement device detection
- [x] E1.4: Implement mock inference engine (streaming, cancellation)
- [x] E1.5: Implement model registry (4 hardcoded models)
- [x] E1.6: Implement structured logging
- [x] E1.7: Engine release pipeline in CI (7 targets)
- [x] E1.8: Unit test suite (21 tests)

### EPIC-2: Android Engine Integration ✅

**Goal**: Android app embeds and communicates with the Go engine via JNI.

**Owner:** Android Team

- [x] E2.1–E2.9: JNI bridge, embedded .so, Kotlin SDK, ChatViewModel, ServiceLocator, end-to-end verified

### EPIC-3: Engine Architecture Foundation ✅ (A–E)

**Goal**: Event bus, plugin system, agent runtime, provider decoupling, stabilization.

**Owner:** Engine Team

- [x] E3.A: Engine Foundation + Android Integration (15 stories)
- [x] E3.B: Architecture Foundation — bus, plugins, source abstraction, module boundaries
- [x] E3.C: Runtime — memory, context assembly, tool pipeline, agent orchestrator
- [x] E3.D: Runtime Providers — provider registry, mock, llama, server decoupling
- [x] E3.E: Stabilization — code review, edge tests, naming, final docs

### EPIC-G1: Governance Sprint 1 ✅

**Goal**: Repository cleanup, rename, documentation governance, engine v1 certification.

**Owner:** Chief Architect

- [x] G1.1: Repository cleanup (6 obsolete files deleted)
- [x] G1.2: Ashwath.AI → Ashwath AI rename (34 files)
- [x] G1.3: AGENT.md rewrite (18-section repository constitution)
- [x] G1.4: DOCUMENTATION_GOVERNANCE.md (Single Writer Principle)
- [x] G1.5: Consistency audit + fixes
- [x] G1.6: REPOSITORY_MANIFEST.md
- [x] G1.7: Engine v1 certification (`arch-engine-v1`)
- [x] G1.8: Project Planning Sprint 1

---

## Epoch 1 — Client Polish (July 2026)

Three parallel tracks. No track blocks another.

### EPIC-4: Engine Stability (E4-ENG)

**Owner:** Engine Agent
**Dependencies:** None (Engine v1 certified)

- [ ] E4.1: Downloads package test coverage (156 lines, 0 tests)
- [ ] E4.2: Download progress streaming via event bus
- [ ] E4.3: llama.cpp binary bundling in release pipeline
- [ ] E4.4: JNI error code granularity (numeric error codes from Go)
- [ ] E4.5: Engine benchmark CI (nightly benchmark runs)
- [ ] E4.6: Provider documentation (how to write a new runtime provider)

### EPIC-5: Android App v1 (E5-AND)

**Owner:** Android Agent
**Dependencies:** EPIC 3, Engine v1 (stable API)

- [ ] E5.1: Resolve Stitch/Google Fonts theme conflict
- [ ] E5.2: Connect Explore and Library screens to wired ViewModels
- [ ] E5.3: Model download progress UI
- [ ] E5.4: Handle engine not installed / offline state
- [ ] E5.5: App lifecycle management (pause/stop engine when backgrounded)
- [ ] E5.6: Instrumented tests for download → install → connect flow
- [ ] E5.7: Fix deprecation warnings (icons, statusBar, use import)
- [ ] E5.8: Android CI reliability improvements

### EPIC-6: Web Frontend v1 (E6-WEB)

**Owner:** Web Agent
**Dependencies:** EPIC 3, Engine v1 (stable API via Runtime)

- [ ] E6.1: gRPC-Web client in TypeScript SDK
- [ ] E6.2: Engine connection management (connected/disconnected/reconnecting)
- [ ] E6.3: Model browser UI (list, install, remove models)
- [ ] E6.4: Chat UI end-to-end (prompt → stream → display)
- [ ] E6.5: Progressive Web App support (service worker, offline fallback)
- [ ] E6.6: Responsive layout (mobile + desktop breakpoints)

---

## Epoch 2 — Knowledge (August 2026)

### EPIC-7: Knowledge & RAG (E7-KNOW)

**Backend Owner:** Engine Agent
**Android UI Owner:** Android Agent
**Web UI Owner:** Web Agent
**Dependencies:** EPIC 4 (tested downloads)

**Backend Stories:**
- [ ] E7.B1: Vector store implementation
- [ ] E7.B2: Document parser (PDF, text, markdown)
- [ ] E7.B3: Embedding generation
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

---

## Epoch 3 — Intelligence (September 2026)

### EPIC-8: Voice & Vision (E8-MODAL)

**Backend Owner:** Engine Agent
**Android UI Owner:** Android Agent
**Dependencies:** EPIC 7 (optional parallel)

**Backend Stories:**
- [ ] E8.B1: Speech-to-Text integration (whisper.cpp)
- [ ] E8.B2: Text-to-Speech integration
- [ ] E8.B3: Image description (multimodal model support)
- [ ] E8.B4: New gRPC RPCs for voice/vision

**Android Stories:**
- [ ] E8.A1: Voice input UI (microphone → STT → generate)
- [ ] E8.A2: Voice output UI (TTS playback)
- [ ] E8.A3: Camera integration → image description

---

## Epoch 4 — Platform Expansion (October 2026+)

### EPIC-10: Desktop App (E10-DESKTOP)

**Owner:** Engine Agent
**Dependencies:** EPIC 4 (binary bundling)

- [ ] E10.1: Tauri app scaffold
- [ ] E10.2: Engine bundling (ashwathd in installer)
- [ ] E10.3: Chat UI
- [ ] E10.4: System tray integration

### EPIC-11: iOS App (E11-IOS)

**Owner:** Engine Agent (future: iOS Agent)
**Dependencies:** EPIC 3 (stable API), EPIC 2 patterns

- [ ] E11.1: Swift SDK with gRPC client
- [ ] E11.2: Engine embedding (XCFramework)
- [ ] E11.3: Chat UI (SwiftUI)
- [ ] E11.4: Knowledge UI

### EPIC-12: Release Engineering (E12-REL)

**Owner:** Chief Architect
**Dependencies:** None (begins in Epoch 1)

- [ ] E12.1: Semantic versioning policy
- [ ] E12.2: Engine binary release automation audit
- [ ] E12.3: Android release pipeline (signed APK/AAB)
- [ ] E12.4: Web deployment pipeline (static hosting)
- [ ] E12.5: Changelog generation automation
- [ ] E12.6: Integration test gate

---

## Ongoing (No Fixed Epoch)

### EPIC-13: Model Research (E13-RESEARCH)

**Owner:** Research Agent
**Dependencies:** None

- [ ] E13.1: GGUF quantization benchmark (q4/q5/q8 across architectures)
- [ ] E13.2: Mobile NPU feasibility study
- [ ] E13.3: Model evaluation harness (perplexity, latency, memory)
- [ ] E13.4: Alternative runtime research (ONNX, TFLite, ExecuTorch)
