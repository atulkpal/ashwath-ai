# Ashwath AI Design System

This directory contains the official design system for the Ashwath AI platform.

## Structure

```
design/
├── README.md                  ← This file — directory overview and navigation
├── shared/                    ← Platform-wide design standards
│   ├── BRAND.md               Brand identity, voice, and personality
│   ├── DESIGN_LANGUAGE.md     Core visual language and philosophy
│   ├── UX_PRINCIPLES.md       User experience principles
│   ├── DESIGN_TOKENS.md       Color, typography, spacing, and elevation tokens
│   ├── COMPONENTS.md          Shared component specifications
│   ├── MOTION.md              Animation and transition guidelines
│   ├── ICONOGRAPHY.md         Icon usage and style
│   └── ACCESSIBILITY.md       Accessibility standards
├── web/                       Web-client-specific design assets
│   └── v1/
│       ├── app-shell/          Application shell layout design
│       ├── chat/               Chat interface design
│       ├── discover/           Model discovery design
│       ├── explore/            Explore section design
│       ├── knowledge/          Knowledge management design
│       ├── library/            Library section design
│       ├── models/             Model management design
│       ├── settings/           Settings design
│       └── theme/              Web-specific theme tokens
└── android/                   Android-client-specific design assets
    └── v1/
```

## Design System Philosophy

The Ashwath AI design system is named **Synthetic Noir** — a high-performance, developer-centric aesthetic that prioritizes speed, precision, and technical authority. It is designed for users who require a focused, low-distraction workspace for interacting with advanced AI systems.

## How to Use These Documents

1. Start with [`BRAND.md`](shared/BRAND.md) to understand product vision and brand identity.
2. Read [`DESIGN_LANGUAGE.md`](shared/DESIGN_LANGUAGE.md) to understand the core visual philosophy.
3. Read [`UX_PRINCIPLES.md`](shared/UX_PRINCIPLES.md) for interaction and experience guidelines.
4. Reference [`DESIGN_TOKENS.md`](shared/DESIGN_TOKENS.md) for exact colour, typography, and spacing values.
5. Use [`COMPONENTS.md`](shared/COMPONENTS.md) when building or modifying shared UI components.
6. Consult [`MOTION.md`](shared/MOTION.md) for animation and transition patterns.
7. Follow [`ICONOGRAPHY.md`](shared/ICONOGRAPHY.md) for icon style and usage rules.
8. Adhere to [`ACCESSIBILITY.md`](shared/ACCESSIBILITY.md) for inclusive design standards.

## Document Relationships

```
BRAND.md (Who we are)
  ↓
DESIGN_LANGUAGE.md (How we look and feel)
  ↓
DESIGN_TOKENS.md (Exact values)
  ↓
COMPONENTS.md (How we build)
  ↓
UX_PRINCIPLES.md + MOTION.md + ICONOGRAPHY.md + ACCESSIBILITY.md (How it behaves)
```

## Platform-Specific Assets

Each client directory (`web/`, `android/`) contains platform-specific design exports, screen mockups, and reference implementations. These are visual references — the source of truth for implementation is the shared design system documents.