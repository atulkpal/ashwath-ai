import { AlertTriangle, X } from "lucide-react"

type DeleteConfirmDialogProps = {
  title: string
  onConfirm: () => void
  onCancel: () => void
}

export function DeleteConfirmDialog({ title, onConfirm, onCancel }: DeleteConfirmDialogProps) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm" onClick={onCancel}>
      <div
        className="w-full max-w-sm rounded-xl border border-[#27272a] bg-[#121212] p-6 shadow-2xl"
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-label="Delete confirmation"
      >
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className="size-8 rounded-lg bg-[#ef4444]/10 flex items-center justify-center">
              <AlertTriangle className="size-4 text-[#ef4444]" />
            </div>
            <h2 className="text-sm font-semibold text-white">Delete Conversation</h2>
          </div>
          <button
            type="button"
            onClick={onCancel}
            className="flex items-center justify-center size-7 rounded-md text-[#a1a1a1] hover:text-white transition-colors"
            aria-label="Cancel"
          >
            <X className="size-4" />
          </button>
        </div>
        <p className="text-sm text-[#a1a1a1] mb-6 leading-relaxed">
          Are you sure you want to delete <span className="font-semibold text-white">"{title}"</span>? This action cannot be undone.
        </p>
        <div className="flex justify-end gap-2">
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 text-sm text-[#a1a1a1] hover:text-white border border-[#27272a] rounded-lg transition-colors duration-150"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={onConfirm}
            className="px-4 py-2 text-sm font-semibold text-[#ef4444] bg-[#ef4444]/10 border border-[#ef4444]/20 rounded-lg hover:bg-[#ef4444]/20 transition-colors duration-150"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  )
}
