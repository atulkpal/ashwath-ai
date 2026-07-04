# Ashwath AI Design Tokens

**Status:** Draft

---

## Token Philosophy

Design tokens are the single source of truth for all visual values in the Ashwath AI platform. Every color, spacing unit, typography scale, and elevation value must be referenced from this set. Hard-coded values are not permitted in implementation.

Tokens are organized into three tiers:
- **Foundation tokens**: Unchanging semantic values (e.g. `--sn-accent: #00f0ff`)
- **Semantic tokens**: Contextual mappings derived from foundation tokens (e.g. `--color-primary: var(--sn-accent)`)
- **Component tokens**: Specific to individual components (e.g. `--nav-item-hover-bg: var(--sn-overlay)`)

Only foundation and semantic tokens are documented here. Component tokens belong in [COMPONENTS.md](COMPONENTS.md).

---

## Foundation Tokens

### Surfaces

| Token | Hex | Usage |
|-------|-----|-------|
| `--sn-base` | `#000000` | Main application background, workspace |
| `--sn-raised` | `#121212` | Sidebars, top bars, cards, containers |
| `--sn-overlay` | `#1e1e1e` | Input fields, pop-overs, nested elements, hover states |
| `--sn-deep` | `#0c0c0c` | Elevated raised surfaces |
| `--sn-subtle` | `#1a1a1a` | Code blocks, AI response containers |

### Borders

| Token | Hex | Usage |
|-------|-----|-------|
| `--sn-border` | `#27272a` | Standard hairline borders (1px) |
| `--sn-border-subtle` | `rgba(255,255,255,0.06)` | Low-contrast dividers inside surfaces |
| `--sn-border-active` | `#00f0ff` | Focus rings, active states |

### Accent

| Token | Hex | Usage |
|-------|-----|-------|
| `--sn-accent` | `#00f0ff` | Primary interactive elements, active indicators, status |
| `--sn-accent-dim` | `rgba(0,240,255,0.15)` | Hover backgrounds, subtle glow effects |
| `--sn-accent-text` | `#000000` | Text on accent backgrounds |

### Text

| Token | Hex | Usage |
|-------|-----|-------|
| `--sn-text-primary` | `#ffffff` | High-priority text, active nav items, headings |
| `--sn-text-secondary` | `#a1a1a1` | Muted labels, disabled states, metadata |
| `--sn-text-tertiary` | `#6b6b6b` | Placeholder text, deprecated content |

### Status

| Token | Hex | Usage |
|-------|-----|-------|
| `--sn-status-active` | `#00f0ff` | Active, running, LOCAL indicator |
| `--sn-status-ready` | `#22c55e` | Healthy, connected, pass states |
| `--sn-status-warning` | `#eab308` | Degraded performance, fallback mode |
| `--sn-status-error` | `#ef4444` | Errors, disconnects, failures |

---

## Semantic Tokens

Semantic tokens map foundation tokens to component-level concepts. Implementations alias these in their platform-native theming systems.

| Semantic Token | Foundation Mapping | Usage |
|----------------|-------------------|-------|
| `--color-background` | `var(--sn-base)` | Page background |
| `--color-foreground` | `var(--sn-text-primary)` | Default text |
| `--color-card` | `var(--sn-raised)` | Card / panel background |
| `--color-card-foreground` | `var(--sn-text-primary)` | Card text |
| `--color-popover` | `var(--sn-raised)` | Dropdown / tooltip background |
| `--color-primary` | `var(--sn-accent)` | Primary buttons, active states |
| `--color-primary-foreground` | `var(--sn-accent-text)` | Text on primary buttons |
| `--color-secondary` | `var(--sn-overlay)` | Secondary buttons, tags |
| `--color-secondary-foreground` | `var(--sn-text-primary)` | Text on secondary buttons |
| `--color-muted` | `var(--sn-overlay)` | Disabled backgrounds |
| `--color-muted-foreground` | `var(--sn-text-secondary)` | Disabled / muted text |
| `--color-accent` | `var(--sn-accent)` | Links, focus rings |
| `--color-border` | `var(--sn-border)` | Standard borders |
| `--color-input` | `var(--sn-overlay)` | Input backgrounds |
| `--color-ring` | `var(--sn-accent)` | Focus rings |
| `--color-destructive` | `var(--sn-status-error)` | Destructive actions |
| `--sidebar` | `var(--sn-raised)` | Sidebar background |
| `--sidebar-foreground` | `var(--sn-text-primary)` | Sidebar text |
| `--sidebar-primary` | `var(--sn-accent)` | Active nav items |
| `--sidebar-primary-foreground` | `var(--sn-accent-text)` | Active nav text |

---

## Typography Tokens

### Font Families

| Token | Value | Usage |
|-------|-------|-------|
| `--sn-font-ui` | `Inter` | Navigation, headers, body text, descriptors |
| `--sn-font-code` | `JetBrains Mono` | Technical readouts, telemetry, labels, code, metadata |

### Type Scale

| Token | Size | Weight | Line | Tracking | Usage |
|-------|------|-------|------|----------|-------|
| `--sn-text-headline-xl` | 32px | 700 | 1.2 | -0.02em | Page-level titles |
| `--sn-text-headline-lg` | 24px | 600 | 1.3 | -0.01em | Section titles |
| `--sn-text-headline-sm` | 20px | 500 | 1.4 | 0em | Card titles |
| `--sn-text-body-lg` | 16px | 400 | 1.6 | 0em | Primary body, chat messages |
| `--sn-text-body-md` | 14px | 400 | 1.5 | 0em | Secondary body, descriptions |
| `--sn-text-label-caps` | 11px | 700 | 1 | +0.1em | Uppercase labels, section headers |
| `--sn-text-label-md` | 12px | 500 | 1.6 | +0.05em | Monospace metadata |
| `--sn-text-label-sm` | 10px | 500 | 1.4 | 0em | Timestamps, token counts |

---

## Spacing Tokens

All spacing is based on a 4px base unit.

| Token | Value | Usage |
|-------|-------|-------|
| `--sn-space-xs` | 4px | Icon padding, tight gaps |
| `--sn-space-sm` | 8px | Related item spacing, compact layouts |
| `--sn-space-md` | 16px | Card padding, standard gaps |
| `--sn-space-lg` | 24px | Section spacing, gutters |
| `--sn-space-xl` | 48px | Major section breaks |

### Layout Constraints

| Token | Value | Usage |
|-------|-------|-------|
| `--sn-container-max` | 1440px | Maximum content width for desktop |
| `--sn-gutter` | 24px | Standard horizontal margin |

---

## Border Radius Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `--sn-radius-sm` | 2px | Checkboxes, radio buttons |
| `--sn-radius-md` | 6px | Buttons, inputs |
| `--sn-radius-lg` | 8px | Cards, containers |
| `--sn-radius-xl` | 10px | Modals, large panels |

---

## Elevation Tokens

Elevation is expressed through surface and border values, not shadows.

| Level | Surface | Border | Usage |
|-------|---------|--------|-------|
| 0 | `--sn-base` | none | Main workspace |
| 1 | `--sn-raised` | `--sn-border` | Sidebars, top bars, cards |
| 2 | `--sn-overlay` | `--sn-border` | Inputs, pop-overs, dropdowns |
| 3 | `--sn-deep` | `--sn-border` | Right panel headers, elevated cards |

---

## Shadow Tokens

Shadows are **not used** in Synthetic Noir. If a shadow is ever required for a one-off exception, it must follow this pattern and be documented as an exception:

```
box-shadow: 0 4px 20px rgba(0,0,0,0.5);
```

No other shadow values are permitted.