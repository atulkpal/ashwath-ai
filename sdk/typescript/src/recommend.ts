import type { DeviceSpec } from "./discover"
import type { ModelCatalogEntry } from "./catalog"

export interface ScoredModel extends ModelCatalogEntry {
  score: number
  recommended: boolean
  reason: string
}

export function scoreModels(device: DeviceSpec, models: ModelCatalogEntry[]): ScoredModel[] {
  return models
    .map((m) => scoreModel(device, m))
    .sort((a, b) => b.score - a.score)
}

function scoreModel(device: DeviceSpec, m: ModelCatalogEntry): ScoredModel {
  if (device.ramGB < m.minRamGB) {
    const shortfall = m.minRamGB - device.ramGB
    if (shortfall > 2) {
      return { ...m, score: 0, recommended: false, reason: `needs ${m.minRamGB} GB RAM` }
    }
    return { ...m, score: 30, recommended: false, reason: "limited by device RAM" }
  }

  let score: number

  if (device.ramGB >= m.recommendedRamGB) {
    score = 90
  } else {
    const ratio = device.ramGB / m.recommendedRamGB
    score = 50 + Math.min(ratio, 1) * 40
  }

  if (device.hasGPU) {
    score += 10
  }

  if (device.ramGB >= m.recommendedRamGB + 4) {
    score -= 5
  }

  if (device.cpuCores >= 8) {
    score += 3
  }

  score = Math.max(0, Math.min(100, score))

  let reason: string
  if (score >= 90) {
    reason = "excellent match for your device"
  } else if (score >= 80) {
    reason = "great match for your device"
  } else if (score >= 60) {
    reason = "compatible — may have reduced performance"
  } else if (score >= 30) {
    reason = "limited by device RAM"
  } else {
    reason = "not recommended for this device"
  }

  return {
    ...m,
    score: Math.round(score),
    recommended: score >= 80,
    reason,
  }
}

export function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`
}

export function getModelsByCapability(models: ScoredModel[], capability: string): ScoredModel[] {
  return models.filter((m) => m.capabilities.includes(capability))
}

export function getTopRecommendations(models: ScoredModel[], count = 3): ScoredModel[] {
  return models.filter((m) => m.recommended).slice(0, count)
}
