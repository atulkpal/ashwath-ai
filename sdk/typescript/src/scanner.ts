export interface LocalModel {
  id: string
  name: string
  sizeBytes: number
  path: string
  source: "ollama" | "gguf"
}

export async function probeEngine(endpoint: string): Promise<boolean> {
  try {
    const response = await fetch(`${endpoint}/ashwath.AshwathEngine/GetDeviceInfo`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: "{}",
    })
    return response.ok
  } catch {
    return false
  }
}

export async function probeOllama(endpoint = "http://127.0.0.1:11434"): Promise<LocalModel[]> {
  try {
    const response = await fetch(`${endpoint}/api/tags`)
    if (!response.ok) return []
    const data = (await response.json()) as { models: Array<{ name: string; size: number; digest: string }> }
    return (data.models || []).map((m) => ({
      id: `ollama:${m.name}`,
      name: m.name,
      sizeBytes: m.size,
      path: m.digest,
      source: "ollama" as const,
    }))
  } catch {
    return []
  }
}
