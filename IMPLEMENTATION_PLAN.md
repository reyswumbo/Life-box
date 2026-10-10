# IMPLEMENTATION PLAN

## Phase 1: Repository Inspection

Inspect the repository.

Determine whether an Android project already exists.

Identify the Gradle version, Android Gradle Plugin version,
JDK requirements, package name, and existing dependencies.

Do not overwrite working files unnecessarily.

Deliver:
- Project structure
- Initial build configuration
- Basic application entry point
- Initial navigation
- Successful debug build

## Phase 2: Database and Link Management

Implement Room entities, DAOs, repositories, and ViewModels.

Implement:
- URL validation
- Link insertion
- Duplicate detection
- Link deletion
- Link editing
- Link search
- Link sorting

Deliver a functional local link manager.

## Phase 3: Collection Management

Implement default collections.

Implement:
- Create collection
- Rename collection
- Delete collection
- Move links
- Collection filtering
- Collection counts
- Safe deletion behavior

Deliver a functional collection system.

## Phase 4: Virtual Pet Renderer

Implement a reusable pet renderer.

Separate body, eyes, mouth, accessories, and texture.

Implement idle animations and expressions.

Deliver a pet that can move and react.

## Phase 5: Link-Eating Animation

Implement the receiving, mouth-opening, eating,
swallowing, and celebrating states.

Connect the animation to the actual link-saving workflow.

Deliver a complete save-link interaction.

## Phase 6: Pet Customization

Implement body shapes, eye styles, accessories,
colors, textures, and character naming.

Implement a live preview.

Persist customization in Room.

Deliver a customizable pet.

## Phase 7: Visual Polish

Implement the final Home, Saved, and Pet Lab layouts.

Follow DESIGN_SPEC.md.

Improve spacing, typography, icons, empty states,
error messages, and transitions.

Deliver a consistent user interface.

## Phase 8: Testing and Optimization

Add unit tests.

Run Android lint.

Enable R8 and resource shrinking.

Optimize assets.

Verify offline functionality.

Inspect the release APK.

Deliver a verified release build.

## Phase 9: GitHub Actions

Create the Android CI workflow.

Run tests and lint.

Build the release APK.

Upload the APK artifact.

Document release signing requirements.

Deliver an automated build pipeline.

## Rules

Complete one phase at a time.

Build and test after each phase.

Do not implement all phases in one enormous code change.

Do not claim success when a build or test has failed.

Record completed work and remaining tasks in this file.

---

## Progress Log

### Phase 1: Repository Inspection — COMPLETED

Status: verified by GitHub Actions run `38043143666` on commit
`cb051bd` (unit tests passed, Android lint passed, release APK
built and uploaded as artifact `release-apk`, 11,538,366 bytes).

Delivered:

- Repository hygiene: the specification files were moved from the
  stray `1/` directory to the repository root. `AGENTS.md.` (with a
  trailing period) was renamed to `AGENTS.md`.
- Gradle project foundation with Gradle Kotlin DSL and a version
  catalog (`gradle/libs.versions.toml`).
- Gradle wrapper (Gradle 8.14).
- `:app` module with `com.reyswumbo.lifeinbox` namespace and
  `applicationId`.
- Application entry point `MainActivity` (edge-to-edge,
  `setContent`).
- Design tokens from DESIGN_SPEC.md (`ui/theme/Color.kt`,
  `Type.kt`, `Theme.kt`).
- Navigation shell: `NavHost` with Home / Saved / Pet Lab routes
  and a floating rounded bottom navigation bar (`ui/LifeInboxApp.kt`).
- Placeholder screens for the three tabs (to be fully implemented
  in later phases).
- Android CI workflow (`.github/workflows/android.yml`) added
  early so every phase is built and verified by GitHub Actions.
- Unit test `AppTabTest` verifying tab routes and labels.

Verified dependency stack (all resolved and built in CI):

| Component | Version |
|---|---|
| Gradle | 8.14 |
| Android Gradle Plugin | 8.13.2 |
| Kotlin / Compose compiler plugin | 2.2.21 |
| Compose BOM | 2025.11.01 |
| Material 3 | via Compose BOM |
| Navigation Compose | 2.9.8 |
| AndroidX Activity Compose | 1.12.0 |
| AndroidX Core KTX | 1.17.0 |
| compileSdk / targetSdk | 36 |
| minSdk | 24 |
| JDK | 17 |

Architecture decisions:

- Single `:app` module; lightweight MVVM to be layered in later
  phases (`data`, `domain`, `ui`).
- No Hilt: manual dependency injection via an application
  container, to keep dependencies minimal.
- Local Gradle builds are intentionally not used because the
  development machine is resource-constrained. GitHub Actions is
  the source of truth for build, test, and lint results.

Issues found and resolved during Phase 1:

- The initial conservative stack (AGP 8.7.3, compileSdk 35) failed
  `checkDebugAarMetadata`: AndroidX Core 1.17.0, Activity 1.12.0,
  and transitive NavigationEvent 1.0.0 require compileSdk 36 and
  AGP 8.9.1 or higher. Resolved by upgrading to AGP 8.13.2,
  Gradle 8.14, and compileSdk/targetSdk 36.
- Deprecated Node.js 20 based GitHub Actions were bumped
  (checkout@v7, setup-java@v6, gradle/actions/setup-gradle@v6).

Remaining tasks and notes for upcoming phases:

1. Phase 2 must add the KSP plugin. Use KSP `2.2.21-2.0.5`
   (matches Kotlin 2.2.21) and Room `2.8.5`.
2. The release build currently has `isMinifyEnabled = false` and
   `isShrinkResources = false`. Enable R8 and resource shrinking
   in Phase 8, then re-measure the APK.
3. The release APK is currently unsigned. Release signing via
   GitHub Actions secrets is documented/added in Phase 9.
4. The launcher icon is a temporary vector placeholder; a proper
   adaptive icon and asset optimization belong to Phase 7/8.
5. Android lint currently passes with no baseline; keep it clean.
6. Theme is light-only, matching the warm pastel direction of
   DESIGN_SPEC.md. Revisit in Phase 7.
7. Navigation currently uses string routes; type-safe routes are
   an optional refinement for a later phase.

