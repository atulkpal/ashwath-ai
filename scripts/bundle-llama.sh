#!/bin/bash
# bundle-llama.sh — Downloads and bundles llama.cpp binaries for release
set -euo pipefail

VERSION="${1:-b4769}"
OUT_DIR="${2:-./dist/llama}"

RELEASES="https://github.com/ggml-org/llama.cpp/releases/download"

declare -A TARGETS
TARGETS["linux-amd64"]="llama-b4769-bin-ubuntu-x64"
TARGETS["linux-arm64"]="llama-b4769-bin-ubuntu-arm64"
TARGETS["darwin-amd64"]="llama-b4769-bin-macos-x64"
TARGETS["darwin-arm64"]="llama-b4769-bin-macos-arm64"
TARGETS["windows-amd64"]="llama-b4769-bin-win-x64"

echo "=== Bundling llama.cpp $VERSION ==="
mkdir -p "$OUT_DIR"

for target in "${!TARGETS[@]}"; do
    archive="${TARGETS[$target]}.zip"
    url="$RELEASES/$VERSION/$archive"
    target_dir="$OUT_DIR/$target"
    mkdir -p "$target_dir"

    echo "Downloading $target..."
    if curl -sLf "$url" -o "/tmp/$archive"; then
        unzip -q -o "/tmp/$archive" -d "$target_dir"
        rm "/tmp/$archive"
        echo "  -> $target_dir"
    else
        echo "  WARNING: Failed to download $url (may not exist for this version)"
    fi
done

echo "Done. Bundled llama.cpp $VERSION in $OUT_DIR"
