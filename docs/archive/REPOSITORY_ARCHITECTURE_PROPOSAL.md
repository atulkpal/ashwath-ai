# Ashwath.AI — Repository Architecture & Migration Proposal

> **Status**: Draft Proposal  
> **Author**: Lead Repository Architect  
> **Date**: 2026-07-06  
> **Audience**: Project Owner (for comparison with other AI proposals)

---

## Executive Summary

This proposal recommends **Trunk-Based Development with Release Branches** as the branching model, replacing the current ad-hoc worktree-merged workflow. The existing model creates dangerous divergence (feature/platform has deleted `web/` entirely), and the cherry-pick sync strategy documented in `docs/SYNC.md` is fragile and unscalable. The migration should proceed in **3 phases** over approximately **2 weeks** to establish a stable, governable baseline.

---

## 1. Current State Assessment

### 1.1 What's Working Well

| Area | Assessment |
|------|-----------|
| **Engine-first architecture** | The Go engine as the sole source of AI business logic is the right call. Thin clients correct. |
| **Documentation philosophy** | `AGENT.md` constitution, `PLATFORM_RULES.md`, `DOCUMENTATION_GOVERNANCE.md`, and `REPOSITORY_MANIFEST.md` are excellent foundations. |
| **EPIC-based planning** | Clear epoch/story decomposition. Well-structured. |
| **Worktree concept** | The idea of parallel isolated checkouts is valid — Android needs Android Studio, Web needs its dev server, Engine work can proceed independently. |
| **CI pipelines** | Engine and Android CI exist, release automation for engine cross-compilation is solid. |
| **.gitignore hygiene** | Good separation of concerns across root, android/, engine/, web/. |

### 1.2 Critical Problems (must fix before migration)

1. **Divergent branch histories are dangerously incompatible**
   - `feature/platform` removed the entire `web/` directory (199 files, ~19,725 deletions). A merge to `main` would **delete the web frontend**.
   - `feature/android-client` and `feature/web-client` are ahead of `main` with engine changes that overlap partially with `feature/platform`.
   - There is no single branch that contains a complete, coherent version of the project.

2. **Cherry-pick sync model is fragile** (`docs/SYNC.md`)
   - Cherry-picking individual engine commits between `main` and feature branches creates duplicate commit chains with different hashes.
   - No automated verification that engine code is identical across branches.
   - `git diff <branch-1>..<branch-2> -- engine/` is not part of the workflow — it should be a CI gate.

3. **6 stale branches at initial commit**
   - `feature/android-installer`, `feature/desktop-frontend`, `feature/engine-llama`, `feature/engine-realtime`, `feature/ios-frontend`, `feature/web-frontend` — all point to `5bf730c` (initial commit). These will cause confusion for new contributors and AI agents.

4. **No main branch protection**
   - Direct commits to `main` are possible (the stash at `87b61e2` was created on `main`).
   - No required PRs, no status checks, no required reviews.

5. **Tag scheme is inconsistent**
   - `arch-engine-v1`, `android-v0.2.0`, `v0.1.1`, `engine/v0.1.0` — four tags with four different conventions.
   - No release notes or changelog entries corresponding to these tags.

6. **No web CI pipeline**
   - `engine-ci.yml` and `android-ci.yml` exist. Web frontend has no automated build or test.

7. **Worktree setup script is stale**
   - `scripts/setup-worktrees.ps1` references `worktrees/` subdirectory, but actual worktrees are at sibling directories (`AshwathAI-Android`, `AshwathAI-Web`, etc.).
   - Script references `feature/research`, but actual branch is `research/lab`.

### 1.3 Moderate Risks

8. **Engine `.so` binaries embedded in Android**: `jniLibs/` is gitignored, so CI must rebuild on every run. If the NDK toolchain changes or Go version updates, engine CI could silently produce incompatible binaries not caught by Android CI.

9. **No integration testing across engine + client**: Engine tests and Android tests run independently. There's no test that verifies the JNI bridge actually works end-to-end with the real gRPC service.

10. **TypeScript SDK pnpm-lock.yaml is suspicious** (only 24 lines vs. 4,000+ lines for the web lockfile). This suggests the SDK's dependencies might be incomplete.

11. **Stash `stash@{0}`**: "WIP Android before web merge" — may contain valuable uncommitted work.

---

## 2. Recommended Branching Model: Trunk-Based Development + Release Branches

### 2.1 The Model

```
main  ──●────●────●────●────●────●────●────●────●────●────●──
          \        /   \        /        \        /
feature/  ●────●──     ●──────  ●────●    ●──────
           (short-lived, squash-merge to main)

release/v0.3.x ──●────●────● (bug fixes only, tagged)
                    \
hotfix/             ●────● (emergency, merge back to main + release)
```

### 2.2 Why NOT GitFlow

| Criterion | GitFlow | Trunk-Based + Release Branches |
|-----------|---------|-------------------------------|
| Team size | Teams of 5+ | 1 human + AI agents |
| Release cadence | Scheduled (weeks/months) | Continuous + periodic store releases |
| Overhead | High (develop, release, hotfix branches) | Low (just main + release branches) |
| Merge debt | High (long-lived develop → main merges) | Low (short feature branches) |
| AI agent suitability | Poor (complex context switching) | Good (simple model) |

GitFlow's `develop` branch would introduce an unnecessary indirection. With a single developer and AI agents, the extra ceremony of maintaining a `develop` branch is not justified.

### 2.3 Why NOT GitHub Flow

GitHub Flow (feature branch → main, deploy from main) is close to what I'm recommending, but it lacks a formal release branch mechanism. Android requires app store releases with stabilization phases, and engine binary releases need version pinning. GitHub Flow alone doesn't provide this.

### 2.4 Why Trunk-Based + Release Branches

1. **main is always deployable** — CI passes, no broken commits.
2. **Feature branches are short-lived** (days, not weeks) — less merge pain.
3. **Release branches** provide stabilization for store submissions.
4. **Hotfix branches** are cut from release branches and merged back to both main and release.
5. **AI agents** can work on independent feature branches with clear ownership by directory.

### 2.5 Branch Naming Convention

| Pattern | Purpose |
|---------|---------|
| `main` | Trunk — always green, always deployable |
| `feature/<agent>/<description>` | Feature work (e.g., `feature/engine/model-catalog`, `feature/android/knowledge-screen`) |
| `fix/<agent>/<description>` | Bug fixes |
| `docs/<agent>/<description>` | Documentation-only changes |
| `research/<topic>` | Long-running experiments (rare, should be short-lived) |
| `release/v<major>.<minor>.x` | Release stabilization (e.g., `release/v0.3.x`) |
| `hotfix/<description>` | Emergency fixes from release branches |

The `<agent>/` prefix (e.g., `engine/`, `android/`, `web/`, `arch/`) allows AI agents to self-organize and enables automated branch-name-based CI routing.

---

## 3. Worktree Strategy

### 3.1 Current Problems

- Worktrees are permanent, creating long-lived divergent branches.
- Cherry-pick sync is fragile and error-prone.
- `feature/platform` has deleted `web/` — a worktree allowed this divergence to grow.

### 3.2 Recommended Approach

**Worktrees are for active development only, not for permanent branching.**

| Scenario | Worktree? | Strategy |
|----------|-----------|----------|
| Active feature development (e.g., new Android screen) | Yes | Feature branch in worktree, merge to main weekly via squash |
| Engine SDK improvements | No (use main worktree) | Direct to main via PR (engine is the root of truth) |
| Cross-platform refactoring | Yes | Create temporary worktree, merge within days |
| Research/experimentation | Yes | `research/*` branch, short-lived, merge or archive within 1 week |
| Release stabilization | No (use main worktree) | `release/v*` branch, cherry-pick-only bug fixes |

**Rule**: A worktree must not exist for more than 2 weeks without merging changes to `main` or the `release/` branch. `git worktree prune` after deletion.

**Sync mechanism** (replacing cherry-pick):
1. Engine changes → commit on `main` (via PR).
2. Feature branches → `git merge main` (not cherry-pick) to stay current.
3. CI enforces `git diff <branch>..main -- engine/ sdk/` is empty before merge.

---

## 4. Multi-Agent Workflow

### 4.1 Agent Assignment by Directory

| Agent | Owns | Branch prefix |
|-------|------|--------------|
| **Engine Agent** | `engine/`, `sdk/go/`, `sdk/typescript/` | `feature/engine/` |
| **Android Agent** | `android/`, `sdk/kotlin/`, `design/android/` | `feature/android/` |
| **Web Agent** | `web/`, `design/web/` | `feature/web/` |
| **Research Agent** | `research/*` ephemeral | `research/` |
| **Chief Architect** | `docs/`, `AGENT.md`, `.github/`, `Makefile` | `feature/arch/` or direct `docs/` |
| **Project Integrator** | Merge coordination, CI, release management | `release/v*`, `hotfix/` |

### 4.2 Agent Workflow

```
Week Start: Agent reads EPICS.md → picks story → creates feature/<agent>/<story>
   ↓
Agent implements (may use worktree if needed)
   ↓
Agent opens PR → CI runs (engine, android, web as applicable)
   ↓
Chief Architect or Integrator reviews → squash-merge to main
   ↓
If feature touches engine + client: feature branch merges main first, client changes on top
```

### 4.3 Conflict Minimization

1. **Directory ownership** prevents agents from touching the same files.
2. **Feature branches from main** — agents always branch from the latest `main`.
3. **Dependency ordering**: Engine API changes first → SDK regeneration → Client updates. This creates a natural left-to-right dependency flow.
4. **Branch naming** with agent prefix enables automated CI routing and PR assignment.

---

## 5. Feature Flow: Development → Stable Release

### 5.1 Daily/Weekly Feature Flow

```
  Agent works in feature/<agent>/<desc>
        ↓
  Opens PR → CI runs (engine tests, android build, web build as applicable)
        ↓
  Code review (Chief Architect or Integrator)
        ↓
  Squash-merge to main
        ↓
  [main is always green]
```

### 5.2 Release Flow

```
  Decide to release v0.3.0
        ↓
  Cut release/v0.3.x from main at stable commit
        ↓
  Stabilization phase (1-7 days):
    - Bug fixes → cherry-pick to release/v0.3.x
    - No new features
        ↓
  Tag v0.3.0 on release/v0.3.x
    - Also tag: android/v0.3.0, engine/v0.3.0 for platform-specific artifacts
        ↓
  CI build + publish (triggered by tag)
    - Android: build + upload to Play Store internal track
    - Engine: cross-compile + upload to GitHub Releases
    - Web: build + deploy (future)
        ↓
  Merge release/v0.3.x back to main (reconcile any fixes)
        ↓
  Delete release/v0.3.x (or keep as historical reference, no new commits)
```

### 5.3 Hotfix Flow

```
  Branch hotfix/critical-fix from release/v0.3.x
        ↓
  Fix → PR against release/v0.3.x
        ↓
  Tag v0.3.1 → release
        ↓
  Merge hotfix to main as well
```

---

## 6. Release & Tagging Strategy

### 6.1 Tag Convention

| Format | Example | When |
|--------|---------|------|
| `v<major>.<minor>.<patch>` | `v0.3.0` | Unified platform release |
| `android/v<major>.<minor>.<patch>` | `android/v0.3.0` | Android-only release |
| `engine/v<major>.<minor>.<patch>` | `engine/v0.3.0` | Engine-only release |
| `web/v<major>.<minor>.<patch>` | `web/v0.3.0` | Web-only release (future) |

### 6.2 Versioning Rules

- **Major**: Architectural changes, breaking API changes, platform additions.
- **Minor**: Features, non-breaking changes. Incremented per Epoch (monthly).
- **Patch**: Bug fixes only. Incremented per stabilization round.

### 6.3 Release Artifacts

- GitHub Releases for engine binaries (already automated in `release-engine.yml`)
- Google Play Internal Track for Android (not yet automated)
- Release notes generated from `CHANGELOG.md` entries

### 6.4 CHANGELOG Convention

Maintain the existing format but add a `### Migration Notes` section for breaking changes.

---

## 7. Migration Strategy — 3 Phases

**Duration**: ~2 weeks. **No destructive operations without creating git bundles first.**

### 7.1 Phase 1: Pre-Migration Audit & Hygiene (Days 1-3)

1. **Archive all stale branches**: Tag them with `archive/` prefix (e.g., `git tag archive/feature/android-installer feature/android-installer`) before deletion. This preserves history without polluting branch listings.

   Branches to archive: `feature/android-installer`, `feature/desktop-frontend`, `feature/engine-llama`, `feature/engine-realtime`, `feature/ios-frontend`, `feature/web-frontend`.

2. **Bail out the stash**: Review `stash@{0}` ("WIP Android before web merge"), create a commit or document its contents, then drop.

3. **Create full git bundle** as disaster recovery: `git bundle create ashwathai-backup-$(date +%Y%m%d).bundle --all`

4. **Fix `.gitignore`**: Add `engine/ashwathd.exe` explicitly (it's on disk and can accidentally be staged).

5. **Fix worktree setup script**: Update `scripts/setup-worktrees.ps1` to reflect actual locations and naming.

6. **Tag current state**: Create a `pre-migration-baseline` tag at `main` HEAD to mark the snapshot before restructuring.

### 7.2 Phase 2: Branch Reconciliation (Days 4-8)

This is the most delicate phase. The goal is to create a single coherent `main` that contains all valuable work from every active branch.

**Step 1 — Establish the baseline**: Choose `main` as the structural baseline (it has the most complete project with both `android/` and `web/`).

**Step 2 — Reconcile `feature/platform`**: This branch contains valuable EPIC-4 work (model catalog, upstream fetcher, Ollama detection, benchmarking, llama.cpp bundling, JNI error codes). However, it diverges massively (199 files changed) and deletes `web/`. The correct approach is to cherry-pick the EPIC-4 commits onto `main`, NOT to merge `feature/platform` into `main`.

   Commits to cherry-pick from `feature/platform` (newest first):
   - `459fef0` — fix: update Go catalog
   - `44d0df1` — feat: upstream model index fetcher
   - `966aa52` — feat: model catalog with capability scoring
   - `494ab60` — feat: Ollama model auto-detection
   - `dea87a0` — E4.6: provider documentation (docs only)
   - `0ae7ae5` — E4.5: engine benchmark tests + nightly CI
   - `b251e92` — E4.4: JNI error code granularity
   - `0409aeb` — E4.3: llama.cpp binary bundling
   - `4495ae0` — E4.2: download progress streaming
   - `d083153` — E4.1: downloads package test coverage

   **Risk**: `feature/platform` removed `engine/internal/agent/`, `engine/internal/plugins/`, `engine/internal/server/` which exist on `main`. The cherry-picks may conflict. Each commit must be reviewed carefully.

**Step 3 — Reconcile `research/lab`**: Its commits (`097166b` through `b08e384`) are mostly about EPIC-2 Android integration. These are earlier versions of work now in `feature/android-client` and `feature/platform`. After cherry-picking `feature/platform`, check if `research/lab` adds anything unique. Likely it does not — `research/lab` will be preserved as a tag but superseded.

**Step 4 — Reconcile `feature/android-client`**: This branch has extensive Android work (58+ files, 2,500+ lines of new code). Merge from `main` into `feature/android-client` first to resolve conflicts, verify the build passes, then squash-merge into `main`.

**Step 5 — Reconcile `feature/web-client`**: Similarly, merge `main` into `feature/web-client`, verify the web build passes, then squash-merge into `main`.

### 7.3 Phase 3: Governance Enablement (Days 9-14)

1. **Enable branch protection on `main`**:
   - Require PR for all merges.
   - Require status checks (engine CI, android CI, web CI once added).
   - Require linear history (NO merge commits on main — only squash merges and rebases).
   - Require updated documentation (check AGENT.md + docs/ if relevant).

2. **Create the `release/v` branch pattern**: Cut the first release branch from the reconciled `main`.

3. **Add web CI workflow** (`.github/workflows/web-ci.yml`):
   - Trigger on paths: `['web/**', 'sdk/typescript/**']`.
   - `pnpm install`, `pnpm lint`, `pnpm build`, `pnpm test` (future).

4. **Add integration gate workflow**: Triggered on PRs that modify `engine/api/proto/` — runs both engine and Android CI together to verify proto compatibility.

5. **Add cross-branch engine consistency check**: Workflow that runs on `main` push and verifies `git diff <branch>..main -- engine/` is empty for all active feature branches.

6. **Update `AGENT.md`**: Document the new branching model, worktree policy, release flow, and agent workflow. Remove references to the cherry-pick sync model.

7. **Add PR template** (`.github/PULL_REQUEST_TEMPLATE.md`):
   ```
   ## Summary
   ## Related EPIC/Story
   ## Platforms affected (engine/android/web/docs)
   ## Testing performed
   ## Documentation updated
   ## Breaking changes
   ```

8. **Add `CHANGELOG.md` entry policy**: Every PR must include a changelog entry or the PR template must have a justification for skipping.

---

## 8. Risks & Architectural Issues

### 8.1 High Severity

| Risk | Impact | Mitigation |
|------|--------|-----------|
| `feature/platform` deleted `web/` — direct merge would delete web frontend | **Data loss** | Cherry-pick EPIC-4 commits individually; do NOT merge the branch |
| `feature/android-client` and `feature/platform` have overlapping engine changes | **Conflict cascade** | Reconcile sequentially: platform engine changes first, then android |
| Cherry-pick history is not idempotent | **Duplicate commits** | Use PR numbers in commit messages for traceability |

### 8.2 Medium Severity

| Risk | Impact | Mitigation |
|------|--------|-----------|
| `.so` binaries are gitignored but critical to Android builds | **Silent CI failure** | Add CI workflow that ensures `.so` is built before Android CI runs |
| No integration tests across engine + JNI bridge | **Production bugs** | Add a smoke test that starts engine via JNI, connects over gRPC, sends a request |
| Web CI doesn't exist | **Web regressions** | Add `web-ci.yml` in Phase 3 |
| `pnpm-lock.yaml` in `sdk/typescript` is incomplete (24 lines) | **Dependency issues** | Regenerate with `pnpm install` |

### 8.3 Low Severity (Should Fix)

| Issue | Fix |
|-------|-----|
| `engine/ashwathd.exe` on disk but gitignored | Add `# Local build artifacts` section to `.gitignore` |
| `docs/SYNC.md` describes obsolete cherry-pick workflow | Archive or rewrite |
| `scripts/setup-worktrees.ps1` mismatches actual setup | Update to reflect current locations |
| `.idea/` directory tracked (partially — some .idea/ content may be untracked) | Verify with `git check-ignore` and add to `.gitignore` if needed |

---

## 9. Trade-offs & Alternatives Considered

### 9.1 Branching Model

| Model | Considered? | Rejected Because |
|-------|-------------|-----------------|
| **GitFlow** | Yes | Too much ceremony for 1 developer + AI agents; `develop` branch adds unnecessary merge step |
| **GitHub Flow** | Yes | Lacks release branch mechanism needed for app store releases and binary distribution |
| **GitLab Flow** | Yes | Environment-based branching doesn't fit; AshwathAI doesn't have staging/production environments |
| **Trunk-Based + Release Branches** | **Chosen** | Right balance of simplicity and release management |

### 9.2 Worktree Philosophy

| Approach | Considered? | Assessment |
|----------|-------------|-----------|
| **No worktrees, single checkout** | Yes | Impractical — Android needs separate IDE, Web needs separate dev server |
| **Worktrees with cherry-pick sync** | Current | Fragile, error-prone, no automated verification |
| **Worktrees with merge-based sync** | **Recommended** | Standard Git workflow; feature branches merge from main regularly |
| **Submodules instead of worktrees** | Yes | Adds complexity; submodules are notoriously painful for active development |

### 9.3 Monorepo vs. Multirepo

The current monorepo structure is correct. Reasons:
- Single source of truth for the engine API (protobuf).
- Coordinated changes across engine + client in one PR.
- Atomic commits across layers.
- Simpler CI configuration.
- Easier for AI agents to understand the full picture.

A multirepo split would be warranted only if:
- Different teams need independent CI/CD pipelines with different access controls (not the case here).
- The engine becomes a standalone product consumed by external teams (future possibility, but not now).

---

## 10. Summary of Decisions Required

| # | Decision | Options | Recommendation |
|---|----------|---------|---------------|
| 1 | **Branching model** | Trunk-Based+Release, GitFlow, GitHub Flow | **Trunk-Based + Release Branches** |
| 2 | **Worktree sync** | Cherry-pick (current), Merge-based | **Merge-based** — feature branches merge main |
| 3 | **Stale branch treatment** | Delete, Archive with tag, Keep | **Archive with tag** — preserve history, remove from branch list |
| 4 | **feature/platform reconciliation** | Merge branch, Cherry-pick commits, Discard | **Cherry-pick EPIC-4 commits** to main |
| 5 | **Tag convention** | Unified `v*`, Per-platform `android/v*` | **Both** — `v*` for unified, `android/v*` for platform-specific |
| 6 | **Release branch cadence** | Per-Epoch (monthly), Per-feature, Scheduled | **Per-Epoch (monthly)** — aligned with current EPIC structure |
| 7 | **AI agent branch prefix** | Optional, Required | **Required** — `feature/<agent>/<description>` for CI routing |
| 8 | **PR template** | None, Simple, Detailed | **Detailed** — include sections for testing, docs, breaking changes |

---

## Appendix: Key Files Referenced

| File | Path | Status |
|------|------|--------|
| Repository Constitution | `AGENT.md` | Active — update after migration |
| Branch Policy | `AGENT.md:§7` | Needs rewrite |
| Worktree Policy | `AGENT.md:§8`, `docs/SYNC.md` | Needs rewrite (SYNC.md to archive) |
| Merge Policy | `AGENT.md:§9` | Needs update for squash-only |
| Directory Ownership | `AGENT.md:§5` | Good — keep |
| EPIC Tracking | `docs/EPICS.md` | Good — keep |
| Documentation Governance | `docs/DOCUMENTATION_GOVERNANCE.md` | Good — minor updates for new workflow |
| Worktree Setup Script | `scripts/setup-worktrees.ps1` | Needs update |
| CI Workflows | `.github/workflows/` | Add web CI, integration gate, engine consistency check |
