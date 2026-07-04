# Ashwath AI Motion Guidelines

**Status:** Draft

---

## Philosophy

Motion in Synthetic Noir is **surgical, not decorative**. Animations exist to communicate state change, not to entertain. Every animation must be fast, purposeful, and respectful of the user's focus.

For component-specific behavioural contracts, see [COMPONENTS.md](COMPONENTS.md). Duration values are defined in this document.

---

## Core Principles

### 1. Speed Over Smoothness

The default animation duration is **150ms**. Maximum allowed duration for any UI animation is **200ms**. Longer animations feel sluggish and disrespect the user's time.

### 2. Easing Rules

| Property | Allowed Easing | Forbidden |
|----------|---------------|-----------|
| Position / width / height | `ease-out` | `linear`, `ease-in`, `ease-in-out` |
| Opacity | `ease-out` | spring physics |
| Colour / border | `ease-out` | any cubic-bezier with overshoot |
| Transform | `ease-out` | bounce, elastic |

**Rule:** Start fast, end slow. This creates the perception of instant response with graceful arrival.

### 3. No Loops, No Idles

Animations must be triggered by user action or state change. No ambient idle animations except:
- The LOCAL pulse dot for LOCAL / active status (2s infinite)
- Engine processing indicators during inference (1.5s infinite)

No other infinite or looping animations are permitted.

### 4. Reduced Motion

Always honor the user's system preference:

```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

When reduced motion is enabled, panel toggles snap to final state with no transition.

---

## Animation Catalog

### Panel Transitions

| Element | Property | Duration | Easing |
|---------|----------|----------|--------|
| Sidebar width | `width` | 200ms | `ease-out` |
| Right panel width | `width` | 200ms | `ease-out` |
| Panel content opacity | `opacity` | 150ms | `ease-out` |

**Rule:** Content inside panels must not fade independently of the panel width. The width transition provides enough visual continuity.

### Buttons

| State | Property | Duration |
|-------|----------|----------|
| Hover / active | `background-color`, `border-color` | 150ms |
| Active press | `transform: translateY(1px)` | 100ms |
| Disabled | `opacity: 0.5` | 150ms |

**Rule:** No scale transforms on buttons. The active state moves the button down 1px via `translateY` to simulate a physical press.

### Navigation

| Element | Property | Duration |
|---------|----------|----------|
| Nav item hover | `background-color`, `color` | 150ms |
| Cyan accent bar | `opacity` | 150ms |
| Nav item active indicator | `opacity` | 150ms |

**Rule:** The cyan accent bar on nav items fades in on hover. It must never slide, grow, or animate its position.

### Inputs

| Element | Property | Duration |
|---------|----------|----------|
| Focus ring | `border-color`, `box-shadow` | 150ms |
| Placeholder fade | `opacity` | 150ms |

**Rule:** Focus ring uses a 2px spread ring of `--sn-accent` with `0px` blur. No glow or blur effects are permitted on inputs.

### Status Indicators

| Element | Property | Duration |
|---------|----------|----------|
| LOCAL pulse dot | `opacity` | 2s infinite, ease-in-out |
| Engine processing | `opacity` | 1.5s infinite, ease-in-out |

Pulse keyframes:
```css
@keyframes sn-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
```

---

## Forbidden Patterns

- No page transitions or route animations
- No skeleton loaders unless engine explicitly requests them
- No parallax, scroll-linked animations, or reveal-on-scroll
- No haptic feedback simulation via animation
- No loading spinners for local operations
- No bounce, spring, or elastic easing