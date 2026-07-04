import { useState, type ReactNode } from "react"
import { Sidebar } from "@/components/layout/Sidebar"
import { TopBar } from "@/components/layout/TopBar"
import { MainContentArea } from "@/components/layout/MainContentArea"
import { RightPanel } from "@/components/layout/RightPanel"
import { StatusBar } from "@/components/layout/StatusBar"
import { ChatMessage } from "@/features/chat/ChatMessage"
import { ChatInput } from "@/features/chat/ChatInput"
import { ModelSelector } from "@/features/chat/ModelSelector"
import { ParameterPanel } from "@/features/chat/ParameterPanel"
import { useChatState } from "@/features/chat/useChatState"

type MainLayoutProps = {
  children?: ReactNode
}

export function MainLayout(_props: MainLayoutProps) {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [rightPanelOpen, setRightPanelOpen] = useState(false)
  const chat = useChatState()

  return (
    <div className="flex h-screen flex-col bg-[var(--sn-base)] text-[var(--sn-text-primary)]">
      {/* Top Bar */}
      <TopBar
        rightPanelOpen={rightPanelOpen}
        onToggleRightPanel={() => setRightPanelOpen((prev) => !prev)}
      />

      {/* Body: Sidebar + Content + Right Panel */}
      <div className="flex flex-1 overflow-hidden">
        <Sidebar
          collapsed={sidebarCollapsed}
          onToggle={() => setSidebarCollapsed((prev) => !prev)}
          mode="conversations"
        />
        <MainContentArea>
          <div className="flex h-full flex-col">
            {/* Workspace header */}
            <header className="flex h-16 items-center justify-between px-6 border-b border-[var(--sn-border)]">
              <div className="flex items-center gap-6">
                <h1 className="text-2xl font-bold text-[var(--sn-text-primary)]">Workspace</h1>
                <div className="hidden md:flex items-center gap-6 font-[var(--sn-font-code)] text-sm text-[var(--sn-text-secondary)] border-l border-[var(--sn-border)] pl-6">
                  <div className="flex items-center gap-2">
                    <span className="text-[var(--sn-accent)]">Tokens/sec:</span>
                    <span className="text-[var(--sn-text-primary)]">124.5</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-[var(--sn-accent)]">Context:</span>
                    <span className="text-[var(--sn-text-primary)]">8k/32k</span>
                  </div>
                  <ModelSelector />
                </div>
              </div>
              <div className="flex items-center gap-2">
                <button type="button" className="p-2 text-[var(--sn-text-secondary)] hover:text-[var(--sn-accent)] transition-colors" aria-label="Monitoring">
                  <span className="material-symbols-outlined text-[18px]">monitoring</span>
                </button>
                <button type="button" className="p-2 text-[var(--sn-text-secondary)] hover:text-[var(--sn-accent)] transition-colors" aria-label="Settings">
                  <span className="material-symbols-outlined text-[18px]">settings_input_component</span>
                </button>
                <button type="button" className="p-2 text-[var(--sn-text-secondary)] hover:text-[var(--sn-accent)] transition-colors" aria-label="Sensors">
                  <span className="material-symbols-outlined text-[18px]">sensors</span>
                </button>
              </div>
            </header>

            {/* Chat feed */}
            <section className="flex-1 overflow-y-auto sn-scrollbar px-6 py-6">
              <div className="mx-auto w-full max-w-4xl space-y-6">
                {chat.messages.map((m) => (
                  <ChatMessage key={m.id} message={m} />
                ))}
                {chat.isLoading && (
                  <div className="flex gap-3">
                    <div className="size-8 rounded bg-[var(--sn-accent)]/10 border border-[var(--sn-accent)]/20 flex items-center justify-center shrink-0">
                      <span className="text-[var(--sn-accent)] text-xs font-bold">AI</span>
                    </div>
                    <div className="flex flex-col items-start">
                      <div className="rounded-lg px-4 py-2.5 bg-transparent text-[var(--sn-text-primary)]">
                        <p className="text-sm leading-relaxed">Generating response...</p>
                      </div>
                    </div>
                  </div>
                )}
                {chat.messages.length === 0 && (
                  <div className="flex h-full items-center justify-center">
                    <p className="text-sm text-[var(--sn-text-secondary)]">No messages yet. Start a conversation below.</p>
                  </div>
                )}
              </div>
            </section>

            {/* Input */}
            <ChatInput
              input={chat.input}
              onInputChange={chat.setInput}
              onSend={chat.sendMessage}
              onKeyDown={chat.handleKeyDown}
              disabled={chat.isLoading}
            />
          </div>
        </MainContentArea>
        <RightPanel
          open={rightPanelOpen}
          onClose={() => setRightPanelOpen(false)}
        >
          <ParameterPanel />
        </RightPanel>
      </div>

      {/* Status Bar */}
      <StatusBar />
    </div>
  )
}
