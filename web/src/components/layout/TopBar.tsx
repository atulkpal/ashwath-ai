import { Button } from "@/components/ui/button"
import { PanelRightOpen, PanelRightClose, Terminal } from "lucide-react"

type TopBarProps = {
  rightPanelOpen: boolean
  onToggleRightPanel: () => void
}

export function TopBar({ rightPanelOpen, onToggleRightPanel }: TopBarProps) {
  return (
    <header className="flex h-14 items-center justify-between border-b border-[var(--sn-border)] bg-[var(--sn-raised)] px-6">
      <div className="flex items-center gap-3">
        <Terminal className="size-4 text-[var(--sn-accent)]" />
        <span className="text-sm font-semibold tracking-tight text-[var(--sn-text-primary)]">
          Ashwath AI
        </span>
      </div>

      <div className="flex items-center gap-3">
        {/* LOCAL status pill */}
        <span className="flex items-center gap-1.5 rounded-md border border-[var(--sn-border)] bg-[var(--sn-overlay)] px-2.5 py-1 text-[11px] font-bold uppercase tracking-[0.1em] text-[var(--sn-accent)]">
          <span className="inline-block size-1.5 rounded-full bg-[var(--sn-accent)]" />
          Local
        </span>

        <Button
          variant="ghost"
          size="icon"
          onClick={onToggleRightPanel}
          aria-label={rightPanelOpen ? "Close right panel" : "Open right panel"}
          className="text-[var(--sn-text-secondary)] hover:text-[var(--sn-text-primary)]"
        >
          {rightPanelOpen ? (
            <PanelRightClose className="size-4" />
          ) : (
            <PanelRightOpen className="size-4" />
          )}
        </Button>
      </div>
    </header>
  )
}