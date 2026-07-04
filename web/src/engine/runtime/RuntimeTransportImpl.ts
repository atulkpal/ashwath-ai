import { NotImplementedError } from "../errors";
import { RuntimeConnectionImpl } from "./RuntimeConnection";
import { RuntimeHealthImpl } from "./RuntimeHealth";
import type { RuntimeConfiguration } from "./RuntimeConfiguration";
import type {
  RuntimeCancelRequest,
  RuntimeCancelResponse,
  RuntimeDeviceInfoRequest,
  RuntimeDeviceInfoResponse,
  RuntimeGenerateRequest,
  RuntimeGenerateResponse,
  RuntimeHealthRequest,
  RuntimeHealthResponse,
  RuntimeInitializeRequest,
  RuntimeInitializeResponse,
  RuntimeInstallModelRequest,
  RuntimeInstallModelResponse,
  RuntimeKnowledgeRequest,
  RuntimeKnowledgeResponse,
  RuntimeListModelsRequest,
  RuntimeListModelsResponse,
  RuntimePluginsRequest,
  RuntimePluginsResponse,
  RuntimeShutdownRequest,
  RuntimeShutdownResponse,
  RuntimeStatusRequest,
  RuntimeStatusResponse,
  RuntimeTelemetryRequest,
  RuntimeTelemetryResponse,
  RuntimeVersionRequest,
  RuntimeVersionResponse,
} from "./RuntimeApi";
import type { RuntimeTransport } from "./RuntimeTransport";

export class RuntimeTransportImpl implements RuntimeTransport {
  readonly configuration: RuntimeConfiguration;
  readonly connection: RuntimeConnectionImpl;
  readonly healthMonitor: RuntimeHealthImpl;

  constructor(configuration: RuntimeConfiguration) {
    this.configuration = configuration;
    this.connection = new RuntimeConnectionImpl(configuration.endpoint);
    this.healthMonitor = new RuntimeHealthImpl();
  }

  async initialize(_request?: RuntimeInitializeRequest): Promise<RuntimeInitializeResponse> {
    this.connection.markConnecting();

    const status = await this.fetchJson<RuntimeStatusResponse>("/status");
    const version = await this.fetchJson<RuntimeVersionResponse>("/version");
    const connected = Boolean(status?.connected || version);

    if (connected) {
      this.connection.markConnected();
    } else {
      this.connection.markDisconnected();
    }

    return {
      initialized: connected,
      endpoint: this.configuration.endpoint,
      startedAt: new Date().toISOString(),
    };
  }

  async shutdown(_request?: RuntimeShutdownRequest): Promise<RuntimeShutdownResponse> {
    await this.connection.disconnect();

    return {
      shutdown: true,
      stoppedAt: new Date().toISOString(),
    };
  }

  async health(_request?: RuntimeHealthRequest): Promise<RuntimeHealthResponse> {
    const payload = await this.fetchJson<RuntimeHealthResponse>("/health");

    if (payload) {
      this.connection.markConnected();
      return payload;
    }

    this.connection.markDisconnected();
    return {
      ok: false,
      status: "unhealthy",
      checkedAt: new Date().toISOString(),
      details: { error: "runtime-not-reachable" },
    };
  }

  async status(_request?: RuntimeStatusRequest): Promise<RuntimeStatusResponse> {
    const payload = await this.fetchJson<RuntimeStatusResponse>("/status");

    if (payload?.connected) {
      this.connection.markConnected();
      return {
        ...payload,
        endpoint: payload.endpoint ?? this.configuration.endpoint,
        checkedAt: payload.checkedAt ?? new Date().toISOString(),
      };
    }

    this.connection.markDisconnected();
    return {
      connected: false,
      state: "disconnected",
      healthy: false,
      endpoint: this.configuration.endpoint,
      checkedAt: new Date().toISOString(),
      version: undefined,
    };
  }

  async version(_request?: RuntimeVersionRequest): Promise<RuntimeVersionResponse> {
    const payload = await this.fetchText("/version");

    if (payload) {
      this.connection.markConnected();
      return {
        version: payload,
        checkedAt: new Date().toISOString(),
      };
    }

    this.connection.markDisconnected();
    return {
      version: "unknown",
      checkedAt: new Date().toISOString(),
    };
  }

  async generate(_request: RuntimeGenerateRequest): Promise<RuntimeGenerateResponse> {
    throw new NotImplementedError("Runtime generation is not implemented yet.");
  }

  async cancel(_request: RuntimeCancelRequest): Promise<RuntimeCancelResponse> {
    throw new NotImplementedError("Runtime generation cancellation is not implemented yet.");
  }

  async listModels(_request?: RuntimeListModelsRequest): Promise<RuntimeListModelsResponse> {
    throw new NotImplementedError("Runtime model listing is not implemented yet.");
  }

  async installModel(_request: RuntimeInstallModelRequest): Promise<RuntimeInstallModelResponse> {
    throw new NotImplementedError("Runtime model installation is not implemented yet.");
  }

  async getDeviceInfo(_request?: RuntimeDeviceInfoRequest): Promise<RuntimeDeviceInfoResponse> {
    throw new NotImplementedError("Runtime device info is not implemented yet.");
  }

  async knowledge(_request: RuntimeKnowledgeRequest): Promise<RuntimeKnowledgeResponse> {
    throw new NotImplementedError("Runtime knowledge lookup is not implemented yet.");
  }

  async plugins(_request?: RuntimePluginsRequest): Promise<RuntimePluginsResponse> {
    throw new NotImplementedError("Runtime plugin listing is not implemented yet.");
  }

  async telemetry(_request: RuntimeTelemetryRequest): Promise<RuntimeTelemetryResponse> {
    throw new NotImplementedError("Runtime telemetry submission is not implemented yet.");
  }

  private buildUrl(path: string): string {
    return new URL(path, `${this.configuration.endpoint.replace(/\/$/, "")}/`).toString();
  }

  private async fetchJson<T>(path: string): Promise<T | null> {
    try {
      const response = await fetch(this.buildUrl(path), {
        headers: { Accept: "application/json" },
      });

      if (!response.ok) {
        return null;
      }

      const text = await response.text();
      if (!text) {
        return null;
      }

      return JSON.parse(text) as T;
    } catch {
      return null;
    }
  }

  private async fetchText(path: string): Promise<string | null> {
    try {
      const response = await fetch(this.buildUrl(path), {
        headers: { Accept: "text/plain" },
      });

      if (!response.ok) {
        return null;
      }

      const text = await response.text();
      return text || null;
    } catch {
      return null;
    }
  }
}
