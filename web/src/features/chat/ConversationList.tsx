import { useState } from "react"
import { conversations as staticConversations } from "./chat-data"
import { ConversationItem } from "./ConversationItem"
import { ConversationSearch } from "./ConversationSearch"
import { DeleteConfirmDialog } from "./DeleteConfirmDialog"
import { MessageSquare, Plus } from "lucide-react"

type ConversationListProps = {
  conversations?: typeof staticConversations
  activeId?: string
  onSelect?: (id: string) => void
  onPin?: (id: string) => void
  onDelete?: (id: string) => void
  onRename?: (id: string, title: string) => void
  onNewChat?: () => void
  searchQuery?: string
  onSearchChange?: (query: string) => void
}

export function ConversationList({
  conversations = staticConversations,
  activeId,
  onSelect,
  onPin,
  onDelete,
  onRename,
  onNewChat,
  searchQuery = "",
  onSearchChange,
}: ConversationListProps) {
  const [deleteTarget, setDeleteTarget] = useState<{ id: string; title: string } | null>(null)

  const handleDelete = (id: string) => {
    const conv = conversations.find((c) => c.id === id)
    if (conv) {
      setDeleteTarget({ id, title: conv.title })
    }
  }

  const confirmDelete = () => {
    if (deleteTarget) {
      onDelete?.(deleteTarget.id)
      setDeleteTarget(null)
    }
  }

  const sorted = [...conversations].sort((a, b) => {
    if (a.pinned && !b.pinned) return -1
    if (!a.pinned && b.pinned) return 1
    if (a.active && !b.active) return -1
    if (!a.active && b.active) return 1
    return 0
  })

  return (
    <div className="flex flex-col h-full bg-[#121212]">
      {/* Header */}
      <div className="flex h-[52px] items-center justify-between px-4 border-b border-[#27272a] shrink-0">
        <div className="flex items-center gap-2.5">
          <div className="size-6 rounded-md bg-[#00f0ff]/10 flex items-center justify-center">
            <MessageSquare className="size-3 text-[#00f0ff]" />
          </div>
          <span className="text-sm font-semibold tracking-tight text-white/90">Chats</span>
        </div>
        <button
          type="button"
          onClick={onNewChat}
          className="flex items-center justify-center size-7 rounded-md text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors duration-150"
          aria-label="New chat"
        >
          <Plus className="size-3.5" />
        </button>
      </div>

      {/* Search */}
      {onSearchChange && (
        <div className="shrink-0">
          <ConversationSearch value={searchQuery} onChange={onSearchChange} />
        </div>
      )}

      {/* List */}
      <div className="flex-1 overflow-y-auto sn-scrollbar-thin px-2 py-1 space-y-0.5">
        {sorted.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
            <MessageSquare className="size-8 text-[#6b6b6b] mb-3" />
            <p className="text-sm text-[#a1a1a1]">No conversations found</p>
            <p className="text-[10px] text-[#6b6b6b] mt-1">Start a new chat to begin</p>
          </div>
        ) : (
          sorted.map((conv) => (
            <ConversationItem
              key={conv.id}
              conversation={conv}
              active={conv.id === activeId}
              onClick={() => onSelect?.(conv.id)}
              onPin={() => onPin?.(conv.id)}
              onDelete={() => handleDelete(conv.id)}
              onRename={(title) => onRename?.(conv.id, title)}
            />
          ))
        )}
      </div>

      {deleteTarget && (
        <DeleteConfirmDialog
          title={deleteTarget.title}
          onConfirm={confirmDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
    </div>
  )
}
