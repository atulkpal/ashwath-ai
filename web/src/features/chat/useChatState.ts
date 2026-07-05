import { useState, useCallback, useRef, useEffect } from "react"
import type { Message, Conversation } from "./chat-data"
import { conversations as initialConversations, messages as initialMessages } from "./chat-data"

export function useChatState() {
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

    setTimeout(() => {
      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: "assistant",
        content: "This is a presentation-only response. In production, the Go Engine would stream the actual inference result here.\n\n```javascript\nconsole.log(\"Hello from Ashwath AI\");\n```\n\nThe response includes **markdown** formatting with code blocks, lists, and other rich text features.",
        timestamp: new Date().toLocaleTimeString("en-US", { hour12: false }),
        model: "ashwath-7b-v1",
        tokens: 128,
        token_sec: 52.3,
      }
      setMessages((prev) => [...prev, assistantMessage])
      setIsLoading(false)
    }, 1200)
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
