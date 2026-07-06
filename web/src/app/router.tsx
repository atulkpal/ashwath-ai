import { BrowserRouter, Routes, Route } from "react-router-dom"
import { MainLayout } from "@/layouts/MainLayout"
import { ChatPage } from "@/pages/ChatPage"
import { LibraryPage } from "@/pages/LibraryPage"
import { KnowledgePage } from "@/pages/KnowledgePage"
import { ExplorePage } from "@/pages/ExplorePage"
import { SettingsPage } from "@/pages/SettingsPage"

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<MainLayout />}>
          <Route index element={<ChatPage />} />
          <Route path="chat" element={<ChatPage />} />
          <Route path="library" element={<LibraryPage />} />
          <Route path="knowledge" element={<KnowledgePage />} />
          <Route path="explore" element={<ExplorePage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
