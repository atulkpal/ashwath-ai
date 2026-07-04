export interface RuntimeConfiguration {
  endpoint: string;
  timeoutMs?: number;
  retryCount?: number;
  healthCheckIntervalMs?: number;
  startupTimeoutMs?: number;
}

export function createRuntimeConfiguration(overrides: Partial<RuntimeConfiguration> = {}): RuntimeConfiguration {
  return {
    endpoint: "http://127.0.0.1:8080",
    timeoutMs: 10000,
    retryCount: 0,
    healthCheckIntervalMs: 5000,
    startupTimeoutMs: 10000,
    ...overrides,
  };
}
