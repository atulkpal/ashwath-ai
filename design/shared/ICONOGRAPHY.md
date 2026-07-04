# Ashwath AI Iconography

**Status:** Draft

---

## Philosophy

Icons in Synthetic Noir are **functional, not decorative**. They communicate navigation, state, and action with maximum clarity at small sizes. Ornamentation, gradients, and transparency effects are forbidden.

For motion rules applied to icon transitions, see [MOTION.md](MOTION.md).

---

## Glyph Types

Synthetic Noir uses four distinct glyph types. Each has a different role and set of constraints. Confusing these categories is a common source of inconsistency.

| Type | Definition | Examples |
|------|-------------|----------|
| **Icon** | A simple, resolution-independent symbol representing an action, object, or concept. Used in buttons, navigation, and inline actions. | `Terminal`, `ChevronLeft`, `Send`, `Settings` |
| **Illustration** | A more complex, often scene-based graphic used to communicate a concept or emotion. Used sparingly in empty states or onboarding. | (Reserved for future use) |
| **Logo** | The branded identifier for the product or company. Used in headers, splash screens, and about pages. | Terminal icon + "Ashwath AI" wordmark |
| **Diagram** | A structured visualization of data, architecture, or flow. Used in documentation and debug panels. | Architecture diagrams, flow charts |

**Rules:**
- Icons are for interaction.
- Illustrations are for narrative.
- Logos are for identity.
- Diagrams are for explanation.
- Never substitute an illustration for an icon, or a logo for an icon.

---

## Icon Library

**Primary:** Lucide

**Style constraint:** Use only `stroke-width="2"` (default) or `stroke-width="1.5"` for small sizes. Never use filled variants except where explicitly noted.

---

## Sizing

| Context | Size | Stroke Width |
|---------|------|--------------|
| Nav items (sidebar) | 18–20px | 2 |
| Top bar buttons | 16–18px | 2 |
| Inline actions | 14–16px | 2 |
| Status indicators | 8–10px | 2 |
| Dense data tables | 12px | 1.5 |

---

## Colour Rules

| State | Colour Token |
|-------|-------------|
| Default | `--sn-text-secondary` |
| Hover | `--sn-text-primary` |
| Active / selected | `--sn-accent` |
| Disabled | `--sn-text-tertiary` |

Icons inherit text colour via `currentColor`. Never apply direct fill or stroke colours to icons unless the icon represents a status indicator (see below).

---

## Status Icons

Status icons override the default colour rule and use semantic colours:

| Icon | Colour | Usage |
|------|--------|-------|
| `Circle` (filled, 6px) | `--sn-status-active` | LOCAL, active processing |
| `Circle` (filled, 6px) | `--sn-status-ready` | Connected, healthy |
| `Circle` (filled, 6px) | `--sn-status-warning` | Degraded |
| `Circle` (filled, 6px) | `--sn-status-error` | Error, disconnected |

**Animation:** Status circles pulse at 2s infinite for `active` state only.

---

## Navigation Icons

Each nav item in the sidebar uses a fixed icon from the Lucide set. The mapping is:

| Section | Icon | Meaning |
|---------|------|-------|
| Chat | `MessageSquare` | Conversation, inference |
| Library | `Library` | Saved models, bookmarks |
| Knowledge | `Database` | RAG, vector store, files |
| Explore | `Compass` | Discovery, trends |
| Settings | `Settings` | Configuration |

**Rules:**
- Icons are never replaced with platform-native alternatives.
- Icon choice is platform-agnostic; the same semantic icon is used on Web, Android, Desktop, and iOS.
- If an icon is unavailable in a platform's native set, use the closest Lucide equivalent.

---

## Action Icons

| Icon | Usage |
|------|-------|
| `Terminal` | App branding |
| `ChevronLeft` | Sidebar collapse toggle |
| `PanelRightOpen` / `PanelRightClose` | Right panel toggle |
| `X` | Close, dismiss |
| `Plus` | Add, create |
| `Download` | Model download |
| `Search` | Search, filter |
| `Send` | Submit prompt |
| `Mic` | Voice input |
| `Build` / `Wrench` | Tools, settings shortcut |

---

## Composition Rules

1. **Icon + label:** Always pair icons with text labels in navigation. Never icon-only nav items except in collapsed sidebar state.
2. **Padding:** Icon buttons use square touch/click targets. Minimum 32×32px on Web, 48×48dp on Android.
3. **Alignment:** Icons align to the left edge of their container or to the text baseline when inline with text.
4. **No badges on icons:** Badge counts are rendered as separate text elements adjacent to the icon, not as SVG overlays.