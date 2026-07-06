import { Compass } from "lucide-react"

export function ExplorePage() {
  return (
    <div className="flex-1 overflow-y-auto sn-scrollbar p-6">
      <div className="max-w-[800px] mx-auto">
        <h1 className="text-lg font-semibold tracking-tight mb-2">Explore Models</h1>
        <p className="text-sm text-[#a1a1a1] mb-8">
          Discover and install AI models from the Ashwath catalog.
        </p>
        <div className="flex flex-col items-center justify-center min-h-[300px] rounded-xl border border-dashed border-[#27272a] bg-[#121212] text-center p-8">
          <Compass className="size-10 text-[#6b6b6b] mb-4" />
          <p className="text-sm font-medium text-[#a1a1a1] mb-1">Model discovery</p>
          <p className="text-xs text-[#6b6b6b] max-w-sm">
            Browse community models and recommended configurations.
          </p>
        </div>
      </div>
    </div>
  )
}
