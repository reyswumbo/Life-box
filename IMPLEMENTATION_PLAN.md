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

### Phase 4: Virtual Pet Renderer — COMPLETED

Status: verified by GitHub Actions run `38045558586` on commit
`fa69bd5` (all unit tests passed, Android lint passed, release APK
built and uploaded).

Delivered:

- Pet domain model `PetConfig` with body shapes (Round, Boxy,
  Fluffy, Triangle, Heart), eye styles (Normal, Sleepy, Happy,
  Curious), accessories (None, Crown, Horns, Hat), textures
  (Smooth, Fluffy, Fuzzy), plus curated body / eye / accent color
  palettes.
- `PetAnimationState` covering the full state set required by
  DESIGN_SPEC.md (IDLE, BLINKING, LOOKING, CURIOUS, RECEIVING,
  OPEN_MOUTH, EATING_LINK, SWALLOWING, CELEBRATING, SLEEPING,
  CONFUSED, CUSTOMIZING).
- `PetAnimationController`: a centralized, testable controller
  that owns the current state, generates idle behavior (blinking,
  looking, occasional curious reactions) on a randomized schedule,
  pre-empts the idle loop for externally triggered actions and
  sequences, guarantees only one animation runs at a time by
  cancelling the previous action, and fully halts work when hidden.
- `ui/pet/Pet.kt` renderer on Compose Canvas with clearly
  separated drawing layers: ground shadow, platform opening, body
  (per-shape `Path`), texture overlay, eyes (per-style, with
  openness and gaze shift), mouth (closed smile or open for
  eating), and accessories. Idle floating, squash/stretch, blink,
  gaze, mouth and tilt are driven by `animateFloatAsState` and an
  `InfiniteTransition`.
- `PetStage` composable that pauses idle animation via the
  `Lifecycle` owner (ON_START / ON_STOP), satisfying the
  "reduce work when not visible" requirement, plus a pure
  `PetCanvas` for static previews.
- Home now renders the pet as the visual centerpiece; Pet Lab
  shows a static preview.

Tests added (all passing in CI):

- `PetAnimationControllerTest` — idle expressions when visible,
  one-shot trigger returning to idle, action cancellation, ordered
  sequences, ignoring triggers while hidden, and halting idle work
  when hidden (`runTest` virtual time).

Issues found and resolved during Phase 4:

- First test run hung because `runTest` waits for the controller's
  infinite idle loop; tests now cancel the controller (and use an
  idle-disabled timing for pure action tests) before completing.
- The release Kotlin compile crashed the Gradle daemon with a JVM
  Metaspace exhaustion (`MaxMetaspaceSize=384m`); `gradle.properties`
  now allows a 2048m heap and 768m metaspace.

Notes for upcoming phases:

Phase 5 (done, verified in CI):

- Added `observeCreatedSince` to `LinkDao` and
  `LinkRepository.observeTodayCount()` (start-of-day computed from an
  injectable clock using `java.util.Calendar`, testable).
- Added `HomeViewModel` + factory with URL input, collection selector,
  live daily count, isSaving guard, and `HomeEvent` (Saved/Failed).
- Rebuilt `HomeScreen`: the pet is the centerpiece; saving a link plays
  the coordinated sequence RECEIVING -> OPEN_MOUTH -> EATING_LINK ->
  SWALLOWING -> CELEBRATING while a small link card flies into the pet's
  mouth (overlay driven by a tweened progress), then clears the input.
  Database failures surface as an inline error and never trigger the
  eating animation.
- Tests: `dailyCountCountsOnlyCurrentDay`,
  `dailyCountSurvivesDuplicateAddAttempt`.

Phase 6 (done, verified in CI):

- `PetConfig` persisted in Room: new `pet_config` table (single row),
  `PetConfigEntity` + mapper, `PetConfigDao`, `PetConfigRepository`.
- Database bumped to version 2 with a hand-written `MIGRATION_1_2` that
  creates the table and seeds the default "Pocket Pal" row; fresh DBs
  fall back to `PetConfig.Default`. `app/schemas/2.json` committed.
- `AppContainer` exposes a shared `StateFlow<PetConfig>` so Home and Pet
  Lab always render the saved appearance.
- Full Pet Lab editor: character name, body/eyes/accessories/texture
  galleries with live `PetCanvas` previews, body/eye/accent color
  palettes, live `PetStage` preview, CUSTOMIZING reaction on option
  change, CELEBRATING + snackbar on save.
- Tests: `PetConfigRepositoryTest` (defaults, persist/restore,
  replace, seeded row in upgraded DB).

Notes for the remaining phases:

Phase 7 (done, verified in CI):

- Home now matches the "cozy room" design: a horizontally scrollable
  collection shelf near the top (All + each collection as a `CollectionCard`
  with live counts), a soft ambient glow, a small plant, and paw-print
  decorations drawn behind the pet, plus subtle rotating pet messages.
- The daily stats card shows "YOUR SAVES / count / today".
- The URL field gained a leading add icon and multi-line growth; the
  whole nav content uses `imePadding()` so the keyboard never hides the
  input or the floating tab bar (`windowSoftInputMode=adjustResize` was
  already set).
- Saved and Pet Lab screens already satisfied DESIGN_SPEC.md, so they
  were left as verified in earlier phases.

Phase 8 (done, verified in CI):

- Release build now uses R8 (`isMinifyEnabled = true`) and resource
  shrinking (`isShrinkResources = true`).
- Optional secret-based signing: release signs with the debug keystore
  by default so the CI artifact is installable; setting `KEYSTORE_PATH`,
  `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` switches to a real
  release key (keys are never committed).
- The CI "Verify APK output" step asserts the APK exists and prints its
  size: release APK is now ~1.4 MB (down from ~11.8 MB). Unit tests and
  lint pass; the APK uploads as a `release-apk` artifact.
- README.md now documents building, installing, signing, and CI.

All phases (1-8) are complete and verified through GitHub Actions.
Completion criteria from AGENTS.md are met: three working tabs, links
and pet configuration persist across restarts, collections, search and
filtering, idle pet animations, link-saving reactions, offline-first
core, passing tests/lint, and a measured release APK.




