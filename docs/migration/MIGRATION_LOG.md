# Migration Log

> **Start**: 2026-07-06  
> **Status**: Active  

## Sprint M1.1 — Repository Backup & Stale Branch Archival

### Completed
- [x] Created full git bundle: `ashwathai-migration-backup-20260706.bundle` (31 refs, 90 MB)
- [x] Bundle verified: `ashwathai-migration-backup-20260706.bundle is okay`
- [x] Created archive tags for 6 stale branches:
  - `archive/feature/android-installer` → `5bf730c`
  - `archive/feature/desktop-frontend` → `5bf730c`
  - `archive/feature/engine-llama` → `5bf730c`
  - `archive/feature/engine-realtime` → `5bf730c`
  - `archive/feature/ios-frontend` → `5bf730c`
  - `archive/feature/web-frontend` → `5bf730c`
- [x] Deleted stale branches locally and from remote
- [x] Created `pre-migration-baseline` tag at `91f515c` (main HEAD)

## Sprint M1.2 — Stash Recovery & Config Drift Correction

### Completed
- [x] Reviewed stash `stash@{0}` — contains Android SDK/ViewModel real gRPC wiring (WIP)
- [x] Saved patch to `docs/migration/stash-recovered-wip.patch`
- [x] Compared with `feature/android-client` — all changes are superseded by more comprehensive versions in that branch
- [x] Dropped stash (no unique work lost)
- [x] Verified `.gitignore` already covers `engine/ashwathd.exe` via `*.exe` wildcard — no change needed
- [x] Verified `.idea/` is already in `.gitignore` and no `.idea/` files are tracked — no change needed
- [x] Updated `scripts/setup-worktrees.ps1`:
  - Changed worktree paths from `worktrees/` subdirectory to sibling directories (`AshwathAI-*`)
  - Fixed research branch name from `feature/research` to `research/lab`
  - Removed `release` workspace (not a feature branch)
  - Added worktree lifetime reminder (2-week rule)

## EPIC-M2: Branch Reconciliation

## Sprint M2.1 — feature/platform → main (EPIC-4 cherry-pick)

### Completed
- [x] Cherry-picked `d083153` — E4.1: downloads package test coverage (clean)
- [x] Cherry-picked `4495ae0` — E4.2: download progress streaming (conflict in registry.go — resolved)
- [x] Cherry-picked `f167ded` — fix: NewRegistry call signature (conflict in service_test.go — skipped, already resolved in main)
- [x] Cherry-picked `0409aeb` — E4.3: llama.cpp binary bundling + CI workflows (clean)
- [x] Cherry-picked `b251e92` — E4.4: JNI error code granularity (3 conflicts in bridge.go, bridge_jni.go, AshwathBridge.kt — all resolved)
- [x] Cherry-picked `0ae7ae5` — E4.5: engine benchmark tests + nightly CI (add/add conflict in benchmark_test.go — merged both test sets)
- [x] Cherry-picked `dea87a0` — E4.6: provider documentation (clean)
- [x] Cherry-picked `494ab60` — feat: Ollama auto-detection (conflict in main.go — kept server.Run architecture, added WithOllamaSource to server.go)
- [x] Cherry-picked `966aa52` — feat: model catalog + capability scoring (clean)
- [x] Cherry-picked `44d0df1` — feat: upstream model index fetcher (conflict in main.go — same pattern, added WithUpstreamIndex to server.go)
- [x] Cherry-picked `459fef0` — fix: update Go catalog URLs (clean)
- [x] Engine builds: `go build ./cmd/ashwathd` ✅
- [x] All engine tests pass: `go test ./...` ✅ (15 packages)
- [x] Web/ directory untouched: `git diff main..HEAD -- web/` is empty ✅
