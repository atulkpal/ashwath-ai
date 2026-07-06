import { ModelPanel } from "@/features/models/ModelPanel"

export function LibraryPage() {
  return (
    <div className="flex-1 overflow-y-auto sn-scrollbar p-6">
      <div className="max-w-[1000px] mx-auto">
        <h1 className="text-lg font-semibold tracking-tight mb-6">Model Library</h1>
        <ModelPanel />
      </div>
    </div>
  )
}
