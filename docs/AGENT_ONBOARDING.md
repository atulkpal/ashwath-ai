# Agent Onboarding

When spawned as a new agent, your instruction will be:

> Read AGENT.md and continue with **EPIC-<N>**.

This document explains the full self-onboarding process. Follow it step by step.

---

## Step 1 — Read Core Documents (in order)

These explain the project constitution, current state, and your mission:

1. **`AGENT.md`** — Repository constitution: ownership, branch policy, worktree policy, merge rules, Definition of Done.
2. **`docs/PROJECT_STATE.md`** — Operational dashboard: build status, known issues, completed milestones, next priorities.
3. **`docs/EPICS.md`** — Epic decomposition. Find your assigned EPIC and read its stories.
4. **`docs/PLATFORM_GUIDE.md`** — Build commands, CI/CD, coding standards, testing philosophy.
5. **`docs/ARCHITECTURE.md`** — Platform-level architecture overview.
6. **Your subsystem architecture doc:**
   - Android Agent → `docs/ANDROID_ARCHITECTURE.md`
   - Web Agent → `docs/WEB_ARCHITECTURE.md`
   - Engine Agent → `docs/engine/MODULE_BOUNDARIES.md`
7. **Your workspace progress log** (append-only, create if needed):
   - Engine Agent → `docs/engine/PROGRESS.md`
   - Android Agent → `docs/android/PROGRESS.md`
   - Web Agent → `docs/web/PROGRESS.md`

---

## Step 2 — Understand Your EPIC

From `docs/EPICS.md`, identify your EPIC and its stories:

| Agent | EPIC | Stories |
|-------|------|---------|
| Android Agent | EPIC-5: Android App v1 | E5.1–E5.8 |
| Web Agent | EPIC-6: Web Frontend v1 | E6.1–E6.6 |
| Engine Agent | (varies) | (varies) |
| Chief Architect | EPIC-12: Release Engineering | E12.1–E12.6 |

Pick one story to start with. Each story is a single unit of work — implement, test, submit.

---

## Step 3 — Create a Worktree (if needed)

If you need a separate IDE or dev server, create a worktree:

```powershell
cd C:\Users\Atul\AndroidStudioProjects
git worktree add -b feature/<agent>/<story> ../AshwathAI-<Agent> main
```

Replace `<agent>` and `<story>` with your values (e.g., `feature/android/stitch-fix`).

**Worktree rules** (from AGENT.md §8):
- Max lifetime: 2 weeks
- Sync via `git merge main` (not cherry-pick)
- Run `git worktree prune` after deletion

---

## Step 4 — Create a Feature Branch

If you're not using a worktree, create a branch directly:

```powershell
git checkout -b feature/<agent>/<story> main
```

Branch naming convention: `feature/<agent>/<description>` where `<agent>` is `engine`, `android`, `web`, or `arch`.

---

## Step 5 — Implement the Story

Follow the workflow from AGENT.md §10:

1. **Plan** — Understand scope and test criteria
2. **Architecture** — Review if architecture changes (create ADR if needed)
3. **Implement** — Code changes in feature branch
4. **Test** — All existing tests pass, new tests for new functionality
5. **Document** — Update relevant docs, append to workspace progress log
6. **Review** — Open a PR
7. **Merge** — Squash-merge to `main`

---

## Step 6 — Report Progress

Append to your workspace progress log (`docs/<agent>/PROGRESS.md`):

```markdown
## YYYY-MM-DD — <Story description>

- Started: <brief>
- Done: <what was implemented>
- Blockers: <if any>
- Next: <next step>
```

---

## Step 7 — Submit via PR

```powershell
git push origin feature/<agent>/<story>
gh pr create --base main --head feature/<agent>/<story>
```

Fill out the PR template (`.github/PULL_REQUEST_TEMPLATE.md`).

**Merge**: `gh pr merge <N> --squash --delete-branch --admin`
(Use `--admin` until CI runners are configured — branch protection requires it.)

---

## Quick Reference

| Action | Command |
|--------|---------|
| Create feature branch | `git checkout -b feature/<agent>/<story> main` |
| Stay current | `git merge main` |
| Push | `git push origin feature/<agent>/<story>` |
| Open PR | `gh pr create --base main --head feature/<agent>/<story>` |
| Merge PR | `gh pr merge <N> --squash --delete-branch --admin` |
| Report progress | Append to `docs/<agent>/PROGRESS.md` |

## Always Respect

- **Directory ownership** (AGENT.md §5) — only modify files you own
- **Single Writer Principle** — every file has exactly one owner
- **Branch protection** — no direct pushes to `main`
- **Definition of Done** (AGENT.md §12) — all checks pass before PR
