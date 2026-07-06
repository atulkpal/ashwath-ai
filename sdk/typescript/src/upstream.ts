import type { ModelCatalogEntry } from "./catalog"
import { modelCatalog } from "./catalog"

const DEFAULT_INDEX_URL = "https://raw.githubusercontent.com/atulkpal/ashwath-ai/main/models/index.json"
const CACHE_KEY = "ashwath_model_index"

interface ModelIndexEntry {
  id: string
  name: string
  provider: string
  description: string
  sizeBytes: number
  parameters: string
  tags: string[]
  minRamGB: number
  recommendedRamGB: number
  capabilities: string[]
  speedClass: "fast" | "medium" | "slow"
  sources: string[]
  filename: string
}

interface ModelIndex {
  version: number
  updated: string
  models: ModelIndexEntry[]
}

function entryToCatalog(e: ModelIndexEntry): ModelCatalogEntry {
  return {
    id: e.id,
    name: e.name,
    provider: e.provider,
    description: e.description,
    sizeBytes: e.sizeBytes,
    parameters: e.parameters,
    tags: e.tags,
    minRamGB: e.minRamGB,
    recommendedRamGB: e.recommendedRamGB,
    capabilities: e.capabilities,
    speedClass: e.speedClass,
    downloadUrl: e.sources[0] || "",
    filename: e.filename,
  }
}

export async function fetchModelIndex(url = DEFAULT_INDEX_URL): Promise<ModelIndexEntry[]> {
  const response = await fetch(url, {
    headers: { "User-Agent": "AshwathAI/0.1" },
  })
  if (!response.ok) {
    throw new Error(`Failed to fetch model index: ${response.status}`)
  }
  const index: ModelIndex = await response.json()
  return index.models || []
}

export function getCachedIndex(): ModelCatalogEntry[] | null {
  try {
    const raw = localStorage.getItem(CACHE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw) as ModelIndexEntry[]
    return parsed.map(entryToCatalog)
  } catch {
    return null
  }
}

export function setCachedIndex(models: ModelIndexEntry[]): void {
  try {
    localStorage.setItem(CACHE_KEY, JSON.stringify(models))
  } catch {
    // localStorage full or unavailable — ignore
  }
}

export async function loadCatalog(): Promise<ModelCatalogEntry[]> {
  try {
    const remoteModels = await fetchModelIndex()
    setCachedIndex(remoteModels)
    return remoteModels.map(entryToCatalog)
  } catch {
    const cached = getCachedIndex()
    if (cached) return cached
    return [...modelCatalog]
  }
}
