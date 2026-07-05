package agent

import "sync"

type Role string

const (
	RoleSystem    Role = "system"
	RoleUser      Role = "user"
	RoleAssistant Role = "assistant"
	RoleTool      Role = "tool"
)

type ToolCall struct {
	Name     string         `json:"name"`
	Args     map[string]any `json:"args"`
	Result   string         `json:"result,omitempty"`
}

type Message struct {
	Role      Role       `json:"role"`
	Content   string     `json:"content"`
	ToolCalls []ToolCall `json:"tool_calls,omitempty"`
}

type Memory interface {
	AddMessage(msg Message) error
	Messages() []Message
	Clear()
	Len() int
}

type Config struct {
	MaxMessages int
}

type inMemory struct {
	mu          sync.RWMutex
	messages    []Message
	maxMessages int
}

func NewMemory(cfg Config) Memory {
	if cfg.MaxMessages <= 0 {
		cfg.MaxMessages = 100
	}
	return &inMemory{
		maxMessages: cfg.MaxMessages,
	}
}

func (m *inMemory) AddMessage(msg Message) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	m.messages = append(m.messages, msg)
	if len(m.messages) > m.maxMessages {
		excess := len(m.messages) - m.maxMessages
		m.messages = m.messages[excess:]
	}
	return nil
}

func (m *inMemory) Messages() []Message {
	m.mu.RLock()
	defer m.mu.RUnlock()
	result := make([]Message, len(m.messages))
	copy(result, m.messages)
	return result
}

func (m *inMemory) Clear() {
	m.mu.Lock()
	defer m.mu.Unlock()
	m.messages = nil
}

func (m *inMemory) Len() int {
	m.mu.RLock()
	defer m.mu.RUnlock()
	return len(m.messages)
}
