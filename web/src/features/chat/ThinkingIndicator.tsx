import { AIAvatar } from "@/components/common/AIAvatar"

export function ThinkingIndicator() {
  return (
    <div className="flex gap-3.5">
      <AIAvatar />
      <div className="flex flex-col items-start justify-center">
        <div className="flex items-center gap-2.5 px-4 py-2.5">
          <div className="flex items-center gap-1">
            <span className="size-1.5 rounded-full bg-[#00f0ff] animate-pulse" style={{ animationDelay: "0ms" }} />
            <span className="size-1.5 rounded-full bg-[#00f0ff] animate-pulse" style={{ animationDelay: "300ms" }} />
            <span className="size-1.5 rounded-full bg-[#00f0ff] animate-pulse" style={{ animationDelay: "600ms" }} />
          </div>
          <span className="text-sm text-[#a1a1a1] font-mono">
            Generating response...
          </span>
        </div>
      </div>
    </div>
  )
}
