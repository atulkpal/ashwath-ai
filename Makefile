.PHONY: all setup setup-engine setup-android build-engine build-android test-engine test-android proto-gen lint clean

all: build-engine build-android

# ─── Setup ──────────────────────────────────────────────────────
setup: setup-engine setup-android

setup-engine:
	cd engine && go mod tidy

setup-android:
	cd android && ./gradlew --version

# ─── Engine (Go) ────────────────────────────────────────────────
build-engine:
	cd engine && go build ./cmd/ashwathd

test-engine:
	cd engine && go test ./...

proto-gen:
	cd engine && protoc --go_out=internal/api/pb --go_opt=paths=source_relative --go-grpc_out=internal/api/pb --go-grpc_opt=paths=source_relative -I api/proto api/proto/service.proto

release-engine: proto-gen
	cd engine && mkdir -p releases && \
	CGO_ENABLED=0 GOOS=android GOARCH=arm64 go build -o releases/ashwathd-arm64-v8a ./cmd/ashwathd && \
	GOOS=linux GOARCH=arm64 go build -o releases/ashwathd-linux-arm64 ./cmd/ashwathd && \
	GOOS=linux GOARCH=amd64 go build -o releases/ashwathd-linux-x64 ./cmd/ashwathd && \
	GOOS=darwin GOARCH=arm64 go build -o releases/ashwathd-macos-arm64 ./cmd/ashwathd && \
	GOOS=darwin GOARCH=amd64 go build -o releases/ashwathd-macos-x64 ./cmd/ashwathd && \
	GOOS=windows GOARCH=amd64 go build -o releases/ashwathd-windows-x64.exe ./cmd/ashwathd && \
	cd releases && sha256sum * > checksums.txt

lint-engine:
	cd engine && golangci-lint run ./...

# ─── Android ────────────────────────────────────────────────────
build-android:
	cd android && ./gradlew assembleDebug

test-android:
	cd android && ./gradlew test

lint-android:
	cd android && ./gradlew lint

# ─── Android .so (JNI) ──────────────────────────────────────────
# Requires ANDROID_NDK_HOME pointing to the NDK installation.
# Example:
#   export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/28.2.13676358
build-android-so:
	$(eval HOST_TAG := $(shell uname -s | tr A-Z a-z)-x86_64)
	$(eval CC := $(ANDROID_NDK_HOME)/toolchains/llvm/prebuilt/$(HOST_TAG)/bin/aarch64-linux-android21-clang)
	cd engine && \
	CGO_ENABLED=1 GOOS=android GOARCH=arm64 CC=$(CC) \
	  go build -buildmode=c-shared \
	  -o ../android/app/src/main/jniLibs/arm64-v8a/libashwath_engine.so \
	  ./cmd/libashwath/

# ─── Release targets ────────────────────────────────────────────
release-all: release-engine release-android-so

release-android-so:
	$(eval HOST_TAG := $(shell uname -s | tr A-Z a-z)-x86_64)
	$(eval CC := $(ANDROID_NDK_HOME)/toolchains/llvm/prebuilt/$(HOST_TAG)/bin/aarch64-linux-android21-clang)
	cd engine && mkdir -p releases && \
	CGO_ENABLED=1 GOOS=android GOARCH=arm64 CC=$(CC) \
	  go build -buildmode=c-shared \
	  -o releases/libashwath_engine-arm64-v8a.so \
	  ./cmd/libashwath/

# ─── Cross-platform ─────────────────────────────────────────────
test-all: test-engine test-android

lint-all: lint-engine lint-android
