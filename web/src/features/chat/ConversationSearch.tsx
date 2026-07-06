import { Search, X } from "lucide-react"

type ConversationSearchProps = {
  value: string
  onChange: (value: string) => void
}

export function ConversationSearch({ value, onChange }: ConversationSearchProps) {
  return (
    <div className="relative px-3 py-2">
      <Search className="absolute left-5 top-1/2 -translate-y-1/2 size-3 text-[#a1a1a1] pointer-events-none" />
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder="Search conversations..."
        className="w-full bg-[#1e1e1e] border border-[#27272a] rounded-lg pl-8 pr-8 py-1.5 text-sm text-white placeholder:text-[#6b6b6b] focus:outline-none focus:border-[#00f0ff] transition-colors duration-150"
      />
      {value && (
        <button
          type="button"
          onClick={() => onChange("")}
          className="absolute right-4 top-1/2 -translate-y-1/2 text-[#a1a1a1] hover:text-white transition-colors"
          aria-label="Clear search"
        >
          <X className="size-3.5" />
        </button>
      )}
    </div>
  )
}
