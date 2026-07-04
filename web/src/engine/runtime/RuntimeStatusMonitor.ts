import type { RuntimeConnection } from "./RuntimeConnection";
import type { RuntimeHealth } from "./RuntimeHealth";

export interface RuntimeStatusSnapshot {
  connected: boolean;
  state: string;
  healthy: boolean;
  endpoint: string;
  checkedAt: string;
}

export interface RuntimeStatusMonitor {
  getSnapshot(): RuntimeStatusSnapshot;
  refresh(): Promise<RuntimeStatusSnapshot>;
}

export class RuntimeStatusMonitorImpl implements RuntimeStatusMonitor {
  private readonly connection: RuntimeConnection;
  private readonly health: RuntimeHealth;

  constructor(connection: RuntimeConnection, health: RuntimeHealth) {
    this.connection = connection;
    this.health = health;
  }

  async refresh(): Promise<RuntimeStatusSnapshot> {
    const healthSnapshot = await this.health.check();

    return {
      connected: this.connection.getState() === "connected",
      state: this.connection.getState(),
      healthy: healthSnapshot.ok,
      endpoint: this.connection.endpoint,
      checkedAt: healthSnapshot.checkedAt,
    };
  }

  getSnapshot(): RuntimeStatusSnapshot {
    return {
      connected: this.connection.getState() === "connected",
      state: this.connection.getState(),
      healthy: this.health.getSnapshot().ok,
      endpoint: this.connection.endpoint,
      checkedAt: this.health.getSnapshot().checkedAt,
    };
  }
}
