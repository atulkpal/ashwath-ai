import { type KeyboardEvent } from "react"
import { Send, Paperclip, Mic, Eye, Wrench } from "lucide-react"

type ChatInputProps = {
  input?: string
  onInputChange?: (value: string) => void
  onSend?: (value: string) => void
  onKeyDown?: (e: KeyboardEvent<HTMLTextAreaElement>) => void
  disabled?: boolean
}

const toolbarButtons = [
  { icon: Wrench, title: "Tools" },
  { icon: Paperclip, title: "Attach" },
  { icon: Mic, title: "Voice" },
  { icon: Eye, title: "Vision" },
] as const

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
    <div className="bg-gradient-to-t from-black via-black to-transparent pt-2 pb-5 px-6">
      <div className="max-w-[800px] mx-auto">
        <div className="relative bg-[#121212] border border-[#27272a] rounded-xl transition-all duration-200 focus-within:border-[#00f0ff] focus-within:shadow-[0_0_0_1px_rgba(0,240,255,0.15)]">
          <textarea
            value={input}
            onChange={(e) => onInputChange?.(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Type your prompt here..."
            rows={2}
            disabled={disabled}
            className="w-full bg-transparent border-none focus:ring-0 px-5 pt-4 pb-2 font-mono text-sm text-white resize-none placeholder:text-[#6b6b6b] disabled:opacity-50 outline-none"
          />
          <div className="flex items-center justify-between px-4 pb-3">
            <div className="flex items-center gap-0.5">
              {toolbarButtons.map((btn) => (
                <button
                  key={btn.title}
                  type="button"
                  className="flex items-center justify-center size-7 rounded-md text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors duration-150"
                  title={btn.title}
                >
                  <btn.icon className="size-3.5" />
                </button>
              ))}
            </div>
            <button
              type="button"
              onClick={handleSend}
              disabled={disabled || !input.trim()}
              className="flex items-center gap-2 bg-[#00f0ff] text-black text-sm font-semibold px-4 py-1.5 rounded-lg hover:bg-white transition-colors duration-150 disabled:opacity-30 disabled:cursor-not-allowed"
            >
              <span>Execute</span>
              <Send className="size-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
