# EPIC 2: Android + SDK Integration — Remaining Work

## ✅ Already Done (engine-side)

| What | Where |
|------|-------|
| Engine default port changed to **50051** | `engine/internal/config/loader.go:15` |
| CI binary naming aligned with Android ABI | `.github/workflows/release-engine.yml` |
| Added `armeabi-v7a` build target | CI + Makefile |
| Go protobuf types generated from shared proto | `engine/internal/api/pb/` |
| Engine service uses generated stubs | `engine/internal/api/service.go` |
| JSON codec removed from service/tests | `jsoncodec.go`, `service_test.go` |
| Smoke test uses protobuf client | `engine/tests/smoke.go` |
| Worktree setup script | `scripts/setup-worktrees.ps1` |
| Initial commit + `engine/v0.1.0` tag created | `git tag` |
| Feature branches created | `feature/android-installer`, `feature/engine-llama`, `feature/web-frontend`, `feature/ios-frontend`, `feature/desktop-frontend` |

---

## 🔴 Critical — Fix Before First Release (Android-side)

### 1. Add `INTERNET` Permission
**File:** `android/app/src/main/AndroidManifest.xml`

Add before `<application>`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Without this, all download and gRPC calls silently fail on API 28+.

### 2. Fix Network I/O Dispatchers
**Files:** `ChatViewModel.kt`, `EngineDownloader.kt`

Wrap network suspend calls in `withContext(Dispatchers.IO)`:
- `ChatViewModel.downloadEngine()` body → IO dispatcher
- `EngineDownloader.downloadFile()` body → IO dispatcher

Currently runs on `Dispatchers.Main` → causes ANR (app minimize).

### 3. Emit Progress Before Checksum Fetch
**File:** `EngineInstaller.kt`

Set `_downloadState.value = DownloadState.Downloading(0f)` before the `httpClient.get(checksumsUrl)` call. Currently no progress is emitted during the checksum fetch, so the UI stays on "idle" for several seconds.

### 4. Fix Download Progress Reporting
**File:** `EngineDownloader.kt`

Replace raw `bodyAsChannel()` loop with `prepareGet().execute` so `onDownload` fires progressively with OkHttp engine. Currently `onDownload` may fire only once at 100%.

---

## 🟡 Medium Priority (Android-side)

### 5. Binary Naming Alignment
The CI now produces binaries named:
- `ashwathd-arm64-v8a` (was `ashwathd-android-arm64`)
- `ashwathd-x86_64` (was `ashwathd-android-x64`)
- `ashwathd-armeabi-v7a` (new)

The `EngineInstaller.kt` constructs `"ashwathd-$abi"` where `$abi` is from `Build.SUPPORTED_ABIS` — this **already matches** the new naming. Verify with a real download once a release exists.

### 6. Add Tests for Download Flow
- Success case: mock HTTP returns binary, verify `Complete` state emitted
- Progress case: verify `Downloading` states are emitted in order
- Checksum failure case: verify `Failed` state

### 7. Handle Engine Process Failure Gracefully
If `EngineProcessManager.start()` fails (e.g., SELinux blocks execution), show a user-friendly error overlay rather than crashing.

---

## 🔵 Build Verification (run before commit)

```powershell
# Engine tests
cd engine
go test -count=1 ./...
go build ./cmd/ashwathd

# Android
cd android
.\gradlew assembleDebug
.\gradlew test
.\gradlew :sdk:jar
```

---

## 📦 Creating a Real Release

To trigger the first binary release:
```powershell
git remote add origin https://github.com/ashwathai/ashwath-engine.git
git push -u origin main
git push origin engine/v0.1.0
```

This triggers `.github/workflows/release-engine.yml` which:
1. Cross-compiles 8 binaries (3 Android ABIs + Linux/Mac/Windows)
2. Generates `checksums.txt` with SHA-256 hashes
3. Publishes a GitHub Release at `https://github.com/ashwathai/ashwath-engine/releases/tag/engine/v0.1.0`

The Android app downloads from `https://github.com/ashwathai/ashwath-engine/releases/latest/download/`.
