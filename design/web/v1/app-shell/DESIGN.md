---
name: Synthetic Noir
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1b1b1b'
  surface-container: '#1f1f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353535'
  on-surface: '#e2e2e2'
  on-surface-variant: '#b9cacb'
  inverse-surface: '#e2e2e2'
  inverse-on-surface: '#303030'
  outline: '#849495'
  outline-variant: '#3b494b'
  surface-tint: '#00dbe9'
  primary: '#dbfcff'
  on-primary: '#00363a'
  primary-container: '#00f0ff'
  on-primary-container: '#006970'
  inverse-primary: '#006970'
  secondary: '#c8c6c5'
  on-secondary: '#313030'
  secondary-container: '#474746'
  on-secondary-container: '#b7b5b4'
  tertiary: '#fff5de'
  on-tertiary: '#3b2f00'
  tertiary-container: '#fed639'
  on-tertiary-container: '#715d00'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#7df4ff'
  primary-fixed-dim: '#00dbe9'
  on-primary-fixed: '#002022'
  on-primary-fixed-variant: '#004f54'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c8c6c5'
  on-secondary-fixed: '#1c1b1b'
  on-secondary-fixed-variant: '#474746'
  tertiary-fixed: '#ffe179'
  tertiary-fixed-dim: '#eac324'
  on-tertiary-fixed: '#231b00'
  on-tertiary-fixed-variant: '#554500'
  background: '#131313'
  on-background: '#e2e2e2'
  surface-variant: '#353535'
  surface-primary: '#000000'
  surface-secondary: '#0c0c0c'
  surface-tertiary: '#1a1a1a'
  accent-primary: '#00f0ff'
  text-primary: '#ffffff'
  text-secondary: '#a1a1a1'
  border-base: '#27272a'
  status-success: '#10b981'
  status-error: '#ff4b4b'
typography:
  headline-xl:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
    letterSpacing: -0.01em
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
    letterSpacing: 0em
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
    letterSpacing: 0em
  code-md:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '450'
    lineHeight: '1.7'
    letterSpacing: 0em
  code-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '400'
    lineHeight: '1.6'
    letterSpacing: 0em
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '700'
    lineHeight: '1'
    letterSpacing: 0.1em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 48px
  container-max: 1440px
  gutter: 24px
---

## Brand & Style

The design system embodies a "Synthetic Noir" aesthetic—a high-performance, developer-centric environment that prioritizes speed, precision, and technical authority. It is designed for users who require a focused, low-distraction workspace for interacting with advanced AI systems.

The style is **Professional Minimalism**. It rejects decorative flourishes like glassmorphism or soft gradients in favor of structural clarity, deep blacks, and surgical accents. The UI should feel like a high-end terminal or a precision engineering tool: fast, secure, and uncompromisingly functional.

## Colors

The palette is anchored in absolute blacks to maximize contrast and reduce eye strain in technical environments. 

- **The Void:** Use `#000000` (Surface Primary) for the main application background to create a sense of infinite depth.
- **Tiers of Depth:** Layering is achieved through strict hex values (`#0c0c0c` and `#1a1a1a`) rather than opacity, ensuring performance and color accuracy.
- **The Glow:** Cyan (`#00f0ff`) is used sparingly and exclusively for primary actions, active states, and technical status indicators. It should appear to "pierce" the dark background.
- **Text:** White is reserved for high-priority content. Secondary information uses a muted gray (`#a1a1a1`) to maintain a clear hierarchy.

## Typography

This design system utilizes a dual-font strategy to distinguish between UI orchestration and technical data.

- **UI Language:** Inter provides a clean, neutral foundation for navigation, headers, and descriptive text. It should be typeset with tight letter-spacing in headlines for a premium, "compact" feel.
- **Data Language:** JetBrains Mono is the dedicated voice for code blocks, telemetry, AI-generated logs, and metadata labels. The monospaced nature emphasizes the "synthetic" and mechanical aspect of the product.
- **Hierarchy:** Use `label-caps` for section headers or small metadata to evoke a sense of architectural labeling.

## Layout & Spacing

The layout is built on a rigorous **8px grid** (with a 4px sub-grid for micro-adjustments). 

- **Grid System:** Use a 12-column fixed-width grid for desktop (max 1440px) and a fluid single-column layout for mobile.
- **Margins:** Standard application margins are set to 24px.
- **Consistency:** Spacing between related items should be 8px; spacing between distinct sections should be 24px or 48px.
- **Alignment:** All elements must align to the grid. Avoid center alignment for technical content; prefer left-aligned layouts to maintain a "scannable" terminal-like flow.

## Elevation & Depth

In a pure black environment, elevation is communicated through **borders and tonal shifts**, never through soft shadows.

- **Outlines:** Use the `border-base` (`#27272a`) color for all card containers, input fields, and separators. Borders should be 1px wide.
- **Active States:** Active containers may use a subtle 1px border of `accent-primary` or a slightly lighter surface (`surface-tertiary`).
- **Nesting:** Child elements (like code blocks inside a chat bubble) should sit on `surface-secondary` to distinguish them from the `surface-primary` background.

## Shapes

The geometric language is **Architectural**. 

- **Radius:** A consistent 4px to 8px radius is applied to all interactive elements. This small radius maintains a "sharp" look while preventing the UI from feeling dangerously jagged.
- **Buttons & Inputs:** Use 6px for standard buttons and input fields.
- **Cards:** Use 8px for larger layout containers.
- **Strictness:** Never use pill-shaped (fully rounded) buttons; they conflict with the technical, "Synthetic Noir" aesthetic.

## Components

### Buttons
- **Primary:** Background `accent-primary`, Text `#000000`. No shadow.
- **Secondary:** Transparent background, 1px border `border-base`, Text `text-primary`.
- **Ghost:** Transparent background, Text `text-secondary`, turns `text-primary` on hover.

### Input Fields
- Background `surface-secondary`, 1px border `border-base`, font `code-md`.
- Focus state: Border changes to `accent-primary` with a 0px blur 2px spread "ring" of the same color.

### Cards & Containers
- Containers should use `surface-secondary` with a 1px `border-base`.
- Header areas within cards should have a subtle bottom-border to separate title from content.

### Technical Telemetry (AI Workspace)
- Code blocks use `surface-tertiary` with no border and JetBrains Mono.
- Status indicators (e.g., "AI Processing") use a small 8px square of `accent-primary` with a simple "pulse" opacity animation.

### Checkboxes & Radios
- Square-shaped (4px radius). 
- Selected state: `accent-primary` background with a black checkmark.