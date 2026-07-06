import type { EngineRequestOptions, EngineClientLike, GenerateRequest, GenerateResponse, EngineStreamEvent } from "./types";
import type { EngineStatusSnapshot } from "./status";
import type { RuntimeStatusResponse, RuntimeVersionResponse } from "./runtime/RuntimeApi";

export abstract class EngineClient implements EngineClientLike {
  abstract getStatus(): Promise<EngineStatusSnapshot>;

  abstract generate(request: GenerateRequest, options?: EngineRequestOptions): Promise<GenerateResponse>;

  abstract streamGenerate(
    request: GenerateRequest,
    onEvent: (event: EngineStreamEvent) => void,
    options?: EngineRequestOptions,
  ): Promise<GenerateResponse>;

  abstract createSession(): Promise<{ sessionId: string }>;

  abstract getRuntimeConnectionStatus(): Promise<RuntimeStatusResponse>;

  abstract getRuntimeVersion(): Promise<RuntimeVersionResponse>;

  abstract listRuntimeModels(request?: { includeInstalled?: boolean }): Promise<import("./runtime/RuntimeApi").RuntimeListModelsResponse>;

  abstract installRuntimeModel(request: { modelId: string }): Promise<import("./runtime/RuntimeApi").RuntimeInstallModelResponse>;

  abstract shutdown(): Promise<void>;
}
