#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ANDROID_DIR="$ROOT_DIR/android"
BUILD_FILE="$ANDROID_DIR/app/build.gradle.kts"
DIST_DIR="$ROOT_DIR/dist"

if [[ ! -f "$BUILD_FILE" ]]; then
  echo "Cannot find Android build file: $BUILD_FILE" >&2
  exit 1
fi

BUILD_FILE_BACKUP="$(mktemp)"
cp "$BUILD_FILE" "$BUILD_FILE_BACKUP"

cleanup() {
  local status=$?
  if [[ $status -ne 0 ]]; then
    cp "$BUILD_FILE_BACKUP" "$BUILD_FILE"
    echo "Packaging failed; restored $BUILD_FILE" >&2
  fi
  rm -f "$BUILD_FILE_BACKUP"
  exit "$status"
}

trap cleanup EXIT

VERSION_INFO="$(
  python3 - "$BUILD_FILE" <<'PY'
from pathlib import Path
import re
import sys

path = Path(sys.argv[1])
text = path.read_text()

version_name_re = re.compile(r'(versionName\s*=\s*")([^"]+)(")')
version_code_re = re.compile(r'(versionCode\s*=\s*)(\d+)')

version_name_match = version_name_re.search(text)
version_code_match = version_code_re.search(text)

if not version_name_match:
    raise SystemExit("Could not find versionName in build.gradle.kts")
if not version_code_match:
    raise SystemExit("Could not find versionCode in build.gradle.kts")

current_version_name = version_name_match.group(2)
version_parts = current_version_name.split(".")

if not version_parts or any(not part.isdigit() for part in version_parts):
    raise SystemExit(f"versionName must be numeric dot-separated, got: {current_version_name}")

version_parts[-1] = str(int(version_parts[-1]) + 1)
next_version_name = ".".join(version_parts)
next_version_code = str(int(version_code_match.group(2)) + 1)

text = version_name_re.sub(
    lambda match: f"{match.group(1)}{next_version_name}{match.group(3)}",
    text,
    count=1,
)
text = version_code_re.sub(
    lambda match: f"{match.group(1)}{next_version_code}",
    text,
    count=1,
)
path.write_text(text)

print(next_version_name, next_version_code)
PY
)"

read -r VERSION_NAME VERSION_CODE <<< "$VERSION_INFO"

echo "Packaging Elow $VERSION_NAME (versionCode $VERSION_CODE)"

(
  cd "$ANDROID_DIR"
  ./gradlew :app:assembleDebug :app:assembleRelease
)

mkdir -p "$DIST_DIR"

DEBUG_APK="$ANDROID_DIR/app/build/outputs/apk/debug/app-debug.apk"
RELEASE_UNSIGNED_APK="$ANDROID_DIR/app/build/outputs/apk/release/app-release-unsigned.apk"

cp "$DEBUG_APK" "$DIST_DIR/Elow.apk"
cp "$DEBUG_APK" "$DIST_DIR/Elow-$VERSION_NAME-debug.apk"

if [[ -f "$RELEASE_UNSIGNED_APK" ]]; then
  cp "$RELEASE_UNSIGNED_APK" "$DIST_DIR/Elow-$VERSION_NAME-release-unsigned.apk"
fi

SDK_DIR="$(sed -n 's/^sdk.dir=//p' "$ANDROID_DIR/local.properties" 2>/dev/null | tail -n 1)"
SDK_DIR="${SDK_DIR:-$HOME/Library/Android/sdk}"
AAPT="$SDK_DIR/build-tools/36.1.0/aapt"

if [[ ! -x "$AAPT" && -d "$SDK_DIR/build-tools" ]]; then
  AAPT="$(find "$SDK_DIR/build-tools" -maxdepth 2 -type f -name aapt | sort | tail -n 1)"
fi

if [[ -x "$AAPT" ]]; then
  "$AAPT" dump badging "$DIST_DIR/Elow.apk" | sed -n '1p'
else
  echo "aapt not found; skipped APK badging verification" >&2
fi

echo "Artifacts:"
ls -lh "$DIST_DIR/Elow.apk" \
  "$DIST_DIR/Elow-$VERSION_NAME-debug.apk" \
  "$DIST_DIR/Elow-$VERSION_NAME-release-unsigned.apk" 2>/dev/null || true

trap - EXIT
rm -f "$BUILD_FILE_BACKUP"
