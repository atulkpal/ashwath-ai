# Ashwath.AI — Repository Migration: EPICs & Implementation Roadmap

> **Status**: Implementation Plan  
> **Author**: Lead Repository Architect  
> **Date**: 2026-07-06  
> **Prerequisite**: `docs/REPOSITORY_ARCHITECTURE_PROPOSAL.md`

---

## Overview

This roadmap decomposes the migration strategy (from `docs/REPOSITORY_ARCHITECTURE_PROPOSAL.md`) into **4 EPICs** and **13 Sprints**. Each sprint is size-constrained to 1-3 days of work and maps to the existing EPIC numbering scheme (EPIC-M1 through EPIC-M4, with the `M` prefix for "Migration").

Each sprint includes: objective, concrete tasks, verification criteria, and risk notes.

---

## EPIC-M1: Pre-Migration Audit & Hygiene

**Goal**: Safe baseline. No destructive operations without backup. Archive dead branches, bail out stash, fix config drift.

| Sprint | Name | Duration | Dependencies |
|--------|------|----------|-------------|
| M1.1 | Repository Backup & Stale Branch Archival | 1 day | None |
| M1.2 | Stash Recovery & Config Drift Correction | 1 day | M1.1 |

---

### Sprint M1.1 — Repository Backup & Stale Branch Archival

**Objective**: Create a recoverable snapshot of the entire repository state and clean up branch listings.

**Tasks**:

1. Create full git bundle:
   ```
   git bundle create ashwathai-migration-backup-YYYYMMDD.bundle --all
   ```

2. Verify bundle integrity:
   ```
   git bundle verify ashwathai-migration-backup-YYYYMMDD.bundle
   ```

3. Tag each stale branch before reference deletion:
   - `git tag archive/feature/android-installer feature/android-installer`
   - `git tag archive/feature/desktop-frontend feature/desktop-frontend`
   - `git tag archive/feature/engine-llama feature/engine-llama`
   - `git tag archive/feature/engine-realtime feature/engine-realtime`
   - `git tag archive/feature/ios-frontend feature/ios-frontend`
   - `git tag archive/feature/web-frontend feature/web-frontend`

4. Delete stale branches locally:
   - `git branch -D feature/android-installer`
   - `git branch -D feature/desktop-frontend`
   - `git branch -D feature/engine-llama`
   - `git branch -D feature/engine-realtime`
   - `git branch -D feature/ios-frontend`
   - `git branch -D feature/web-frontend`

5. Delete stale branches on remote:
   - `git push origin --delete feature/android-installer`
   - `git push origin --delete feature/desktop-frontend`
   - `git push origin --delete feature/engine-llama`
   - `git push origin --delete feature/engine-realtime`
   - `git push origin --delete feature/ios-frontend`
   - `git push origin --delete feature/web-frontend`

6. Tag current main HEAD as `pre-migration-baseline`:
   ```
   git tag pre-migration-baseline main
   ```

7. Create a document `docs/migration/MIGRATION_LOG.md` to record every action taken.

**Verification**:
- `git branch` shows only: `main`, `feature/android-client`, `feature/platform`, `feature/web-client`, `research/lab`
- `git tag | grep archive/` shows all 6 archived branches
- Bundle file exists and verifies cleanly
- `pre-migration-baseline` tag exists at main HEAD

**Risk**: If any stale branch has unpushed commits, the bundle ensures nothing is lost. Always verify the bundle before deleting.

---

### Sprint M1.2 — Stash Recovery & Config Drift Correction

**Objective**: Recover or document uncommitted work, fix `.gitignore` and worktree script.

**Tasks**:

1. Review `stash@{0}` ("WIP Android before web merge"):
   ```
   git stash show -p stash@{0} > docs/migration/stash-recovered-wip.patch
   ```
   - Inspect contents. If valuable, create a commit on a temp branch. If superseded, add note to MIGRATION_LOG.md.
   - `git stash drop stash@{0}`

2. Add `engine/ashwathd.exe` to root `.gitignore` under a `# Local build artifacts` section.

3. Fix `scripts/setup-worktrees.ps1`:
   - Update worktree paths to use sibling directories (not `worktrees/` subdirectory).
   - Rename `feature/research` to `research/lab` in script.
   - Add usage documentation header.

4. Verify `.idea/` is fully ignored:
   - `git ls-files --cached | Select-String '\.idea/'` to find any tracked `.idea/` files
   - If any found: `git rm --cached` each one

**Verification**:
- `git stash list` is empty
- `docs/migration/stash-recovered-wip.patch` exists (even if empty of content)
- `git check-ignore engine/ashwathd.exe` returns the path
- `git ls-files --cached | Select-String '\.idea/'` returns nothing
- `scripts/setup-worktrees.ps1` can be parsed without errors

---

## EPIC-M2: Branch Reconciliation

**Goal**: Produce a single coherent `main` branch containing all valuable work from every active branch.

| Sprint | Name | Duration | Dependencies |
|--------|------|----------|-------------|
| M2.1 | feature/platform → main (EPIC-4 cherry-pick) | 2 days | M1.2 |
| M2.2 | feature/android-client → main | 2 days | M2.1 |
| M2.3 | feature/web-client → main | 1 day | M2.2 |
| M2.4 | research/lab Review & Resolution | 1 day | M2.3 |

---

### Sprint M2.1 — feature/platform → main (EPIC-4 cherry-pick)

**Objective**: Incorporate EPIC-4 engine improvements without bringing in the `web/` deletion.

This is the **highest-risk sprint**. `feature/platform` removed `engine/internal/agent/`, `engine/internal/plugins/`, `engine/internal/server/server.go` and deleted the entire `web/` directory. A direct merge would destroy the web frontend. Cherry-picking individual commits is mandatory.

**Tasks**:

1. Create a temporary branch from `main`:
   ```
   git checkout -b reconciliation/platform-to-main main
   ```

2. Cherry-pick EPIC-4 commits in chronological order (oldest first to minimize conflicts):
   ```
   # E4.1: downloads package test coverage
   git cherry-pick d083153

   # E4.2: download progress streaming via event bus
   git cherry-pick 4495ae0

   # E4.3: llama.cpp binary bundling
   git cherry-pick 0409aeb

   # E4.4: JNI error code granularity
   git cherry-pick b251e92

   # E4.5: engine benchmark tests + nightly CI
   git cherry-pick 0ae7ae5

   # E4.6: provider documentation
   git cherry-pick dea87a0

   # Ollama model auto-detection
   git cherry-pick 494ab60

   # model catalog with capability scoring
   git cherry-pick 966aa52

   # upstream model index fetcher
   git cherry-pick 44d0df1

   # fix: update Go catalog to verified bartowski GGUF URLs
   git cherry-pick 459fef0
   ```

3. For each conflict:
   - `feature/platform` may have removed files that `main` still has (`engine/internal/agent/`, `engine/internal/plugins/`, `engine/internal/server/server.go`)
   - Resolution strategy: **Keep `main`'s version** of any file that `feature/platform` deleted. The agent/ and plugins/ packages exist on `main` and should stay. The cherry-pick should only bring new files and modifications.
   - Document each conflict resolution in `docs/migration/MIGRATION_LOG.md`

4. After all cherry-picks succeed:
   - `go build ./cmd/ashwathd` (engine compiles)
   - `go test ./...` (all engine tests pass)
   - Verify web/ directory is intact: `git diff --name-only main..HEAD -- web/` should show nothing (or only new files, never deletions)

5. Open a PR from `reconciliation/platform-to-main` → `main`, document all conflict resolutions in PR description.

6. Squash-merge to `main`.

**Verification**:
- `go build ./cmd/ashwathd` succeeds
- `go test ./...` passes with no regressions
- `git diff main --name-status -- web/` shows only additions, no deletions or modifications
- New files exist: `engine/internal/models/capability.go`, `engine/internal/models/catalog.go`, `engine/internal/models/upstream.go`, `engine/internal/models/ollama_source.go`, `engine/pkg/api/api.go`
- `.github/workflows/benchmark.yml` and `.github/workflows/build.yml` exist
- `scripts/bundle-llama.sh` exists

**Risk**: The cherry-pick of `966aa52` (model catalog) may conflict with `main`'s `engine/internal/models/models.go`. This is the most likely conflict point. The model registry on `main` and the one in `feature/platform` have overlapping but different implementations.

---

### Sprint M2.2 — feature/android-client → main

**Objective**: Merge all Android client improvements into the now-stable `main`.

`feature/android-client` is ahead of `main` with 58+ Android files changed and 7 engine-level model files. This sprint should use a standard merge (not cherry-pick) because the branch properly tracks `main` via prior merge commits.

**Tasks**:

1. Ensure `main` now contains the EPIC-4 changes from M2.1.

2. Create integration branch:
   ```
   git checkout -b reconciliation/android-to-main main
   ```

3. Merge `feature/android-client`:
   ```
   git merge feature/android-client
   ```

4. Resolve conflicts:
   - Engine model files: `engine/internal/models/models.go` likely conflicts (both `main` from M2.1 and `feature/android-client` changed it)
   - SDK Kotlin files: `sdk/kotlin/` files may conflict
   - `android/` files should merge cleanly (only `feature/android-client` touches them)

5. Verify build:
   - `go build ./cmd/ashwathd` (engine)
   - `cd android && ./gradlew assembleDebug` (Android app)
   - `cd android && ./gradlew test` (Android tests)

6. Verify engine consistency:
   ```
   git diff main..HEAD -- engine/
   ```
   Should show only engine changes that were part of `feature/android-client` (not already in `main` from M2.1).

7. Open PR, squash-merge to `main`.

**Verification**:
- Android app builds: `./gradlew assembleDebug` passes
- All Android tests pass: `./gradlew test` passes
- All engine tests pass: `go test ./...` passes
- No unexpected changes to `web/`: `git diff main -- web/` is clean

**Risk**: `feature/android-client` includes an `android_changes.patch` file (0 bytes). Investigate what this was meant for before merging. If it's a leftover artifact, exclude it (`git merge --no-commit` then `git reset android_changes.patch`).

---

### Sprint M2.3 — feature/web-client → main

**Objective**: Incorporate web frontend improvements into the reconciled `main`.

`feature/web-client` is ahead of its remote tracking by 36 commits. This is the most straightforward sprint because `feature/web-client` only touches `web/`, `sdk/typescript/`, and `docs/`.

**Tasks**:

1. Create integration branch:
   ```
   git checkout -b reconciliation/web-to-main main
   ```

2. Merge `feature/web-client`:
   ```
   git merge feature/web-client
   ```

3. Resolve conflicts (expected to be minimal — `web/` is not touched by other branches).

4. Verify:
   - `cd web && pnpm install && pnpm build` succeeds

5. Verify engine-side tools are unmodified:
   ```
   git diff main..HEAD -- engine/
   ```
   Should be empty.

6. Open PR, squash-merge to `main`.

**Verification**:
- `pnpm build` passes in `web/`
- `pnpm lint` passes in `web/`
- No engine changes leaked into the merge
- PWA workbox config (commit `50d8fc6`) is present

---

### Sprint M2.4 — research/lab Review & Resolution

**Objective**: Determine whether `research/lab` contains unique work not captured elsewhere.

**Tasks**:

1. Compare `research/lab` with `feature/android-client` and the EPIC-4 cherry-picks:
   - `git log research/lab --oneline` shows 8 commits, all EPIC-2 Android work
   - `feature/android-client` and M2.1 already supersede these (they contain later versions of the same work)

2. If confirmation holds: add archive tag, delete branch:
   ```
   git tag archive/research/lab research/lab
   git branch -D research/lab
   git push origin --delete research/lab
   ```

3. If any unique commit exists: cherry-pick to `main`, then archive.

4. Update `docs/migration/MIGRATION_LOG.md` with findings.

**Verification**:
- `archive/research/lab` tag exists
- `research/lab` branch deleted from local and remote
- MIGRATION_LOG.md documents the review and conclusion

---

## EPIC-M3: Governance & CI Enablement

**Goal**: Protect `main`, automate quality gates, and formalize the branching model.

| Sprint | Name | Duration | Dependencies |
|--------|------|----------|-------------|
| M3.1 | Branch Protection & PR Workflow | 1 day | M2.4 |
| M3.2 | Web CI & Integration Gates | 1 day | M3.1 |
| M3.3 | Release Branch Setup & Tag Standardization | 1 day | M3.2 |

---

### Sprint M3.1 — Branch Protection & PR Workflow

**Objective**: Prevent direct pushes to `main`, add PR template, update AGENT.md.

**Tasks**:

1. Enable GitHub branch protection on `main`:
   - Require pull request before merging
   - Require status checks (engine CI, android CI)
   - Require linear history (no merge commits — only squash merges and rebases)
   - Include administrators
   - (Optional) Require up-to-date branches before merging

2. Create `.github/PULL_REQUEST_TEMPLATE.md`:
   ```
   ## Summary
   <!-- Brief description of changes -->

   ## Related EPIC/Story
   <!-- e.g., EPIC-4, E4.3 -->

   ## Platforms affected
   - [ ] engine
   - [ ] android
   - [ ] web
   - [ ] sdk
   - [ ] docs
   - [ ] other: _______

   ## Testing performed
   <!-- List what was tested -->

   ## Documentation updated
   - [ ] AGENT.md
   - [ ] docs/ (specify which)
   - [ ] CHANGELOG.md
   - [ ] Not needed

   ## Breaking changes
   - [ ] Yes (describe)
   - [ ] No
   ```

3. Update `AGENT.md`:
   - `§7 Branch Policy`: Rewrite to describe Trunk-Based Development + Release Branches
   - `§8 Worktree Policy`: Replace cherry-pick sync with merge-based sync; add 2-week worktree lifetime rule
   - `§9 Merge Policy`: Change to squash-merge only on `main`; rebase for feature branches that need linear history
   - Add agent branch prefix convention to `§4 AI Agent Responsibilities`
   - Remove all references to cherry-pick sync

4. Add `CHANGELOG.md` policy: Every merged PR must include a changelog entry or the PR description must explain why it's not needed.

**Verification**:
- `git push origin main` (direct) is rejected by GitHub
- PR template renders on `github.com/ashwathai/ashwathai/pull/new`
- AGENT.md `§7-9` reflect the new model
- Build succeeds, tests pass

---

### Sprint M3.2 — Web CI & Integration Gates

**Objective**: Add missing CI workflows and an engine consistency check.

**Tasks**:

1. Create `.github/workflows/web-ci.yml`:
   ```yaml
   name: Web CI
   on:
     push: { paths: ['web/**', 'sdk/typescript/**'] }
     pull_request: { paths: ['web/**', 'sdk/typescript/**'] }
   jobs:
     build:
       runs-on: ubuntu-latest
       defaults: { run: { working-directory: web } }
       steps:
         - actions/checkout@v4
         - uses: pnpm/action-setup@v4
           with: { version: latest }
         - uses: actions/setup-node@v4
           with: { node-version: 20 }
         - run: pnpm install
         - run: pnpm lint
         - run: pnpm build
   ```

2. Create `.github/workflows/integration-gate.yml` (runs when proto changes):
   ```yaml
   name: Integration Gate
   on:
     pull_request:
       paths: ['engine/api/proto/**']
   jobs:
     verify:
       runs-on: ubuntu-latest
       steps:
         - actions/checkout@v4
         # Regenerate protos for all SDKs
         # Verify engine compiles with regenerated protos
         # Verify Android compiles with regenerated protos
         # Verify TypeScript compiles with regenerated protos
   ```

3. Create `.github/workflows/engine-consistency.yml`:
   ```yaml
   name: Engine Consistency Check
   on:
     push: { branches: [main] }
   jobs:
     check:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v4
           with: { fetch-depth: 0 }
         - run: |
             # Compare engine/ and sdk/ between main and active feature branches
             # If discrepancies found, post warning
   ```

4. Verify all workflows parse correctly.

**Verification**:
- Web CI triggers on `web/` changes
- Integration gate triggers on `engine/api/proto/` changes
- Engine consistency check runs on every `main` push
- All workflows have valid YAML syntax

---

### Sprint M3.3 — Release Branch Setup & Tag Standardization

**Objective**: Create the `release/v` branch pattern and standardize tags.

**Tasks**:

1. Create first release branch from reconciled `main`:
   ```
   git checkout -b release/v0.2.x main
   git push origin release/v0.2.x
   ```

2. Standardize existing tags:
   - `v0.1.1` — already compliant, keep
   - `engine/v0.1.0` — already compliant, keep
   - `android-v0.2.0` → migrate to `android/v0.2.0` (create new tag, deprecate old in comment)
   - `arch-engine-v1` — milestone tag, keep as-is but add clarifying annotation

3. Document tag convention in `AGENT.md` or a new `docs/releases/TAGGING.md`.

4. Add release workflow trigger for `android/v*` tags in `release-engine.yml` (or create `release-android.yml`).

**Verification**:
- `release/v0.2.x` branch exists locally and on remote
- `android/v0.2.0` tag exists
- Tag convention is documented
- `git tag -l` shows consistent naming

---

## EPIC-M4: Ongoing Improvements & Hardening

**Goal**: Address the "moderate risk" items, documentation entropy, and test gaps. These are lower urgency but should be completed within 2-3 weeks of the migration.

| Sprint | Name | Duration | Dependencies |
|--------|------|----------|-------------|
| M4.1 | Documentation Cleanup & Archive | 1 day | M3.3 |
| M4.2 | SDK Dependency Verification | 0.5 day | M3.3 |
| M4.3 | Integration Testing Strategy | 2 days | M4.2 |

---

### Sprint M4.1 — Documentation Cleanup & Archive

**Objective**: Reduce documentation entropy by archiving obsolete docs and updating stale ones.

**Tasks**:

1. Archive obsolete documents (move to `docs/archive/` or delete with justification):
   - `docs/ENGINE_CLIENT_CONTRACT.md` — is this superseded by `ENGINE_API.md`?
   - `docs/SYNC.md` — describes the old cherry-pick model, archive
   - `docs/analysis/SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md` — still relevant?
   - `docs/analysis/PROJECT_PLANNING_SPRINT_1.md` — historical sprint report
   - `docs/GUILD.md` — partially overlaps with AGENT.md; consolidate
   - `docs/PLATFORM_RULES.md` — partially overlaps with AGENT.md; consolidate

2. Update stale documents:
   - `docs/REPOSITORY_MANIFEST.md` — verify all paths still exist
   - `docs/PROJECT_STATE.md` — update for new branching model
   - `docs/ARCHITECTURE.md` — verify architecture diagram accuracy

3. Remove empty or stub directories if any:
   - `examples/` — has only a README.md that says "see engine/"
   - `tools/` — has only a README.md
   - `ios/`, `desktop/` — stub directories, keep but consider consolidating READMEs

**Verification**:
- `docs/` has no documents referencing the old cherry-pick sync model
- Every file in `docs/` is either current or in `docs/archive/`
- `REPOSITORY_MANIFEST.md` accurately reflects all top-level paths

---

### Sprint M4.2 — SDK Dependency Verification

**Objective**: Fix the suspiciously small `sdk/typescript/pnpm-lock.yaml` and verify all SDK builds.

**Tasks**:

1. Regenerate TypeScript SDK lockfile:
   - `cd sdk/typescript && pnpm install`
   - Verify `pnpm build` or `tsc` passes

2. Verify Go SDK scaffold:
   - `cd sdk/go && go mod tidy && go build ./...`

3. Verify Kotlin SDK:
   - `cd sdk/kotlin && ./gradlew build`

**Verification**:
- `sdk/typescript/pnpm-lock.yaml` is no longer suspiciously small (24 lines)
- All three SDKs compile

---

### Sprint M4.3 — Integration Testing Strategy

**Objective**: Add smoke tests that verify the engine-to-client contract end-to-end.

**Tasks**:

1. Create `engine/tests/integration/` directory:
   - Smoke test: Start engine via gRPC, send a `Generate` request, verify response
   - Use `bufconn` for in-process testing (already used in existing tests)
   - If JNI is available in CI, test `nativeStartServer` → gRPC connect → inference

2. Create `android/app/src/test/java/com/ashwathai/ashwathai/integration/` directory:
   - Smoke test using `EngineGrpcClient` against a test gRPC server
   - Already partially exists (`ClientInferenceEngineTest.kt`, `EngineGrpcClientTest.kt`)

3. Create CI workflow that runs integration tests on every PR to `engine/api/proto/`.

**Verification**:
- `go test ./tests/integration/...` passes
- Android integration tests pass
- Integration tests are triggered by proto changes

---

## Dependency Graph

```
M1.1 (Backup + Archive)
  └── M1.2 (Stash + Config)
        ├── M2.1 (feature/platform → main) ◄── HIGHEST RISK
        │     └── M2.2 (feature/android-client → main)
        │           └── M2.3 (feature/web-client → main)
        │                 └── M2.4 (research/lab review)
        │                       └── M3.1 (Branch protection, AGENT.md, PR template)
        │                             ├── M3.2 (Web CI, integration gates)
        │                             │     └── M3.3 (Release branch, tag standardization)
        │                             │           └── M4.x (ongoing improvements)
        │                             └── (M4.x can proceed in parallel)
        └── M4.x (ongoing improvements — can proceed after M1.2 + M2.1)
```

---

## Timeline Estimate

| Epic | Sprints | Estimated Duration | Calendar Days |
|------|---------|-------------------|---------------|
| EPIC-M1 | M1.1, M1.2 | 2 days | Days 1-2 |
| EPIC-M2 | M2.1-M2.4 | 6 days | Days 3-8 |
| EPIC-M3 | M3.1-M3.3 | 3 days | Days 9-11 |
| EPIC-M4 | M4.1-M4.3 | 3.5 days | Days 12-16 (can overlap with M3) |
| **Total** | **13 sprints** | **~14.5 days** | **~3 calendar weeks** |

---

## Key Decisions Still Needed

These are questions that affect sprint ordering and implementation approach:

1. **M2.1 cherry-pick order**: Oldest-first or newest-first? Oldest-first minimizes semantic conflicts (dependencies are in place). Newest-first minimizes textual conflicts (less code to move past). **Recommendation**: Oldest-first.

2. **M2.2 and M2.3 ordering**: Should Android or Web be merged first? Android has engine-level changes that could conflict with M2.1 engine changes. Web is isolated. **Recommendation**: Android first (M2.2), then Web (M2.3) — this way any Android-introduced engine conflicts are resolved before Web merge, and Web is the simplest (no conflicts expected).

3. **M4.x parallelization**: M4.1 (docs) and M4.2 (SDK deps) can run in parallel with M3.x. M4.3 (integration tests) requires M3.x CI workflows to be in place. **Recommendation**: Begin M4.1 and M4.2 after M3.1; begin M4.3 after M3.2.

4. **Archive vs. delete obsolete docs**: Some docs overlap with AGENT.md (GUILD.md, PLATFORM_RULES.md). **Recommendation**: Archive duplicates, keep single source of truth in AGENT.md.
