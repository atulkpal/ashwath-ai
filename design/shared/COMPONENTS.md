# Ashwath AI Shared Components

**Status:** Draft

---

## Component Strategy

Components in the Ashwath AI platform are **presentational only**. They receive data via props and emit events via callbacks. Business logic resides exclusively in the Go Engine or in feature-specific view models.

Shared components live in each client's component library. Each client translates the visual and behavioural contracts defined here into its native platform idioms.

---

## Component Categories

### Layout Shell

The application shell is consistent across all clients. See `DESIGN_LANGUAGE.md` for spatial mapping.

| Component | Purpose |
|-----------|---------|
| `Sidebar` | Primary navigation. Collapsible on desktop. |
| `TopBar` | App branding, model status, right-panel toggle |
| `MainContentArea` | Scrollable content outlet |
| `RightPanel` | Detail/inspection panel, hidden by default |
| `StatusBar` | Hardware telemetry strip |

### Navigation

| Component | Description |
|-----------|-------------|
| `NavItem` | Icon + label pair. Ghost variant by default. Active state uses cyan accent bar on left edge. |
| `NavSectionHeader` | Uppercase `label-caps` text. Visible only when parent nav is expanded. |
| `NavSeparator` | 1px `--sn-border` hairline. Full width within nav container. |

### Buttons

| Variant | Background | Border | Text | Usage |
|---------|------------|--------|------|-------|
| **Primary** | `--sn-accent` | none | `--sn-accent-text` (#000) | Main CTAs: Send, Download, Initialize |
| **Secondary** | transparent | `--sn-border` | `--sn-text-primary` | Alternative actions |
| **Ghost** | transparent | none | `--sn-text-secondary` → primary on hover | Icon buttons, nav items |
| **Destructive** | `--sn-status-error` at 10% | none | `--sn-status-error` | Delete, purge, reset |

**Rules:**
- No shadow on any button variant.
- Border radius: `--sn-radius-md` (6px).
- Text: UI font weight 500.
- Icon-only buttons: square aspect ratio, centred icon.

### Inputs

| Element | Rules |
|---------|-------|
| Text fields | Background `--sn-overlay`, border `--sn-border` 1px, radius 6px. Focus: border `--sn-accent` + 2px spread ring. Font: `--sn-font-ui`. |
| Textareas | Same as text fields. Auto-resize only where engine streaming is not involved. |
| Range sliders | Track: `--sn-overlay`. Thumb: `--sn-accent`. |
| Selects | Same border/token treatment as text fields. Chevron icon in `--sn-text-secondary`. |

### Cards & Containers

| Rule | Value |
|------|-------|
| Background | `--sn-raised` |
| Border | `--sn-border` 1px |
| Radius | `--sn-radius-lg` (8px) |
| Padding | `--sn-space-md` (16px) |
| Header separator | `--sn-border` 1px bottom border inside card |

### Status Indicators

| State | Token | Rendering |
|-------|-------|-----------|
| Active / LOCAL | `--sn-status-active` | 6px cyan circle + pulse animation |
| Ready / Connected | `--sn-status-ready` | 6px green circle |
| Warning | `--sn-status-warning` | 6px amber circle |
| Error | `--sn-status-error` | 6px red circle |

Pulse animation:
```css
@keyframes sn-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
```
Duration: 2s. Easing: ease-in-out. Infinite.

### Telemetry

Telemetry displays must use `--sn-font-code` at `--sn-text-label-md` or `--sn-text-label-sm`. Values that update live must use the `label-caps` uppercase tracking style.

---

## Components Not Shared

Not every UI element belongs in the shared design system. The following component types are **feature-specific** and live within their respective feature modules:

| Component Type | Location | Rationale |
|----------------|----------|-----------|
| `ChatMessage` | Domain-specific to chat. Not reused across features. |
| `ModelCard` | Business logic tied to model management. |
| `KnowledgeDocument` | Specific to knowledge-base workflows. |
| `ExploreCard` | Discovery-specific layout. |
| `SettingsToggle` | Feature-specific state interaction. |

**Rule:** If a component contains domain logic, engine-facing state, or is unlikely to appear in more than one feature, it does not belong in the shared system. Share only the **container primitives** (cards, inputs, buttons) and **layout shell** (sidebar, topbar, panels).

---

## Composition Rules

1. **Container maximum:** Content width must not exceed `--sn-container-max` (1440px) on desktop. Mobile is fluid with `--sn-gutter` margins.
2. **Grid alignment:** All elements align to the 4px baseline. Spacing between related items is `--sn-space-sm` (8px). Section spacing is `--sn-space-lg` (24px).
3. **Z-index layers:**
   - 0: Content
   - 10: Sidebar / TopBar
   - 20: RightPanel
   - 30: StatusBar
   - 40: Modal overlays
4. **No overlap:** Panels push content via width transitions; they never overlay or float above other elements.