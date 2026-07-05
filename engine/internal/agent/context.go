package agent

import (
	"encoding/json"
	"fmt"
	"strings"

	"github.com/ashwathai/ashwath-engine/internal/plugins"
)

type ContextBuilder struct {
	systemPrompt string
}

func NewContextBuilder(systemPrompt string) *ContextBuilder {
	return &ContextBuilder{systemPrompt: systemPrompt}
}

type BuildInput struct {
	UserInput    string
	History      []Message
	ToolSchemas  []plugins.ToolSchema
}

func (b *ContextBuilder) Build(input BuildInput) string {
	var parts []string

	if b.systemPrompt != "" {
		parts = append(parts, formatSystem(b.systemPrompt, input.ToolSchemas))
	}

	if len(input.History) > 0 {
		parts = append(parts, formatHistory(input.History))
	}

	parts = append(parts, formatUser(input.UserInput))

	parts = append(parts, formatAssistant(""))

	return strings.Join(parts, "\n")
}

func formatSystem(prompt string, tools []plugins.ToolSchema) string {
	var b strings.Builder
	b.WriteString("<|im_start|>system\n")
	b.WriteString(prompt)

	if len(tools) > 0 {
		b.WriteString("\n\nYou have access to the following tools:\n")
		for _, t := range tools {
			b.WriteString(fmt.Sprintf("- %s: %s\n", t.Name, t.Description))
			if len(t.Parameters) > 0 {
				b.WriteString("  Parameters:\n")
				for name, param := range t.Parameters {
					b.WriteString(fmt.Sprintf("    %s (%s): %s", name, param.Type, param.Description))
					if param.Required {
						b.WriteString(" [required]")
					}
					b.WriteString("\n")
				}
			}
		}

		schemaBytes, _ := json.MarshalIndent(tools, "  ", "  ")
		b.WriteString("\nTool schemas (JSON):\n")
		b.WriteString(string(schemaBytes))
		b.WriteString("\n")
	}

	b.WriteString("<|im_end|>")
	return b.String()
}

func formatHistory(messages []Message) string {
	var b strings.Builder
	for _, msg := range messages {
		switch msg.Role {
		case RoleUser:
			b.WriteString(formatUser(msg.Content))
		case RoleAssistant:
			b.WriteString(formatAssistant(msg.Content))
		case RoleTool:
			b.WriteString(formatTool(msg.Content, msg.ToolCalls))
		}
	}
	return b.String()
}

func formatUser(content string) string {
	return fmt.Sprintf("<|im_start|>user\n%s<|im_end|>", content)
}

func formatAssistant(content string) string {
	return fmt.Sprintf("<|im_start|>assistant\n%s", content)
}

func formatTool(content string, calls []ToolCall) string {
	if len(calls) > 0 {
		data, _ := json.Marshal(calls)
		return fmt.Sprintf("<|im_start|>tool\n%s\n%s<|im_end|>", string(data), content)
	}
	return fmt.Sprintf("<|im_start|>tool\n%s<|im_end|>", content)
}
