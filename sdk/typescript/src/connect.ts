import type {
  GenerateRequest,
  GenerateResponse,
  ModelList,
  InstallRequest,
  InstallResponse,
  RemoveRequest,
  RemoveResponse,
  DeviceInfo,
} from "./pb/service"

export interface AshwathEngineClient {
  generate(request: GenerateRequest): AsyncIterable<GenerateResponse>
  listModels(): Promise<ModelList>
  installModel(request: InstallRequest): Promise<InstallResponse>
  removeModel(request: RemoveRequest): Promise<RemoveResponse>
  getDeviceInfo(): Promise<DeviceInfo>
  shutdown(): Promise<void>
}

export function createEngineClient(baseUrl: string): AshwathEngineClient {
  async function post<T>(method: string, body: unknown): Promise<T> {
    const response = await fetch(`${baseUrl}/ashwath.AshwathEngine/${method}`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      body: JSON.stringify(body),
    })
    if (!response.ok) {
      throw new Error(`${method} failed: ${response.status}`)
    }
    return response.json() as Promise<T>
  }

  return {
    async *generate(request: GenerateRequest): AsyncIterable<GenerateResponse> {
      const response = await fetch(`${baseUrl}/ashwath.AshwathEngine/Generate`, {
        method: "POST",
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        body: JSON.stringify(request),
      })
      if (!response.ok) {
        throw new Error(`generate failed: ${response.status}`)
      }
      const reader = response.body?.getReader()
      if (!reader) throw new Error("no response body")

      const decoder = new TextDecoder()
      let buffer = ""
      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split("\n")
        buffer = lines.pop() || ""
        for (const line of lines) {
          if (line.startsWith("data: ")) {
            const data = JSON.parse(line.slice(6))
            yield { text: data.text || "", tokensUsed: data.tokens_used || 0, done: data.done || false }
          }
        }
      }
    },

    listModels: () => post<ModelList>("ListModels", {}),
    installModel: (req: InstallRequest) => post<InstallResponse>("InstallModel", { model_id: req.modelId }),
    removeModel: (req: RemoveRequest) => post<RemoveResponse>("RemoveModel", { model_id: req.modelId }),
    getDeviceInfo: () => post<DeviceInfo>("GetDeviceInfo", {}),
    shutdown: async () => { await post<unknown>("Shutdown", {}) },
  }
}
