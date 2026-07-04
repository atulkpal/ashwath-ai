import type { RuntimeApi } from "./RuntimeApi";
import type { RuntimeConfiguration } from "./RuntimeConfiguration";
import type { RuntimeConnection } from "./RuntimeConnection";
import type { RuntimeHealth } from "./RuntimeHealth";

export interface RuntimeTransport extends RuntimeApi {
  readonly configuration: RuntimeConfiguration;
  readonly connection: RuntimeConnection;
  readonly healthMonitor: RuntimeHealth;
}
