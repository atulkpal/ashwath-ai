# Web Architecture

**Status:** Draft

---

# Purpose

The Web Client is a presentation layer for the Ashwath AI platform.

It is responsible for:

- Rendering the user interface
- Managing client-side state
- Communicating with the Go Engine
- Presenting streamed responses

The Web Client does **not** contain business logic.

The Go Engine remains the single source of truth.

---

# Architecture

```
Browser
    │
React + Vite
    │
Application Layer
    │
Engine Client
    │
gRPC / gRPC-Web
    │
Ashwath Engine
```

---

# Responsibilities

## Web Client

- UI
- Navigation
- Theme
- Routing
- Responsive layouts
- Accessibility
- Client-side state

---

## Go Engine

- AI inference
- Sessions
- Memory
- Knowledge
- RAG
- Voice
- Vision
- Plugins
- Downloads
- Model management

---

# Source Layout

```
src/
├── app/
├── components/
├── engine/
├── features/
├── hooks/
├── layouts/
├── services/
├── stores/
├── styles/
└── types/
```

---

# Design Principles

- Thin client
- Engine-first
- Feature-first organization
- No business logic in UI
- Reusable components
- Accessibility first
- Responsive by default

---

# Data Flow

```
User
 ↓
Feature
 ↓
Store
 ↓
Engine Client
 ↓
Go Engine
 ↓
Engine Client
 ↓
Store
 ↓
UI
```

---

# Rules

1. UI never communicates directly with transport protocols.
2. Engine communication is isolated behind the Engine Client.
3. Components are presentation only.
4. Features own user workflows.
5. Shared UI belongs in `components/`.

---

# Technology

- React
- Vite
- TypeScript
- Tailwind CSS v4
- shadcn/ui
- Lucide Icons
- gRPC-Web