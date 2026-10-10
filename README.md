# Life Inbox

A lightweight, offline-first link manager with a customizable animated
virtual pet, built for Android.

Save links, organize them into collections, and watch your pet happily
eat each one. Personalize your companion's body, eyes, accessories,
colors, and texture in the Pet Lab.

## Features

- Home
  - Save links to any collection with one tap.
  - Your pet reacts to each save with a coordinated
    eat-the-link animation.
  - Live count of links saved today (computed from saved records).
  - A cozy, calm layout with soft room decorations.
- Saved
  - Search your library by title, URL, domain, or notes.
  - Sort links and filter by collection.
  - Open, copy, edit, move, favorite, and delete links.
  - Create, rename, and safely delete collections (links move to Inbox).
- Pet Lab
  - Customize body shape, eyes, accessories, colors, and texture.
  - Live preview that reacts to your changes.
  - Character naming; everything persists across restarts.

The core experience works fully offline. No network permission is used.

## Technology

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose, ViewModel, Kotlin Coroutines, StateFlow
- Room for local persistence
- Compose Canvas + a centralized pet animation controller
- MVVM architecture with repositories isolating persistence

## Building

Requirements: JDK 17 and an Android SDK.

```bash
./gradlew assembleRelease
```

The signed, installable release APK is produced at:

```
app/build/outputs/apk/release/app-release.apk
```

R8 and resource shrinking are enabled for release builds. The release
build signs with the debug keystore by default so the artifact is
installable. To sign with a real release key in CI, set the
`KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD`
environment variables (e.g. GitHub Actions secrets); the keystore file
itself must never be committed to the repository.

Other useful tasks:

```bash
./gradlew testDebugUnitTest   # run unit tests
./gradlew lintDebug           # run Android lint
./gradlew installDebug        # build and install a debug build
```

## Installing

1. Build the release APK as described above (or download it from the
   GitHub Actions artifacts of `android.yml`).
2. Transfer the APK to your device or emulator.
3. Open the APK and confirm the install when prompted
   (allow "install unknown apps" if needed).

Requires Android 7.0 (API 24) or newer.

## Continuous Integration

`.github/workflows/android.yml` runs on every push to `main` and every
pull request, and can be triggered manually. It runs unit tests, Android
lint, and builds the release APK, then uploads the APK as an artifact.

## Design

See `DESIGN_SPEC.md` for the visual language and interaction design, and
`IMPLEMENTATION_PLAN.md` for the phase-by-phase implementation record.