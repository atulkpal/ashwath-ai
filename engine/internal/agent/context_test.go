package agent

import (
	"strings"
	"testing"

	"github.com/ashwathai/ashwath-engine/internal/plugins"
)

func TestContextBuilderSimple(t *testing.T) {
	b := NewContextBuilder("You are a helpful assistant.")
	input := BuildInput{
		UserInput: "Hello!",
	}

	prompt := b.Build(input)

	if !strings.Contains(prompt, "You are a helpful assistant") {
		t.Error("prompt should contain system prompt")
	}
	if !strings.Contains(prompt, "Hello") {
		t.Error("prompt should contain user input")
	}
	if !strings.Contains(prompt, "assistant") {
		t.Error("prompt should contain assistant marker")
	}
}

func TestContextBuilderWithHistory(t *testing.T) {
	b := NewContextBuilder("System msg.")
	input := BuildInput{
		UserInput: "Second question",
		History: []Message{
			{Role: RoleUser, Content: "First question"},
			{Role: RoleAssistant, Content: "First answer"},
		},
	}

	prompt := b.Build(input)

	if !strings.Contains(prompt, "First question") {
		t.Error("prompt should contain history")
	}
	if !strings.Contains(prompt, "First answer") {
		t.Error("prompt should contain history response")
	}
	if !strings.Contains(prompt, "Second question") {
		t.Error("prompt should contain user input")
	}
}

func TestContextBuilderWithTools(t *testing.T) {
	b := NewContextBuilder("You are a helpful assistant.")
	input := BuildInput{
		UserInput: "What's the weather?",
		ToolSchemas: []plugins.ToolSchema{
			{
				Name:        "get_weather",
				Description: "Get weather for a location",
				Parameters: map[string]plugins.ParameterSchema{
					"location": {Type: "string", Description: "City name", Required: true},
				},
			},
		},
	}

	prompt := b.Build(input)

	if !strings.Contains(prompt, "get_weather") {
		t.Error("prompt should contain tool name")
	}
	if !strings.Contains(prompt, "Tool schemas") {
		t.Error("prompt should contain tool schemas section")
	}
}

func TestContextBuilderEmptySystem(t *testing.T) {
	b := NewContextBuilder("")
	input := BuildInput{
		UserInput: "Hello",
	}

	prompt := b.Build(input)

	if strings.Contains(prompt, "system") {
		t.Error("empty system prompt should not produce system block")
	}
}

func TestContextBuilderWithToolMessages(t *testing.T) {
	b := NewContextBuilder("System msg.")
	input := BuildInput{
		UserInput: "Continue",
		History: []Message{
			{
				Role:    RoleTool,
				Content: "Weather data",
				ToolCalls: []ToolCall{
					{Name: "get_weather", Args: map[string]any{"location": "NYC"}, Result: "sunny"},
				},
			},
		},
	}

	prompt := b.Build(input)

	if !strings.Contains(prompt, "Weather data") {
		t.Error("prompt should contain tool result")
	}
}

func TestContextBuilderOrder(t *testing.T) {
	b := NewContextBuilder("Be concise.")
	input := BuildInput{
		UserInput: "Hi",
		History: []Message{
			{Role: RoleUser, Content: "Prev"},
			{Role: RoleAssistant, Content: "PrevRes"},
		},
	}

	prompt := b.Build(input)

	sysIdx := strings.Index(prompt, "Be concise")
	histIdx := strings.Index(prompt, "Prev")
	userIdx := strings.Index(prompt, "Hi")
	asstIdx := strings.LastIndex(prompt, "assistant")

	if sysIdx > histIdx {
		t.Error("system should come before history")
	}
	if histIdx > userIdx {
		t.Error("history should come before user input")
	}
	if userIdx > asstIdx {
		t.Error("user input should come before assistant marker")
	}
}
