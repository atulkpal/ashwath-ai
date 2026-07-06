import { useEffect, useState } from "react"
import { useEngine, type RuntimeConnectionStateValue } from "@/engine"

export function StatusBar() {
  const { client } = useEngine()
  const [connectionState, setConnectionState] = useState<RuntimeConnectionStateValue>("connecting")
  const [runtimeVersion, setRuntimeVersion] = useState("Checking...")

  useEffect(() => {
    let active = true

    const refreshStatus = async () => {
      try {
        const status = await client.getRuntimeConnectionStatus()
        const version = await client.getRuntimeVersion()

        if (!active) {
          return
        }

        setConnectionState(status.connected ? "connected" : "disconnected")
        setRuntimeVersion(version.version)
      } catch {
        if (active) {
          setConnectionState("disconnected")
          setRuntimeVersion("offline")
        }
      }
    }

    void refreshStatus()
    const timer = window.setInterval(() => {
      void refreshStatus()
    }, 5000)

    return () => {
      active = false
      window.clearInterval(timer)
    }
  }, [client])

  const isConnected = connectionState === "connected"

  return (
    <footer className="flex h-7 items-center border-t border-[#27272a] bg-black px-5 text-[10px] text-[#6b6b6b]">
      <div className="flex w-full items-center justify-between">
        <div className="flex items-center gap-3">
          <span className={`inline-block size-1.5 rounded-full ${isConnected ? "bg-[#22c55e]" : "bg-[#ef4444]"}`} />
          <span className="font-mono tracking-wide">
            {isConnected ? "Runtime Connected" : "Runtime Offline"}
          </span>
          <span className="text-[#27272a]">|</span>
          <span className="font-mono text-[#6b6b6b]">v{runtimeVersion}</span>
        </div>
        <span className="font-mono tracking-wider text-[#6b6b6b] uppercase">
          Secure Local Compute
        </span>
      </div>
    </footer>
  )
}
