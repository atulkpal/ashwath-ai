export interface GenerateRequest {
  model?: string
  prompt: string
  maxTokens?: number
  temperature?: number
  topK?: number
  topP?: number
}

export interface GenerateResponse {
  text: string
  tokensUsed: number
  done: boolean
}

export interface ModelInfo {
  id: string
  name: string
  provider: string
  sizeBytes: number
  parameters: string
  tags: string[]
  installed: boolean
}

export interface ModelList {
  models: ModelInfo[]
}

export interface InstallRequest {
  modelId: string
}

export interface InstallResponse {
  started: boolean
  message: string
}

export interface RemoveRequest {
  modelId: string
}

export interface RemoveResponse {
  success: boolean
  message: string
}

export interface DeviceInfo {
  ramGb: number
  cpuCores: number
  hasNpu: boolean
  hasGpu: boolean
  gpuVendor: string
  os: string
  arch: string
}
