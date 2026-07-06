import type { ReactNode } from "react"
import { ScrollArea } from "@/components/ui/scroll-area"

type MainContentAreaProps = {
  children?: ReactNode
}

export function MainContentArea({ children }: MainContentAreaProps) {
  return (
    <main className="flex-1 overflow-hidden bg-[var(--sn-base)]">
      <ScrollArea className="h-full sn-scrollbar">
        <div className="p-6">
          {children || (
            <div className="flex min-h-[400px] items-center justify-center">
              <span className="text-[11px] font-bold uppercase tracking-[0.1em] text-[var(--sn-text-secondary)]">
                Select a feature from the sidebar
              </span>
            </div>
          )}
        </div>
      </ScrollArea>
    </main>
  )
}