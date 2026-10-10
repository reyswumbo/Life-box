# LIFE INBOX — DEVELOPMENT INSTRUCTIONS

## Role

Act as a senior Android engineer and UI/UX implementation
specialist.

Build Life Inbox as a complete Android application.

Do not stop at mockups, screenshots, or placeholder screens.

All required interactions must be functional.

## Primary Objective

Create a lightweight, offline-first link manager
with a customizable animated virtual pet.

The application must be buildable through GitHub Actions.

## Read These Files First

Before implementing features, read:

- DESIGN_SPEC.md
- IMPLEMENTATION_PLAN.md
- README.md

Treat DESIGN_SPEC.md as the source of truth for visual
requirements.

## Technology Stack

Use:

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Kotlin Coroutines
- StateFlow
- ViewModel
- Compose Canvas
- Animatable
- Gradle Kotlin DSL
- R8
- GitHub Actions

Use DataStore for preferences when appropriate.

Avoid unnecessary dependencies.

Use compatible stable versions of Android libraries.

Do not invent dependency versions.

Verify compatibility before introducing a new dependency.

## Architecture

Use a maintainable MVVM architecture.

Separate:

- UI
- UI state
- ViewModels
- Domain logic
- Repositories
- Database entities
- Database access objects
- Pet animation logic

Avoid placing database operations directly inside composable
functions.

Use repositories to isolate persistence.

Keep composables focused on rendering and user interaction.

## Implementation Rules

1. Inspect the existing repository before modifying files.
2. Preserve working code.
3. Implement features incrementally.
4. Build after major changes.
5. Run relevant tests.
6. Fix compilation errors before continuing.
7. Avoid placeholder implementations for required features.
8. Do not claim that a feature is complete without verifying it.
9. Keep dependencies minimal.
10. Document significant architectural decisions.

If a requirement is ambiguous, choose a simple and maintainable
implementation that follows DESIGN_SPEC.md.

Do not repeatedly ask for approval for ordinary implementation
decisions.

## Data Persistence

Use Room for:

- Links
- Collections
- Pet configuration

Persist all user-created links and collections.

Persist the selected pet appearance and character name.

Do not persist temporary animation states.

Handle database errors explicitly.

Use transactions for operations that modify related records.

Do not silently discard user data when a collection is deleted.

## Link Saving

Implement URL validation and duplicate detection.

Support standard HTTP and HTTPS links.

Reject malformed URLs.

Allow users to select a destination collection.

Save links locally.

Keep metadata retrieval optional.

The application must remain usable without network access.

When a database operation fails, preserve the user's input
and show an appropriate error message.

Prevent duplicate operations caused by repeated taps.

## Collection Management

Implement:

- Collection creation
- Collection renaming
- Collection deletion
- Collection filtering
- Collection counts
- Moving links
- Default Inbox collection

The All Links view must be derived from saved records.

Do not duplicate links to populate collection views.

## Pet Rendering

Implement a reusable pet renderer.

Separate body, eyes, mouth, accessories, colors, and textures.

Prefer Canvas and lightweight drawing operations for
geometric components.

Use reusable image assets for complex textures.

Support the customization options described in DESIGN_SPEC.md.

Do not create separate full-size images for every possible
combination of pet features.

## Animation Engine

Implement a centralized pet animation controller.

Support:

- IDLE
- BLINKING
- LOOKING
- CURIOUS
- RECEIVING
- OPEN_MOUTH
- EATING_LINK
- SWALLOWING
- CELEBRATING
- SLEEPING
- CONFUSED
- CUSTOMIZING

Prevent conflicting animations.

Ensure animations can be cancelled safely.

Stop or reduce idle animation work when the pet is not visible.

Implement the link-eating animation using coordinated
position, scale, mouth, and body movements.

The animation must not replace database persistence.

## UI

Implement the three main tabs:

- Home
- Saved
- Pet Lab

Follow DESIGN_SPEC.md.

Use responsive layouts.

Respect Android system insets.

Handle keyboard visibility.

Use consistent design tokens for colors, spacing,
typography, and rounded corners.

Provide loading, error, and empty states.

## Testing

Add tests for:

- URL validation
- Duplicate detection
- Link insertion
- Link deletion
- Collection creation
- Collection deletion
- Moving links between collections
- Pet configuration persistence
- Daily link counting

Run tests after implementing the corresponding features.

Add UI tests for critical user workflows when practical.

## Optimization

Enable R8 and resource shrinking for release builds.

Remove unused dependencies.

Compress raster assets.

Prefer vector graphics for simple icons.

Avoid unnecessary network requests and background tasks.

Verify the actual release APK.

Do not claim a specific APK size without measuring it.

## GitHub Actions

Create:

.github/workflows/android.yml

The workflow must:

- Trigger on pushes to main
- Trigger on pull requests targeting main
- Support manual execution
- Configure a compatible JDK
- Configure Gradle
- Cache dependencies
- Run unit tests
- Run Android lint
- Build the release APK
- Verify the APK output
- Upload the APK as an artifact

Use secure signing practices.

Never commit signing keys or passwords.

Use GitHub Actions secrets when release signing is required.

## Required Workflow

Phase 1:
Inspect the repository and establish the project structure.

Phase 2:
Create the application shell and navigation.

Phase 3:
Implement Room and link management.

Phase 4:
Implement collection management.

Phase 5:
Implement the pet renderer and animation controller.

Phase 6:
Implement pet customization and persistence.

Phase 7:
Polish the UI according to DESIGN_SPEC.md.

Phase 8:
Implement tests and optimize the release build.

Phase 9:
Implement and verify GitHub Actions.

## Completion Criteria

The project is complete only when:

- All three primary tabs work.
- Links persist after application restart.
- Collections work correctly.
- Search and filtering work.
- Pet customization persists.
- The pet performs idle animations.
- The pet reacts to link-saving actions.
- The application works offline for core functionality.
- Automated tests pass.
- Android lint has been run.
- A release APK is produced.
- GitHub Actions uploads the APK successfully.
- README.md explains how to build and install the application.
