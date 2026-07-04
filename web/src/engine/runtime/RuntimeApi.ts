import type { GenerateRequest, GenerateResponse } from "../types";

export interface RuntimeInitializeRequest {
  endpoint?: string;
  timeoutMs?: number;
}

export interface RuntimeInitializeResponse {
  initialized: boolean;
  endpoint: string;
  startedAt: string;
}

export interface RuntimeShutdownRequest {
  reason?: string;
}

export interface RuntimeShutdownResponse {
  shutdown: boolean;
  stoppedAt: string;
}

export interface RuntimeHealthRequest {
  includeDetails?: boolean;
}

export interface RuntimeHealthResponse {
  ok: boolean;
  status: "healthy" | "degraded" | "unhealthy";
  checkedAt: string;
  details?: Record<string, unknown>;
}

export interface RuntimeStatusRequest {
  includeDetails?: boolean;
}

export type RuntimeConnectionStateValue = "connecting" | "connected" | "disconnected" | "error";

export interface RuntimeStatusResponse {
  connected: boolean;
  state: RuntimeConnectionStateValue;
  healthy: boolean;
  endpoint: string;
  checkedAt: string;
  version?: string;
}

export interface RuntimeVersionRequest {
  includeDetails?: boolean;
}

export interface RuntimeVersionResponse {
  version: string;
  checkedAt: string;
}

export interface RuntimeGenerateRequest extends GenerateRequest {
  stream?: boolean;
}

export interface RuntimeGenerateResponse extends GenerateResponse {}

export interface RuntimeCancelRequest {
  requestId: string;
  reason?: string;
}

export interface RuntimeCancelResponse {
  cancelled: boolean;
  requestId: string;
  cancelledAt: string;
}

export interface RuntimeListModelsRequest {
  includeInstalled?: boolean;
}

export interface RuntimeModelSummary {
  id: string;
  name: string;
  installed: boolean;
  sizeBytes?: number;
}

export interface RuntimeListModelsResponse {
  models: RuntimeModelSummary[];
}

export interface RuntimeInstallModelRequest {
  modelId: string;
}

export interface RuntimeInstallModelResponse {
  modelId: string;
  status: "queued" | "downloading" | "installed" | "failed";
  installedAt?: string;
}

export interface RuntimeDeviceInfoRequest {
  includeCapabilities?: boolean;
}

export interface RuntimeDeviceInfoResponse {
  platform: string;
  engineVersion: string;
  capabilities: string[];
}

export interface RuntimeKnowledgeRequest {
  query: string;
  limit?: number;
}

export interface RuntimeKnowledgeItem {
  id: string;
  title: string;
  summary?: string;
}

export interface RuntimeKnowledgeResponse {
  items: RuntimeKnowledgeItem[];
}

export interface RuntimePluginsRequest {
  includeDisabled?: boolean;
}

export interface RuntimePluginSummary {
  id: string;
  name: string;
  enabled: boolean;
}

export interface RuntimePluginsResponse {
  plugins: RuntimePluginSummary[];
}

export interface RuntimeTelemetryRequest {
  event: string;
  payload?: Record<string, unknown>;
}

export interface RuntimeTelemetryResponse {
  accepted: boolean;
  recordedAt: string;
}

export interface RuntimeApi {
  /** Initialize the runtime and prepare it for future operations. */
  initialize(request?: RuntimeInitializeRequest): Promise<RuntimeInitializeResponse>;

  /** Shut down the runtime and release any local resources. */
  shutdown(request?: RuntimeShutdownRequest): Promise<RuntimeShutdownResponse>;

  /** Report the current runtime health signal. */
  health(request?: RuntimeHealthRequest): Promise<RuntimeHealthResponse>;

  /** Report connection and status information for the runtime. */
  status(request?: RuntimeStatusRequest): Promise<RuntimeStatusResponse>;

  /** Report the runtime version for handshake and diagnostics. */
  version(request?: RuntimeVersionRequest): Promise<RuntimeVersionResponse>;

  /** Generate a response using the runtime. */
  generate(request: RuntimeGenerateRequest): Promise<RuntimeGenerateResponse>;

  /** Cancel an in-flight runtime generation request. */
  cancel(request: RuntimeCancelRequest): Promise<RuntimeCancelResponse>;

  /** List available or installed models from the runtime. */
  listModels(request?: RuntimeListModelsRequest): Promise<RuntimeListModelsResponse>;

  /** Install a model through the runtime. */
  installModel(request: RuntimeInstallModelRequest): Promise<RuntimeInstallModelResponse>;

  /** Retrieve device information relevant to runtime execution. */
  getDeviceInfo(request?: RuntimeDeviceInfoRequest): Promise<RuntimeDeviceInfoResponse>;

  /** Query knowledge sources exposed by the runtime. */
  knowledge(request: RuntimeKnowledgeRequest): Promise<RuntimeKnowledgeResponse>;

  /** List enabled or disabled plugins exposed by the runtime. */
  plugins(request?: RuntimePluginsRequest): Promise<RuntimePluginsResponse>;

  /** Record telemetry to the runtime for observability. */
  telemetry(request: RuntimeTelemetryRequest): Promise<RuntimeTelemetryResponse>;
}
