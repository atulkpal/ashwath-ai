# EPIC 2: Android + SDK Integration — Task List

## Build Fixes Already Applied (review these, don't revert)

Your SDK/app code was good. These 3 build fixes were needed:

1. **`sdk/kotlin/build.gradle.kts`**: Removed `version "2.2.10"` from `kotlin("jvm")` (plugin already on classpath). Removed `repositories { mavenCentral() }` block (conflicts with `FAIL_ON_PROJECT_REPOS` in settings).

2. **`android/app/build.gradle.kts`**: Replaced `sourceSets { java.srcDir("../../sdk/kotlin/src/main/kotlin") }` with `implementation(project(":sdk"))` — use proper project dependency, don't inline SDK sources into the app module.

3. **`android/settings.gradle.kts`**: Added `pluginManagement.plugins` block for `org.jetbrains.kotlin.jvm` and `com.google.protobuf` so the SDK resolves its plugin correctly.

DO NOT touch these files unless adding NEW dependencies.

---

## Remaining Work (in priority order)

### 1. Generate gRPC Stubs from Proto

The `ServiceProto.kt` file is a hand-written proto — replace it with real generated stubs.

- [ ] Add protobuf plugin config to `sdk/kotlin/build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm")
    id("com.google.protobuf")
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:4.28.2" }
    plugins {
        create("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.68.0" }
        create("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins { create("grpc") {}; create("grpckt") {} }
        }
    }
}
```

- [ ] Create `sdk/kotlin/src/main/proto/ashwathai/v1/engine.proto` — copy from `engine/proto/ashwathai/v1/engine.proto`
- [ ] Add build dependency: `implementation("io.grpc:grpc-kotlin-stub:1.4.1")`
- [ ] Update `sdk/kotlin/build.gradle.kts` dependencies to include protobuf-java and grpc-kotlin-stub
- [ ] Run `./gradlew :sdk:generateProto` and verify stubs are generated in `sdk/kotlin/build/generated/`

### 2. Wire Real gRPC Stubs in EngineGrpcClient

`EngineGrpcClient.generate()` currently returns mock responses. Replace with real stub calls.

- [ ] In `EngineGrpcClient.kt`, replace mocked flow with:
  ```kotlin
  val stub = AshwathEngineGrpcKt.AshwathEngineCoroutineStub(channel)
  val request = generateRequest { /* set fields from prompt + options */ }
  stub.generate(request).collect { response ->
      emit(GenerateResponse(response.text, response.tokenCount, response.done))
  }
  ```

### 3. Fix Deprecation Warnings

Current build has 4 warnings. Fix them:

- [ ] `Screen.kt:21,27`: Replace `Icons.Filled.Chat` → `Icons.AutoMirrored.Filled.Chat`, same for `LibraryBooks`
- [ ] `Theme.kt:40`: Remove deprecated `window.statusBarColor` usage
- [ ] `EngineDownloader.kt:36`: Remove `import kotlin.io.use` (stdlib version auto-available)

### 4. Add Tests

- [ ] Unit tests for `EngineGrpcClient` (mock channel)
- [ ] Unit tests for `EngineProcessManager` and `EngineInstaller`
- [ ] Unit tests for `ClientInferenceEngine`
- [ ] Instrumentation test for full download → install → connect flow

### 5. Polish

- [ ] Add error handling for missing engine binary
- [ ] Add retry logic for gRPC connection
- [ ] Add progress UI for model downloads
- [ ] Handle app lifecycle (pause/resume engine process)

---

## Build Verification

Always run these before committing:

```powershell
# Root: engine tests
cd engine
go test ./...
go build ./cmd/ashwathd

# Android: app + SDK
cd android
./gradlew assembleDebug

# SDK jar explicitly
./gradlew :sdk:jar
```

All three must pass without errors.
