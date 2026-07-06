export interface Message {
  id: string
  role: "user" | "assistant"
  content: string
  timestamp: string
  model?: string
  tokens?: number
  token_sec?: number
}

export interface Conversation {
  id: string
  title: string
  preview: string
  timestamp: string
  active: boolean
  pinned?: boolean
}

export const conversations: Conversation[] = [
  { id: "1", title: "Transformer Architecture", preview: "Explain how attention handles long-range dependencies...", timestamp: "14:02", active: true, pinned: true },
  { id: "2", title: "Model Fine-tuning", preview: "What are the best practices for LoRA adapters?", timestamp: "Yesterday", active: false },
  { id: "3", title: "RAG Pipeline Design", preview: "How to chunk documents for vector retrieval?", timestamp: "Yesterday", active: false },
  { id: "4", title: "Quantization Strategies", preview: "Compare GPTQ vs AWQ for 4-bit inference.", timestamp: "Mon", active: false },
]

export const messages: Message[] = [
  { id: "1", role: "user", content: "Explain how the Transformer architecture handles long-range dependencies in text sequences.", timestamp: "14:02:11" },
  { id: "2", role: "assistant", content: "The Transformer architecture handles long-range dependencies primarily through its **Self-Attention Mechanism**. Unlike RNNs that process sequences token-by-token linearly, Transformers process all tokens simultaneously, allowing every token to \"attend\" to every other token regardless of distance.\n\nKey mechanisms include:\n\n• **Positional Encoding**: Injects spatial information since attention is permutation-invariant.\n• **Multi-Head Attention**: Allows the model to jointly attend to information from different representation subspaces.\n\n```python\nimport torch\nimport torch.nn as nn\n\nclass MultiHeadAttention(nn.Module):\n    def __init__(self, d_model, n_heads):\n        super().__init__()\n        self.n_heads = n_heads\n        self.d_model = d_model\n        self.d_k = d_model // n_heads\n        \n        self.W_q = nn.Linear(d_model, d_model)\n        self.W_k = nn.Linear(d_model, d_model)\n        self.W_v = nn.Linear(d_model, d_model)\n        self.W_o = nn.Linear(d_model, d_model)\n    \n    def forward(self, x):\n        batch_size = x.size(0)\n        Q = self.W_q(x).view(batch_size, -1, self.n_heads, self.d_k).transpose(1, 2)\n        K = self.W_k(x).view(batch_size, -1, self.n_heads, self.d_k).transpose(1, 2)\n        V = self.W_v(x).view(batch_size, -1, self.n_heads, self.d_k).transpose(1, 2)\n        \n        scores = torch.matmul(Q, K.transpose(-2, -1)) / (self.d_k ** 0.5)\n        attn = torch.softmax(scores, dim=-1)\n        out = torch.matmul(attn, V)\n        return out.transpose(1, 2).contiguous().view(batch_size, -1, self.d_model)\n```\n\nThis architecture enables **O(1)** path length between any two positions, compared to **O(n)** for RNNs, making long-range dependency capture significantly more efficient.", timestamp: "14:02:14", model: "ashwath-7b-v1", tokens: 342, token_sec: 45.6 },
]