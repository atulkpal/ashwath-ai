export interface DeviceSpec {
  ramGB: number
  cpuCores: number
  hasGPU: boolean
  gpuVendor: string
}

export async function detectDevice(): Promise<DeviceSpec> {
  const ramGB = detectRAM()
  const cpuCores = detectCPUCores()
  const gpuInfo = await detectGPU()
  return {
    ramGB,
    cpuCores,
    hasGPU: gpuInfo.hasGPU,
    gpuVendor: gpuInfo.vendor,
  }
}

function detectRAM(): number {
  if ("deviceMemory" in navigator) {
    return (navigator as Navigator & { deviceMemory: number }).deviceMemory
  }
  return 8
}

function detectCPUCores(): number {
  if ("hardwareConcurrency" in navigator) {
    return navigator.hardwareConcurrency
  }
  return 4
}

async function detectGPU(): Promise<{ hasGPU: boolean; vendor: string }> {
  if ("gpu" in navigator) {
    const adapter = await (navigator.gpu as unknown as GPU).requestAdapter()
    if (adapter) {
      const info = adapter.info
      return {
        hasGPU: true,
        vendor: info.vendor || "WebGPU",
      }
    }
  }
  return { hasGPU: false, vendor: "" }
}
