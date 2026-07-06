import { useState, useCallback, useRef, useEffect } from "react"
import type { Message, Conversation } from "./chat-data"
import { conversations as initialConversations, messages as initialMessages } from "./chat-data"
import { useEngine } from "@/engine"

export function useChatState() {
  const { client } = useEngine()
  const [conversations, setConversations] = useState<Conversation[]>(initialConversations)
  const [messages, setMessages] = useState<Message[]>(initialMessages)

  const [input, setInput] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [activeConversationId, setActiveConversationId] = useState("1")
  const [searchQuery, setSearchQuery] = useState("")
  const scrollRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [messages, isLoading])

  const sendMessage = useCallback(async () => {
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

    const assistantId = (Date.now() + 1).toString()
    const assistantMessage: Message = {
      id: assistantId,
      role: "assistant",
      content: "",
      timestamp: new Date().toLocaleTimeString("en-US", { hour12: false }),
    }
    setMessages((prev) => [...prev, assistantMessage])

    try {
      let fullText = ""
      await client.streamGenerate(
        { prompt: trimmed },
        (event) => {
          if (event.text) {
            fullText += event.text
            setMessages((prev) =>
              prev.map((m) => (m.id === assistantId ? { ...m, content: fullText } : m))
            )
          }
        }
      )
    } catch (e) {
      const msg = e instanceof TypeError
        ? "⚡ Engine not running. Start it with:  cd engine && go run ./cmd/ashwathd/"
        : `Error: ${e instanceof Error ? e.message : "Unknown error"}`
      setMessages((prev) =>
        prev.map((m) =>
          m.id === assistantId
            ? { ...m, content: msg }
            : m
        )
      )
    } finally {
      setIsLoading(false)
    }
  }, [input, client])

  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault()
      sendMessage()
    }
  }, [sendMessage])

  const selectConversation = useCallback((id: string) => {
    setActiveConversationId(id)
  }, [])

  const togglePin = useCallback((id: string) => {
    setConversations((prev) =>
      prev.map((c) => (c.id === id ? { ...c, pinned: !c.pinned } : c))
    )
  }, [])

  const deleteConversation = useCallback((id: string) => {
    setConversations((prev) => prev.filter((c) => c.id !== id))
  }, [])

  const renameConversation = useCallback((id: string, title: string) => {
    setConversations((prev) =>
      prev.map((c) => (c.id === id ? { ...c, title } : c))
    )
  }, [])

  const addConversation = useCallback(() => {
    const newId = Date.now().toString()
    const newConv: Conversation = {
      id: newId,
      title: "New Chat",
      preview: "Start a new conversation...",
      timestamp: "Now",
      active: true,
    }
    setConversations((prev) =>
      prev.map((c) => ({ ...c, active: false })).concat(newConv)
    )
    setActiveConversationId(newId)
    setMessages([])
  }, [])

  const filteredConversations = conversations.filter((c) =>
    c.title.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return {
    conversations: filteredConversations,
    allConversations: conversations,
    messages,
    input,
    setInput,
    isLoading,
    sendMessage,
    handleKeyDown,
    activeConversationId,
    selectConversation,
    searchQuery,
    setSearchQuery,
    togglePin,
    deleteConversation,
    renameConversation,
    addConversation,
    scrollRef,
  }
}
