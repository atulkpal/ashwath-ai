import { useState, type ReactNode } from "react"
import { Sidebar } from "@/components/layout/Sidebar"
import { TopBar } from "@/components/layout/TopBar"
import { MainContentArea } from "@/components/layout/MainContentArea"
import { RightPanel } from "@/components/layout/RightPanel"
import { StatusBar } from "@/components/layout/StatusBar"

type MainLayoutProps = {
  children?: ReactNode
}

export function MainLayout({ children }: MainLayoutProps) {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [rightPanelOpen, setRightPanelOpen] = useState(false)

  return (
    <div className="flex h-screen flex-col bg-[var(--sn-base)] text-[var(--sn-text-primary)]">
      {/* Top Bar */}
      <TopBar
        rightPanelOpen={rightPanelOpen}
        onToggleRightPanel={() => setRightPanelOpen((prev) => !prev)}
      />

      {/* Body: Sidebar + Content + Right Panel */}
      <div className="flex flex-1 overflow-hidden">
        <Sidebar
          collapsed={sidebarCollapsed}
          onToggle={() => setSidebarCollapsed((prev) => !prev)}
        />
        <MainContentArea>{children}</MainContentArea>
        <RightPanel
          open={rightPanelOpen}
          onClose={() => setRightPanelOpen(false)}
        />
      </div>

      {/* Status Bar */}
      <StatusBar />
    </div>
  )
}