# Ashwath AI Repository Manifest

**Authoritative map of every directory and file.**
**Owner:** Chief Architect
**Lifecycle:** Active — update when directories or files are added/removed.
**Last updated:** 2026-07-06

---

## Legend

- **Owner**: The team/agent responsible for this path (Single Writer Principle).
- **Lifecycle**: `Active` (in use) / `Stale` (no longer maintained) / `Archived` (historical reference).
- **Type**: `gov` (governance) / `doc` (documentation) / `src` (source code) / `cfg` (configuration) / `build` (build artifacts) / `design` (design assets).

---

## Root (`/`)

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `.editorconfig` | cfg | Chief Architect | Active | Editor settings across all languages |
| `.gitignore` | cfg | Chief Architect | Active | Git exclusion rules |
| `AGENT.md` | gov | Chief Architect | Active | Repository constitution — mission, principles, governance, workflow |
| `CHANGELOG.md` | doc | Chief Architect | Active | Release changelog |
| `CONTRIBUTING.md` | doc | Chief Architect | Active | Contribution guidelines |
| `LICENSE` | cfg | Chief Architect | Active | Apache 2.0 license |
| `Makefile` | cfg | Engine Agent | Active | Engine build targets |
| `README.md` | doc | Chief Architect | Active | Project overview and quickstart |

---

## `.github/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `PULL_REQUEST_TEMPLATE.md` | gov | Chief Architect | Active | PR template with platforms, testing, docs, breaking changes |
| `workflows/android-ci.yml` | cfg | Android Agent | Active | Android CI pipeline |
| `workflows/engine-ci.yml` | cfg | Engine Agent | Active | Go engine CI (lint, test, build) |
| `workflows/engine-consistency.yml` | cfg | Chief Architect | Active | Post-push engine/sdk drift check on active feature branches |
| `workflows/integration-gate.yml` | cfg | Chief Architect | Active | Triggered on proto changes — builds engine, tests, Android SDK |
| `workflows/release-engine.yml` | cfg | Engine Agent | Active | Engine binary release pipeline |
| `workflows/web-ci.yml` | cfg | Web Agent | Active | Web CI (pnpm install, lint, build) |

---

## `android/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `.gitignore` | cfg | Android Agent | Active | Android git exclusion rules |
| `build.gradle.kts` | cfg | Android Agent | Active | Root Gradle build config |
| `gradle.properties` | cfg | Android Agent | Active | Gradle JVM/Android properties |
| `gradlew` | cfg | Android Agent | Active | Gradle wrapper (Unix) |
| `gradlew.bat` | cfg | Android Agent | Active | Gradle wrapper (Windows) |
| `local.properties` | cfg | Android Agent | Active | Local SDK path |
| `settings.gradle.kts` | cfg | Android Agent | Active | Module includes |
| `app/` | src | Android Agent | Active | Android application module |
| `gradle/` | cfg | Android Agent | Active | Gradle version catalog and wrapper |

---

## `design/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Design Team | Active | Design system overview |
| `android/v1/` | design | Design Team | Active | Android screen designs (chat, discover, knowledge, models, settings, theme) |
| `shared/` | design | Design Team | Active | Shared design tokens, brand, components, icons, motion, UX, accessibility |
| `web/v1/` | design | Design Team | Active | Web screen designs (app-shell, chat, discover, explore, knowledge, library, models, settings, theme) |

---

## `docs/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `ANDROID_ARCHITECTURE.md` | doc | Android Agent | Active | Android app architecture |
| `ARCHITECTURE.md` | doc | Engine Agent | Active | Overall system architecture |
| `DECISIONS.md` | doc | Chief Architect | Active | Architectural decision records |
| `DESIGN_SYSTEM.md` | doc | Design Team | Active | Synthetic Noir design system reference |
| `DOCUMENTATION_GOVERNANCE.md` | gov | Chief Architect | Active | Documentation ownership, types, lifecycle policies |
| `ENGINE_API.md` | doc | Engine Agent | Active | Go engine gRPC API reference |
| `EPICS.md` | doc | Chief Architect | Active | Epic tracking and story status |
| `JNI_ARCHITECTURE.md` | doc | Android Agent | Active | JNI bridge architecture |
| `PLATFORM_GUIDE.md` | doc | Engine Agent | Active | Platform team development guide |
| `PROJECT_STATE.md` | doc | Chief Architect | Active | Operational dashboard — milestones, health, current work |
| `ROADMAP.md` | doc | Chief Architect | Active | Product roadmap |
| `VISION.md` | doc | Chief Architect | Active | Project vision statement |
| `WEB_ARCHITECTURE.md` | doc | Web Agent | Active | Web frontend architecture |

### `docs/analysis/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `EPIC3_FINAL.md` | doc | Chief Architect | Archived | EPIC 3 final report — historical reference |
| `GOVERNANCE_SPRINT_1_FINAL.md` | doc | Chief Architect | Archived | Governance Sprint 1 final report |

### `docs/archive/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `ENGINE_CLIENT_CONTRACT.md` | doc | Chief Architect | Archived | Superseded by ENGINE_API.md |
| `GUILD.md` | doc | Chief Architect | Archived | Superseded by AGENT.md |
| `MIGRATION_ROADMAP.md` | doc | Chief Architect | Archived | Migration implementation plan — migration complete |
| `PLATFORM_RULES.md` | doc | Chief Architect | Archived | Superseded by AGENT.md |
| `PROJECT_PLANNING_SPRINT_1.md` | doc | Chief Architect | Archived | Historical sprint report |
| `REPOSITORY_ARCHITECTURE_PROPOSAL.md` | doc | Chief Architect | Archived | Migration proposal — migration complete |
| `SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md` | doc | Chief Architect | Archived | Historical sprint report |
| `SYNC.md` | doc | Chief Architect | Archived | Cherry-pick sync model — superseded by merge-based sync |

### `docs/engine/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `MODULE_BOUNDARIES.md` | doc | Engine Agent | Active | Go engine module dependency diagram and invariants |
| `PROGRESS.md` | doc | Engine Agent | Active | Engine development progress tracking |

### `docs/engineering/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `gradle-go-integration.md` | doc | Android Agent | Active | Gradle + Go build integration guide |

### `docs/migration/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `MIGRATION_LOG.md` | doc | Chief Architect | Archived | Running log of migration actions — migration complete |
| `stash-recovered-wip.patch` | doc | Chief Architect | Archived | Recovered WIP patch from pre-migration stash |

---

## `engine/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| (tree) | src | Engine Agent | Active | Go engine — gRPC server, agent runtime, provider registry |

---

## `scripts/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `setup-worktrees.ps1` | src | Chief Architect | Active | Git worktree setup script |

---

## `sdk/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `go/` | src | Engine Agent | Stale | Go SDK scaffold — not yet implemented |
| `kotlin/` | src | Android Agent | Active | Kotlin SDK — gRPC client, JNI bridge, engine integration |
| `swift/` | doc | Engine Agent | Stale | Swift SDK stub — not yet implemented |
| `typescript/` | src | Web Agent | Active | TypeScript SDK — gRPC-Web client for web frontend |

---

## `web/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| (config files) | cfg | Web Agent | Active | Vite, ESLint, TypeScript, Tailwind configuration |
| `package.json` | cfg | Web Agent | Active | Node dependencies and scripts |
| `pnpm-lock.yaml` | cfg | Web Agent | Active | Dependency lockfile |
| `public/` | src | Web Agent | Active | Static assets (favicon, icons) |
| `src/app/` | src | Web Agent | Active | App shell, providers, router |
| `src/components/` | src | Web Agent | Active | UI components (layout + shadcn/ui) |
| `src/engine/` | src | Web Agent | Active | Engine SDK (RuntimeClient, EngineClient, transport, health) |
| `src/features/chat/` | src | Web Agent | Active | Chat UI (input, messages, conversation list, model selector) |
| `src/layouts/` | src | Web Agent | Active | Page layout components |
| `src/lib/` | src | Web Agent | Active | Shared utilities |
