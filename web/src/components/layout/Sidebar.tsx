import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import {
  MessageSquare,
  Library,
  Database,
  Compass,
  Settings,
  ChevronLeft,
  Terminal,
} from "lucide-react"
import { ConversationList } from "@/features/chat/ConversationList"

const navItems = [
  { icon: MessageSquare, label: "Chat", id: "chat" },
  { icon: Library, label: "Library", id: "library" },
  { icon: Database, label: "Knowledge", id: "knowledge" },
  { icon: Compass, label: "Explore", id: "explore" },
  { icon: Settings, label: "Settings", id: "settings" },
] as const

type SidebarProps = {
  collapsed: boolean
  onToggle: () => void
  mode?: "navigation" | "conversations"
}

export function Sidebar({ collapsed, onToggle, mode = "navigation" }: SidebarProps) {
  return (
    <aside
      data-collapsed={collapsed}
      className="flex flex-col border-r border-[var(--sn-border)] bg-[var(--sn-raised)] transition-all duration-200 data-[collapsed=true]:w-14 data-[collapsed=false]:w-64"
    >
      {/* Brand header */}
      <div className="flex h-14 items-center gap-3 px-4 data-[collapsed=true]:justify-center">
        <Terminal className="size-4 shrink-0 text-[var(--sn-accent)]" />
        {!collapsed && (
          <span className="text-sm font-semibold tracking-tight text-[var(--sn-text-primary)]">
            Ashwath AI
          </span>
        )}
      </div>

      <Separator className="bg-[var(--sn-border)]" />

      {mode === "navigation" && (
        <>
          {/* Navigation label (expanded only) */}
          {!collapsed && (
            <div className="px-4 pt-4 pb-2">
              <span className="text-[11px] font-bold tracking-[0.1em] uppercase text-[var(--sn-text-secondary)]">
                Navigation
              </span>
            </div>
          )}

          {/* Nav items */}
          <nav className="flex-1 space-y-0.5 px-2 pb-2">
            {navItems.map((item) => (
              <Button
                key={item.id}
                variant="ghost"
                className={`group relative w-full justify-start gap-3 rounded-md px-3 text-sm text-[var(--sn-text-secondary)] transition-all duration-150 hover:bg-[var(--sn-overlay)] hover:text-[var(--sn-text-primary)] ${
                  collapsed ? "justify-center px-0" : ""
                }`}
                asChild
              >
                <a href="#">
                  <span className="absolute left-0 top-1/2 h-4 w-0.5 -translate-y-1/2 rounded-full bg-[var(--sn-accent)] opacity-0 transition-opacity duration-150 group-hover:opacity-100" />
                  <item.icon className="size-4 shrink-0" />
                  {!collapsed && <span>{item.label}</span>}
                </a>
              </Button>
            ))}
          </nav>
        </>
      )}

      {mode === "conversations" && (
        <div className="flex-1 min-h-0">
          <ConversationList />
        </div>
      )}

      {/* Collapse toggle at bottom */}
      <div className="flex items-center justify-center border-t border-[var(--sn-border)] p-2">
        <Button
          variant="ghost"
          size="icon"
          onClick={onToggle}
          aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          className="shrink-0 text-[var(--sn-text-secondary)] hover:text-[var(--sn-text-primary)]"
        >
          <ChevronLeft
            className={`size-4 transition-transform duration-200 ${
              collapsed ? "rotate-180" : ""
            }`}
          />
        </Button>
      </div>
    </aside>
  )
}
