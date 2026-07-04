# Ashwath AI Accessibility Standards

**Status:** Draft

---

## Philosophy

Accessibility is not an afterthought. In Synthetic Noir, accessibility features are **integrated invisibly** into the design so that all users can operate the application with the same precision and control expected by power users.

The goal is **functional accessibility**: the interface must be operable by keyboard, screen reader, and high-contrast modes without compromising the aesthetic or performance of the design system.

For interaction rules affected by accessibility, see [UX_PRINCIPLES.md](UX_PRINCIPLES.md). For motion constraints under reduced-motion preferences, see [MOTION.md](MOTION.md).

---

## Standards Baseline

Ashwath AI targets **WCAG 2.1 Level AA** compliance across all clients.

| Criterion | Requirement |
|-----------|-------------|
| **1.4.3 Contrast (Minimum)** | Text: 4.5:1. Large text: 3:1. UI components: 3:1. |
| **1.4.11 Non-text Contrast** | Active states, borders, and focus indicators must meet 3:1 against adjacent backgrounds. |
| **2.1.1 Keyboard** | All functionality available via keyboard. No mouse-only interactions. |
| **2.4.3 Focus Order** | Tab order follows visual hierarchy. |
| **2.4.7 Focus Visible** | Focus indicator must be visible on all interactive elements. |
| **4.1.2 Name, Role, Value** | All UI components expose accessible names and states. |

---

## Colour Contrast

Synthetic Noir naturally exceeds contrast requirements due to its pure-black background and high-contrast typography.

| Pairing | Ratio | Status |
|---------|-------|--------|
| White (#ffffff) on Black (#000000) | 21:1 | ✅ Exceeds AAA |
| Grey (#a1a1a1) on Black (#000000) | 4.6:1 | ✅ Meets AA |
| Cyan (#00f0ff) on Black (#000000) | 15.5:1 | ✅ Exceeds AAA |
| White on Raised (#121212) | 12.6:1 | ✅ Exceeds AAA |
| Cyan on Raised (#121212) | 11.2:1 | ✅ Exceeds AAA |

**Rule:** Never use `--sn-text-tertiary` (#6b6b6b) for interactive text. It falls below 4.5:1 on both `--sn-base` and `--sn-raised`.

---

## Focus Management

### Focus Indicators

All interactive elements must display a visible focus indicator. The Synthetic Noir focus ring uses:

```
border: 2px solid var(--sn-accent)
outline: none
box-shadow: 0 0 0 2px var(--sn-accent)
```

The 2px spread ring provides a minimum 3:1 contrast against the dark background.

### Focus Trap (Modals)

Modal dialogs trap focus within the dialog container. On close, focus returns to the triggering element.

### Skip Links

Skip links are hidden by default and revealed on first keyboard focus. They allow keyboard users to bypass navigation and jump directly to main content.

---

## Keyboard Navigation

| Key Combination | Behaviour |
|-----------------|-----------|
| `Tab` | Advances to next focusable element in DOM order |
| `Shift + Tab` | Returns to previous focusable element |
| `Escape` | Closes modals, right panel, and dropdowns |
| `Enter` / `Space` | Activates focused button or link |
| `ArrowRight` / `ArrowLeft` | Moves focus between tabs and nav sections |
| `ArrowUp` / `ArrowDown` | Moves focus within lists and dropdown menus |

**Rule:** Navigation must never require pointer input. All shell patterns (sidebar collapse, panel toggles) must have keyboard-accessible equivalents.

---

## Screen Reader Support

### Live Regions

| Region | `aria-live` | Usage |
|--------|-------------|-------|
| Status bar | `polite` | Announces engine status changes |
| Streaming output | `polite` | Announces new tokens during inference |
| Error banners | `assertive` | Announces critical errors immediately |

### Semantic Roles

- Sidebar: `role="navigation"` with `aria-label="Main navigation"`
- Main content: `role="main"` with `aria-label="Content"`
- Right panel: `role="complementary"` with `aria-label="Details panel"`
- Status bar: `role="status"` with `aria-live="polite"`

### Icon Buttons

All icon-only buttons must have `aria-label`. Decorative icons must have `aria-hidden="true"`.

---

## Reduced Motion

Respect `prefers-reduced-motion: reduce`:

```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

When reduced motion is enabled:
- Panel width transitions snap to final state
- Focus ring appears instantly
- Pulse animations stop (static dot remains visible)
- No content is hidden or removed

---

## Platform Notes

### Web

- Use native HTML elements (`button`, `a`, `input`, `nav`, `main`, `header`, `footer`) rather than `div` soup.
- All interactive elements must be reachable via `Tab`.
- Focus management for single-page navigation is the responsibility of the routing layer.

### Android

- Use Jetpack Compose semantics: `Modifier.semantics { ... }`
- Set `contentDescription` on all Image and Icon composables.
- Announce state changes via ` LiveRegion`.

### Desktop / iOS

- Follow platform-native accessibility APIs (NSAccessibility, AT-SPI).
- Preserve Synthetic Noir token meanings in platform accessibility labels.