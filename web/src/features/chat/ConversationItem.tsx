import type { Conversation } from "./chat-data"
import { MessageSquare } from "lucide-react"

type ConversationItemProps = {
  conversation: Conversation
  active?: boolean
  onClick?: () => void
}

export function ConversationItem({ conversation, active, onClick }: ConversationItemProps) {
  return (
    <button
      onClick={onClick}
      className={`w-full flex items-center gap-2 px-3 py-2 rounded-md text-left transition-colors duration-150 ${
        active
          ? "bg-[var(--sn-overlay)] text-[var(--sn-text-primary)]"
          : "text-[var(--sn-text-secondary)] hover:bg-[var(--sn-overlay)] hover:text-[var(--sn-text-primary)]"
      }`}
    >
      <MessageSquare className="size-4 shrink-0" />
      <div className="flex-1 min-w-0">
        <div className="text-sm truncate">{conversation.title}</div>
        <div className="text-[11px] text-[var(--sn-text-secondary)] truncate">{conversation.preview}</div>
      </div>
      <span className="text-[10px] text-[var(--sn-text-secondary)] tabular-nums">{conversation.timestamp}</span>
    </button>
  )
}