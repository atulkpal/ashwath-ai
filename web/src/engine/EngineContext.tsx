import { createContext, useContext, useMemo, useState } from "react";
import { RuntimeClient } from "./RuntimeClient";
import type { EngineClientLike } from "./types";
import { createInitialEngineStatus } from "./status";
import type { PropsWithChildren } from "react";

interface EngineContextValue {
  client: EngineClientLike;
  status: ReturnType<typeof createInitialEngineStatus>;
  setStatus: React.Dispatch<React.SetStateAction<ReturnType<typeof createInitialEngineStatus>>>;
}

const EngineContext = createContext<EngineContextValue | undefined>(undefined);

interface EngineProviderProps extends PropsWithChildren {
  client?: EngineClientLike;
}

export function EngineProvider({ children, client }: EngineProviderProps) {
  const [status, setStatus] = useState(() => createInitialEngineStatus());
  const resolvedClient = useMemo(() => client ?? new RuntimeClient(), [client]);

  const value = useMemo<EngineContextValue>(
    () => ({
      client: resolvedClient,
      status,
      setStatus,
    }),
    [resolvedClient, status],
  );

  return <EngineContext.Provider value={value}>{children}</EngineContext.Provider>;
}

export function useEngine() {
  const context = useContext(EngineContext);

  if (!context) {
    throw new Error("useEngine must be used within an EngineProvider");
  }

  return context;
}
