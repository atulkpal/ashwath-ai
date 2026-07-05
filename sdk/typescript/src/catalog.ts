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
    id: "gemma-3-4b",
    name: "Gemma 3 4B",
    provider: "Google",
    description: "Lightweight, state-of-the-art open model from Google. Excels at reasoning and chat.",
    sizeBytes: 2_800_000_000,
    parameters: "4B",
    tags: ["General", "Efficient", "Chat"],
    downloadUrl: "https://huggingface.co/google/gemma-3-4b-it-gguf/resolve/main/gemma-3-4b-it-Q4_K_M.gguf",
    filename: "gemma-3-4b-it-Q4_K_M.gguf",
    minRamGB: 4.0,
    recommendedRamGB: 6.0,
    capabilities: ["chat", "reasoning", "summarization"],
    speedClass: "fast",
  },
  {
    id: "phi-4-mini",
    name: "Phi-4 Mini",
    provider: "Microsoft",
    description: "Extremely capable small language model. Strong at reasoning, coding, and math.",
    sizeBytes: 2_100_000_000,
    parameters: "3.8B",
    tags: ["Reasoning", "Coding", "Math"],
    downloadUrl: "https://huggingface.co/microsoft/Phi-4-mini-instruct-gguf/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf",
    filename: "Phi-4-mini-instruct-Q4_K_M.gguf",
    minRamGB: 3.0,
    recommendedRamGB: 5.0,
    capabilities: ["reasoning", "coding", "math", "logic"],
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
    downloadUrl: "https://huggingface.co/meta-llama/Llama-3.2-3B-Instruct-gguf/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
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
    downloadUrl: "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-gguf/resolve/main/Qwen2.5-3B-Instruct-Q4_K_M.gguf",
    filename: "Qwen2.5-3B-Instruct-Q4_K_M.gguf",
    minRamGB: 4.0,
    recommendedRamGB: 6.0,
    capabilities: ["multilingual", "coding", "translation"],
    speedClass: "medium",
  },
]
