import { MessageSquare, Library, Database, Compass, Settings, ChevronLeft, Terminal, ChevronDown, ChevronRight } from "lucide-react"
import { useState } from "react"
import { Link, useLocation } from "react-router-dom"
import { ConversationList } from "@/features/chat/ConversationList"
import { ModelPanel } from "@/features/models/ModelPanel"

const navItems = [
  { icon: MessageSquare, label: "Chat", id: "chat", path: "/chat" },
  { icon: Library, label: "Library", id: "library", path: "/library" },
  { icon: Database, label: "Knowledge", id: "knowledge", path: "/knowledge" },
  { icon: Compass, label: "Explore", id: "explore", path: "/explore" },
  { icon: Settings, label: "Settings", id: "settings", path: "/settings" },
] as const

type SidebarProps = {
  collapsed?: boolean
  onToggle?: () => void
  mode?: "navigation" | "conversations"
}

export function Sidebar({ collapsed = false, onToggle, mode = "navigation" }: SidebarProps) {
  const location = useLocation()

  return (
    <aside
      data-collapsed={collapsed}
      className="flex flex-col h-full border-r border-[#27272a] bg-[#121212] transition-all duration-300 ease-out"
    >
      <div className="flex h-[52px] items-center gap-3 px-4 data-[collapsed=true]:justify-center shrink-0">
        <div className="size-6 rounded-lg bg-[#00f0ff]/10 flex items-center justify-center">
          <Terminal className="size-3.5 text-[#00f0ff]" />
        </div>
        {!collapsed && (
          <span className="text-sm font-semibold tracking-tight">
            Ashwath AI
          </span>
        )}
      </div>

      {mode === "navigation" && (
        <>
          {!collapsed && (
            <div className="px-4 pb-1">
              <span className="text-[10px] font-semibold tracking-[0.12em] uppercase text-[#6b6b6b]">
                Navigation
              </span>
            </div>
          )}
          <nav className="flex-1 space-y-0.5 px-2 pb-2">
            {navItems.map((item) => {
              const isActive = location.pathname === item.path || (item.path === "/chat" && location.pathname === "/")
              return (
                <Link
                  key={item.id}
                  to={item.path}
                  className={`group relative flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition-all duration-150 hover:bg-[#1e1e1e] hover:text-white ${
                    collapsed ? "justify-center px-0" : ""
                  } ${
                    isActive ? "bg-[#1e1e1e] text-white" : "text-[#a1a1a1]"
                  }`}
                >
                  <item.icon className="size-4 shrink-0" />
                  {!collapsed && <span>{item.label}</span>}
                </Link>
              )
            })}
          </nav>
        </>
      )}

      {mode === "conversations" && (
        <div className="flex-1 flex flex-col min-h-0">
          <ConversationList />
          <ModelsSection />
        </div>
      )}

      <div className="flex items-center justify-center border-t border-[#27272a] p-2 shrink-0">
        <button
          type="button"
          onClick={onToggle}
          aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          className="flex items-center justify-center size-8 rounded-lg text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors duration-150"
        >
          <ChevronLeft
            className={`size-4 transition-transform duration-300 ease-out ${
              collapsed ? "rotate-180" : ""
            }`}
          />
        </button>
      </div>
    </aside>
  )
}

function ModelsSection() {
  const [open, setOpen] = useState(false)
  return (
    <div className="border-t border-[#27272a]">
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className="flex items-center gap-2 w-full px-4 py-2.5 text-[10px] font-semibold tracking-[0.12em] uppercase text-[#6b6b6b] hover:text-[#a1a1a1] transition-colors"
      >
        {open ? <ChevronDown className="size-3" /> : <ChevronRight className="size-3" />}
        Available Models
      </button>
      {open && <div className="h-[200px]"><ModelPanel /></div>}
    </div>
  )
}
