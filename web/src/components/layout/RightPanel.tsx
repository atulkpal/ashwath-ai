import { Button } from "@/components/ui/button"
import { X } from "lucide-react"

type RightPanelProps = {
  open: boolean
  onClose: () => void
  children?: React.ReactNode
}

export function RightPanel({ open, onClose, children }: RightPanelProps) {
  return (
    <aside
      data-open={open}
      className="border-l border-[var(--sn-border)] bg-[var(--sn-raised)] transition-all duration-200 overflow-hidden data-[open=true]:w-80 data-[open=false]:w-0"
    >
      <div className="flex h-14 items-center justify-between border-b border-[var(--sn-border)] px-4">
        <span className="text-[11px] font-bold uppercase tracking-[0.1em] text-[var(--sn-text-secondary)]">
          Details
        </span>
        <Button
          variant="ghost"
          size="icon"
          onClick={onClose}
          aria-label="Close right panel"
          className="text-[var(--sn-text-secondary)] hover:text-[var(--sn-text-primary)]"
        >
          <X className="size-4" />
        </Button>
      </div>
      <div className="px-4 py-4 text-sm text-[var(--sn-text-secondary)]">
        {children}
      </div>
    </aside>
  )
}
