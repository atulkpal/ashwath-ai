import { EngineClient } from "./EngineClient";
import { NotImplementedError } from "./errors";
import { createInitialEngineStatus, type EngineRuntimeStatus } from "./status";
import { createRuntimeConfiguration, type RuntimeConfiguration } from "./runtime/RuntimeConfiguration";
import type {
  RuntimeApi,
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
} from "./runtime/RuntimeApi";
import { RuntimeTransportImpl } from "./runtime/RuntimeTransportImpl";
import type { RuntimeTransport } from "./runtime/RuntimeTransport";
import type { EngineRequestOptions, GenerateRequest, GenerateResponse, EngineStreamEvent } from "./types";

export interface RuntimeClientOptions {
  endpoint?: string;
  transport?: RuntimeTransport;
  configuration?: RuntimeConfiguration;
}

export class RuntimeClient extends EngineClient {
  readonly endpoint: string;
  readonly runtimeApi: RuntimeApi;

  constructor(options: RuntimeClientOptions = {}) {
    super();
    this.endpoint = options.endpoint ?? options.configuration?.endpoint ?? "http://127.0.0.1:8080";
    const configuration = options.configuration ?? createRuntimeConfiguration({ endpoint: this.endpoint });
    this.runtimeApi = options.transport ?? new RuntimeTransportImpl(configuration);
  }

  override async getStatus() {
    const status = await this.getRuntimeStatus({});
    const runtimeStatus: EngineRuntimeStatus = status.connected ? "ready" : "idle";

    return {
      ...createInitialEngineStatus(),
      runtimeStatus,
      connected: status.connected,
      lastUpdatedAt: new Date().toISOString(),
    };
  }

  override async generate(request: GenerateRequest, _options?: EngineRequestOptions): Promise<GenerateResponse> {
    const response = await this.runtimeApi.generate({
      prompt: request.prompt,
      sessionId: request.sessionId,
      modelId: request.modelId,
      stream: request.stream,
      metadata: request.metadata,
    });

    return response;
  }

  override async streamGenerate(
    _request: GenerateRequest,
    _onEvent: (event: EngineStreamEvent) => void,
    _options?: EngineRequestOptions,
  ): Promise<GenerateResponse> {
    throw new NotImplementedError("Runtime streaming is not implemented yet.");
  }

  override async createSession(): Promise<{ sessionId: string }> {
    throw new NotImplementedError("Runtime session creation is not implemented yet.");
  }

  override async getRuntimeConnectionStatus(): Promise<RuntimeStatusResponse> {
    return this.getRuntimeStatus({});
  }

  override async getRuntimeVersion(): Promise<RuntimeVersionResponse> {
    return this.getRuntimeVersionValue({});
  }

  override async shutdown(): Promise<void> {
    await this.runtimeApi.shutdown();
  }

  async initializeRuntime(request?: RuntimeInitializeRequest): Promise<RuntimeInitializeResponse> {
    return this.runtimeApi.initialize(request);
  }

  async shutdownRuntime(request?: RuntimeShutdownRequest): Promise<RuntimeShutdownResponse> {
    return this.runtimeApi.shutdown(request);
  }

  async getRuntimeHealth(request?: RuntimeHealthRequest): Promise<RuntimeHealthResponse> {
    return this.runtimeApi.health(request);
  }

  async getRuntimeStatus(request?: RuntimeStatusRequest): Promise<RuntimeStatusResponse> {
    return this.runtimeApi.status(request);
  }

  async getRuntimeVersionValue(request?: RuntimeVersionRequest): Promise<RuntimeVersionResponse> {
    return this.runtimeApi.version(request);
  }

  async generateRuntime(request: RuntimeGenerateRequest): Promise<RuntimeGenerateResponse> {
    return this.runtimeApi.generate(request);
  }

  async cancelRuntime(request: RuntimeCancelRequest): Promise<RuntimeCancelResponse> {
    return this.runtimeApi.cancel(request);
  }

  async listRuntimeModels(request?: RuntimeListModelsRequest): Promise<RuntimeListModelsResponse> {
    return this.runtimeApi.listModels(request);
  }

  async installRuntimeModel(request: RuntimeInstallModelRequest): Promise<RuntimeInstallModelResponse> {
    return this.runtimeApi.installModel(request);
  }

  async getRuntimeDeviceInfo(request?: RuntimeDeviceInfoRequest): Promise<RuntimeDeviceInfoResponse> {
    return this.runtimeApi.getDeviceInfo(request);
  }

  async queryRuntimeKnowledge(request: RuntimeKnowledgeRequest): Promise<RuntimeKnowledgeResponse> {
    return this.runtimeApi.knowledge(request);
  }

  async listRuntimePlugins(request?: RuntimePluginsRequest): Promise<RuntimePluginsResponse> {
    return this.runtimeApi.plugins(request);
  }

  async submitRuntimeTelemetry(request: RuntimeTelemetryRequest): Promise<RuntimeTelemetryResponse> {
    return this.runtimeApi.telemetry(request);
  }
}
