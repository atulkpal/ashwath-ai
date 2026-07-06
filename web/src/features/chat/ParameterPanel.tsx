import { useState, useCallback } from "react"
import { BarChart3, FileText, RotateCcw, Sliders, ScrollText, Cpu } from "lucide-react"

type ParameterPanelProps = {
  temperature?: number
  topP?: number
  maxTokens?: number
  systemPrompt?: string
}

const DEFAULTS = {
  temperature: 0.72,
  topP: 0.9,
  maxTokens: 2048,
  systemPrompt: "You are Ashwath AI, a high-performance research assistant specializing in deep technical analysis. Maintain a concise, technical, and objective tone.",
}

type TabId = "parameters" | "system"

const tabs: { id: TabId; label: string; icon: typeof Sliders }[] = [
  { id: "parameters", label: "Controls", icon: Sliders },
  { id: "system", label: "Prompt", icon: ScrollText },
]

export function ParameterPanel({
  temperature = DEFAULTS.temperature,
  topP = DEFAULTS.topP,
  maxTokens = DEFAULTS.maxTokens,
  systemPrompt = DEFAULTS.systemPrompt,
}: ParameterPanelProps) {
  const [localTemp, setLocalTemp] = useState(temperature)
  const [localTopP, setLocalTopP] = useState(topP)
  const [localMax, setLocalMax] = useState(maxTokens)
  const [localPrompt, setLocalPrompt] = useState(systemPrompt)
  const [activeTab, setActiveTab] = useState<TabId>("parameters")

  const handleReset = useCallback(() => {
    setLocalTemp(DEFAULTS.temperature)
    setLocalTopP(DEFAULTS.topP)
    setLocalMax(DEFAULTS.maxTokens)
    setLocalPrompt(DEFAULTS.systemPrompt)
  }, [])

  const hasChanges =
    localTemp !== DEFAULTS.temperature ||
    localTopP !== DEFAULTS.topP ||
    localMax !== DEFAULTS.maxTokens ||
    localPrompt !== DEFAULTS.systemPrompt

  return (
    <div className="flex flex-col h-full bg-[#121212]">
      <div className="px-5 py-4 border-b border-[#27272a]">
        <div className="flex items-center justify-between mb-0.5">
          <span className="text-[10px] font-semibold text-[#6b6b6b] uppercase tracking-[0.12em]">
            Configuration
          </span>
          {hasChanges && (
            <button
              type="button"
              onClick={handleReset}
              className="flex items-center gap-1 px-1.5 py-0.5 text-[9px] text-[#6b6b6b] hover:text-[#00f0ff] rounded transition-colors duration-150"
            >
              <RotateCcw className="size-2.5" />
              Reset
            </button>
          )}
        </div>
        <div className="flex items-center gap-2">
          <Cpu className="size-3.5 text-[#00f0ff]" />
          <span className="text-base font-semibold tracking-tight text-white/90">Parameters</span>
        </div>
      </div>

      <div className="grid grid-cols-2 border-b border-[#27272a]">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center justify-center gap-2 py-2.5 text-[10px] font-semibold uppercase tracking-wider transition-colors duration-150 ${
              activeTab === tab.id
                ? "text-[#00f0ff] bg-[#1e1e1e] border-b border-[#00f0ff]"
                : "text-[#6b6b6b] hover:text-[#a1a1a1]"
            }`}
          >
            <tab.icon className="size-3" />
            {tab.label}
          </button>
        ))}
      </div>

      <div className="flex-1 overflow-y-auto sn-scrollbar-thin p-4 space-y-4">
        {activeTab === "parameters" && (
          <>
            <div className="space-y-2.5">
              <div className="flex items-center justify-between">
                <div>
                  <span className="text-xs font-medium text-white/80">Temperature</span>
                  <p className="text-[10px] text-[#6b6b6b] mt-0.5 leading-relaxed">Controls randomness</p>
                </div>
                <span className="font-mono text-xs text-[#00f0ff] tabular-nums">{localTemp.toFixed(2)}</span>
              </div>
              <input
                type="range"
                min={0}
                max={2}
                step={0.01}
                value={localTemp}
                onChange={(e) => setLocalTemp(Number(e.target.value))}
                className="slider-thumb"
              />
            </div>

            <div className="h-px bg-[#27272a]/50" />

            <div className="space-y-2.5">
              <div className="flex items-center justify-between">
                <div>
                  <span className="text-xs font-medium text-white/80">Top-P</span>
                  <p className="text-[10px] text-[#6b6b6b] mt-0.5 leading-relaxed">Nucleus sampling threshold</p>
                </div>
                <span className="font-mono text-xs text-[#00f0ff] tabular-nums">{localTopP.toFixed(2)}</span>
              </div>
              <input
                type="range"
                min={0}
                max={1}
                step={0.05}
                value={localTopP}
                onChange={(e) => setLocalTopP(Number(e.target.value))}
                className="slider-thumb"
              />
            </div>

            <div className="h-px bg-[#27272a]/50" />

            <div className="space-y-2.5">
              <div className="flex items-center justify-between">
                <div>
                  <span className="text-xs font-medium text-white/80">Max Tokens</span>
                  <p className="text-[10px] text-[#6b6b6b] mt-0.5 leading-relaxed">Maximum response length</p>
                </div>
                <span className="font-mono text-xs text-[#00f0ff] tabular-nums">{localMax}</span>
              </div>
              <input
                type="range"
                min={256}
                max={8192}
                step={128}
                value={localMax}
                onChange={(e) => setLocalMax(Number(e.target.value))}
                className="slider-thumb"
              />
              <div className="flex justify-between text-[9px] text-[#6b6b6b] font-mono">
                <span>256</span>
                <span>8,192</span>
              </div>
            </div>
          </>
        )}

        {activeTab === "system" && (
          <div className="space-y-3">
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <ScrollText className="size-3.5 text-[#00f0ff]" />
                <span className="text-xs font-medium text-white/80">System Prompt</span>
              </div>
              <p className="text-[10px] text-[#6b6b6b] leading-relaxed">
                Defines the model's persona and behavior constraints.
              </p>
            </div>
            <textarea
              value={localPrompt}
              onChange={(e) => setLocalPrompt(e.target.value)}
              placeholder="Define the model's persona..."
              rows={12}
              className="w-full bg-[#0c0c0c] border border-[#27272a] text-sm text-white/90 rounded-lg focus:border-[#00f0ff] focus:ring-1 focus:ring-[#00f0ff]/20 transition-all duration-150 resize-none p-3.5 placeholder:text-[#6b6b6b] font-mono text-[13px] leading-relaxed"
            />
          </div>
        )}
      </div>

      <div className="border-t border-[#27272a]">
        <div className="grid grid-cols-2">
          <button className="flex items-center justify-center gap-2 py-3 text-[10px] text-[#00f0ff] bg-[#1e1e1e] font-semibold uppercase tracking-wider transition-colors">
            <BarChart3 className="size-3" />
            Telemetry
          </button>
          <button className="flex items-center justify-center gap-2 py-3 text-[10px] text-[#6b6b6b] hover:text-[#a1a1a1] hover:bg-[#1e1e1e] font-semibold uppercase tracking-wider transition-colors duration-150">
            <FileText className="size-3" />
            Logs
          </button>
        </div>
      </div>
    </div>
  )
}
