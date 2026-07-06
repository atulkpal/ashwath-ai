import { useState, useEffect } from "react"
import { Outlet } from "react-router-dom"
import { Sidebar } from "@/components/layout/Sidebar"
import { TopBar } from "@/components/layout/TopBar"
import { StatusBar } from "@/components/layout/StatusBar"
import { X } from "lucide-react"

function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState(false)
  useEffect(() => {
    const mq = window.matchMedia(query)
    setMatches(mq.matches)
    const handler = (e: MediaQueryListEvent) => setMatches(e.matches)
    mq.addEventListener("change", handler)
    return () => mq.removeEventListener("change", handler)
  }, [query])
  return matches
}

export function MainLayout() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false)
  const isMobile = useMediaQuery("(max-width: 768px)")

  useEffect(() => {
    if (isMobile) {
      setSidebarCollapsed(true)
    }
  }, [isMobile])

  const sidebarWidth = sidebarCollapsed ? "60px" : "280px"

  return (
    <div className="flex h-screen flex-col bg-black text-white overflow-hidden">
      <div className="h-[52px] shrink-0">
        <TopBar
          rightPanelOpen={false}
          onToggleRightPanel={() => {}}
          runtimeStatus="disconnected"
          modelName="ashwath-7b-v1"
          contextUsed={8}
          contextMax={32}
          tokenSec={0}
          onMenuClick={isMobile ? () => setMobileSidebarOpen(true) : undefined}
        />
      </div>

      <div className="flex flex-1 overflow-hidden relative">
        {isMobile && mobileSidebarOpen && (
          <div className="fixed inset-0 z-50 flex">
            <div
              className="absolute inset-0 bg-black/60 backdrop-blur-sm"
              onClick={() => setMobileSidebarOpen(false)}
            />
            <div className="relative w-[280px] h-full">
              <button
                onClick={() => setMobileSidebarOpen(false)}
                className="absolute top-3 right-3 z-10 p-1 text-[#a1a1a1] hover:text-white"
              >
                <X className="size-4" />
              </button>
              <Sidebar
                collapsed={false}
                onToggle={() => setMobileSidebarOpen(false)}
                mode="navigation"
              />
            </div>
          </div>
        )}

        <div
          className="hidden md:block shrink-0 transition-all duration-300 ease-out overflow-hidden"
          style={{ width: sidebarWidth }}
        >
          <Sidebar
            collapsed={sidebarCollapsed}
            onToggle={() => setSidebarCollapsed((prev) => !prev)}
            mode="navigation"
          />
        </div>

        <Outlet />
      </div>

      <StatusBar />
    </div>
  )
}
