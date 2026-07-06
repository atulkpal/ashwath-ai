export type {
  GenerateRequest,
  GenerateResponse,
  ModelInfo,
  ModelList,
  InstallRequest,
  InstallResponse,
  RemoveRequest,
  RemoveResponse,
  DeviceInfo,
} from "./pb/service"
export type { ModelCatalogEntry } from "./catalog"
export { modelCatalog } from "./catalog"
export type { DeviceSpec } from "./discover"
export { detectDevice } from "./discover"
export type { ScoredModel } from "./recommend"
export {
  scoreModels,
  formatBytes,
  getModelsByCapability,
  getTopRecommendations,
} from "./recommend"
export type { LocalModel } from "./scanner"
export { probeEngine, probeOllama } from "./scanner"
export { loadCatalog, fetchModelIndex, getCachedIndex } from "./upstream"
export { createEngineClient } from "./connect"
export type { AshwathEngineClient } from "./connect"
