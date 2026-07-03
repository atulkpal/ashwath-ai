package api

import (
	"encoding/json"
	"google.golang.org/grpc/encoding"
)

const jsonCodecName = "json"

type jsonCodec struct{}

func (c *jsonCodec) Marshal(v any) ([]byte, error) {
	return json.Marshal(v)
}

func (c *jsonCodec) Unmarshal(data []byte, v any) error {
	return json.Unmarshal(data, v)
}

func (c *jsonCodec) Name() string {
	return jsonCodecName
}

func init() {
	encoding.RegisterCodec(&jsonCodec{})
}

var _ encoding.Codec = (*jsonCodec)(nil)
