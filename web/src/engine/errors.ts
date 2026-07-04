export class EngineError extends Error {
  readonly code: string;
  readonly status?: number;

  constructor(message: string, options?: { code?: string; status?: number; cause?: unknown }) {
    super(message);
    this.name = "EngineError";
    this.code = options?.code ?? "ENGINE_ERROR";
    this.status = options?.status;

    if (options?.cause !== undefined) {
      (this as Error & { cause?: unknown }).cause = options.cause;
    }
  }
}

export class NotImplementedError extends EngineError {
  constructor(message = "Engine transport is not implemented yet.") {
    super(message, { code: "NOT_IMPLEMENTED" });
    this.name = "NotImplementedError";
  }
}

export class EngineUnavailableError extends EngineError {
  constructor(message = "The engine runtime is unavailable.") {
    super(message, { code: "ENGINE_UNAVAILABLE", status: 503 });
    this.name = "EngineUnavailableError";
  }
}

export class EngineConfigurationError extends EngineError {
  constructor(message = "The engine client is misconfigured.") {
    super(message, { code: "ENGINE_CONFIGURATION", status: 500 });
    this.name = "EngineConfigurationError";
  }
}
