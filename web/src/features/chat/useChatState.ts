import { useState, useCallback } from "react"
import type { Message, Conversation } from "./chat-data"

export function useChatState() {
  const [conversations] = useState<Conversation[]>([
    { id: "1", title: "Transformer Architecture", preview: "Explain how attention handles long-range dependencies...", timestamp: "14:02", active: true },
    { id: "2", title: "Model Fine-tuning", preview: "What are the best practices for LoRA adapters?", timestamp: "Yesterday", active: false },
    { id: "3", title: "RAG Pipeline Design", preview: "How to chunk documents for vector retrieval?", timestamp: "Yesterday", active: false },
    { id: "4", title: "Quantization Strategies", preview: "Compare GPTQ vs AWQ for 4-bit inference.", timestamp: "Mon", active: false },
  ])

  const [messages, setMessages] = useState<Message[]>([
    { id: "1", role: "user", content: "Explain how the Transformer architecture handles long-range dependencies in text sequences.", timestamp: "14:02:11" },
    { id: "2", role: "assistant", content: "The Transformer architecture handles long-range dependencies primarily through its Self-Attention Mechanism. Unlike RNNs that process sequences token-by-token linearly, Transformers process all tokens simultaneously, allowing every token to \"attend\" to every other token regardless of distance.\n\nKey mechanisms include:\n\n• Positional Encoding: Injects spatial information since attention is permutation-invariant.\n• Multi-Head Attention: Allows the model to jointly attend to information from different representation subspaces.", timestamp: "14:02:14" },
  ])

  const [input, setInput] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [activeConversationId, setActiveConversationId] = useState("1")

  const sendMessage = useCallback(() => {
    const trimmed = input.trim()
    if (!trimmed) return

    const userMessage: Message = {
      id: Date.now().toString(),
      role: "user",
      content: trimmed,
      timestamp: new Date().toLocaleTimeString("en-US", { hour12: false }),
    }

    setMessages((prev) => [...prev, userMessage])
    setInput("")
    setIsLoading(true)

    // Simulate assistant response for presentation realism
    setTimeout(() => {
      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: "assistant",
        content: "This is a presentation-only response. In production, the Go Engine would stream the actual inference result here.",
        timestamp: new Date().toLocaleTimeString("en-US", { hour12: false }),
      }
      setMessages((prev) => [...prev, assistantMessage])
      setIsLoading(false)
    }, 800)
  }, [input])

  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault()
      sendMessage()
    }
  }, [sendMessage])

  const selectConversation = useCallback((id: string) => {
    setActiveConversationId(id)
  }, [])

  return {
    conversations,
    messages,
    input,
    setInput,
    isLoading,
    sendMessage,
    handleKeyDown,
    activeConversationId,
    selectConversation,
  }
}