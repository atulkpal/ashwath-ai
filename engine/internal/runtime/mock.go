package runtime

import (
	"context"
	"strings"
	"time"
)

const mockName = "mock"

var mockResponse = "This is a mocked response from the Ashwath Engine. The engine is running and ready to serve requests. In the future, this will be powered by real AI models running locally on your device."

type mockEngine struct{}

func NewMock() Engine {
	return &mockEngine{}
}

func (m *mockEngine) Name() string {
	return mockName
}

func (m *mockEngine) Initialize(ctx context.Context, opts Options) error {
	return nil
}

func (m *mockEngine) Generate(ctx context.Context, req Request) (<-chan Result, error) {
	ch := make(chan Result)
	words := strings.Fields(mockResponse)
	go func() {
		defer close(ch)
		for i, word := range words {
			select {
			case <-ctx.Done():
				ch <- Result{Error: ctx.Err()}
				return
			case <-time.After(50 * time.Millisecond):
				isLast := i == len(words)-1
				ch <- Result{Text: word + " ", Done: isLast}
			}
		}
	}()
	return ch, nil
}

func (m *mockEngine) Stop(ctx context.Context) error {
	return nil
}
