import { MessageSquare, Library, Database, Compass, Settings, ChevronLeft, Terminal } from "lucide-react"
import { ConversationList } from "@/features/chat/ConversationList"

const navItems = [
  { icon: MessageSquare, label: "Chat", id: "chat" },
  { icon: Library, label: "Library", id: "library" },
  { icon: Database, label: "Knowledge", id: "knowledge" },
  { icon: Compass, label: "Explore", id: "explore" },
  { icon: Settings, label: "Settings", id: "settings" },
] as const

type SidebarProps = {
  collapsed?: boolean
  onToggle?: () => void
  mode?: "navigation" | "conversations"
}

export function Sidebar({ collapsed = false, onToggle, mode = "navigation" }: SidebarProps) {
  return (
    <aside
      data-collapsed={collapsed}
      className="flex flex-col h-full border-r border-[#27272a] bg-[#121212] transition-all duration-300 ease-out"
    >
      {/* Brand */}
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
            {navItems.map((item) => (
              <a
                key={item.id}
                href="#"
                className={`group relative flex items-center gap-3 rounded-lg px-3 py-2 text-sm text-[#a1a1a1] transition-all duration-150 hover:bg-[#1e1e1e] hover:text-white ${
                  collapsed ? "justify-center px-0" : ""
                }`}
              >
                <item.icon className="size-4 shrink-0" />
                {!collapsed && <span>{item.label}</span>}
              </a>
            ))}
          </nav>
        </>
      )}

      {mode === "conversations" && (
        <div className="flex-1 min-h-0">
          <ConversationList />
        </div>
      )}

      {/* Collapse toggle */}
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
