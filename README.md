# binthere — Android

Native Android app for tracking physical items stored in bins, drawers, and containers. Scan a QR code on a bin, see what's in it, check items in and out, get reminders to return borrowed items.

Android counterpart to the iOS app at [binthere](https://github.com/patrickisgreat/binthere). Both share the same Supabase backend but are independent codebases.

## Quick start

Prerequisites:

- JDK 21 (`brew install --cask zulu@21` or equivalent)
- Android Studio (Hedgehog or newer) with Android SDK, Platform 34+, and command-line tools

Configure secrets in `local.properties` (git-ignored):

```
supabase.url=https://xxxxx.supabase.co
supabase.anonKey=eyJhbGc...
```

Build and run:

```bash
./gradlew assembleDebug        # build debug APK
./gradlew installDebug         # install to connected device/emulator
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests (device/emulator required)
./gradlew lint detekt ktlintCheck
./gradlew ktlintFormat         # auto-format
```

## Architecture + conventions

See [CLAUDE.md](CLAUDE.md) for the full architecture, testing, and workflow guide.
