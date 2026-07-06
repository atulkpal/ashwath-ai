export interface ModelCatalogEntry {
  id: string
  name: string
  provider: string
  description: string
  sizeBytes: number
  parameters: string
  tags: string[]
  downloadUrl: string
  filename: string
  minRamGB: number
  recommendedRamGB: number
  capabilities: string[]
  speedClass: "fast" | "medium" | "slow"
}

export const modelCatalog: ModelCatalogEntry[] = [
  {
    id: "phi-4-mini",
    name: "Phi-4 Mini",
    provider: "Microsoft",
    description: "Small, capable model. Strong at reasoning, coding, and math.",
    sizeBytes: 2_100_000_000,
    parameters: "3.8B",
    tags: ["Reasoning", "Coding", "Math"],
    downloadUrl: "https://huggingface.co/bartowski/microsoft_Phi-4-mini-instruct-GGUF/resolve/main/microsoft_Phi-4-mini-instruct-Q4_K_M.gguf",
    filename: "microsoft_Phi-4-mini-instruct-Q4_K_M.gguf",
    minRamGB: 3.0,
    recommendedRamGB: 5.0,
    capabilities: ["reasoning", "coding", "math", "logic"],
    speedClass: "fast",
  },
  {
    id: "phi-3.1-mini",
    name: "Phi-3.1 Mini",
    provider: "Microsoft",
    description: "Excellent small model for chat and general tasks on mobile.",
    sizeBytes: 2_500_000_000,
    parameters: "3.8B",
    tags: ["Chat", "General", "Mobile"],
    downloadUrl: "https://huggingface.co/bartowski/Phi-3.1-mini-4k-instruct-GGUF/resolve/main/Phi-3.1-mini-4k-instruct-Q4_K_M.gguf",
    filename: "Phi-3.1-mini-4k-instruct-Q4_K_M.gguf",
    minRamGB: 3.0,
    recommendedRamGB: 5.0,
    capabilities: ["chat", "general", "reasoning"],
    speedClass: "fast",
  },
  {
    id: "llama-3.2-3b",
    name: "Llama 3.2 3B",
    provider: "Meta",
    description: "Optimized for mobile and edge devices. Well-balanced chat and general tasks.",
    sizeBytes: 2_500_000_000,
    parameters: "3B",
    tags: ["Balanced", "Chat", "General"],
    downloadUrl: "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
    filename: "Llama-3.2-3B-Instruct-Q4_K_M.gguf",
    minRamGB: 4.0,
    recommendedRamGB: 6.0,
    capabilities: ["chat", "general", "creative"],
    speedClass: "medium",
  },
  {
    id: "qwen-2.5-3b",
    name: "Qwen 2.5 3B",
    provider: "Alibaba",
    description: "High performance multilingual model with strong coding abilities.",
    sizeBytes: 3_100_000_000,
    parameters: "3B",
    tags: ["Multilingual", "Coding"],
    downloadUrl: "https://huggingface.co/bartowski/Qwen2.5-3B-Instruct-GGUF/resolve/main/Qwen2.5-3B-Instruct-Q4_K_M.gguf",
    filename: "Qwen2.5-3B-Instruct-Q4_K_M.gguf",
    minRamGB: 4.0,
    recommendedRamGB: 6.0,
    capabilities: ["multilingual", "coding", "translation"],
    speedClass: "medium",
  },
]
