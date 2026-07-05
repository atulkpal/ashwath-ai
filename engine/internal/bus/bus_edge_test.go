package bus

import "testing"

func TestPublishNoSubscribers(t *testing.T) {
	b := New()
	b.Publish("nonexistent", "data")
}

func TestMultiplePublishToSameTopic(t *testing.T) {
	b := New()
	count := 0
	b.Subscribe("topic", func(e Event) {
		count++
	})
	b.Publish("topic", 1)
	b.Publish("topic", 2)
	if count != 2 {
		t.Errorf("expected 2 events, got %d", count)
	}
}

func TestSubscribeTwiceUnsubscribeFirst(t *testing.T) {
	b := New()
	var first, second int

	unsub1 := b.Subscribe("topic", func(e Event) {
		first++
	})
	b.Subscribe("topic", func(e Event) {
		second++
	})

	b.Publish("topic", nil)
	unsub1()
	b.Publish("topic", nil)

	if first != 1 {
		t.Errorf("first handler should have been called once, got %d", first)
	}
	if second != 2 {
		t.Errorf("second handler should have been called twice, got %d", second)
	}
}

func TestSubscribeEmptyTopic(t *testing.T) {
	b := New()
	called := false
	b.Subscribe("", func(e Event) {
		called = true
	})
	b.Publish("", nil)
	if !called {
		t.Error("handler for empty topic should be called")
	}
}
