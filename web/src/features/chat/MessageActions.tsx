import { Copy, Check, RefreshCw, Pencil } from "lucide-react"
import { useState, useCallback } from "react"

type MessageActionsProps = {
  content: string
  onCopy?: () => void
  onEdit?: () => void
  onRegenerate?: () => void
}

export function MessageActions({ content, onCopy, onEdit, onRegenerate }: MessageActionsProps) {
  const [copied, setCopied] = useState(false)

  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(content).then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    })
    onCopy?.()
  }, [content, onCopy])

  return (
    <div className="flex items-center gap-1 mt-1.5 opacity-0 group-hover:opacity-100 transition-all duration-150">
      <button
        type="button"
        onClick={handleCopy}
        className="flex items-center gap-1 px-2 py-1 text-[9px] text-[#6b6b6b] hover:text-white rounded-md hover:bg-[#1e1e1e] transition-colors duration-150"
        aria-label={copied ? "Copied" : "Copy message"}
      >
        {copied ? (
          <Check className="size-2.5 text-[#22c55e]" />
        ) : (
          <Copy className="size-2.5" />
        )}
        <span className="font-mono uppercase tracking-wider">{copied ? "Copied" : "Copy"}</span>
      </button>
      {onEdit && (
        <button
          type="button"
          onClick={onEdit}
          className="flex items-center gap-1 px-2 py-1 text-[9px] text-[#6b6b6b] hover:text-white rounded-md hover:bg-[#1e1e1e] transition-colors duration-150"
          aria-label="Edit message"
        >
          <Pencil className="size-2.5" />
          <span className="font-mono uppercase tracking-wider">Edit</span>
        </button>
      )}
      {onRegenerate && (
        <button
          type="button"
          onClick={onRegenerate}
          className="flex items-center gap-1 px-2 py-1 text-[9px] text-[#6b6b6b] hover:text-white rounded-md hover:bg-[#1e1e1e] transition-colors duration-150"
          aria-label="Regenerate response"
        >
          <RefreshCw className="size-2.5" />
          <span className="font-mono uppercase tracking-wider">Retry</span>
        </button>
      )}
    </div>
  )
}
