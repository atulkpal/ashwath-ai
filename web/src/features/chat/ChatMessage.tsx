import type { Message } from "./chat-data"

type ChatMessageProps = {
  message: Message
}

export function ChatMessage({ message }: ChatMessageProps) {
  const isUser = message.role === "user"

  return (
    <div className={`flex gap-3 ${isUser ? "flex-row-reverse" : ""}`}>
      {!isUser && (
        <div className="size-8 rounded bg-[var(--sn-accent)]/10 border border-[var(--sn-accent)]/20 flex items-center justify-center shrink-0">
          <span className="text-[var(--sn-accent)] text-xs font-bold">AI</span>
        </div>
      )}
      <div className={`flex flex-col ${isUser ? "items-end" : "items-start"} max-w-[70%]`}>
        <div
          className={`rounded-lg px-4 py-2.5 ${
            isUser
              ? "bg-[var(--sn-overlay)] border border-[var(--sn-border)] text-[var(--sn-text-primary)]"
              : "bg-transparent text-[var(--sn-text-primary)]"
          }`}
        >
          <p className="text-sm leading-relaxed whitespace-pre-wrap">{message.content}</p>
        </div>
        <span className="mt-1.5 text-[11px] text-[var(--sn-text-secondary)] tabular-nums">{message.timestamp}</span>
      </div>
    </div>
  )
}