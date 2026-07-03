# Ashwath.AI Engineering Charter (The Guild)

This document serves as the official Engineering Charter for Ashwath.AI. It defines the responsibilities, workflows, and technical philosophies that guide all human and AI contributors to the project.

---

## 1. Project Philosophy

Ashwath.AI is a **local-first AI platform**, not merely a collection of standalone applications. Our goal is to democratize private, offline artificial intelligence by providing a robust, high-performance **Engine** that can be leveraged across multiple frontends.

The long-term vision is a unified AI core (the Engine) that serves as the "brain" for various clients:
- **Mobile**: Android (primary) and iOS.
- **Web**: Browser-based interfaces for desktop and mobile.
- **Desktop**: Native applications for Linux, macOS, and Windows.

Every line of code written should contribute to the stability, portability, and modularity of the platform. We build platforms, not isolated applications.

---

## 2. Engineering Organization

To maintain high velocity and clear focus, engineering efforts are divided into functional teams and roles:

### Platform Team
Responsible for the "Heart of Ashwath." They maintain the Engine, platform communication interfaces, cross-platform build pipelines, and shared SDKs. Their focus is performance, resource management, and hardware acceleration.

### Android Client Team
Responsible for the flagship mobile experience. They maintain the Android application, Jetpack Compose UI, and platform-specific integrations (Foreground Services, Battery Optimization).

### Web Client Team
Responsible for bringing Ashwath to the browser. They maintain the web-client implementation, ensuring that platform capabilities are accessible via standard web technologies.

### Research Lab
The innovation wing. They explore new LLM architectures, quantization techniques, and multimodal capabilities. Their work is experimental and serves as the roadmap for future platform features.

### Chief Architect
A critical role responsible for architecture reviews, engineering standards, repository organization, API consistency, and the long-term technical direction of the platform.

### Product Owner
Responsible for the project vision, roadmap, priorities, milestone planning, and final architectural approval.

---

## 3. Repository Visibility

Visibility enables better engineering decisions. Every contributor (human or AI) should strive to understand the entire platform, even when they are primarily responsible for only one subsystem. Visibility into the repository is unrestricted. 

Cross-team awareness is a core engineering principle because better decisions come from understanding the complete system, including:
- Engine internals and performance characteristics.
- Multi-platform Client implementations.
- SDK abstractions and API contracts.
- Documentation and Architectural decisions.

Ownership in this repository defines responsibility, not isolation.

---

## 4. Repository Primary Maintainers

Ashwath.AI uses a monorepo structure. While visibility is open to all, responsibility is assigned to **Primary Maintainers**. This concept signifies responsibility and stewardship, not exclusivity. Contributors may propose improvements outside their primary area, but significant cross-boundary modifications should be discussed and approved by the respective Primary Maintainer or the Chief Architect.

 Directory | Primary Maintainer | Description |
 :--- | :--- | :--- |
 `engine/` | **Platform Team** | Core Engine logic and low-level bindings. |
 `sdk/` | **Platform Team** | Language-specific platform wrappers. |
 `docs/` | **Platform Team / Docs** | Core platform documentation and API specs. |
 `android/` | **Android Client Team** | Native Android application and mobile assets. |
 `web/` | **Web Client Team** | Web frontend and related tooling. |
 `research/` | **Research Lab** | Experimental models and evaluation data. |

---

## 5. Development Workflow

We utilize a **Git Worktree** workflow to facilitate parallel development.

### Branching Strategy
- **`main`**: The integration branch. It must always be stable, building, and passing all tests. **Never commit directly to `main`.**
- **`feature/platform`**: Active development of the Engine and core services.
- **`feature/android-client`**: Active development of the mobile application.
- **`feature/web-client`**: Active development of the browser application.
- **`research/lab`**: Experimental branch for the Research Lab.

---

## 6. Decision Making

Technical and product decisions follow a structured hierarchy to ensure consistency:
- **Product Decisions**: Belong to the Product Owner.
- **Architecture Decisions**: Require a review by the Chief Architect and recording via an **ADR (Architectural Decision Record)**.
- **Implementation Details**: Belong to the Primary Maintainer of the respective subsystem.

---

## 7. Engineering & Architecture Principles

- **Platform First**: Design the Engine and SDK as a generic platform first, application second.
- **Offline First**: Primary functionality must not rely on an active internet connection.
- **Privacy First**: Local execution by default. Zero-knowledge by design.
- **Evolution over Rewrite**: Prefer evolving the existing codebase over starting from scratch. The repository should become easier to understand after every contribution.
- **Maintainability over Cleverness**: Optimize for readable, sustainable code. Clever solutions that increase technical debt are discouraged.
- **Interfaces are Long-lived**: Define stable communication contracts. Implementations may change, but interfaces should remain consistent.
- **Clean Architecture**: Strict separation of concerns between business logic, data access, and UI.

---

## 8. AI Agent Responsibilities

### Platform Agent (OpenCode)
Primary Maintainer of `engine/`, `sdk/`, repository architecture, Engine APIs, and platform documentation.

### Android Agent (Android Studio Gemini)
Primary Maintainer of `android/`, including implementation, Compose UI, and Android integration.
- **Special Rule**: Gemini may update platform documentation in `docs/` **ONLY** when explicitly instructed. It should never rewrite or reorganize platform documentation autonomously.

### Web Agent
Primary Maintainer of `web/` and web-specific implementation details.

---

## 9. Documentation Standards

Documentation is a shared responsibility. Each subsystem maintains its own technical documentation.
- **README**: Clear setup and usage instructions for the subsystem.
- **Architecture**: Diagrams and high-level descriptions under `docs/`.
- **API Documentation**: Documentation of public interfaces (e.g., KDoc, Javadoc).
- **Evolutionary Approach**: Documentation should evolve alongside the code rather than be rewritten wholesale. Never remove existing information unless intentionally deprecated. Preserve historical architectural decisions whenever practical.
- **Shared Docs**: Platform documentation under `docs/` is shared and should only be modified when explicitly requested or required by an approved milestone.

---

## 10. Repository Quality Standards

- **Testing**: Comprehensive unit tests for all business logic are mandatory.
- **Code Reviews**: Required for all merges to `main` to ensure adherence to this Charter.
- **Naming**: Consistent casing and descriptive naming conventions (PascalCase for types, camelCase for functions).
- **Long-term Thinking**: Architect for the future platform, not just the immediate task at hand.

---

## 11. Engineering Oath

Every contribution should leave Ashwath.AI in a better state than it was found. Before completing any milestone, contributors should ask:

• Is the architecture clearer?
• Is the code easier to understand?
• Is the documentation still accurate?
• Have I reduced technical debt?
• Would a new contributor understand this change?

The goal is continuous improvement of both the software and the engineering organization that builds it.

---

## 12. Final Mission Statement

**Ashwath.AI is a long-term open-source platform dedicated to local, private AI. We believe that intelligence should be accessible to everyone, everywhere, without compromising privacy or performance.**
