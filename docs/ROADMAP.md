# Ashwath AI Roadmap

> **Model:** Five parallel tracks, coordinated at epoch boundaries (every 2-3 weeks).
> Epochs are **planning windows**, not fixed deadlines. Stories that miss one epoch roll to the next.
> See `docs/analysis/PROJECT_PLANNING_SPRINT_1.md` for full context.

---

## Foundation ✅ (Complete)

All foundation work is delivered. Engine v1 is certified. Repository governance is established.

| Milestone | Date | Tag |
|---|---|---|
| Platform Foundation | March 2026 | — |
| Engine MVP (EPIC 1) | March 2026 | — |
| Android Engine Integration (EPIC 2) | June 2026 | — |
| Engine Architecture (EPIC 3 A–E) | July 2026 | — |
| Governance Sprint 1 | July 2026 | — |
| Engine v1 Certification | July 2026 | `arch-engine-v1` |
| Project Planning Sprint 1 | July 2026 | — |

---

## Epoch 1 — Client Polish (July 2026)

```
Engine Track           Android Track         Web Track            Platform Track       Research Track
────────────────────   ────────────────      ────────────────     ─────────────────    ────────────────
E4: Stability          E5: App v1            E6: Web v1           E12: Release Eng.    E13: Research
  Downloads tests       Stitch fix            gRPC-Web SDK         Semver policy        Quant benchmarks
  Progress events       Screen wiring         Connection mgmt      Pipeline audit       NPU study
  Llama bundling        Progress UI           Model browser        Android release      Eval harness
  JNI error codes       Offline state         Chat end-to-end      Web deploy
  Benchmark CI          Lifecycle mgmt        PWA support          Changelog gen
  Provider docs         Instrumented tests    Responsive layout
```

---

## Epoch 2 — Knowledge (August 2026)

```
Engine Track           Android Track         Web Track
────────────────────   ────────────────      ────────────────
E7: Knowledge BE       E7: Knowledge UI      E7: Knowledge UI
  Vector store          Document list         Document list
  Document parser       Upload UI             Upload UI
  Embedding gen         RAG indicator         RAG indicator
  Retrieval pipeline
  gRPC RPCs
```

---

## Epoch 3 — Intelligence (September 2026)

```
Engine Track           Android Track
────────────────────   ────────────────
E8: Voice/Vision BE    E8: Voice/Vision UI
  STT (whisper.cpp)     Voice input
  TTS                   Voice output
  Image description     Camera integration
  gRPC RPCs
```

---

## Epoch 4 — Platform Expansion (October 2026+)

```
Engine Track           Platform Track
────────────────────   ─────────────────
E9: Plugins v2         E10: Desktop
  External loading       Tauri scaffold
  Plugin manager v2      Engine bundling
                        E11: iOS
                          Swift SDK
                          Engine embed
                          Chat UI
```
