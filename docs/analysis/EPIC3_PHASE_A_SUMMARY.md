# EPIC 3 – Phase A Summary: Engine Foundation Stabilization

## 1. Overview
Phase A focused on transitioning the Ashwath Engine from a mock-heavy scaffold to a functional foundation capable of handling real AI model lifecycles and inference backend wiring.

**Status**: ✅ Completed
**Date**: 2024-03-24

## 2. Goals
- Establish a real model installation and download pipeline.
- Implement a persistent model registry for tracking installed models.
- Wire the initial `llama.cpp` backend for real GGUF inference.
- Provide on-device benchmarking capabilities.
- Ensure end-to-end stability between the engine and the Android frontend.

## 3. Work Completed

### Backend Wiring
- Integrated `llama.cpp` server (via `llama-server`) as a runtime backend.
- Implemented process management for the local inference server (Start/Stop/Health).
- Established a unified `Engine` interface for both `mock` and `llama` backends.

### Model Management
- **Persistent Registry**: Created `registry.json` to track model metadata and installation status across restarts.
- **Install/Remove Lifecycle**: Implemented RPC handlers for managing model presence on disk.
- **Download Manager**: Developed a multi-threaded downloader with support for:
    - Background workers.
    - Multi-chunked downloads.
    - SHA-256 checksum verification.
    - Progress reporting via gRPC.

### Performance & Stability
- **Benchmarks**: Implemented an internal benchmarking suite to measure:
    - Prompt processing speed (tokens/sec).
    - Generation speed (tokens/sec).
    - Peak memory usage (MB).
- **Fixes**:
    - Resolved race conditions in gRPC stream cancellation.
    - Optimized memory buffering during large GGUF downloads.
    - Corrected JNI boundary data types for more reliable string passing.

### Verification
- **Test Suite**: Expanded Go unit tests from 21 to 45, covering the new download and registry logic.
- **End-to-End**: Verified that the Android Library UI correctly reflects the state of the persistent model registry and displays real-time download progress.

## 4. Key Architectural Decisions
- **Isolation of Data Directory**: All engine artifacts (models, logs, registry) are now strictly isolated in the configured `DataDir`, preventing pollution of the app's internal storage.
- **Decoupled Downloader**: The download manager is a standalone internal package, allowing for future support of multiple sources (HuggingFace, custom S3 buckets) without modifying core engine logic.

## 5. Lessons Learned
- **Download Resumption**: While Phase A handles chunked downloads, partial resumption after a crash needs more robust state tracking (scheduled for Phase B).
- **Process Cleanup**: Ensuring `llama-server` is killed when the Android app is swiped away requires tight integration with the Android Service lifecycle.

## 6. Outstanding Work
- Robust download resumption logic.
- Fine-grained error codes for JNI-level failures.
- Multi-model concurrent loading support (exploration).

## 7. Next Steps: Phase B (Architecture Foundation)
- Standardize gRPC contracts across all features.
- Refine SDK abstractions to better support non-Android platforms.
- Begin implementation of Phase B architecture ADRs.
