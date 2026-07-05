package runtime

import (
	"context"
	"errors"
	"testing"
)

type testProvider struct {
	name    string
	failInit bool
}

func (p *testProvider) Name() string { return p.name }

func (p *testProvider) Create(ctx context.Context, opts Options) (Engine, error) {
	if p.failInit {
		return nil, errors.New("creation failed")
	}
	return NewMock(), nil
}

func TestRegisterAndCreate(t *testing.T) {
	provider := &testProvider{name: "test-provider"}
	RegisterProvider(provider)

	eng, err := CreateEngine(context.Background(), "test-provider", Options{})
	if err != nil {
		t.Fatalf("CreateEngine failed: %v", err)
	}
	if eng == nil {
		t.Fatal("CreateEngine returned nil")
	}
}

func TestCreateUnknown(t *testing.T) {
	_, err := CreateEngine(context.Background(), "unknown", Options{})
	if err == nil {
		t.Fatal("expected error for unknown provider")
	}
}

func TestListProviders(t *testing.T) {
	RegisterProvider(&testProvider{name: "list-test"})
	names := ListProviders()
	found := false
	for _, n := range names {
		if n == "list-test" {
			found = true
			break
		}
	}
	if !found {
		t.Error("list-test should be in provider list")
	}
}

func TestProviderCreateError(t *testing.T) {
	RegisterProvider(&testProvider{name: "failing-provider", failInit: true})
	_, err := CreateEngine(context.Background(), "failing-provider", Options{})
	if err == nil {
		t.Fatal("expected error for failing provider")
	}
}

func TestProviderEngineName(t *testing.T) {
	RegisterProvider(&testProvider{name: "eng-name-test"})
	eng, err := CreateEngine(context.Background(), "eng-name-test", Options{})
	if err != nil {
		t.Fatalf("CreateEngine failed: %v", err)
	}
	if eng.Name() != mockName {
		t.Errorf("expected name %s, got %s", mockName, eng.Name())
	}
}
