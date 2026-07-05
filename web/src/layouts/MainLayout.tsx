import { useState, useEffect } from "react"
import { Sidebar } from "@/components/layout/Sidebar"
import { TopBar } from "@/components/layout/TopBar"
import { StatusBar } from "@/components/layout/StatusBar"
import { ChatMessage } from "@/features/chat/ChatMessage"
import { ChatInput } from "@/features/chat/ChatInput"
import { ParameterPanel } from "@/features/chat/ParameterPanel"
import { ThinkingIndicator } from "@/features/chat/ThinkingIndicator"
import { useChatState } from "@/features/chat/useChatState"
import { X } from "lucide-react"

function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState(false)
  useEffect(() => {
    const mq = window.matchMedia(query)
    setMatches(mq.matches)
    const handler = (e: MediaQueryListEvent) => setMatches(e.matches)
    mq.addEventListener("change", handler)
    return () => mq.removeEventListener("change", handler)
  }, [query])
  return matches
}

export function MainLayout() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [rightPanelOpen, setRightPanelOpen] = useState(true)
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false)
  const isMobile = useMediaQuery("(max-width: 768px)")
  const { scrollRef, messages, isLoading, input, setInput, sendMessage, handleKeyDown } = useChatState()

  useEffect(() => {
    if (isMobile) {
      setSidebarCollapsed(true)
      setRightPanelOpen(false)
    }
  }, [isMobile])

  const sidebarWidth = sidebarCollapsed ? "60px" : "280px"
  const rightPanelWidth = rightPanelOpen ? "320px" : "0px"

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
          onMenuClick={isMobile ? () => setMobileSidebarOpen(true) : undefined}
        />
      </div>

      <div className="flex flex-1 overflow-hidden relative">
        {isMobile && mobileSidebarOpen && (
          <div className="fixed inset-0 z-50 flex">
            <div
              className="absolute inset-0 bg-black/60 backdrop-blur-sm"
              onClick={() => setMobileSidebarOpen(false)}
            />
            <div className="relative w-[280px] h-full">
              <button
                onClick={() => setMobileSidebarOpen(false)}
                className="absolute top-3 right-3 z-10 p-1 text-[#a1a1a1] hover:text-white"
              >
                <X className="size-4" />
              </button>
              <Sidebar
                collapsed={false}
                onToggle={() => setMobileSidebarOpen(false)}
                mode="conversations"
              />
            </div>
          </div>
        )}

        <div
          className="hidden md:block shrink-0 transition-all duration-300 ease-out overflow-hidden"
          style={{ width: sidebarWidth }}
        >
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
              <div className="max-w-[800px] mx-auto px-4 md:px-8 pt-8 pb-4 min-h-full flex flex-col justify-end">
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

        {isMobile ? (
          rightPanelOpen && (
            <div className="fixed inset-0 z-50 flex">
              <div
                className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                onClick={() => setRightPanelOpen(false)}
              />
              <div className="relative w-full h-full">
                <button
                  onClick={() => setRightPanelOpen(false)}
                  className="absolute top-3 right-3 z-10 p-1 text-[#a1a1a1] hover:text-white"
                >
                  <X className="size-4" />
                </button>
                <ParameterPanel />
              </div>
            </div>
          )
        ) : (
          <div
            className="shrink-0 border-l border-[#27272a] bg-[#121212] transition-all duration-300 ease-out overflow-hidden"
            style={{ width: rightPanelWidth }}
          >
            <ParameterPanel />
          </div>
        )}
      </div>

      <StatusBar />
    </div>
  )
}
