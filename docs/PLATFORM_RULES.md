# Ashwath.AI Platform Rules

Status: Living Document

---

# Purpose

These rules govern every repository, every client, every SDK and the Go Engine.

Whenever an architectural decision is made, it must comply with these rules.

---

# Rule 1

The Go Engine is the product.

Everything else is a client.

---

# Rule 2

Business logic belongs in the Engine.

Clients never duplicate business logic.

---

# Rule 3

Clients own presentation only.

Clients are responsible for:

- UI
- UX
- Navigation
- Platform integration
- Accessibility
- Local state

---

# Rule 4

The Engine owns intelligence.

Including:

- AI
- Memory
- RAG
- Plugins
- Models
- Downloads
- Sessions
- Voice
- Vision

---

# Rule 5

Architecture before implementation.

If architecture is unclear, implementation stops.

---

# Rule 6

Documentation is part of the codebase.

Major architectural changes require documentation updates.

---

# Rule 7

One source of truth.

Never duplicate:

- APIs
- Architecture
- Business rules
- Documentation

---

# Rule 8

Shared contracts before platform implementations.

Engine

↓

SDK

↓

Clients

Never the opposite.

---

# Rule 9

Feature parity.

Every supported client should expose the same platform capabilities whenever technically possible.

---

# Rule 10

Small commits.

Every meaningful milestone should be committed independently.

---

# Rule 11

Platform-first thinking.

Build Ashwath.AI.

Do not build separate applications.

---

# Rule 12

Quality over speed.

A correct architecture is always preferred over a quick implementation.