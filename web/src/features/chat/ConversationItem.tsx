import { useState, useCallback, useRef, useEffect } from "react"
import type { Conversation } from "./chat-data"
import { MessageSquare, Pin, PinOff, Trash2, Pencil, Check, X, MoreHorizontal } from "lucide-react"

type ConversationItemProps = {
  conversation: Conversation
  active?: boolean
  onClick?: () => void
  onPin?: () => void
  onDelete?: () => void
  onRename?: (title: string) => void
}

export function ConversationItem({ conversation, active, onClick, onPin, onDelete, onRename }: ConversationItemProps) {
  const [showMenu, setShowMenu] = useState(false)
  const [isRenaming, setIsRenaming] = useState(false)
  const [renameValue, setRenameValue] = useState(conversation.title)
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (isRenaming && inputRef.current) {
      inputRef.current.focus()
      inputRef.current.select()
    }
  }, [isRenaming])

  const handleRename = useCallback(() => {
    const trimmed = renameValue.trim()
    if (trimmed && trimmed !== conversation.title) {
      onRename?.(trimmed)
    }
    setIsRenaming(false)
  }, [renameValue, conversation.title, onRename])

  const handleRenameKeyDown = useCallback((e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleRename()
    } else if (e.key === "Escape") {
      setRenameValue(conversation.title)
      setIsRenaming(false)
    }
  }, [handleRename, conversation.title])

  return (
    <div className="group relative">
      <button
        onClick={onClick}
        className={`w-full flex items-start gap-3 px-3 py-2.5 rounded-lg text-left transition-all duration-150 ${
          active
            ? "bg-[#1e1e1e] text-white"
            : "text-[#a1a1a1] hover:bg-[#1e1e1e]/60 hover:text-white"
        }`}
      >
        <MessageSquare className="size-3.5 shrink-0 mt-0.5 text-[#6b6b6b] group-hover:text-[#a1a1a1] transition-colors duration-150" />
        <div className="flex-1 min-w-0">
          {isRenaming ? (
            <div className="flex items-center gap-1" onClick={(e) => e.stopPropagation()}>
              <input
                ref={inputRef}
                type="text"
                value={renameValue}
                onChange={(e) => setRenameValue(e.target.value)}
                onKeyDown={handleRenameKeyDown}
                className="flex-1 bg-black border border-[#00f0ff] rounded px-1.5 py-0.5 text-sm text-white outline-none"
                onClick={(e) => e.stopPropagation()}
              />
              <button
                type="button"
                onClick={(e) => { e.stopPropagation(); handleRename() }}
                className="p-0.5 text-[#22c55e] hover:text-white transition-colors"
              >
                <Check className="size-3" />
              </button>
              <button
                type="button"
                onClick={(e) => { e.stopPropagation(); setRenameValue(conversation.title); setIsRenaming(false) }}
                className="p-0.5 text-[#a1a1a1] hover:text-white transition-colors"
              >
                <X className="size-3" />
              </button>
            </div>
          ) : (
            <>
              <div className="text-sm font-medium truncate flex items-center gap-1.5">
                {conversation.title}
                {conversation.pinned && (
                  <Pin className="size-2.5 text-[#00f0ff] fill-[#00f0ff]" />
                )}
              </div>
              <div className="text-[11px] text-[#6b6b6b] truncate mt-0.5">
                {conversation.preview}
              </div>
            </>
          )}
        </div>
        <span className="text-[9px] text-[#6b6b6b] font-mono tabular-nums shrink-0 mt-0.5">{conversation.timestamp}</span>
      </button>

      {!isRenaming && (
        <div className="absolute right-2 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-150">
          <div className="relative">
            <button
              type="button"
              onClick={(e) => { e.stopPropagation(); setShowMenu(!showMenu) }}
              className="flex items-center justify-center size-6 rounded-md text-[#6b6b6b] hover:text-white hover:bg-black/40 transition-colors"
              aria-label="More actions"
            >
              <MoreHorizontal className="size-3" />
            </button>
            {showMenu && (
              <div
                className="absolute right-0 top-full mt-1 w-32 rounded-lg border border-[#27272a] bg-[#121212] py-1 shadow-lg z-10"
                onClick={(e) => e.stopPropagation()}
              >
                <button
                  type="button"
                  onClick={() => { setIsRenaming(true); setShowMenu(false) }}
                  className="w-full flex items-center gap-2 px-3 py-1.5 text-xs text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors"
                >
                  <Pencil className="size-3" />
                  Rename
                </button>
                <button
                  type="button"
                  onClick={() => { onPin?.(); setShowMenu(false) }}
                  className="w-full flex items-center gap-2 px-3 py-1.5 text-xs text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors"
                >
                  {conversation.pinned ? (
                    <><PinOff className="size-3" /> Unpin</>
                  ) : (
                    <><Pin className="size-3" /> Pin</>
                  )}
                </button>
                <div className="h-px bg-[#27272a] my-1" />
                <button
                  type="button"
                  onClick={() => { onDelete?.(); setShowMenu(false) }}
                  className="w-full flex items-center gap-2 px-3 py-1.5 text-xs text-[#ef4444] hover:bg-[#ef4444]/10 transition-colors"
                >
                  <Trash2 className="size-3" />
                  Delete
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {showMenu && (
        <div className="fixed inset-0 z-0" onClick={() => setShowMenu(false)} />
      )}
    </div>
  )
}
