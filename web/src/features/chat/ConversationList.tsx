import { conversations } from "./chat-data"
import { ConversationItem } from "./ConversationItem"
import { MessageSquare } from "lucide-react"

export function ConversationList() {
  return (
    <div className="flex flex-col h-full bg-[var(--sn-raised)]">
      {/* Header */}
      <div className="flex h-14 items-center gap-2 px-4 border-b border-[var(--sn-border)]">
        <MessageSquare className="size-4 text-[var(--sn-accent)]" />
        <span className="text-sm font-semibold text-[var(--sn-text-primary)]">Chats</span>
      </div>

      {/* List */}
      <div className="flex-1 overflow-y-auto sn-scrollbar p-2 space-y-0.5">
        {conversations.map((conv) => (
          <ConversationItem key={conv.id} conversation={conv} active={conv.active} />
        ))}
      </div>
    </div>
  )
}