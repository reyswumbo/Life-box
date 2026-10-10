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
