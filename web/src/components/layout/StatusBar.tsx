export function StatusBar() {
  return (
    <footer className="flex h-8 items-center border-t border-[var(--sn-border)] bg-[var(--sn-base)] px-6 text-xs text-[var(--sn-text-secondary)]">
      <div className="flex w-full items-center justify-between">
        <span className="tracking-wide">Ready</span>
      </div>
    </footer>
  )
}