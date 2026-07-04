export type EngineRuntimeStatus =
  | "idle"
  | "starting"
  | "ready"
  | "busy"
  | "stopping"
  | "failed"
  | "restarting";

export interface EngineStatusSnapshot {
  runtimeStatus: EngineRuntimeStatus;
  connected: boolean;
  engineVersion: string;
  lastUpdatedAt: string;
  capabilities: {
    supportsStreaming: boolean;
    supportsSessions: boolean;
    supportsModels: boolean;
  };
}

export function createInitialEngineStatus(): EngineStatusSnapshot {
  return {
    runtimeStatus: "idle",
    connected: false,
    engineVersion: "unknown",
    lastUpdatedAt: new Date().toISOString(),
    capabilities: {
      supportsStreaming: true,
      supportsSessions: true,
      supportsModels: true,
    },
  };
}
