
type ModelSelectorProps = {
  model?: string
}

export function ModelSelector({ model = "Llama 3 8B" }: ModelSelectorProps) {
  return (
    <div className="flex items-center gap-2">
      <div className="size-2 rounded-full bg-[var(--sn-status-ready)]" />
      <span className="text-sm text-[var(--sn-text-primary)]">{model}</span>
    </div>
  )
}