export interface RuntimeHealthSnapshot {
  ok: boolean;
  status: "unknown" | "healthy" | "degraded" | "unhealthy";
  checkedAt: string;
  latencyMs?: number;
  details?: Record<string, unknown>;
}

export interface RuntimeHealth {
  check(): Promise<RuntimeHealthSnapshot>;
  getSnapshot(): RuntimeHealthSnapshot;
}

export class RuntimeHealthImpl implements RuntimeHealth {
  private snapshot: RuntimeHealthSnapshot = {
    ok: false,
    status: "unknown",
    checkedAt: new Date().toISOString(),
  };

  async check(): Promise<RuntimeHealthSnapshot> {
    this.snapshot = {
      ok: false,
      status: "unknown",
      checkedAt: new Date().toISOString(),
      details: {
        source: "runtime-health-check",
      },
    };

    return this.snapshot;
  }

  getSnapshot(): RuntimeHealthSnapshot {
    return this.snapshot;
  }
}
