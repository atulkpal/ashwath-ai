export interface Message {
  id: string
  role: "user" | "assistant"
  content: string
  timestamp: string
}

export interface Conversation {
  id: string
  title: string
  preview: string
  timestamp: string
  active: boolean
}

export const conversations: Conversation[] = [
  { id: "1", title: "Transformer Architecture", preview: "Explain how attention handles long-range dependencies...", timestamp: "14:02", active: true },
  { id: "2", title: "Model Fine-tuning", preview: "What are the best practices for LoRA adapters?", timestamp: "Yesterday", active: false },
  { id: "3", title: "RAG Pipeline Design", preview: "How to chunk documents for vector retrieval?", timestamp: "Yesterday", active: false },
  { id: "4", title: "Quantization Strategies", preview: "Compare GPTQ vs AWQ for 4-bit inference.", timestamp: "Mon", active: false },
]

export const messages: Message[] = [
  { id: "1", role: "user", content: "Explain how the Transformer architecture handles long-range dependencies in text sequences.", timestamp: "14:02:11" },
  { id: "2", role: "assistant", content: "The Transformer architecture handles long-range dependencies primarily through its Self-Attention Mechanism. Unlike RNNs that process sequences token-by-token linearly, Transformers process all tokens simultaneously, allowing every token to \"attend\" to every other token regardless of distance.\n\nKey mechanisms include:\n\n• Positional Encoding: Injects spatial information since attention is permutation-invariant.\n• Multi-Head Attention: Allows the model to jointly attend to information from different representation subspaces.", timestamp: "14:02:14" },
]