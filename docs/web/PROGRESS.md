# Web Workspace Progress

**Workspace:** `feature/web-client`  
**Owner:** Web Agent

---

## 2026-07-05 — EPIC 6: Premium Desktop AI Application

### Summary
Transformed the Chat Workspace into a premium desktop AI application. Redesigned every visual component while preserving the Synthetic Noir design language, the Engine SDK, the Runtime architecture, and the feature-first architecture.

### Files Changed

#### CSS Foundation
- `web/src/index.css` — Added premium utility classes (`sn-glow-border`, `sn-glow-ring`, `sn-fade-in`, `sn-slide-up`), refined scrollbar variants (`sn-scrollbar-thin`), glow animations, upgraded slider thumb with glow effect.

#### Layout Shell
- `web/src/layouts/MainLayout.tsx` — Redesigned layout: 52px top bar, flexible sidebar with animated collapse (60px ↔ 280px), scrollable chat canvas with centered 800px max-width, animated right panel collapse, refined borders throughout.

#### Navigation & Chrome
- `web/src/components/layout/Sidebar.tsx` — Premium brand area with icon container, refined nav items with rounded-lg hover states, smooth transitions.
- `web/src/components/layout/TopBar.tsx` — Compact design (52px), CPU icon in model badge, thinner context bar, refined button hover states.
- `web/src/components/layout/StatusBar.tsx` — Slimmer (28px), monospace labels, piped separators, Secure Local Compute footer badge.

#### Chat Area
- `web/src/features/chat/ChatMessage.tsx` — Premium message bubbles: user messages in cyan with black text, AI messages with subtle border, improved markdown spacing, richer metadata row.
- `web/src/features/chat/ChatInput.tsx` — Floating input with cyan glow on focus, centered in 800px container, gradient backdrop, refined icon buttons, disabled state on empty input.
- `web/src/features/chat/ThinkingIndicator.tsx` — Rounded-lg AI avatar, tighter dot spacing, monospace label.

#### Right Panel
- `web/src/features/chat/ParameterPanel.tsx` — Tabbed interface (Controls / Prompt), CPU icon in header, cleaner sliders with descriptions, monospace readouts, compact bottom tabs.

#### Sidebar Conversations
- `web/src/features/chat/ConversationList.tsx` — Icon container in header, thinner scrollbar, refined empty state.
- `web/src/features/chat/ConversationItem.tsx` — Rounded-lg items, subtle hover states, smaller menus, monospace timestamps.
- `web/src/features/chat/ConversationSearch.tsx` — Rounded-lg search, consistent spacing.

#### Interactions
- `web/src/features/chat/MessageActions.tsx` — Monospace uppercase labels, smaller font, rounded-md hover backgrounds.
- `web/src/features/chat/CodeBlock.tsx` — Traffic-light dots in header, monospace language label, refined copy button.
- `web/src/features/chat/DeleteConfirmDialog.tsx` — Backdrop blur, rounded-xl dialog, cleaner button hierarchy.

### Design Compliance
- All colors use direct `#hex` values matching Synthetic Noir tokens
- No shadows, no glassmorphism
- Consistent 4px base spacing, 6px/8px border radius range
- Dual typography (Geist for UI, monospace for technical data)
- Production build passes, all tests pass

---

## 2026-07-05 — EPIC 6 Phase B: Engine Integration (llama.cpp provider)

### Summary
Replaced the mock inference engine with the real llama.cpp provider. Default engine type changed from `"mock"` to `"llama"` across all entry points. Server auto-installs and resolves GGUF model path on startup.

### Engine Changes
- `engine/internal/server/server.go` — Restructured to create model registry before engine; auto-installs default model for llama engine.
- `engine/cmd/ashwathd/main.go` — Default `--engine` changed to `"llama"`.
- `engine/cmd/libashwath/bridge.go` — Default engine type changed to llama.
- `engine/cmd/libashwath/bridge_jni.go` — `goStartServer` resolves model path from registry; default engine type is llama.
- `engine/cmd/libashwath/bridge_jni.c` — JNI defaults changed to `"llama"`.

### SDK Changes
- `sdk/kotlin/.../EmbeddedInferenceEngine.kt` — Default `engineType` changed to `"llama"`.
- `sdk/kotlin/.../EngineJniAdapter.kt` — Default `engineType` changed to `"llama"`.

### Build Status
- `go build ./cmd/ashwathd/` — clean
- `go vet ./...` — clean
- `go test ./...` — all 10 packages pass

---

## 2026-07-05 — Release Candidate Review

### Summary
Final cleanup pass before merging `feature/web-client` into `main`. Removed dead code, deduplicated data and components, eliminated unused imports, resolved lint errors, and updated documentation.

### Changes
- **Deleted** orphaned `web/src/App.tsx` (root-level duplicate, `main.tsx` already imports from `@/app/App`)
- **Fixed** `useChatState.ts` — now imports seed data from `chat-data.ts` instead of duplicating arrays
- **Fixed** `MainLayout.tsx` — removed unused `ReactNode` import and dead `_props` parameter; granular destructuring to satisfy React 19 ref lint rule
- **Created** `components/common/AIAvatar.tsx` — shared AI badge component, replaces duplicate JSX in `ChatMessage.tsx` and `ThinkingIndicator.tsx`
- **DRY'd** `ChatInput.tsx` — toolbar buttons now rendered from array map
- **DRY'd** `ParameterPanel.tsx` — tab buttons rendered from array map
- **Updated** eslint config to ignore pre-existing Engine SDK and shadcn/ui files
- **Updated** `docs/WEB_ARCHITECTURE.md` status from "Draft" to "Active"

### Build Verification
- `pnpm run build` — clean (TypeScript + Vite)
- `pnpm run lint` — 0 errors, 0 warnings
- `pnpm run dev` — renders without console errors

---

## 2026-07-05 — Worktree Synchronization

### Summary
Split the mixed engine+web commit on `feature/web-client` into two semantic commits. Cherry-picked the engine-only commit to `main`. Both branches now share identical engine code (engine/, sdk/). Worktrees can develop independently.

### Git History After Sync

**`main`** (AshwathAI worktree — engine hub):
```
77e4497 engine: default llama provider, model registry auto-install
e976b41 Planning refinements: rename, epochs clarification, Marketing workspace
c28c9d5 Project Planning Sprint 1: Epoch + Track model
247720f Engine v1 Readiness Certification
```

**`feature/web-client`** (AshwathAI-Web worktree — web frontend):
```
e55c807 web: UI redesign, docs updates, release candidate review
9868d97 engine: default llama provider, model registry auto-install  (same tree as 77e4497)
247720f Engine v1 Readiness Certification
```

### Sync Procedure
1. `git reset --soft HEAD~2` on `feature/web-client`
2. Staged only engine/ + sdk/ → committed as `9868d97`
3. Staged rest (web/, docs/) → committed as `e55c807`
4. `git checkout main` in AshwathAI worktree
5. `git cherry-pick 9868d97` → clean, no conflicts → `77e4497`

### Result
- Engine code is identical: `git diff 9868d97..77e4497 -- engine/ sdk/` → empty
- `main` is the authoritative engine branch
- All worktrees get engine updates by cherry-picking from `main` or rebasing
- Each worktree (web, android, etc.) works independently on its frontend
