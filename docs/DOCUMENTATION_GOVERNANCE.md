# Documentation Governance

**Status**: Active  
**Last Updated**: 2026-07-04  

---

## Documentation Philosophy

Documentation is code.

It requires ownership, review, and version control. Every document in this repository has a designated owner. Everyone may read any document, but only the designated owner may modify it.

This policy exists to:
- Prevent documentation conflicts when multiple AI agents work in parallel.
- Ensure every document has a clear maintainer.
- Preserve historical decisions by preventing unauthorized rewrites.
- Enable safe parallel development across workspaces.

---

## Single Writer Principle

Every project document has exactly one owner.

| Role | May Read | May Edit |
|------|----------|----------|
| Any agent | ✅ All documents | ❌ |
| Document owner | ✅ | ✅ Their documents only |
| Project Orchestrator | ✅ | ✅ All documents |

Agents may propose changes to any document (via issue, PR, or request), but only the designated owner applies the change.

---

## Documentation Levels

### Level 1 — Workspace Progress (Agent Owned)

Each workspace owns its own append-only progress log.

**Files:**
- `docs/engine/PROGRESS.md` — Platform/Engine Team
- `docs/android/PROGRESS.md` — Android Client Team
- `docs/web/PROGRESS.md` — Web Client Team
- `docs/platform/PROGRESS.md` — Platform Team

**Rules:**
- Append only — never rewrite history.
- Only the owning workspace updates its own file.
- Entries are timestamped.
- Previous entries are never modified or deleted.

### Level 2 — Workspace Decisions (Agent Owned)

Each workspace may maintain a local decisions log for design choices made during development.

**Files:**
- `docs/engine/DECISIONS.md`
- `docs/android/DECISIONS.md`
- `docs/web/DECISIONS.md`

**Rules:**
- Only the owning workspace edits its decisions document.
- Decisions are timestamped and link to relevant commits or issues.

### Level 3 — Architecture Documents (Orchestrator Maintained)

These documents describe the permanent architecture of the platform and its clients. They are reference documents, not progress logs.

**Files:**
- `docs/ARCHITECTURE.md` — Platform architecture
- `docs/ANDROID_ARCHITECTURE.md` — Android client architecture
- `docs/WEB_ARCHITECTURE.md` — Web client architecture
- `docs/ENGINE_API.md` — Engine gRPC API reference
- `docs/JNI_ARCHITECTURE.md` — JNI bridge design

**Rules:**
- Maintained only by the Project Orchestrator after reviewing workspace progress.
- Feature agents should not edit architecture documents directly.
- Architecture documents are updated when a milestone changes the public contract.

### Level 4 — Repository State (Orchestrator Maintained)

These documents track the live state of the project. They are the single source of truth for what is happening now and what happened in the past.

**Files:**
- `docs/PROJECT_STATE.md` — Operational dashboard
- `docs/CHANGELOG.md` — Release history
- `docs/ROADMAP.md` — Future plans

**Rules:**
- Maintained only by the Project Orchestrator.
- Feature agents never modify repository state documents.
- Changes are made after milestone completion, not during development.

### Level 5 — Historical Reports (Immutable)

Permanent milestone summaries stored under `docs/analysis/`.

**Files:**
- `docs/analysis/EPIC3_PHASE_A_FINAL.md`

**Rules:**
- Never modify historical reports. They are permanent records.
- Create a new report for each milestone instead of editing an existing one.
- Historical reports are append-only by addition of new files.

---

## AGENT.md

- `AGENT.md` is maintained only by the Project Orchestrator.
- Feature agents may propose changes via pull request or issue, but should not edit AGENT.md directly.
- AGENT.md is the onboarding guide for all AI agents. It should change rarely.

---

## Merge Policy

- Merge latest `main` into the feature branch before debugging.
- Never debug stale branches — always sync with `main` first.
- Keep documentation synchronized with implementation when merging.

---

## Main Branch Policy

- `main` is the integration branch.
- No feature development occurs directly on `main`.
- All implementation work happens in feature branches / worktrees.
- Only the Project Orchestrator merges into `main`.

---

## Worktree Policy

- Each feature branch has its own worktree.
- Feature work stays in its own worktree until merged and verified.
- After merging, the worktree branch is deleted.

---

## Documentation Flow

```
Workspace Agent
      │
      ▼
Workspace Progress (Level 1)
Workspace Decisions (Level 2)
      │
      ▼
Project Orchestrator Review
      │
      ▼
Architecture (Level 3) / Repository State (Level 4) / Historical Reports (Level 5)
```

Only the Project Orchestrator updates Level 3, Level 4, and Level 5 documents. Feature agents contribute by keeping Level 1 and Level 2 documents accurate during development.

---

## Policy Violations

If a document is modified by someone other than its designated owner:
1. The change is reverted.
2. The correct owner is notified.
3. The contributor is reminded of this policy.

This policy exists to prevent the documentation conflicts that arise from multi-agent parallel development. It is not intended to discourage contributions — only to ensure every document has clear, single-writer ownership.
