# Documentation Governance

**Status**: Active  
**Last Updated**: 2026-07-05

---

## Documentation Philosophy

Documentation is code. It requires ownership, review, and version control. Every document in this repository has a designated owner. Everyone may read any document, but only the designated owner may modify it.

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
| Any agent | All documents | Nothing without owner approval |
| Document owner | All documents | Their documents only |
| Chief Architect | All documents | Repository-wide documents only |

Agents may propose changes to any document (via issue, PR, or request), but only the designated owner applies the change.

---

## Documentation Structure

```
docs/
  architecture/       Living architecture documents (one source of truth per topic)          Owner: Chief Architect
  engine/             Engine workspace progress (append-only)                                Owner: Platform Agent
  android/            Android workspace docs (append-only)                                   Owner: Android Agent
  web/                Web workspace docs (append-only)                                       Owner: Web Agent
  platform/           Platform-wide technical documentation                                  Owner: Chief Architect
  analysis/           Historical reports — NEVER EDITED after creation                        Owner: Chief Architect
  decisions/          ADRs — Append-only                                                      Owner: Chief Architect
  proposals/          Proposals — Reviewed, archived, or implemented                         Owner: Author → Chief Architect
  governance/         Governance rules (AGENT.md, DOCUMENTATION_GOVERNANCE.md)               Owner: Chief Architect
```

---

## Documentation Types

| Type | Owner | Lifecycle | Editable By |
|------|-------|-----------|-------------|
| Constitution (`AGENT.md`) | Chief Architect | Living | Chief Architect only |
| Governance (`DOCUMENTATION_GOVERNANCE.md`) | Chief Architect | Living | Chief Architect only |
| Architecture (`docs/architecture/*.md`) | Chief Architect | Living | Chief Architect only |
| Snapshot (`PROJECT_STATE.md`) | Chief Architect | Per sprint overwrite | Chief Architect only |
| Progress Log (`docs/{engine,android,web}/*.md`) | Workspace owner | Append-only | Workspace owner |
| ADR (`docs/decisions/*.md`) | Chief Architect | Append-only | Chief Architect only |
| Proposal (`docs/proposals/*.md`) | Any agent | Review → Archive/Implement | Author (until review) |
| Historical Report (`docs/analysis/*.md`) | Chief Architect | Permanent | Never edited |
| Design System (`design/shared/*.md`) | Design Team | Draft → Living | Design Team |
| Workspace Document | Workspace owner | Per sprint | Workspace owner |

---

## Documentation Levels

### Level 1 — Workspace Progress (Agent Owned)

Each workspace owns its own append-only progress log.

**Files:**
- `docs/engine/PROGRESS.md` — Platform/Engine Team
- `docs/android/PROGRESS.md` — Android Client Team
- `docs/web/PROGRESS.md` — Web Client Team

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

### Level 3 — Architecture Documents (Chief Architect Maintained)

These documents describe the permanent architecture of the platform and its clients. They are reference documents, not progress logs.

**Files:**
- `docs/ARCHITECTURE.md` — Platform architecture
- `docs/ANDROID_ARCHITECTURE.md` — Android client architecture
- `docs/WEB_ARCHITECTURE.md` — Web client architecture
- `docs/ENGINE_API.md` — Engine gRPC API reference
- `docs/JNI_ARCHITECTURE.md` — JNI bridge design

**Rules:**
- Maintained only by the Chief Architect after reviewing workspace progress.
- Feature agents should not edit architecture documents directly.
- Architecture documents are updated when a milestone changes the public contract.

### Level 4 — Repository State (Chief Architect Maintained)

These documents track the live state of the project. They are the single source of truth for what is happening now and what happened in the past.

**Files:**
- `docs/PROJECT_STATE.md` — Operational dashboard
- `CHANGELOG.md` — Release history (root level)
- `docs/ROADMAP.md` — Future plans

**Rules:**
- Maintained only by the Chief Architect.
- Feature agents never modify repository state documents.
- Changes are made after milestone completion, not during development.

### Level 5 — Historical Reports (Immutable)

Permanent milestone summaries stored under `docs/analysis/`.

**Files:**
- `docs/analysis/EPIC3_FINAL.md`
- `docs/analysis/SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md` (archived)

**Rules:**
- Never modify historical reports. They are permanent records.
- Create a new report for each milestone instead of editing an existing one.
- Historical reports are append-only by addition of new files.

---

## Documentation Creation Policy

New documents may only be created in these locations:

| Location | Purpose | Creator |
|----------|---------|---------|
| `docs/analysis/` | Historical reports after EPIC completion | Chief Architect |
| `docs/decisions/` | ADRs | Chief Architect |
| `docs/proposals/` | Proposals for review | Any agent |
| `docs/architecture/` | New architecture documents | Chief Architect |
| `docs/engine/` | Engine workspace docs | Platform Agent |
| `docs/android/` | Android workspace docs | Android Agent |
| `docs/web/` | Web workspace docs | Web Agent |

No new documents may be created at `docs/` root level without Chief Architect approval.

---

## Archive Policy

| Type | Location | Policy |
|------|----------|--------|
| Historical reports | `docs/analysis/` | Never edited after creation |
| Architecture docs | `docs/architecture/` | Living — updated when architecture changes |
| Progress logs | `docs/{engine,android,web}/` | Append-only — new entries added, old entries preserved |
| Project state | `PROJECT_STATE.md` | Snapshot — overwritten each sprint |
| ADRs | `docs/decisions/` | Append-only — new entries added, old entries never changed |
| Proposals | `docs/proposals/` | Reviewed → archive or implement; never edited after review |

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
Chief Architect Review
      │
      ▼
Architecture (Level 3) / Repository State (Level 4) / Historical Reports (Level 5)
```

Only the Chief Architect updates Level 3, Level 4, and Level 5 documents. Feature agents contribute by keeping Level 1 and Level 2 documents accurate during development.

---

## Policy Violations

If a document is modified by someone other than its designated owner:
1. The change is reverted.
2. The correct owner is notified.
3. The contributor is reminded of this policy.

This policy exists to prevent the documentation conflicts that arise from multi-agent parallel development. It is not intended to discourage contributions — only to ensure every document has clear, single-writer ownership.
