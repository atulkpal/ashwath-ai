import { useState, useEffect, useCallback } from "react"
import { useEngine } from "@/engine"

export interface ModelInfo {
  id: string
  name: string
  provider: string
  sizeBytes: number
  parameters: string
  installed: boolean
}

export function useModelBrowser() {
  const { client } = useEngine()
  const [models, setModels] = useState<ModelInfo[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const result = await client.listRuntimeModels()
      setModels(
        result.models.map((m) => ({
          id: m.id,
          name: m.name,
          provider: "",
          sizeBytes: m.sizeBytes ?? 0,
          parameters: "",
          installed: m.installed,
        }))
      )
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load models")
    } finally {
      setIsLoading(false)
    }
  }, [client])

  useEffect(() => {
    refresh()
  }, [refresh])

  const installModel = useCallback(async (modelId: string) => {
    await client.installRuntimeModel({ modelId })
    await refresh()
  }, [client, refresh])

  return { models, isLoading, error, refresh, installModel }
}
