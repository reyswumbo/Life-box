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

1. KSP `2.2.21-2.0.5` and Room `2.8.5` were added in Phase 2 and
   build cleanly in CI.
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

### Phase 2: Database and Link Management — COMPLETED

Status: verified by GitHub Actions run `38044009630` on commit
`cb78bd1` (32 unit tests passed, Android lint passed, release APK
built and uploaded as artifact `release-apk`, 11,800,836 bytes;
Room schema uploaded as artifact `room-schemas`).

Delivered:

- Room persistence: `LifeInboxDatabase` (version 1, `exportSchema
  = true`), entities `LinkEntity` and `CollectionEntity`, DAOs
  `LinkDao` and `CollectionDao`.
- Default collections seeded on first database creation (Inbox as
  the default, plus Ideas and Inspiration) with stable IDs.
- Repository layer: `LinkRepository` and `CollectionRepository`
  isolating all persistence concerns from the UI.
- URL validation and normalization in pure Kotlin
  (`domain/UrlValidator.kt`): accepts http/https, rejects
  malformed input and unsupported schemes, lowercases scheme/host,
  strips default ports and trailing slashes so duplicates are
  detected reliably.
- `LinkRepository.addLink` returns a typed `SaveLinkResult`
  (Success / Duplicate / InvalidUrl / Error) and never discards the
  user's input on failure.
- `SavedViewModel` + `SavedUiState` expose a searchable, sortable
  `StateFlow` stream combining links and collections, with a
  one-shot `SavedEvent` channel for saved/updated/message events.
- `SavedScreen` fully implemented: search field, sort menu
  (`LinkSortOption`), link cards with favorite toggle, overflow
  menu (Open / Edit / Copy URL / Delete), add/edit dialog with
  collection picker, floating action button, snackbars, and
  loading/empty states. Links open via `Intent.ACTION_VIEW`.
- Manual dependency injection via `AppContainer`, wired through
  `LifeInboxApplication` (`android:name` in the manifest).
- CI now also uploads the generated Room schema as `room-schemas`.

Tests added (all passing in CI):

- `UrlValidatorTest` — scheme handling, normalization, and
  rejection cases.
- `LinkSortOptionTest` — sorting order for every option.
- `LinkRepositoryTest` — Robolectric + in-memory Room verifying
  seed data, insert, duplicate detection (exact and normalized),
  invalid URL rejection, delete, update, favorite toggle, and
  search.

Issues found and resolved during Phase 2:

- Trailing-slash normalization tests failed on the first CI run
  (run `38043830986`); the validator now strips trailing slashes
  before comparing, so `.../Path` and `.../Path/` are treated as
  the same link.

Remaining tasks and notes for upcoming phases:

1. Release build still has `isMinifyEnabled = false` and
   `isShrinkResources = false` (Phase 8).
2. Collection management (create / rename / delete / move / filter
   / counts / safe deletion) is Phase 3. The link editor already
   allows choosing an existing collection.
3. `Home` and `Pet Lab` tabs remain placeholders until Phases 4-7.
4. Consider a UI test for the save-link workflow in a later phase.

### Phase 3: Collection Management — COMPLETED

Status: verified by GitHub Actions run `38044479936` on commit
`6b48b02` (all unit tests passed, Android lint passed, release APK
built and uploaded).

Delivered:

- `CollectionDao` extended with insert, case-insensitive name
  lookup, `observeWithCounts` (per-collection link counts via a
  correlated subquery), rename/update, link reassignment, and a
  `@Transaction deleteAndReassign` that moves a collection's links
  to the fallback collection before deleting it.
- `CollectionWithCount` projection (embedded entity + link count).
- `CollectionRepository` now exposes `createCollection`,
  `renameCollection`, and `deleteCollection`, all returning a typed
  `CollectionResult` (Success / DuplicateName / Invalid / Error).
  Names are trimmed, limited to 40 characters, and must be unique
  (case-insensitive). The default Inbox collection cannot be
  deleted; deleting a non-empty collection moves its links to Inbox
  in a single transaction, so no links are silently lost.
- `LinkRepository.moveLink` to change a link's collection.
- `SavedViewModel` now streams collections-with-counts plus a
  `selectedCollectionId` filter, and handles create / rename /
  delete / move with separate saving and error state for the
  collection dialog.
- Saved UI: horizontally scrollable collection shelf (All +
  Inbox + Ideas + Inspiration + custom) with per-collection
  counts; selected-state styling; long-press menu to rename or
  delete (delete hidden for the default collection); a header
  add-menu (Add link / New collection); a collection editor dialog
  with icon and color pickers (`CollectionVisuals` curated palette);
  a delete-confirmation dialog that explains the Inbox fallback
  policy; and a Move dialog in each link's overflow menu.

Tests added (all passing in CI):

- `CollectionRepositoryTest` — create, case-insensitive duplicate
  rejection, blank/overlong name rejection, rename (including
  keeping the same name and rejecting clashes), safe delete that
  reassigns links to Inbox, default-collection delete rejection,
  unknown-collection deletion, count accuracy, and link moving.

Notes for upcoming phases:

1. `Home` and `Pet Lab` tabs remain placeholders until Phases 4-7.
2. Release build still lacks R8 / resource shrinking (Phase 8).



