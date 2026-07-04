export type RuntimeConnectionState = "idle" | "connecting" | "connected" | "disconnected" | "error";

export interface RuntimeConnection {
  readonly state: RuntimeConnectionState;
  readonly endpoint: string;
  readonly connectedAt?: string;
  readonly lastError?: string;
  connect(): Promise<void>;
  disconnect(): Promise<void>;
  getState(): RuntimeConnectionState;
  setState(state: RuntimeConnectionState, error?: string): void;
  markConnecting(): void;
  markConnected(): void;
  markDisconnected(): void;
  markError(error: string): void;
}

export class RuntimeConnectionImpl implements RuntimeConnection {
  private _state: RuntimeConnectionState = "idle";
  private _connectedAt?: string;
  private _lastError?: string;
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

  async connect(): Promise<void> {
    this.markConnecting();
    await Promise.resolve();
    this.markConnected();
  }

  async disconnect(): Promise<void> {
    this.markDisconnected();
  }

  getState(): RuntimeConnectionState {
    return this._state;
  }

  setState(state: RuntimeConnectionState, error?: string): void {
    this._state = state;
    if (state === "connected") {
      this._connectedAt = this._connectedAt ?? new Date().toISOString();
      this._lastError = undefined;
      return;
    }

    if (state === "disconnected") {
      this._connectedAt = undefined;
      this._lastError = undefined;
      return;
    }

    this._lastError = error;
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
