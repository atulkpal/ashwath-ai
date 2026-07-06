# Web Client Progress

## 2026-07-06 — EPIC-6: Routing, pages, conversation persistence

- Added `react-router-dom` with 5 routes: Chat, Library, Knowledge, Explore, Settings
- Created page components for all sidebar navigation items
- Refactored `MainLayout` to use `<Outlet />` for route-based content
- Updated `Sidebar` to use `<Link>` instead of `<a href="#">` with active state highlighting
- Added localStorage persistence for conversations and messages (survive refresh)
- Build: `tsc -b && vite build` passes (449 KB JS, 46 KB CSS, PWA active)
