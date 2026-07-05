package bus

import (
	"sync"
	"testing"
	"time"
)

func TestPublishSubscribe(t *testing.T) {
	b := New()
	var mu sync.Mutex
	var received []Event

	unsub := b.Subscribe("test.topic", func(e Event) {
		mu.Lock()
		received = append(received, e)
		mu.Unlock()
	})
	defer unsub()

	b.Publish("test.topic", "hello")

	mu.Lock()
	if len(received) != 1 {
		t.Fatalf("expected 1 event, got %d", len(received))
	}
	if received[0].Topic != "test.topic" {
		t.Errorf("expected topic test.topic, got %s", received[0].Topic)
	}
	if received[0].Payload != "hello" {
		t.Errorf("expected payload hello, got %v", received[0].Payload)
	}
	mu.Unlock()
}

func TestUnsubscribe(t *testing.T) {
	b := New()
	count := 0

	unsub := b.Subscribe("test.unsub", func(e Event) {
		count++
	})
	b.Publish("test.unsub", nil)

	unsub()
	b.Publish("test.unsub", nil)

	if count != 1 {
		t.Errorf("expected 1 event after unsubscribe, got %d", count)
	}
}

func TestMultipleSubscribers(t *testing.T) {
	b := New()
	var mu sync.Mutex
	count := 0

	b.Subscribe("test.multi", func(e Event) {
		mu.Lock()
		count++
		mu.Unlock()
	})
	b.Subscribe("test.multi", func(e Event) {
		mu.Lock()
		count++
		mu.Unlock()
	})

	b.Publish("test.multi", nil)

	mu.Lock()
	if count != 2 {
		t.Errorf("expected 2 events, got %d", count)
	}
	mu.Unlock()
}

func TestDifferentTopics(t *testing.T) {
	b := New()
	received := make(map[string]bool)

	b.Subscribe("topic.a", func(e Event) {
		received["a"] = true
	})
	b.Subscribe("topic.b", func(e Event) {
		received["b"] = true
	})

	b.Publish("topic.a", nil)
	b.Publish("topic.b", nil)

	if !received["a"] {
		t.Error("topic.a handler not called")
	}
	if !received["b"] {
		t.Error("topic.b handler not called")
	}
}

func TestConcurrentPublish(t *testing.T) {
	b := New()
	var mu sync.Mutex
	count := 0

	b.Subscribe("test.concurrent", func(e Event) {
		mu.Lock()
		count++
		mu.Unlock()
	})

	var wg sync.WaitGroup
	for i := 0; i < 100; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			b.Publish("test.concurrent", nil)
		}()
	}
	wg.Wait()

	time.Sleep(100 * time.Millisecond)
	mu.Lock()
	if count != 100 {
		t.Errorf("expected 100 events, got %d", count)
	}
	mu.Unlock()
}
