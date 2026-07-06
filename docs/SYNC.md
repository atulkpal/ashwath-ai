# Worktree Synchronization

## Architecture

```
main (AshwathAI/)          — Engine hub
├── engine/                — Go engine, server, runtime providers
├── sdk/                   — Kotlin/Java SDK
├── cmd/                   — Entry points (ashwathd, libashwath)
└── docs/                  — Shared documentation

feature/web-client (AshwathAI-Web/)    — Web frontend
├── web/                                — React + Vite + Tailwind
└── docs/web/                           — Web workspace logs

feature/android-client (AshwathAI-Android/)  — Android frontend
├── sdk/                                       — Android-specific SDK
├── app/                                       — Android app
└── ...

research/lab (AshwathAI-Lab/)           — Research
feature/platform (AshwathAI-Platform/)  — Platform engineering
```

## Principle

- **`main` is the authoritative branch** for engine code (`engine/`, `sdk/`, `cmd/`).
- **All worktrees share the same engine code**, synchronized via cherry-pick or merge.
- **Each worktree owns its frontend directory** (`web/`, `app/`, etc.) and works independently.
- **When a frontend feature is ready**, its branch merges into `main`.

## Sync Procedure

### To push engine changes to a worktree

```bash
# On main (engine hub)
git commit -m "engine: description of change"

# In the worktree directory (e.g. AshwathAI-Web/)
git fetch
git cherry-pick <engine-commit-hash>
```

No conflicts expected — worktree branches never modify engine/ or sdk/ files.

### To pull engine changes from main into a worktree

```bash
# In the worktree directory
git fetch origin main
git cherry-pick <engine-commit-hash>
```

## Current State (2026-07-05)

| Branch | Worktree | Engine Commit | Notes |
|--------|----------|---------------|-------|
| `main` | `AshwathAI/` | `77e4497` | Engine hub |
| `feature/web-client` | `AshwathAI-Web/` | `9868d97` (same tree) | Web frontend |
| `feature/android-client` | `AshwathAI-Android/` | `e652358` | Android frontend (needs sync) |

## Verification

```bash
# Compare engine trees between branches
git diff <branch-1>..<branch-2> -- engine/ sdk/ cmd/
# Empty output = identical engine code
```
