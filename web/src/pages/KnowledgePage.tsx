import { Database } from "lucide-react"

export function KnowledgePage() {
  return (
    <div className="flex-1 overflow-y-auto sn-scrollbar p-6">
      <div className="max-w-[800px] mx-auto">
        <h1 className="text-lg font-semibold tracking-tight mb-2">Knowledge Base</h1>
        <p className="text-sm text-[#a1a1a1] mb-8">
          Upload documents to build a local knowledge base. Your AI will use these
          as context when answering questions.
        </p>
        <div className="flex flex-col items-center justify-center min-h-[300px] rounded-xl border border-dashed border-[#27272a] bg-[#121212] text-center p-8">
          <Database className="size-10 text-[#6b6b6b] mb-4" />
          <p className="text-sm font-medium text-[#a1a1a1] mb-1">No documents yet</p>
          <p className="text-xs text-[#6b6b6b] max-w-sm">
            Upload PDFs, text files, or markdown documents. Coming in Epoch 2.
          </p>
        </div>
      </div>
    </div>
  )
}
