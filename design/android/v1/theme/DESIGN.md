---
name: Synthetic Noir
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#201f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353534'
  on-surface: '#e5e2e1'
  on-surface-variant: '#b9cacb'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#849495'
  outline-variant: '#3b494b'
  surface-tint: '#00dbe9'
  primary: '#dbfcff'
  on-primary: '#00363a'
  primary-container: '#00f0ff'
  on-primary-container: '#006970'
  inverse-primary: '#006970'
  secondary: '#c6c6c7'
  on-secondary: '#2f3131'
  secondary-container: '#454747'
  on-secondary-container: '#b4b5b5'
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
  secondary-fixed: '#e2e2e2'
  secondary-fixed-dim: '#c6c6c7'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#454747'
  tertiary-fixed: '#ffe179'
  tertiary-fixed-dim: '#eac324'
  on-tertiary-fixed: '#231b00'
  on-tertiary-fixed-variant: '#554500'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353534'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 4px
  gutter: 16px
  margin-mobile: 20px
  margin-desktop: 32px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style

This design system embodies a high-performance, developer-centric aesthetic tailored for local AI execution on Android. The visual narrative is rooted in **Minimalism** with a **Futuristic** edge, prioritizing raw power and data clarity over decorative fluff. 

The personality is cold, precise, and authoritative. By utilizing a "Pure Black" foundation, the interface recedes to let the content—large language models and technical data—take center stage. The emotional response should be one of absolute privacy, security, and low-level control. High-contrast elements and generous whitespace ensure that even complex configuration screens remain legible and approachable for power users.

## Colors

The palette is strictly functional. The **Pure Black (#000000)** background is mandatory for OLED efficiency and to maximize perceived contrast. Surfaces use a tiered grayscale to establish hierarchy without relying on shadows.

- **Primary (Cyan):** Reserved strictly for interactive triggers, progress indicators, and active AI states. It must never be used for large backgrounds or decorative elements.
- **Surface Tier 1 (#121212):** Used for cards, navigation bars, and primary containers.
- **Surface Tier 2 (#1E1E1E):** Used for input fields and nested UI elements.
- **Borders (#2C2C2C):** Subtle hairline strokes that define structure in a dark environment.

## Typography

The typography system uses **Inter** for all primary communication due to its exceptional legibility at small sizes and its neutral, systematic feel. To reinforce the "local execution" and "technical" nature of the app, **JetBrains Mono** is used for all metadata, system logs, token counts, and hardware telemetry.

- **Headlines:** Use tight letter-spacing and semi-bold weights to create a sense of density and impact.
- **Monospace Accents:** All labels and technical readouts should be in JetBrains Mono, set in uppercase for labels to differentiate from readable content.

## Layout & Spacing

This design system utilizes a **Fixed Grid** model with a strict 4px baseline rhythm. 

- **Mobile:** A 4-column grid with 20px outside margins and 16px gutters.
- **Desktop/Tablet:** A 12-column centered grid with a maximum content width of 1024px to ensure AI chat logs remain readable and do not stretch excessively.
- **Spacing Logic:** Components should be separated by increments of 8px (2 units). Internal padding within cards and surfaces should default to 16px to maintain a breathable, premium feel despite the dark aesthetic.

## Elevation & Depth

Depth is conveyed through **Tonal Layers** and **Low-Contrast Outlines** rather than traditional shadows. 

1. **Level 0 (Base):** #000000.
2. **Level 1 (Cards/Nav):** #121212 with a 1px border of #2C2C2C.
3. **Level 2 (Inputs/Pop-overs):** #1E1E1E with a 1px border of #2C2C2C.

This approach creates a flat, architectural feel. Interacting with an element (e.g., a pressed button) should not move it "higher" in Z-space via shadows, but rather change its border color or background luminosity.

## Shapes

The shape language is "Soft-Sharp." All primary containers, buttons, and input fields use a **4px (rounded-md)** or **8px (rounded-lg)** radius. This subtle rounding prevents the UI from feeling aggressive while maintaining the precision of a professional tool. Avoid pill-shaped elements entirely to stay consistent with the futuristic-technical theme.

## Components

- **Buttons:** Primary buttons use a solid Cyan (#00F0FF) background with black text. Secondary buttons are ghost-style with a #2C2C2C border and white text.
- **Input Fields:** Rectangular boxes with #1E1E1E fill and a subtle #2C2C2C border. The border shifts to Cyan on focus.
- **Chips:** Small, square-cornered tags with #1E1E1E background and JetBrains Mono text for displaying model tags (e.g., "Llama-3", "FP16").
- **Cards:** No shadows. Defined by 1px #2C2C2C borders against the #121212 surface. 
- **AI Response Bubbles:** Distinct from user messages; use a slightly lighter surface (#1E1E1E) or a vertical Cyan accent line on the left border to indicate "System" or "AI" origin.
- **Hardware Telemetry:** A persistent or collapsible strip using Monospace font to show RAM/NPU usage, utilizing Cyan for progress bars.