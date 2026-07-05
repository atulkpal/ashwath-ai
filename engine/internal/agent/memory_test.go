package agent

import (
	"testing"
)

func TestNewMemory(t *testing.T) {
	m := NewMemory(Config{MaxMessages: 50})
	if m == nil {
		t.Fatal("NewMemory returned nil")
	}
	if m.Len() != 0 {
		t.Errorf("expected 0 messages, got %d", m.Len())
	}
}

func TestAddMessage(t *testing.T) {
	m := NewMemory(Config{})
	if err := m.AddMessage(Message{Role: RoleUser, Content: "hello"}); err != nil {
		t.Fatalf("AddMessage failed: %v", err)
	}
	if m.Len() != 1 {
		t.Errorf("expected 1 message, got %d", m.Len())
	}
}

func TestMessages(t *testing.T) {
	m := NewMemory(Config{})
	m.AddMessage(Message{Role: RoleUser, Content: "msg1"})
	m.AddMessage(Message{Role: RoleAssistant, Content: "msg2"})

	msgs := m.Messages()
	if len(msgs) != 2 {
		t.Fatalf("expected 2 messages, got %d", len(msgs))
	}
	if msgs[0].Role != RoleUser || msgs[0].Content != "msg1" {
		t.Errorf("unexpected first message: %+v", msgs[0])
	}
	if msgs[1].Role != RoleAssistant || msgs[1].Content != "msg2" {
		t.Errorf("unexpected second message: %+v", msgs[1])
	}
}

func TestClear(t *testing.T) {
	m := NewMemory(Config{})
	m.AddMessage(Message{Role: RoleUser, Content: "test"})
	m.Clear()
	if m.Len() != 0 {
		t.Errorf("expected 0 messages after clear, got %d", m.Len())
	}
}

func TestMaxMessages(t *testing.T) {
	m := NewMemory(Config{MaxMessages: 3})
	for i := 0; i < 10; i++ {
		m.AddMessage(Message{Role: RoleUser, Content: "msg"})
	}
	if m.Len() != 3 {
		t.Errorf("expected 3 messages, got %d", m.Len())
	}
}

func TestMessagesCopy(t *testing.T) {
	m := NewMemory(Config{})
	m.AddMessage(Message{Role: RoleUser, Content: "original"})

	msgs := m.Messages()
	msgs[0].Content = "modified"

	messages := m.Messages()
	if messages[0].Content != "original" {
		t.Error("Messages() should return a copy")
	}
}

func TestDefaultConfig(t *testing.T) {
	m := NewMemory(Config{})
	m.AddMessage(Message{Role: RoleUser, Content: "a"})
	m.AddMessage(Message{Role: RoleUser, Content: "b"})
	if m.Len() != 2 {
		t.Errorf("expected 2 messages, got %d", m.Len())
	}
}

func TestConcurrentAccess(t *testing.T) {
	m := NewMemory(Config{MaxMessages: 1000})
	done := make(chan bool)

	go func() {
		for i := 0; i < 500; i++ {
			m.AddMessage(Message{Role: RoleUser, Content: "write"})
		}
		done <- true
	}()

	go func() {
		for i := 0; i < 500; i++ {
			m.Messages()
		}
		done <- true
	}()

	<-done
	<-done
}
