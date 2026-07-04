import { useEffect, useState } from "react";
import { useEngine, type RuntimeConnectionStateValue } from "@/engine";

export function StatusBar() {
  const { client } = useEngine();
  const [connectionState, setConnectionState] = useState<RuntimeConnectionStateValue>("connecting");
  const [runtimeVersion, setRuntimeVersion] = useState("Checking...");

  useEffect(() => {
    let active = true;

    const refreshStatus = async () => {
      try {
        const status = await client.getRuntimeConnectionStatus();
        const version = await client.getRuntimeVersion();

        if (!active) {
          return;
        }

        setConnectionState(status.connected ? "connected" : "disconnected");
        setRuntimeVersion(version.version);
      } catch {
        if (active) {
          setConnectionState("disconnected");
          setRuntimeVersion("offline");
        }
      }
    };

    void refreshStatus();
    const timer = window.setInterval(() => {
      void refreshStatus();
    }, 5000);

    return () => {
      active = false;
      window.clearInterval(timer);
    };
  }, [client]);

  const isConnected = connectionState === "connected";

  return (
    <footer className="flex h-8 items-center border-t border-[var(--sn-border)] bg-[var(--sn-base)] px-6 text-xs text-[var(--sn-text-secondary)]">
      <div className="flex w-full items-center justify-between">
        <span className="tracking-wide">
          {isConnected ? "Runtime Connected" : "Runtime Offline"}
        </span>
        <span className="flex items-center gap-2">
          <span className={`inline-block size-2 rounded-full ${isConnected ? "bg-emerald-500" : "bg-rose-500"}`} />
          <span>{runtimeVersion}</span>
        </span>
      </div>
    </footer>
  )
}