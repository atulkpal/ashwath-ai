# Ashwath AI UX Principles

**Status:** Draft

---

## Core Philosophy

Ashwath AI is a tool for power users. The UX must therefore prioritize **control, clarity, and speed** over onboarding hand-holding or decorative playfulness. Every interaction should feel like operating precision equipment — responsive, predictable, and purposeful.

---

## Foundational Principles

### 1. Offline-First Default

The primary user expectation is that the application works without an internet connection. UX patterns must reflect this:
- No loading spinners for local operation
- Status indicators clearly distinguish "local" from "remote"
- Error messages explain local constraints (disk space, RAM, NPU availability)

### 2. Engine-Owned Intelligence

The client owns presentation only. All AI behavior originates from the Go Engine. UX must never imply client-side intelligence:
- Streaming responses render exactly as received from engine
- No "typing" simulation unless engine sends partial tokens
- Errors surface engine error codes verbatim

### 3. Local State Sovereignty

Client state is limited to:
- Theme preference
- Navigation state
- Window/split-panel layout
- Dialog open/close state
- Local form inputs

The client never caches AI conversation state, model weights, or knowledge-base data as "owned" — those belong to the engine.

### 4. Progressive Disclosure

Technical power must not intimidate casual users, but must not be hidden from experts:
- Advanced settings are always accessible but visually subdued
- Telemetry data is visible but not dominant
- Complex operations surface warnings, not blockers

### 5. Feedback Over Fashion

Every user action must produce observable feedback within 100ms:
- Buttons: background color shift or border highlight
- Nav items: cyan accent bar + text brightness shift
- Panel toggles: width transition (no fade)
- Form fields: border color change on focus

**Visual feedback rules:**
- No animation exceeds 200ms
- No spring or bounce physics
- Transitions use `ease-out` curves only

### 6. Keyboard-First Navigation

All navigation and interaction patterns must be keyboard accessible:
- Tab order follows visual hierarchy
- Escape closes overlays, panels, and dialogs
- Arrow keys navigate within lists and grids
- Enter activates the focused control

### 7. Never Surprise the User

The interface must behave exactly as the user expects. Predictability beats delight:
- Consistent placement of controls across all clients
- No hidden gestures or secret interactions
- Destructive actions require explicit confirmation
- Errors explain what happened and how to recover
- State changes are immediately visible

**Rule:** If a user cannot predict the outcome of an action from the current interface state, the design has failed.

---

## Interaction Patterns

### Panels

| Pattern | Behaviour |
|---------|-----------|
| Right panel | Toggles via top-bar button. Animates width (0 <-> 320px). Does not overlay content. Escape to close. |
| Sidebar collapse | Toggles width (64px <-> 256px). Content hides with zero delay. No animation on icons. |
| Modal overlays | Reserved for critical confirmations only. Backdrop is pure black at 80% opacity. |

### Data Density

- **Information density is the default.** Generous whitespace does not mean empty space — it means measured spacing aligned to the 8px grid.
- Scan-ability trumps discoverability. Primary actions should be visible, not buried behind menus.

### Error Handling

- Errors are surfaced inline, not as toast notifications (except for engine-disconnect scenarios).
- Retry actions are adjacent to the error message.
- Engine errors preserve original error codes for debugging.

---

## Mental Model

Users should understand the application architecture from the interface itself:
- The **sidebar** represents available capabilities
- The **main area** represents the active workspace
- The **right panel** represents detail/inspection
- The **status bar** represents system health
- The **cyan accent** represents engine activity / AI processing

This spatial mapping must remain consistent across all clients.