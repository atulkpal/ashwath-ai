import { useState, useCallback } from "react"
import { Copy, Check } from "lucide-react"

type CodeBlockProps = {
  language: string
  code: string
}

export function CodeBlock({ language, code }: CodeBlockProps) {
  const [copied, setCopied] = useState(false)

  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(code).then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    })
  }, [code])

  return (
    <div className="my-4 rounded-xl border border-[#27272a] bg-[#0c0c0c] overflow-hidden">
      <div className="flex items-center justify-between px-4 py-2 border-b border-[#27272a] bg-[#1e1e1e]/50">
        <div className="flex items-center gap-2">
          <span className="size-2 rounded-full bg-[#ef4444]" />
          <span className="size-2 rounded-full bg-[#eab308]" />
          <span className="size-2 rounded-full bg-[#22c55e]" />
          <span className="ml-2 text-[10px] font-mono text-[#6b6b6b] uppercase tracking-wider">
            {language}
          </span>
        </div>
        <button
          type="button"
          onClick={handleCopy}
          className="flex items-center gap-1.5 px-2 py-1 text-[10px] font-mono text-[#6b6b6b] hover:text-white rounded-md hover:bg-black/40 transition-all duration-150"
          aria-label={copied ? "Copied" : "Copy code"}
        >
          {copied ? (
            <>
              <Check className="size-3 text-[#22c55e]" />
              <span className="text-[#22c55e]">Copied</span>
            </>
          ) : (
            <>
              <Copy className="size-3" />
              <span>Copy</span>
            </>
          )}
        </button>
      </div>
      <div className="overflow-x-auto">
        <pre className="p-4 text-sm leading-relaxed">
          <code className="font-mono text-white/90">{code}</code>
        </pre>
      </div>
    </div>
  )
}
