import { type KeyboardEvent } from "react"
import { Send, Paperclip, Mic, Eye, Wrench } from "lucide-react"

type ChatInputProps = {
  input?: string
  onInputChange?: (value: string) => void
  onSend?: (value: string) => void
  onKeyDown?: (e: KeyboardEvent<HTMLTextAreaElement>) => void
  disabled?: boolean
}

export function ChatInput({ input = "", onInputChange, onSend, onKeyDown, disabled }: ChatInputProps) {
  const handleSend = () => {
    const trimmed = input.trim()
    if (!trimmed) return
    onSend?.(trimmed)
    onInputChange?.("")
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
    onKeyDown?.(e)
  }

  return (
    <div className="p-6 bg-gradient-to-t from-[var(--sn-base)] via-[var(--sn-base)] to-transparent">
      <div className="max-w-4xl mx-auto bg-[var(--sn-raised)] border border-[var(--sn-border)] rounded-lg focus-within:ring-1 focus-within:ring-[var(--sn-accent)] transition-all duration-150">
        <textarea
          value={input}
          onChange={(e) => onInputChange?.(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Type your prompt here..."
          rows={2}
          disabled={disabled}
          className="w-full bg-transparent border-none focus:ring-0 p-4 font-[var(--sn-font-code)] text-sm text-[var(--sn-text-primary)] resize-none placeholder:text-[var(--sn-text-secondary)] disabled:opacity-50"
        />
        <div className="flex items-center justify-between px-4 pb-3">
          <div className="flex items-center gap-1">
            <button type="button" className="p-2 text-[var(--sn-accent)] hover:bg-[var(--sn-accent)]/10 rounded transition-colors duration-150" title="Tools">
              <Wrench className="size-4" />
            </button>
            <button type="button" className="p-2 text-[var(--sn-text-secondary)] hover:text-[var(--sn-text-primary)] hover:bg-[var(--sn-overlay)] rounded transition-colors duration-150" title="Attach">
              <Paperclip className="size-4" />
            </button>
            <button type="button" className="p-2 text-[var(--sn-text-secondary)] hover:text-[var(--sn-text-primary)] hover:bg-[var(--sn-overlay)] rounded transition-colors duration-150" title="Voice">
              <Mic className="size-4" />
            </button>
            <button type="button" className="p-2 text-[var(--sn-text-secondary)] hover:text-[var(--sn-text-primary)] hover:bg-[var(--sn-overlay)] rounded transition-colors duration-150" title="Vision">
              <Eye className="size-4" />
            </button>
          </div>
          <button
            type="button"
            onClick={handleSend}
            disabled={disabled}
            className="bg-[var(--sn-accent)] text-[var(--sn-accent-text)] text-sm font-semibold px-4 py-2 rounded hover:bg-white transition-colors duration-150 flex items-center gap-2 disabled:opacity-50"
          >
            <span>Execute</span>
            <Send className="size-4" />
          </button>
        </div>
      </div>
      <p className="text-center mt-2 font-[var(--sn-font-code)] text-[10px] text-[var(--sn-text-secondary)] uppercase tracking-widest opacity-50">
        Secure Local Compute Protocol Active
      </p>
    </div>
  )
}