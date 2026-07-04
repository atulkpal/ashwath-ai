import { useState } from "react"
import { BarChart3, FileText } from "lucide-react"

type ParameterPanelProps = {
  temperature?: number
  topP?: number
  maxTokens?: number
  systemPrompt?: string
}

export function ParameterPanel({
  temperature = 0.72,
  topP = 0.9,
  maxTokens = 2048,
  systemPrompt = "You are Ashwath AI, a high-performance research assistant specializing in deep technical analysis. Maintain a concise, technical, and objective tone."
}: ParameterPanelProps) {
  const [localTemp, setLocalTemp] = useState(temperature)
  const [localTopP, setLocalTopP] = useState(topP)
  const [localMax, setLocalMax] = useState(maxTokens)

  return (
    <div className="flex flex-col h-full bg-[var(--sn-raised)]">
      {/* Header */}
      <div className="px-4 py-4 border-b border-[var(--sn-border)]">
        <div className="flex items-center justify-between mb-1">
          <span className="text-[11px] font-semibold text-[var(--sn-text-secondary)] uppercase tracking-widest">Context</span>
          <span className="text-[var(--sn-text-secondary)] text-sm" aria-hidden="true">⚙</span>
        </div>
        <p className="text-xl font-bold text-[var(--sn-accent)]">Parameters</p>
      </div>

      <div className="flex-1 overflow-y-auto sn-scrollbar p-4 space-y-6">
        {/* Temperature */}
        <div className="space-y-2">
          <div className="flex justify-between items-center">
            <span className="text-[11px] font-semibold text-[var(--sn-text-primary)] uppercase tracking-widest">Temperature</span>
            <span className="font-[var(--sn-font-code)] text-sm text-[var(--sn-accent)] tabular-nums">{localTemp.toFixed(2)}</span>
          </div>
          <input
            type="range"
            min={0}
            max={2}
            step={0.01}
            value={localTemp}
            onChange={(e) => setLocalTemp(Number(e.target.value))}
            className="w-full h-1 bg-[var(--sn-overlay)] rounded-lg appearance-none cursor-pointer accent-[var(--sn-accent)]"
          />
          <p className="text-[10px] text-[var(--sn-text-secondary)] italic">Controls randomness: Lower is more deterministic, higher is more creative.</p>
        </div>

        {/* Top-P */}
        <div className="space-y-2">
          <div className="flex justify-between items-center">
            <span className="text-[11px] font-semibold text-[var(--sn-text-primary)] uppercase tracking-widest">Top-P</span>
            <span className="font-[var(--sn-font-code)] text-sm text-[var(--sn-accent)] tabular-nums">{localTopP.toFixed(2)}</span>
          </div>
          <input
            type="range"
            min={0}
            max={1}
            step={0.05}
            value={localTopP}
            onChange={(e) => setLocalTopP(Number(e.target.value))}
            className="w-full h-1 bg-[var(--sn-overlay)] rounded-lg appearance-none cursor-pointer accent-[var(--sn-accent)]"
          />
          <p className="text-[10px] text-[var(--sn-text-secondary)] italic">Nucleus sampling: Only consider the top tokens whose sum probability is P.</p>
        </div>

        {/* Max Tokens */}
        <div className="space-y-2">
          <div className="flex justify-between items-center">
            <span className="text-[11px] font-semibold text-[var(--sn-text-primary)] uppercase tracking-widest">Max Tokens</span>
            <span className="font-[var(--sn-font-code)] text-sm text-[var(--sn-accent)] tabular-nums">{localMax}</span>
          </div>
          <input
            type="range"
            min={256}
            max={8192}
            step={128}
            value={localMax}
            onChange={(e) => setLocalMax(Number(e.target.value))}
            className="w-full h-1 bg-[var(--sn-overlay)] rounded-lg appearance-none cursor-pointer accent-[var(--sn-accent)]"
          />
        </div>

        {/* System Prompt */}
        <div className="space-y-2">
          <label className="text-[11px] font-semibold text-[var(--sn-text-primary)] uppercase tracking-widest flex items-center gap-2">
            <span className="text-sm">⌨</span>
            System Prompt
          </label>
          <textarea
            value={systemPrompt}
            onChange={() => {}}
            placeholder="Define the model's persona..."
            rows={6}
            className="w-full bg-[var(--sn-overlay)] border border-[var(--sn-border)] text-sm text-[var(--sn-text-secondary)] rounded focus:border-[var(--sn-accent)] transition-colors resize-none p-3"
          />
        </div>
      </div>

      {/* Tabs */}
      <div className="border-t border-[var(--sn-border)]">
        <div className="grid grid-cols-2">
          <button className="flex items-center justify-center gap-2 py-3 text-[11px] text-[var(--sn-accent)] bg-[var(--sn-overlay)]">
            <BarChart3 className="size-4" />
            <span>Telemetry</span>
          </button>
          <button className="flex items-center justify-center gap-2 py-3 text-[11px] text-[var(--sn-text-secondary)] hover:bg-[var(--sn-overlay)] transition-colors">
            <FileText className="size-4" />
            <span>Logs</span>
          </button>
        </div>
      </div>
    </div>
  )
}
