---
name: morphe-cli-reference
description: Morphe CLI v1.8.1 complete command reference — patch, list-patches, list-versions, options-create, install, uninstall. Use when running CLI commands, testing patches, or troubleshooting CLI usage.
---

> **When to use:** User wants to run CLI commands — patching, listing, installing, testing. Always resolve MPP path from `gradle.properties` version first. Always use `morphe-cli.jar` (not full path) and `--keystore Morphe.keystore`. Output patched APKs to `analysis/<app>/builds/`. Use `-f` for split APK base.apk.

# Morphe CLI v1.8.1 Reference

- CLI jar: `morphe-cli.jar` (symlink at project root, run `./setup-cli.sh` to build)
- Keystore: `Morphe.keystore` (project root — use for all patches)
- MPP path: always resolve from `gradle.properties` version (see below)

## Getting the MPP path

Always use this pattern to get the exact MPP file:

```bash
VER=$(grep "^version" paresh-patches/gradle.properties | cut -d= -f2 | tr -d ' ')
MPP="paresh-patches/patches/build/libs/patches-${VER}.mpp"
```

## patch — Patch an APK

```bash
java -jar morphe-cli.jar patch -p "$MPP" [options] input.apk
```

Key flags:
- `-p "$MPP"` — MPP file (required)
- `-o output.apk` — output path (always use `analysis/<app>/builds/`)
- `-i` — install via ADB after patching
- `-e "Name"` / `--ei N` — enable patch by name/index
- `-d "Name"` / `--di N` — disable patch by name/index
- `--exclusive` — disable all except enabled
- `-O key=value` — set patch option
- `--options-file options.json` — use options JSON
- `-f` — force (skip version check, needed for split APK base.apk)
- `--mount` — mount install (root)
- `--unsigned` — skip signing
- `--keystore Morphe.keystore` — use shared keystore
- `--striplibs arm64-v8a` — keep only specified arch
- `--continue-on-error` — don't stop on first failure
- `--purge` — delete temp files after
- `-t path` — custom temp directory
- `-r path` — save result file

### Standard patch

```bash
java -jar morphe-cli.jar patch \
  -p "$MPP" \
  --keystore Morphe.keystore \
  -o analysis/<app>/builds/<app>_patched.apk \
  -f analysis/<app>/<app>_<version>.<ext>
```

### Patch + install

```bash
java -jar morphe-cli.jar patch \
  -p "$MPP" \
  --keystore Morphe.keystore \
  -o analysis/<app>/builds/<app>_patched.apk \
  -f -i analysis/<app>/<app>_<version>.<ext>
```

### Exclusive mode (only specific patches)

```bash
java -jar morphe-cli.jar patch \
  -p "$MPP" \
  --keystore Morphe.keystore \
  --exclusive -e "Premium Unlock" -e "Remove Ads" \
  -o analysis/<app>/builds/<app>_patched.apk \
  -f analysis/<app>/<app>_<version>.<ext>
```

## list-patches — List available patches

```bash
java -jar morphe-cli.jar list-patches --patches "$MPP" -pvo
java -jar morphe-cli.jar list-patches --patches "$MPP" -f com.truecaller -pvo
```

## list-versions — Recommended versions

```bash
java -jar morphe-cli.jar list-versions "$MPP"
java -jar morphe-cli.jar list-versions "$MPP" -f com.truecaller
```

## options-create — Generate options JSON

```bash
java -jar morphe-cli.jar options-create -p "$MPP" -o options.json
```

## utility install / uninstall

```bash
java -jar morphe-cli.jar utility install -a analysis/<app>/builds/<app>_patched.apk
java -jar morphe-cli.jar utility uninstall -p com.example.app
java -jar morphe-cli.jar utility uninstall -p com.example.app -u  # unmount
```

## Full Workflow

```bash
# 1. Build patches
cd paresh-patches && ./gradlew buildAndroid && cd ..

# 2. Get MPP path
VER=$(grep "^version" paresh-patches/gradle.properties | cut -d= -f2 | tr -d ' ')
MPP="paresh-patches/patches/build/libs/patches-${VER}.mpp"

# 3. List patches
java -jar morphe-cli.jar list-patches --patches "$MPP" -pvo

# 4. Patch + install
java -jar morphe-cli.jar patch \
  -p "$MPP" \
  --keystore Morphe.keystore \
  -o analysis/<app>/builds/<app>_patched.apk \
  -f -i analysis/<app>/<app>_<version>.<ext>
```
