# Engine Client Contract

Status: Active

> This contract governs all existing and future clients (Android, Web, Desktop, iOS, CLI).

---

# Purpose

This document defines the contract between the Ashwath Engine and every client application.

Clients include:

- Android
- Web
- Desktop
- iOS
- CLI
- Future clients

The purpose is to ensure every client behaves consistently while keeping the Go Engine as the single source of truth.

---

# Fundamental Rule

The Go Engine owns business logic.

Clients own presentation.

---

# Engine Responsibilities

The engine is responsible for:

- AI inference
- Conversation orchestration
- Memory
- Knowledge
- RAG
- Plugins
- Downloads
- Voice
- Vision
- Sessions
- Model management
- Tool execution

---

# Client Responsibilities

Clients are responsible for:

- UI
- Navigation
- Rendering
- Animations
- Client-side state
- Accessibility
- Platform integrations

Clients never duplicate engine logic.

---

# Communication

Clients communicate only through the Engine API.

Clients never access internal engine packages.

---

# Streaming

All streamed responses originate from the engine.

Clients only render stream events.

---

# State Ownership

Engine State

- Conversations
- Models
- Memory
- Knowledge
- Plugins

Client State

- Theme
- Window state
- Navigation
- Dialogs
- Local preferences

---

# Offline Behaviour

The client may cache UI state.

The engine owns AI state.

---

# Error Handling

The engine returns structured errors.

Clients present those errors.

Clients never invent engine behaviour.

---

# Future Compatibility

Every new client must implement this contract.

The contract is platform independent.

No client receives special behaviour.