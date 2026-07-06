import type { Message } from "./chat-data"
import { CodeBlock } from "./CodeBlock"
import { MessageActions } from "./MessageActions"
import { AIAvatar } from "@/components/common/AIAvatar"
import Markdown from "react-markdown"
import remarkGfm from "remark-gfm"
import type { Components } from "react-markdown"

type ChatMessageProps = {
  message: Message
}

export function ChatMessage({ message }: ChatMessageProps) {
  const isUser = message.role === "user"

  const markdownComponents: Components = {
    code({ className, children, ...props }) {
      const match = /language-(\w+)/.exec(className || "")
      const codeString = String(children).replace(/\n$/, "")
      if (match) {
        return <CodeBlock language={match[1]} code={codeString} />
      }
      return (
        <code
          className="px-1.5 py-0.5 rounded-md bg-[#1a1a1a] text-[#00f0ff] text-sm font-mono"
          {...props}
        >
          {children}
        </code>
      )
    },
    pre({ children }) {
      return <>{children}</>
    },
    p({ children }) {
      return <p className="text-[15px] leading-[1.75] mb-4 last:mb-0">{children}</p>
    },
    ul({ children }) {
      return <ul className="list-disc pl-6 mb-4 space-y-1.5 text-[15px] leading-[1.75]">{children}</ul>
    },
    ol({ children }) {
      return <ol className="list-decimal pl-6 mb-4 space-y-1.5 text-[15px] leading-[1.75]">{children}</ol>
    },
    li({ children }) {
      return <li>{children}</li>
    },
    strong({ children }) {
      return <strong className="font-semibold text-white">{children}</strong>
    },
    h1({ children }) {
      return <h1 className="text-lg font-bold mb-3 mt-7 first:mt-0 text-white">{children}</h1>
    },
    h2({ children }) {
      return <h2 className="text-base font-bold mb-2 mt-6 first:mt-0 text-white">{children}</h2>
    },
    h3({ children }) {
      return <h3 className="text-[15px] font-bold mb-1.5 mt-5 first:mt-0 text-white">{children}</h3>
    },
    blockquote({ children }) {
      return (
        <blockquote className="border-l-2 border-[#00f0ff] pl-4 my-4 text-[#a1a1a1] italic text-[15px] leading-[1.75]">
          {children}
        </blockquote>
      )
    },
    hr() {
      return <hr className="my-6 border-[#27272a]" />
    },
    a({ href, children }) {
      return (
        <a
          href={href}
          target="_blank"
          rel="noopener noreferrer"
          className="text-[#00f0ff] hover:underline decoration-1 underline-offset-2"
        >
          {children}
        </a>
      )
    },
  }

  const timeOnly = message.timestamp.split(":").slice(0, 2).join(":")

  return (
    <div className={`flex gap-4 group ${isUser ? "flex-row-reverse" : ""}`}>
      {/* Avatar */}
      <div className={`shrink-0 ${isUser ? "" : "pt-1"}`}>
        {isUser ? (
          <div className="size-8 rounded-lg bg-[#1e1e1e] border border-[#27272a] flex items-center justify-center">
            <span className="text-[11px] font-semibold text-[#a1a1a1]">U</span>
          </div>
        ) : (
          <AIAvatar />
        )}
      </div>

      {/* Content */}
      <div className={`flex flex-col ${isUser ? "items-end" : "items-start"} min-w-0 flex-1 max-w-[85%]`}>
        <div className={`rounded-2xl px-5 py-3.5 w-full ${
          isUser
            ? "bg-[#00f0ff] text-black"
            : "bg-[#1a1a1a] text-white border border-[#27272a]/50"
        }`}>
          {isUser ? (
            <p className="text-[15px] leading-[1.75] whitespace-pre-wrap font-medium">{message.content}</p>
          ) : (
            <div className="text-[15px] leading-[1.75] [&>:first-child]:mt-0 [&>:last-child]:mb-0">
              <Markdown remarkPlugins={[remarkGfm]} components={markdownComponents}>
                {message.content}
              </Markdown>
            </div>
          )}
        </div>
        <div className={`flex items-center gap-3 mt-1.5 px-1 ${
          isUser ? "flex-row-reverse" : ""
        }`}>
          <span className="text-[10px] text-[#6b6b6b] font-mono tabular-nums">{timeOnly}</span>
          {message.model && !isUser && (
            <span className="text-[9px] text-[#6b6b6b] font-mono uppercase tracking-wider">
              {message.model}
            </span>
          )}
          {message.tokens && !isUser && (
            <span className="text-[9px] text-[#6b6b6b] font-mono">{message.tokens} tok</span>
          )}
          {message.token_sec && !isUser && (
            <span className="text-[9px] text-[#6b6b6b] font-mono">{message.token_sec} t/s</span>
          )}
        </div>
        {!isUser && <MessageActions content={message.content} />}
      </div>
    </div>
  )
}
