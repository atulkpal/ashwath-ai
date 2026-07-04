# Ashwath.AI AI Contributor Guide

## 1. Purpose
This document is the operational manual and onboarding guide for every AI agent contributing to Ashwath.AI. Its goal is to provide sufficient context and repository knowledge for an AI to begin productive work with minimal external prompt context.

**Implementation is the source of truth.** Always verify documentation against the actual code.

---

## 2. Repository Overview
Ashwath.AI is a modular, local-first AI platform. It consists of a high-performance core **Engine** (Go) and multiple **Native Clients** (Android, Web, etc.) that communicate with the engine via platform communication interfaces.

For deep-dives, refer to:
- **Philosophy & Rules**: `docs/GUILD.md`
- **Architecture**: `docs/ARCHITECTURE.md` and `docs/JNI_ARCHITECTURE.md`
- **Vision**: `docs/VISION.md`
- **Current Status**: `docs/PROJECT_STATE.md`

---

## 3. Required Reading for Every Session
Every AI session MUST begin by reading:
1. `README.md`
2. `docs/GUILD.md` (The Engineering Charter)
3. `docs/PROJECT_STATE.md` (Operational Dashboard)

---

## 4. Current Mission
The engine foundation has been stabilized (EPIC 3 - Phase A). The current mission is **EPIC 3 – Phase B (Architecture Foundation)**. This involves standardizing gRPC contracts, refining SDK abstractions, and preparing for multi-backend support.

Before making changes, identify the specific goals of Phase B from `docs/PROJECT_STATE.md`. If `docs/PROJECT_STATE.md` and the implementation disagree, report the inconsistency before implementing changes. **The implementation remains the source of truth.**

## 5. Repository Audit (Engine Maturity)
As of Phase A completion:
- **Engine**: Supports real `llama.cpp` backend wiring, persistent model registry, and background downloads.
- **Android**: Successfully embeds the engine and communicates via loopback gRPC.
- **SDK**: Kotlin SDK is the reference implementation for gRPC communication.

---

## 6. Universal Engineering Rules
- **Preserve Architecture**: Do not modify the monorepo structure or cross-module boundaries without approval.
- **Respect Primary Maintainers**: Responsibility for specific folders is assigned to specific teams (see `docs/GUILD.md`).
- **Incremental Improvements**: Prefer surgical edits and evolutionary changes over wholesale rewrites.
- **Synchronized Documentation**: If code changes, update the corresponding docs, ADRs, or CHANGELOG immediately.
- **No Inventions**: Never invent project milestones, versions, or architectural decisions. If a fact is missing, state it explicitly.
- **Leave it Cleaner**: Every session should reduce technical debt or improve clarity.

---

## 7. Team Sections

### Platform Team (OpenCode Agent)
- **Mission**: Maintain the "Heart of Ashwath."
- **Primary Folders**: `engine/`, `sdk/`, `docs/`
- **Responsibilities**: Engine logic, platform communication boundaries, cross-platform build pipelines.
- **Avoid**: Modifying Android/Web UI components unless fixing a direct integration bug.

### Android Client Team (Android Studio Gemini Agent)
- **Mission**: Deliver the flagship mobile experience.
- **Primary Folders**: `android/`
- **Responsibilities**: Jetpack Compose UI, ViewModels, Android system integrations (services, hardware).
- **Avoid**: Modifying Engine internal logic. Always communicate via the SDK interfaces.

### Web Client Team
- **Mission**: Bring local AI to the browser.
- **Primary Folders**: `web/`
- **Responsibilities**: React/TS implementation, platform communication integration.

### Research Lab
- **Mission**: Innovation and experimental models.
- **Primary Folders**: `research/` (Future)
- **Responsibilities**: Quantization, RAG experiments, multimodal testing.

---

## 8. Cross-Team Collaboration
- **Visibility**: All agents have unrestricted visibility. You are encouraged to understand the entire system to make better local decisions.
- **Responsibility vs. Exclusivity**: Primary Maintainer status does not mean you cannot suggest changes elsewhere, but major cross-boundary modifications (e.g., API changes) must be discussed.
- **Contracts**: All communication between Engine and Clients must follow the established SDK contracts and platform communication definitions.

---

## 9. Session Checklist
Before concluding a session, verify:
- [ ] The project still builds (Android: `./gradlew assembleDebug`, Engine: `go build`).
- [ ] Unit tests pass in the modified module.
- [ ] `docs/PROJECT_STATE.md` is updated if a milestone was reached.
- [ ] Ownership boundaries and `docs/GUILD.md` rules were respected.
- [ ] No historical architectural decisions were accidentally removed.

---

## 10. Maintaining AGENT.md
- The **Universal Engineering Rules** change rarely.
- Team sections should be updated as new AI agents join or responsibilities shift.
- Platform-wide status belongs in `docs/PROJECT_STATE.md`, not here.
