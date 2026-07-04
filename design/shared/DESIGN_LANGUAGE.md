# Ashwath AI Design Language

**Status:** Draft

---

## Name

**Synthetic Noir**

This is the official name of the Ashwath AI design language. It should be referenced as "Synthetic Noir" in all design documentation.

---

## Visual Narrative

Synthetic Noir is a high-performance, developer-centric aesthetic that prioritizes speed, precision, and technical authority. It is designed for users who require a focused, low-distraction workspace for interacting with advanced AI systems.

The personality is **cold, precise, and authoritative**. By utilizing a pure black foundation, the interface recedes to let the content — large language models and technical data — take center stage. The emotional response should be one of absolute privacy, security, and low-level control.

---

## Core Visual Principles

### 1. The Void

Pure black (`#000000`) is mandatory for the main application background. It creates a sense of infinite depth, maximizes perceived contrast, and provides OLED efficiency on supported displays. The background should never use grey, gradient, or pattern fills.

### 2. Tonal Depth

Depth is conveyed exclusively through **surface colour tiers** and **low-contrast borders**, never through shadows, drop-shadows, or box-shadows. The three surface tiers are:

| Level | Hex | Usage |
|-------|-----|-------|
| Base | `#000000` | Main background, workspace |
| Raised | `#121212` | Sidebars, headers, cards, containers |
| Overlay | `#1e1e1e` | Input fields, pop-overs, nested elements |

### 3. The Glow

Cyan (`#00f0ff`) is reserved strictly for:
- Interactive triggers (buttons, links)
- Active/focus states (borders, rings)
- AI processing indicators (pulsing dots, progress bars)
- Status indicators (LOCAL badge, active sessions)

Cyan must **never** be used for:
- Large background areas
- Decorative elements
- Passive content

### 4. Soft-Sharp Geometry

All interactive elements use a consistent 4px–8px radius range. This "soft-sharp" language prevents the UI from feeling aggressive while maintaining the precision of a professional tool. Pill-shaped elements (fully rounded) are strictly forbidden.

- Buttons and inputs: 6px
- Cards and containers: 8px
- Checkboxes: 4px

### 5. Dual Typography

| Role | Font | Weight | Usage |
|------|------|--------|-------|
| UI Communication | Inter | 400–700 | Navigation, headers, body text |
| Technical Data | JetBrains Mono | 400–500 | Code, telemetry, labels, metadata |

This dual strategy creates a clear separation between "orchestration" (what the user reads and navigates) and "data" (what the system reports).

---

## Anti-Goals

Synthetic Noir is intentionally **not** the following:

- **A marketing website.** No hero sections, no gradient headlines, no testimonial carousels. The interface is a tool, not a brochure.
- **A generic admin dashboard.** No card grids with stock photos, no pastel colour schemes, no playful microcopy.
- **Glassmorphism.** No translucent panels, no background blur effects, no layered transparency. Depth comes from tonal shifts, not material physics.
- **Neumorphism.** No soft shadows, no extruded surfaces, no "press" animations that make elements look physical.
- **Skeuomorphism.** No leather textures, no paper metaphors, no representations of physical objects.
- **Social-media-adjacent UI.** No infinite scroll feeds, no reaction buttons, no algorithmic content presentation.

These styles are deliberately excluded to preserve the cold, authoritative, and professional character of the platform.

---

## Emotional Design Goals

| Goal | How It Is Achieved |
|------|-------------------|
| **Security** | Dark background, high contrast, no decorative elements |
| **Control** | Clear surface hierarchy, precise spacing, predictable interactions |
| **Focus** | Minimal visual noise, generous whitespace, restrained accent colour |
| **Performance** | Flat design (no shadows), fast transitions, surgical animations |
| **Authority** | Uppercase labels, monospace telemetry, direct language |

---

## Platform Application

This design language applies uniformly to all Ashwath AI clients. Each client translates Synthetic Noir tokens into its native platform idioms while preserving the visual principles defined here.