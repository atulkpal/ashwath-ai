import { createEngineClient, type AshwathEngineClient } from "@ashwathai/sdk"
import type { RuntimeTransport } from "./RuntimeTransport"
import type { RuntimeConfiguration } from "./RuntimeConfiguration"
import { RuntimeConnectionImpl } from "./RuntimeConnection"
import { RuntimeHealthImpl } from "./RuntimeHealth"
import type {
  RuntimeInitializeResponse,
  RuntimeShutdownResponse,
  RuntimeHealthResponse,
  RuntimeStatusResponse,
  RuntimeVersionResponse,
  RuntimeGenerateRequest,
  RuntimeGenerateResponse,
  RuntimeCancelRequest,
  RuntimeCancelResponse,
  RuntimeListModelsResponse,
  RuntimeInstallModelRequest,
  RuntimeInstallModelResponse,
  RuntimeDeviceInfoResponse,
  RuntimeKnowledgeResponse,
  RuntimePluginsResponse,
  RuntimeTelemetryRequest,
  RuntimeTelemetryResponse,
} from "./RuntimeApi"

export class RuntimeTransportImpl implements RuntimeTransport {
  readonly configuration: RuntimeConfiguration
  readonly connection: RuntimeConnectionImpl
  readonly healthMonitor: RuntimeHealthImpl
  private readonly sdkClient: AshwathEngineClient

  constructor(configuration: RuntimeConfiguration) {
    this.configuration = configuration
    this.connection = new RuntimeConnectionImpl(configuration.endpoint)
    this.healthMonitor = new RuntimeHealthImpl()
    this.sdkClient = createEngineClient(configuration.endpoint)
  }

  async initialize(): Promise<RuntimeInitializeResponse> {
    this.connection.markConnecting()

    try {
      await this.sdkClient.getDeviceInfo()
      this.connection.markConnected()
      return {
        initialized: true,
        endpoint: this.configuration.endpoint,
        startedAt: new Date().toISOString(),
      }
    } catch {
      this.connection.markDisconnected()
      return {
        initialized: false,
        endpoint: this.configuration.endpoint,
        startedAt: new Date().toISOString(),
      }
    }
  }

  async shutdown(): Promise<RuntimeShutdownResponse> {
    await this.sdkClient.shutdown()
    return {
      shutdown: true,
      stoppedAt: new Date().toISOString(),
    }
  }

  async health(): Promise<RuntimeHealthResponse> {
    try {
      await this.sdkClient.getDeviceInfo()
      this.connection.markConnected()
      return {
        ok: true,
        status: "healthy",
        checkedAt: new Date().toISOString(),
      }
    } catch {
      this.connection.markDisconnected()
      return {
        ok: false,
        status: "unhealthy",
        checkedAt: new Date().toISOString(),
        details: { error: "runtime-not-reachable" },
      }
    }
  }

  async status(): Promise<RuntimeStatusResponse> {
    try {
      await this.sdkClient.getDeviceInfo()
      this.connection.markConnected()
      return {
        connected: true,
        state: "connected",
        healthy: true,
        endpoint: this.configuration.endpoint,
        checkedAt: new Date().toISOString(),
      }
    } catch {
      this.connection.markDisconnected()
      return {
        connected: false,
        state: "disconnected",
        healthy: false,
        endpoint: this.configuration.endpoint,
        checkedAt: new Date().toISOString(),
      }
    }
  }

  async version(): Promise<RuntimeVersionResponse> {
    return {
      version: "0.1.0",
      checkedAt: new Date().toISOString(),
    }
  }

  async generate(request: RuntimeGenerateRequest): Promise<RuntimeGenerateResponse> {
    let fullText = ""
    for await (const chunk of this.streamGenerate(request)) {
      fullText += chunk.text
      if (chunk.done) break
    }
    return {
      id: Date.now().toString(),
      text: fullText,
      sessionId: request.sessionId,
    }
  }

  async *streamGenerate(request: RuntimeGenerateRequest): AsyncIterable<RuntimeGenerateResponse> {
    const iterable = this.sdkClient.generate({
      model: request.modelId,
      prompt: request.prompt,
      maxTokens: request.metadata?.max_tokens as number | undefined,
      temperature: request.metadata?.temperature as number | undefined,
    })

    for await (const chunk of iterable) {
      yield {
        id: Date.now().toString(),
        text: chunk.text,
        done: chunk.done,
        tokensUsed: chunk.tokensUsed,
        sessionId: request.sessionId,
      }
    }
  }

  async cancel(_request: RuntimeCancelRequest): Promise<RuntimeCancelResponse> {
    return {
      cancelled: true,
      requestId: _request.requestId,
      cancelledAt: new Date().toISOString(),
    }
  }

  async listModels(): Promise<RuntimeListModelsResponse> {
    const modelList = await this.sdkClient.listModels()
    return {
      models: modelList.models.map((m) => ({
        id: m.id,
        name: m.name,
        installed: m.installed,
        sizeBytes: m.sizeBytes,
      })),
    }
  }

  async installModel(request: RuntimeInstallModelRequest): Promise<RuntimeInstallModelResponse> {
    const result = await this.sdkClient.installModel({ modelId: request.modelId })
    return {
      modelId: request.modelId,
      status: result.started ? "downloading" : "failed",
      installedAt: result.started ? new Date().toISOString() : undefined,
    }
  }

  async getDeviceInfo(): Promise<RuntimeDeviceInfoResponse> {
    const info = await this.sdkClient.getDeviceInfo()
    return {
      platform: info.os,
      engineVersion: "0.1.0",
      capabilities: [],
    }
  }

  async knowledge(_request: { query: string; limit?: number }): Promise<RuntimeKnowledgeResponse> {
    return { items: [] }
  }

  async plugins(): Promise<RuntimePluginsResponse> {
    return { plugins: [] }
  }

  async telemetry(_request: RuntimeTelemetryRequest): Promise<RuntimeTelemetryResponse> {
    return { accepted: true, recordedAt: new Date().toISOString() }
  }
}
