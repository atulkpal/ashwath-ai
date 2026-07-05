export type RuntimeConnectionState = "idle" | "connecting" | "connected" | "disconnected" | "error";

export type ConnectionStateListener = (state: RuntimeConnectionState, error?: string) => void;

export interface RuntimeConnection {
  readonly state: RuntimeConnectionState;
  readonly endpoint: string;
  readonly connectedAt?: string;
  readonly lastError?: string;
  readonly retryCount: number;
  connect(): Promise<void>;
  disconnect(): Promise<void>;
  getState(): RuntimeConnectionState;
  setState(state: RuntimeConnectionState, error?: string): void;
  markConnecting(): void;
  markConnected(): void;
  markDisconnected(): void;
  markError(error: string): void;
  onStateChange(listener: ConnectionStateListener): () => void;
}

export const DEFAULT_RETRY_CONFIG = {
  maxRetries: 5,
  baseDelayMs: 1000,
  maxDelayMs: 30000,
};

export class RuntimeConnectionImpl implements RuntimeConnection {
  private _state: RuntimeConnectionState = "idle";
  private _connectedAt?: string;
  private _lastError?: string;
  private _retryCount = 0;
  private _listeners: Set<ConnectionStateListener> = new Set();
  private _reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private _disconnected = false;
  readonly endpoint: string;

  constructor(endpoint: string) {
    this.endpoint = endpoint;
  }

  get state(): RuntimeConnectionState {
    return this._state;
  }

  get connectedAt(): string | undefined {
    return this._connectedAt;
  }

  get lastError(): string | undefined {
    return this._lastError;
  }

  get retryCount(): number {
    return this._retryCount;
  }

  onStateChange(listener: ConnectionStateListener): () => void {
    this._listeners.add(listener);
    return () => this._listeners.delete(listener);
  }

  private emit(): void {
    for (const listener of this._listeners) {
      listener(this._state, this._lastError);
    }
  }

  async connect(): Promise<void> {
    this._disconnected = false;
    this.markConnecting();
    await Promise.resolve();
    this.markConnected();
  }

  async disconnect(): Promise<void> {
    this._disconnected = true;
    this._retryCount = 0;
    if (this._reconnectTimer !== null) {
      clearTimeout(this._reconnectTimer);
      this._reconnectTimer = null;
    }
    this.markDisconnected();
  }

  scheduleReconnect(onReconnect: () => Promise<void>): void {
    if (this._disconnected) return;

    this._retryCount++;
    if (this._retryCount > DEFAULT_RETRY_CONFIG.maxRetries) {
      this.setState("error", `max retries (${DEFAULT_RETRY_CONFIG.maxRetries}) exceeded`);
      return;
    }

    const delay = Math.min(
      DEFAULT_RETRY_CONFIG.baseDelayMs * Math.pow(2, this._retryCount - 1),
      DEFAULT_RETRY_CONFIG.maxDelayMs,
    );

    this._reconnectTimer = setTimeout(async () => {
      if (this._disconnected) return;
      this.markConnecting();
      try {
        await onReconnect();
        this._retryCount = 0;
      } catch {
        this.scheduleReconnect(onReconnect);
      }
    }, delay);
  }

  getState(): RuntimeConnectionState {
    return this._state;
  }

  setState(state: RuntimeConnectionState, error?: string): void {
    this._state = state;
    if (state === "connected") {
      this._connectedAt = this._connectedAt ?? new Date().toISOString();
      this._lastError = undefined;
      this._retryCount = 0;
      this.emit();
      return;
    }

    if (state === "disconnected") {
      this._connectedAt = undefined;
      this._lastError = undefined;
      this.emit();
      return;
    }

    this._lastError = error;
    this.emit();
  }

  markConnecting(): void {
    this.setState("connecting");
  }

  markConnected(): void {
    this.setState("connected");
  }

  markDisconnected(): void {
    this.setState("disconnected");
  }

  markError(error: string): void {
    this.setState("error", error);
  }
}
