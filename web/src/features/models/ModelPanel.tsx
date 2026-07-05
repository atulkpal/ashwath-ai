import { useModelBrowser } from "./useModelBrowser"
import { Download, Cpu, HardDrive } from "lucide-react"

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`
}

export function ModelPanel() {
  const { models, isLoading, error, installModel } = useModelBrowser()

  return (
    <div className="flex flex-col h-full bg-[#121212]">
      <div className="flex h-14 items-center gap-2 px-4 border-b border-[#27272a]">
        <Cpu className="size-4 text-[#00f0ff]" />
        <span className="text-sm font-semibold">Models</span>
      </div>

      <div className="flex-1 overflow-y-auto sn-scrollbar p-3 space-y-2">
        {isLoading && (
          <div className="flex items-center justify-center py-12">
            <div className="size-5 rounded-full border-2 border-[#27272a] border-t-[#00f0ff] animate-spin" />
          </div>
        )}

        {error && (
          <div className="text-xs text-[#ef4444] p-3 bg-[#ef4444]/10 rounded-lg">
            {error}
          </div>
        )}

        {!isLoading && !error && models.length === 0 && (
          <div className="text-xs text-[#6b6b6b] p-3 text-center">
            No models available. Start the engine to see models.
          </div>
        )}

        {models.map((model) => (
          <div
            key={model.id}
            className="rounded-lg border border-[#27272a] bg-[#1e1e1e] p-3 space-y-2"
          >
            <div className="flex items-start justify-between">
              <div>
                <div className="text-sm font-medium text-white">{model.name}</div>
                {model.provider && (
                  <div className="text-[10px] text-[#00f0ff] uppercase tracking-wider">
                    {model.provider}
                  </div>
                )}
              </div>
              <div className={`text-[10px] font-medium px-1.5 py-0.5 rounded ${
                model.installed
                  ? "bg-[#22c55e]/10 text-[#22c55e]"
                  : "bg-[#27272a] text-[#6b6b6b]"
              }`}>
                {model.installed ? "Ready" : "Available"}
              </div>
            </div>

            <div className="flex items-center gap-3 text-[11px] text-[#a1a1a1]">
              {model.sizeBytes > 0 && (
                <span className="flex items-center gap-1">
                  <HardDrive className="size-3" />
                  {formatBytes(model.sizeBytes)}
                </span>
              )}
              {model.parameters && (
                <span>{model.parameters}</span>
              )}
            </div>

            {!model.installed && (
              <button
                type="button"
                onClick={() => installModel(model.id)}
                className="flex items-center justify-center gap-1.5 w-full py-1.5 rounded-md bg-[#00f0ff] text-black text-[11px] font-semibold hover:bg-white transition-colors"
              >
                <Download className="size-3" />
                Download
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
