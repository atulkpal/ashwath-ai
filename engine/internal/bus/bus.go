package bus

import (
	"sync"
)

type Event struct {
	Topic   string
	Payload any
}

type Handler func(Event)

type Bus interface {
	Publish(topic string, payload any)
	PublishSync(topic string, payload any)
	Subscribe(topic string, handler Handler) func()
}

type subscription struct {
	id      uint64
	handler Handler
}

type inmemBus struct {
	mu       sync.RWMutex
	handlers map[string][]subscription
	nextID   uint64
}

func New() Bus {
	return &inmemBus{
		handlers: make(map[string][]subscription),
	}
}

func (b *inmemBus) Publish(topic string, payload any) {
	b.mu.RLock()
	subs := b.handlers[topic]
	b.mu.RUnlock()
	for _, sub := range subs {
		sub.handler(Event{Topic: topic, Payload: payload})
	}
}

func (b *inmemBus) PublishSync(topic string, payload any) {
	b.Publish(topic, payload)
}

func (b *inmemBus) Subscribe(topic string, handler Handler) func() {
	b.mu.Lock()
	id := b.nextID
	b.nextID++
	b.handlers[topic] = append(b.handlers[topic], subscription{id: id, handler: handler})
	b.mu.Unlock()
	return func() {
		b.mu.Lock()
		defer b.mu.Unlock()
		subs := b.handlers[topic]
		for i, sub := range subs {
			if sub.id == id {
				b.handlers[topic] = append(subs[:i], subs[i+1:]...)
				return
			}
		}
	}
}
