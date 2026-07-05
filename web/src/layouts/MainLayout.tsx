import { useState } from "react"
import { Sidebar } from "@/components/layout/Sidebar"
import { TopBar } from "@/components/layout/TopBar"
import { StatusBar } from "@/components/layout/StatusBar"
import { ChatMessage } from "@/features/chat/ChatMessage"
import { ChatInput } from "@/features/chat/ChatInput"
import { ParameterPanel } from "@/features/chat/ParameterPanel"
import { ThinkingIndicator } from "@/features/chat/ThinkingIndicator"
import { useChatState } from "@/features/chat/useChatState"

export function MainLayout() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [rightPanelOpen, setRightPanelOpen] = useState(true)
  const { scrollRef, messages, isLoading, input, setInput, sendMessage, handleKeyDown } = useChatState()

  return (
    <div className="flex h-screen flex-col bg-black text-white overflow-hidden">
      <div className="h-[52px] shrink-0">
        <TopBar
          rightPanelOpen={rightPanelOpen}
          onToggleRightPanel={() => setRightPanelOpen((prev) => !prev)}
          runtimeStatus="disconnected"
          modelName="ashwath-7b-v1"
          contextUsed={8}
          contextMax={32}
          tokenSec={0}
        />
      </div>

      <div className="flex flex-1 overflow-hidden">
        <div className={`
          shrink-0 transition-all duration-300 ease-out
          ${sidebarCollapsed ? "w-[60px]" : "w-[280px]"}
        `}>
          <Sidebar
            collapsed={sidebarCollapsed}
            onToggle={() => setSidebarCollapsed((prev) => !prev)}
            mode="conversations"
          />
        </div>

        <div className="flex-1 flex flex-col min-w-0">
          <div className="flex-1 flex flex-col min-w-0 relative">
            <section
              ref={scrollRef}
              className="absolute inset-0 overflow-y-auto sn-scrollbar"
            >
              <div className="max-w-[800px] mx-auto px-8 pt-8 pb-4 min-h-full flex flex-col justify-end">
                <div className="space-y-6">
                  {messages.map((m) => (
                    <ChatMessage key={m.id} message={m} />
                  ))}
                  {isLoading && (
                    <div className="pl-[52px]">
                      <ThinkingIndicator />
                    </div>
                  )}
                  {messages.length === 0 && !isLoading && (
                    <div className="flex flex-col items-center justify-center min-h-[500px] text-center">
                      <div className="relative mb-8">
                        <div className="size-16 rounded-2xl bg-[#00f0ff]/[0.04] border border-[#00f0ff]/[0.08] flex items-center justify-center">
                          <span className="text-lg font-bold tracking-tight text-[#00f0ff]">AI</span>
                        </div>
                        <span className="absolute -top-1 -right-1 size-2.5 rounded-full bg-[#22c55e] animate-pulse" />
                      </div>
                      <h2 className="text-base font-semibold tracking-tight mb-2">
                        Start a conversation
                      </h2>
                      <p className="text-sm text-[#a1a1a1] max-w-sm leading-relaxed">
                        Type a prompt below to begin interacting with your local AI model.
                        All processing happens on-device.
                      </p>
                      <div className="flex items-center gap-2 mt-6 text-[11px] font-mono tracking-wider text-[#6b6b6b] uppercase">
                        <span className="inline-block size-1.5 rounded-full bg-[#6b6b6b]" />
                        <span>Secure local compute protocol active</span>
                        <span className="inline-block size-1.5 rounded-full bg-[#6b6b6b]" />
                      </div>
                    </div>
                  )}
                </div>
                <div className="h-4" />
              </div>
            </section>
          </div>

          <div className="shrink-0 border-t border-[#27272a] bg-black">
            <ChatInput
              input={input}
              onInputChange={setInput}
              onSend={sendMessage}
              onKeyDown={handleKeyDown}
              disabled={isLoading}
            />
          </div>
        </div>

        <div className={`
          shrink-0 border-l border-[#27272a] bg-[#121212]
          transition-all duration-300 ease-out
          ${rightPanelOpen ? "w-[320px]" : "w-0 overflow-hidden"}
        `}>
          <ParameterPanel />
        </div>
      </div>

      <StatusBar />
    </div>
  )
}
