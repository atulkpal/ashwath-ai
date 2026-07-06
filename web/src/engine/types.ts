import type { EngineStatusSnapshot } from "./status";
import type { RuntimeStatusResponse, RuntimeVersionResponse } from "./runtime/RuntimeApi";

export type EngineMessageRole = "user" | "assistant" | "system";

export interface EngineMessage {
  id: string;
  role: EngineMessageRole;
  content: string;
  createdAt: string;
}

export interface GenerateRequest {
  prompt: string;
  sessionId?: string;
  modelId?: string;
  stream?: boolean;
  metadata?: Record<string, unknown>;
}

export interface GenerateResponse {
  id: string;
  text: string;
  sessionId?: string;
  metadata?: Record<string, unknown>;
}

export interface EngineStreamEvent {
  type: "token" | "delta" | "done" | "error";
  text?: string;
  error?: {
    message: string;
    code?: string;
  };
  metadata?: Record<string, unknown>;
}

export interface EngineRequestOptions {
  abortSignal?: AbortSignal;
  timeoutMs?: number;
}

export interface EngineClientLike {
  getStatus(): Promise<EngineStatusSnapshot>;
  generate(request: GenerateRequest, options?: EngineRequestOptions): Promise<GenerateResponse>;
  streamGenerate(
    request: GenerateRequest,
    onEvent: (event: EngineStreamEvent) => void,
    options?: EngineRequestOptions,
  ): Promise<GenerateResponse>;
  createSession(): Promise<{ sessionId: string }>;
  getRuntimeConnectionStatus(): Promise<RuntimeStatusResponse>;
  getRuntimeVersion(): Promise<RuntimeVersionResponse>;
  listRuntimeModels(request?: { includeInstalled?: boolean }): Promise<import("./runtime/RuntimeApi").RuntimeListModelsResponse>;
  installRuntimeModel(request: { modelId: string }): Promise<import("./runtime/RuntimeApi").RuntimeInstallModelResponse>;
  shutdown(): Promise<void>;
}
