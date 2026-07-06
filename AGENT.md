# Repository Constitution

This document is the single source of truth for how this repository is governed, how work is performed, and how AI agents must operate. Every AI agent must read and comply with this document before performing any work.

---

## 1. Mission

Ashwath AI is an open-source, offline-first AI platform. One shared Go engine powers multiple native frontends — Android, iOS, Desktop, and Web. All inference, storage, and processing happen locally. Zero data leaves the device.

The repository is a monorepo containing the engine, all client frontends, SDKs, documentation, and design assets.

---

## 2. Repository Philosophy

### 2.1 Engine is the Product
The Go engine is the product. Everything else is a client. Business logic belongs in the engine. Clients own presentation only.

### 2.2 Architecture before Implementation
If architecture is unclear, implementation stops. Every significant change must be preceded by an architectural decision.

### 2.3 One Source of Truth
Never duplicate APIs, architecture descriptions, business rules, or documentation. Cross-reference instead of copying.

### 2.4 Quality over Speed
A correct architecture is always preferred over a quick implementation. Small, well-scoped commits are expected.

### 2.5 Evolution over Rewrite
Prefer evolving existing code over starting from scratch. The repository should become easier to understand after every contribution.

---

## 3. Engineering Principles

1. **Offline First** — Primary functionality must not rely on an active internet connection.
2. **Privacy by Default** — Local execution. Zero-knowledge design.
3. **Platform First** — Design the engine and SDK as a generic platform first, application second.
4. **Clean Architecture** — Strict separation of concerns: business logic, data access, UI.
5. **Interfaces are Long-lived** — Stable communication contracts. implementations may change; interfaces remain consistent.
6. **Maintainability over Cleverness** — Optimize for readable, sustainable code.
7. **Feature Parity** — Every supported client exposes the same capabilities whenever technically possible.
8. **No Circular Dependencies** — Package dependency direction is always one-way: engine ← SDK ← clients.

---

## 4. AI Agent Responsibilities

### 4.1 Chief Architect
- Owner of AGENT.md, DOCUMENTATION_GOVERNANCE.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, ARCHITECTURE.md
- Repository-wide governance and documentation
- Architecture reviews and ADR approval
- Final authority on all cross-cutting decisions

### 4.2 Engine Agent
- Primary maintainer of `engine/`, `sdk/`, gRPC API, platform documentation
- May modify engine, SDK, platform docs under docs/
- Must consult Chief Architect for architecture changes

### 4.3 Android Agent
- Primary maintainer of `android/`
- May modify Android-specific documentation only
- Never modifies repository governance, engine, or SDK independently

### 4.4 Web Agent
- Primary maintainer of `web/`
- May modify web-specific documentation only
- Never modifies repository governance, engine, or SDK independently

### 4.5 Research Agent
- Works in `research/` (when created) or experimental branches
- May propose but never merge architecture changes without review

### 4.6 Marketing Agent (Future)
- Responsible for website, community, release announcements, marketing materials
- Works primarily in documentation and outward-facing content
- Does not modify engine, SDK, or client code

### 4.7 Project Integrator
- Manages CI/CD, release pipeline, version tags
- Coordinates merge windows and release cycles

### 4.7 Agent Constraints
- No AI agent may modify a file it does not own without explicit approval from the owner.
- No AI agent may rewrite AGENT.md, DOCUMENTATION_GOVERNANCE.md, or repository-wide architecture documentation.
- All new documentation must comply with the Documentation Creation Policy (Section 8).
- All EPIC work must have a corresponding EPIC prompt document in docs/analysis/ after completion.

---

## 5. Repository Governance

### 5.1 Directory Ownership

| Directory | Owner | Purpose |
|-----------|-------|---------|
| `engine/` | Engine Agent | Go AI Engine |
| `android/` | Android Agent | Android frontend (Kotlin) |
| `web/` | Web Agent | Web frontend (React/TypeScript) |
| `sdk/` | Engine Agent | Client SDKs |
| `docs/` | Chief Architect | Repository documentation |
| `design/` | Design Team | Shared design assets |
| `ios/` | (future) | iOS frontend placeholder |
| `desktop/` | (future) | Desktop frontend placeholder |
| `scripts/` | Project Integrator | Build and CI scripts |
| `tools/` | Owner TBD | Development tools |
| `examples/` | Engine Agent | Usage examples |

### 5.2 File Ownership (Documentation)

| File | Owner | Type |
|------|-------|------|
| `AGENT.md` | Chief Architect | Constitution — Living |
| `DOCUMENTATION_GOVERNANCE.md` | Chief Architect | Governance — Living |
| `PROJECT_STATE.md` | Chief Architect | Snapshot — Updated per sprint |
| `ROADMAP.md` | Chief Architect | Living |
| `CHANGELOG.md` | Chief Architect | Append-only |
| `ARCHITECTURE.md` | Chief Architect | Living |
| `PLATFORM_RULES.md` | Chief Architect | Living |
| `PLATFORM_GUIDE.md` | Chief Architect | Living |
| `ENGINE_API.md` | Chief Architect | Living |
| `ENGINE_CLIENT_CONTRACT.md` | Chief Architect | Living |
| `ANDROID_ARCHITECTURE.md` | Chief Architect | Living |
| `WEB_ARCHITECTURE.md` | Chief Architect | Living |
| `JNI_ARCHITECTURE.md` | Chief Architect | Living |
| `EPICS.md` | Chief Architect | Living |
| `VISION.md` | Chief Architect | Living |
| `DECISIONS.md` | Chief Architect | Append-only |
| `DESIGN_SYSTEM.md` | Design Team | Living |
| `docs/analysis/*.md` | Chief Architect | Historical — Never edited |
| `docs/engine/*.md` | Engine Agent | Workspace progress |
| `docs/decisions/*.md` | Chief Architect | ADR — Append-only |
| `docs/proposals/*.md` | Any Agent | Proposal — Read-only after review |
| `design/shared/*.md` | Design Team | Draft → Living |
| `design/android/*` | Design Team | Android Design |
| `design/web/*` | Design Team | Web Design |

### 5.3 Everyone May Read
Visibility into the repository is unrestricted. Ownership defines responsibility, not isolation.

### 5.4 Only the Owner May Modify
Documentation changes require the owner's approval. Cross-boundary modifications require the owner's review.

---

## 6. Documentation Governance

### 6.1 Single Writer Principle
Every file has exactly one owner. Everyone may read. Only the owner may modify.

### 6.2 Documentation Structure

```
docs/
  architecture/       Living architecture documents (one source of truth per topic)
  engine/             Workspace progress logs (append-only per workspace)
  android/            Android-specific documentation
  web/                Web-specific documentation
  platform/           Platform-wide technical documentation
  analysis/           Historical reports — NEVER EDITED after creation
  decisions/          ADRs — Append-only
  proposals/          Proposals — Reviewed, then archived or implemented
  governance/         Governance rules (AGENT.md, DOCUMENTATION_GOVERNANCE.md)
```

### 6.3 Documentation Types

| Type | Owner | Lifecycle | Editable |
|------|-------|-----------|----------|
| Constitution | Chief Architect | Living | Owner only |
| Governance | Chief Architect | Living | Owner only |
| Architecture | Chief Architect | Living | Owner only |
| Snapshot | Chief Architect | Per sprint | Owner only |
| Progress | Workspace | Append-only | Workspace owner |
| Decision (ADR) | Chief Architect | Append-only | Never after creation |
| Proposal | Any Agent | Review → Archive/Implement | Never after review |
| Historical | Chief Architect | Permanent | NEVER EDITED |
| Design | Design Team | Draft → Living | Design team |
| Workspace | Workspace | Per sprint | Workspace owner |

### 6.4 Documentation Creation Policy
New documents may only be created in these locations:
- `docs/analysis/` — Historical reports (owner: Chief Architect)
- `docs/decisions/` — ADRs (owner: Chief Architect)
- `docs/proposals/` — Proposals (any agent, for review)
- `docs/engine/` — Engine workspace progress (owner: Engine Agent)
- `docs/android/` — Android workspace docs (owner: Android Agent)
- `docs/web/` — Web workspace docs (owner: Web Agent)
- `docs/architecture/` — Architecture docs (owner: Chief Architect)

No new documents may be created at `docs/` root level without Chief Architect approval.

### 6.5 Archive Policy
- **Historical reports** (`docs/analysis/`): Never edited after creation.
- **Architecture docs** (`docs/architecture/`): Living — updated when architecture changes.
- **Progress logs** (`docs/engine/`, `docs/android/`, `docs/web/`): Append-only. New entries added, old entries preserved.
- **Project state** (`PROJECT_STATE.md`): Snapshot — overwritten each sprint.
- **ADRs** (`docs/decisions/`): Append-only. New entries added, old entries never changed.
- **Design system** (`design/shared/`): Draft → Living. Published when finalized.

---

## 7. Branch Policy

### 7.1 Branch Naming

| Branch | Purpose | Base |
|--------|---------|------|
| `main` | Integration branch. Always stable, building, passing all tests. | — |
| `feature/*` | Feature development | main |
| `fix/*` | Bug fixes | main |
| `docs/*` | Documentation changes | main |
| `research/*` | Experimental work | main |

### 7.2 Branch Rules
- Never commit directly to `main`.
- Feature branches must be short-lived (days, not weeks).
- Large features must be broken into multiple small PRs.
- One branch = one concern.

---

## 8. Worktree Policy

### 8.1 Parallel Development
This repository supports multiple AI agents working in parallel. Each agent workspace is a separate Git worktree.

### 8.2 Worktree Rules
- Each worktree is a complete working copy of the repository.
- Worktrees may diverge temporarily but must be reconciled via PR to `main`.
- Shared files (engine API, proto definitions, documentation) must be coordinated.
- No two agents may modify the same file simultaneously.

---

## 9. Merge Policy

### 9.1 Pull Request Requirements
- All tests must pass.
- `go vet` must be clean (for Go changes).
- Lint must pass (for TypeScript/Kotlin changes).
- Documentation must be updated if architecture or API changes.
- No merge conflicts.

### 9.2 Review Requirements
- Cross-boundary changes require the owning agent's review.
- Architecture changes require Chief Architect review.
- Engine API changes require Engine Agent + Chief Architect review.

### 9.3 Merge Strategy
- Squash merge for feature branches.
- Rebase merge for documentation branches.
- No merge commits on `main`.

---

## 10. Sprint Workflow

1. **Plan** — EPIC defined with scope, deliverables, and test criteria.
2. **Architecture** — Architecture review before implementation (ADR if needed).
3. **Implement** — Code changes in feature branch.
4. **Test** — All tests pass. New tests for new functionality.
5. **Document** — Update documentation. Workspace progress log appended.
6. **Review** — PR created, reviewed, approved.
7. **Merge** — PR merged to `main`.
8. **Close** — EPIC marked complete. Historical report created in `docs/analysis/`.

---

## 11. Review Workflow

1. Author opens PR.
2. CI runs tests automatically.
3. Reviewer checks:
   - Architecture compliance
   - Code quality
   - Test coverage
   - Documentation accuracy
   - No regressions
4. Author addresses feedback.
5. Reviewer approves.
6. Author merges (squash or rebase).

---

## 12. Definition of Done

A task is done when:
- [ ] Code is implemented
- [ ] All existing tests pass
- [ ] New tests cover new functionality
- [ ] Build succeeds
- [ ] Lint/vet is clean
- [ ] Documentation is updated
- [ ] No dead code remains
- [ ] No TODO/FIXME introduced without tracking
- [ ] ADR created if architecture changed
- [ ] Historical report created if EPIC completed
- [ ] Progress log appended

---

## 13. Repository Audit Checklist

Before every commit, verify:
- [ ] No sensitive data (keys, secrets, passwords) in the diff
- [ ] No large binaries added
- [ ] No empty placeholder files
- [ ] No duplicate documentation
- [ ] All cross-references are valid paths
- [ ] No stale EPIC references
- [ ] No obsolete TODOs
- [ ] No drift between proto definitions (engine ↔ SDK)
- [ ] Module boundaries respected (Go imports)
- [ ] Single Writer Principle respected

---

## 14. Session Startup Checklist

When an AI agent begins a work session:
- [ ] Read this document (AGENT.md)
- [ ] Read DOCUMENTATION_GOVERNANCE.md
- [ ] Read PROJECT_STATE.md
- [ ] Read the relevant EPIC document
- [ ] Read the current progress log for the workspace
- [ ] Read the architecture document for the affected subsystem
- [ ] Check git log for recent changes (`git log --oneline -10`)
- [ ] Check current state of the worktree

---

## 15. Session Shutdown Checklist

When an AI agent ends a work session:
- [ ] All changes are committed
- [ ] Commit message references the EPIC/phase
- [ ] Progress log is appended
- [ ] No uncommitted work remains
- [ ] No worktree is dirty
- [ ] Tests pass
- [ ] Build succeeds

---

## 16. Required Reading

Before performing any work, agents must read:
1. `AGENT.md` — This document
2. `docs/DOCUMENTATION_GOVERNANCE.md` — Documentation ownership rules
3. `docs/PROJECT_STATE.md` — Current status
4. `docs/ARCHITECTURE.md` — Platform architecture
5. `docs/EPICS.md` — Current EPIC and phase
6. The architecture document for the affected subsystem
7. The workspace progress log for the affected workspace

---

## 17. Engineering Oath

Every contribution should leave the repository in a better state than it was found. Before completing any milestone, ask:

- Is the architecture clearer?
- Is the code easier to understand?
- Is the documentation still accurate?
- Have I reduced technical debt?
- Would a new contributor understand this change?
- Have I respected the Single Writer Principle?

---

## 18. Final Authority

The Chief Architect is the final authority on:
- Architecture decisions
- Documentation structure and ownership
- Repository governance
- Merge approval for cross-boundary changes
- Interpretation of this constitution

In case of conflict between documents, this document (AGENT.md) takes precedence. In case of ambiguity, consult the Chief Architect.
