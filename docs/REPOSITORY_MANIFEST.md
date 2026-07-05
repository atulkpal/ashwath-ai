# Ashwath AI Repository Manifest

**Authoritative map of every directory and file.**
**Owner:** Chief Architect
**Lifecycle:** Active — update when directories or files are added/removed.
**Last updated:** July 2026 (Planning Sprint 1)

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
| `CHANGELOG.md` | doc | Platform Team | Active | Release changelog |
| `CONTRIBUTING.md` | doc | Chief Architect | Active | Contribution guidelines |
| `LICENSE` | cfg | Chief Architect | Active | Apache 2.0 license |
| `Makefile` | cfg | Platform Team | Active | Engine build targets |
| `README.md` | doc | Chief Architect | Active | Project overview and quickstart |

---

## `.github/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `workflows/android-ci.yml` | cfg | Android Team | Active | Android CI pipeline |
| `workflows/engine-ci.yml` | cfg | Platform Team | Active | Go engine CI (lint, test, build) |
| `workflows/release-engine.yml` | cfg | Platform Team | Active | Engine binary release pipeline |

---

## `android/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `.gitignore` | cfg | Android Team | Active | Android git exclusion rules |
| `build.gradle.kts` | cfg | Android Team | Active | Root Gradle build config |
| `gradle.properties` | cfg | Android Team | Active | Gradle JVM/Android properties |
| `gradlew` | cfg | Android Team | Active | Gradle wrapper (Unix) |
| `gradlew.bat` | cfg | Android Team | Active | Gradle wrapper (Windows) |
| `local.properties` | cfg | Android Team | Active | Local SDK path |
| `settings.gradle.kts` | cfg | Android Team | Active | Module includes |
| `app/` | src | Android Team | Active | Android application module |
| `gradle/` | cfg | Android Team | Active | Gradle version catalog and wrapper |

---

## `design/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Design Team | Active | Design system overview |
| `android/v1/` | design | Design Team | Active | Android screen designs (chat, discover, knowledge, models, settings, theme) |
| `shared/` | design | Design Team | Active | Shared design tokens, brand, components, icons, motion, UX, accessibility |
| `web/v1/` | design | Design Team | Active | Web screen designs (app-shell, chat, discover, explore, knowledge, library, models, settings, theme) |

---

## `desktop/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Platform Team | Active | Desktop platform stub — future work |

---

## `docs/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `ANDROID_ARCHITECTURE.md` | doc | Android Team | Active | Android app architecture |
| `ARCHITECTURE.md` | doc | Platform Team | Active | Overall system architecture |
| `DECISIONS.md` | doc | Chief Architect | Active | Architectural decision records |
| `DESIGN_SYSTEM.md` | doc | Design Team | Active | Synthetic Noir design system reference |
| `DOCUMENTATION_GOVERNANCE.md` | gov | Chief Architect | Active | Documentation ownership, types, lifecycle policies |
| `ENGINE_API.md` | doc | Platform Team | Active | Go engine gRPC API reference |
| `ENGINE_CLIENT_CONTRACT.md` | doc | Platform Team | Active | Engine-sdk client contract |
| `EPICS.md` | doc | Chief Architect | Active | Epic tracking and story status |
| `GUILD.md` | doc | Chief Architect | Active | Engineering charter — team structure, culture, principles |
| `JNI_ARCHITECTURE.md` | doc | Android Team | Active | JNI bridge architecture |
| `PLATFORM_GUIDE.md` | doc | Platform Team | Active | Platform team development guide |
| `PLATFORM_RULES.md` | doc | Platform Team | Active | Platform engineering rules |
| `PROJECT_STATE.md` | doc | Chief Architect | Active | Operational dashboard — milestones, health, current work |
| `ROADMAP.md` | doc | Chief Architect | Active | Product roadmap |
| `VISION.md` | doc | Chief Architect | Active | Project vision statement |
| `WEB_ARCHITECTURE.md` | doc | Web Team | Active | Web frontend architecture |

### `docs/analysis/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `EPIC3_FINAL.md` | doc | Chief Architect | Archived | EPIC 3 final report — historical reference |
| `GOVERNANCE_SPRINT_1_FINAL.md` | doc | Chief Architect | Active | Governance Sprint 1 final report |
| `SPRINT_W3A_ENGINE_INTEGRATION_ARCHITECTURE.md` | doc | Web Team | Archived | Web-engine integration architecture (historical) |

### `docs/engine/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `MODULE_BOUNDARIES.md` | doc | Platform Team | Active | Go engine module dependency diagram and invariants |
| `PROGRESS.md` | doc | Platform Team | Active | Engine development progress tracking |

### `docs/engineering/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `gradle-go-integration.md` | doc | Android Team | Active | Gradle + Go build integration guide |

---

## `engine/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| (tree) | src | Platform Team | Active | Go engine — gRPC server, agent runtime, provider registry |

---

## `examples/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Platform Team | Stale | Example code stub — not yet populated |

---

## `ios/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Platform Team | Active | iOS platform stub — future work |

---

## `scripts/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Chief Architect | Active | Scripts overview |
| `setup-worktrees.ps1` | src | Chief Architect | Active | Git worktree setup script |

---

## `sdk/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `go/` | src | Platform Team | Active | Go SDK — gRPC client library |
| `kotlin/` | src | Android Team | Active | Kotlin SDK — gRPC client, JNI bridge, engine integration |
| `swift/` | doc | Platform Team | Stale | Swift SDK stub — not yet implemented |
| `typescript/` | doc | Web Team | Stale | TypeScript SDK stub — not yet implemented |

---

## `tools/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| `README.md` | doc | Platform Team | Stale | Tools stub — not yet populated |

---

## `web/`

| Path | Type | Owner | Lifecycle | Purpose |
|---|---|---|---|---|
| (config files) | cfg | Web Team | Active | Vite, ESLint, TypeScript, Tailwind configuration |
| `package.json` | cfg | Web Team | Active | Node dependencies and scripts |
| `pnpm-lock.yaml` | cfg | Web Team | Active | Dependency lockfile |
| `public/` | src | Web Team | Active | Static assets (favicon, icons) |
| `src/app/` | src | Web Team | Active | App shell, providers, router |
| `src/components/` | src | Web Team | Active | UI components (layout + shadcn/ui) |
| `src/engine/` | src | Web Team | Active | Engine SDK (RuntimeClient, EngineClient, transport, health) |
| `src/features/chat/` | src | Web Team | Active | Chat UI (input, messages, conversation list, model selector) |
| `src/layouts/` | src | Web Team | Active | Page layout components |
| `src/lib/` | src | Web Team | Active | Shared utilities |
