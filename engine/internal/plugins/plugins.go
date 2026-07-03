// Package plugins defines the plugin system for extending engine capabilities.
package plugins

type Plugin interface {
	Name() string
	Version() string
	Init(ctx interface{}) error
}

type Manager interface {
	Load(path string) (Plugin, error)
	List() []Plugin
	Unload(name string) error
}
