import { PanelRightOpen, PanelRightClose, Terminal, Cpu, Menu } from "lucide-react"

type TopBarProps = {
  rightPanelOpen: boolean
  onToggleRightPanel: () => void
  runtimeStatus?: "connected" | "disconnected" | "connecting"
  modelName?: string
  contextUsed?: number
  contextMax?: number
  tokenSec?: number
  onMenuClick?: () => void
}

export function TopBar({
  rightPanelOpen,
  onToggleRightPanel,
  runtimeStatus = "disconnected",
  modelName = "ashwath-7b-v1",
  contextUsed = 8,
  contextMax = 32,
  tokenSec = 0,
  onMenuClick,
}: TopBarProps) {
  const statusConfig = {
    connected: { dot: "bg-[#22c55e]", label: "Connected" },
    connecting: { dot: "bg-[#eab308] animate-pulse", label: "Connecting" },
    disconnected: { dot: "bg-[#ef4444]", label: "Disconnected" },
  }

  const status = statusConfig[runtimeStatus]

  return (
    <header className="flex h-full items-center justify-between border-b border-[#27272a] bg-[#121212] px-5">
      {/* Left */}
      <div className="flex items-center gap-3">
        {onMenuClick && (
          <button
            type="button"
            onClick={onMenuClick}
            className="md:hidden flex items-center justify-center size-7 rounded-md text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors duration-150"
            aria-label="Open sidebar"
          >
            <Menu className="size-3.5" />
          </button>
        )}
        <div className="size-7 rounded-md bg-[#00f0ff]/10 flex items-center justify-center">
          <Terminal className="size-3.5 text-[#00f0ff]" />
        </div>
        <span className="text-sm font-semibold tracking-tight">Ashwath AI</span>
        <span className="size-1 rounded-full bg-[#27272a]" />
        <div className="flex items-center gap-1.5">
          <span className={`inline-block size-1.5 rounded-full ${status.dot}`} />
          <span className="text-[10px] font-mono text-[#a1a1a1] uppercase tracking-wider">
            {status.label}
          </span>
        </div>
      </div>

      {/* Right */}
      <div className="flex items-center gap-3">
        {/* Model badge */}
        <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-md border border-[#27272a] bg-[#1e1e1e]/50">
          <Cpu className="size-3 text-[#00f0ff]" />
          <span className="text-[10px] font-bold uppercase tracking-[0.08em] text-[#00f0ff]">
            {modelName}
          </span>
        </div>

        {/* Context */}
        <div className="hidden lg:flex items-center gap-2">
          <div className="w-14 h-1 rounded-full bg-[#1e1e1e] overflow-hidden">
            <div
              className="h-full rounded-full bg-[#00f0ff] transition-all duration-500 ease-out"
              style={{ width: `${(contextUsed / contextMax) * 100}%` }}
            />
          </div>
          <span className="text-[10px] font-mono text-[#a1a1a1] tabular-nums">
            {contextUsed}k/{contextMax}k
          </span>
        </div>

        {/* Tokens */}
        <div className="hidden lg:flex items-center gap-1 text-[10px] font-mono text-[#a1a1a1]">
          <span>{tokenSec > 0 ? `${tokenSec.toFixed(1)} t/s` : "-- t/s"}</span>
        </div>

        <div className="size-[1px] h-4 bg-[#27272a]" />

        <button
          type="button"
          onClick={onToggleRightPanel}
          aria-label={rightPanelOpen ? "Close right panel" : "Open right panel"}
          className="flex items-center justify-center size-7 rounded-md text-[#a1a1a1] hover:text-white hover:bg-[#1e1e1e] transition-colors duration-150"
        >
          {rightPanelOpen ? (
            <PanelRightClose className="size-3.5" />
          ) : (
            <PanelRightOpen className="size-3.5" />
          )}
        </button>
      </div>
    </header>
  )
}
