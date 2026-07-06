import { useState } from "react"

export function SettingsPage() {
  const [theme] = useState("dark")
  const [autoConnect] = useState(true)
  const [enginePort, setEnginePort] = useState("50052")

  return (
    <div className="flex-1 overflow-y-auto sn-scrollbar p-6">
      <div className="max-w-[800px] mx-auto">
        <h1 className="text-lg font-semibold tracking-tight mb-6">Settings</h1>

        <div className="space-y-6">
          <section>
            <h2 className="text-sm font-medium mb-3">Engine Connection</h2>
            <div className="rounded-lg border border-[#27272a] bg-[#121212] p-4 space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm">Engine Port</p>
                  <p className="text-xs text-[#6b6b6b]">Local gRPC server port</p>
                </div>
                <input
                  type="text"
                  value={enginePort}
                  onChange={(e) => setEnginePort(e.target.value)}
                  className="w-24 rounded-md border border-[#27272a] bg-black px-3 py-1.5 text-sm text-white text-right focus:outline-none focus:border-[#00f0ff]/50"
                />
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm">Auto-connect</p>
                  <p className="text-xs text-[#6b6b6b]">Connect to engine on startup</p>
                </div>
                <div className={`size-4 rounded border ${autoConnect ? "bg-[#00f0ff] border-[#00f0ff]" : "border-[#27272a]"} flex items-center justify-center transition-colors`}>
                  {autoConnect && <span className="text-black text-[10px] font-bold">✓</span>}
                </div>
              </div>
            </div>
          </section>

          <section>
            <h2 className="text-sm font-medium mb-3">Appearance</h2>
            <div className="rounded-lg border border-[#27272a] bg-[#121212] p-4 space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm">Theme</p>
                  <p className="text-xs text-[#6b6b6b]">Color scheme</p>
                </div>
                <span className="text-sm text-[#a1a1a1] capitalize">{theme}</span>
              </div>
            </div>
          </section>

          <section>
            <h2 className="text-sm font-medium mb-3">About</h2>
            <div className="rounded-lg border border-[#27272a] bg-[#121212] p-4 space-y-2 text-sm text-[#a1a1a1]">
              <p>Ashwath AI Web Client</p>
              <p className="text-xs text-[#6b6b6b]">Version 0.2.0 — Epoch 1</p>
            </div>
          </section>
        </div>
      </div>
    </div>
  )
}
