# CLAUDE.md — binthere (Android)

## What is this project?

binthere is a native Android app for tracking physical items stored in bins, drawers, and containers. Users scan a QR code on a bin to instantly see its contents, add items with photos (with AI-powered auto-description), check items in/out, and get reminders to return borrowed items. The goal: never say "where the fuck is this?" again.

This is the Android counterpart to the iOS app at `~/code/binthere`. The two apps share the same Supabase backend (database, auth, RLS policies, storage buckets) but are independent codebases.

## Requirements

1. Scan a QR code on a bin and see what's in it
2. Store pictures of items and associate them to item records
3. AI analyzes photos of bin contents and pre-fills item data (user verifies/edits)
4. Check an item out of a bin
5. Check an item back into a bin
6. Bins have metadata (zone, location, description)
7. Reminders to check items back in
8. Notifications to people who have something checked out
9. Remove items from the system (sold, damaged, etc.)
10. Move items between bins
11. Custom fields and tags on items

## Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **Data**: Room (local persistence) + DataStore (preferences)
- **Backend**: Supabase (Auth, PostgreSQL, Storage) via the official `supabase-kt` SDK
- **AI/Vision**: Claude and OpenAI HTTP APIs (same endpoints used by iOS)
- **QR**: `zxing-android-embedded` for scanning, `zxing-core` for generation
- **Notifications**: `NotificationCompat` + Firebase Cloud Messaging (for cross-device push later)
- **DI**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Build**: Gradle with Kotlin DSL (`build.gradle.kts`)
- **Testing**: JUnit 5 (unit), Compose UI testing + Espresso (instrumented)
- **CI/CD**: GitHub Actions
- **Minimum SDK**: 26 (Android 8.0 — matches iOS 17 floor in market coverage)
- **Target SDK**: latest stable
- **Kotlin**: latest stable, K2 compiler

## Common Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install to connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (needs device/emulator)
./gradlew connectedAndroidTest

# Lint + static analysis
./gradlew lint
./gradlew detekt

# Format (ktlint)
./gradlew ktlintFormat

# Build release bundle for Play Store
./gradlew bundleRelease
```

## Project Structure

```
binthere-android/
├── app/
│   ├── src/main/java/app/binthere/
│   │   ├── MainActivity.kt
│   │   ├── BinthereApp.kt               # Application class (Hilt entry)
│   │   ├── data/
│   │   │   ├── model/                   # Room @Entity classes (Bin, Item, Zone, CheckoutRecord)
│   │   │   ├── dao/                     # Room DAOs
│   │   │   ├── database/                # RoomDatabase subclass, migrations
│   │   │   ├── remote/                  # Supabase DTOs + coding
│   │   │   └── repository/              # BinRepository, ItemRepository, etc.
│   │   ├── service/
│   │   │   ├── SupabaseService.kt       # Client wrapper
│   │   │   ├── AuthService.kt
│   │   │   ├── SyncService.kt
│   │   │   ├── ImageAnalysisService.kt  # Anthropic + OpenAI routing
│   │   │   ├── NotificationService.kt
│   │   │   ├── QRService.kt
│   │   │   └── ImageStorageService.kt
│   │   ├── ui/
│   │   │   ├── theme/                   # Material 3 color scheme, typography
│   │   │   ├── components/              # Reusable Composables (ColorDot, ZoneIcon, etc.)
│   │   │   ├── bins/                    # BinListScreen, BinDetailScreen, AddBinScreen
│   │   │   ├── items/                   # ItemDetailScreen, AddItemScreen, AIAnalysisScreen
│   │   │   ├── zones/                   # ZonesGridScreen, ZoneDetailScreen
│   │   │   ├── scanner/                 # QrScannerScreen
│   │   │   ├── search/                  # SearchScreen
│   │   │   ├── auth/                    # SignInScreen, SignUpScreen
│   │   │   ├── onboarding/              # OnboardingScreen
│   │   │   ├── household/               # HouseholdScreen, InviteSheet
│   │   │   └── settings/                # SettingsScreen
│   │   └── util/                        # Extensions, helpers
│   ├── src/test/                        # Unit tests (JUnit)
│   └── src/androidTest/                 # Instrumented + UI tests
├── build.gradle.kts                     # Project-level
├── app/build.gradle.kts                 # App module
├── settings.gradle.kts
├── gradle/libs.versions.toml            # Version catalog
└── .github/workflows/                   # CI
```

## Architecture

### MVVM with unidirectional data flow

Each screen is a Composable that observes a `ViewModel`. ViewModels expose immutable `UIState` via `StateFlow`. User intents become `ViewModel` function calls, which update state.

```kotlin
@HiltViewModel
class BinListViewModel @Inject constructor(
    private val repository: BinRepository,
) : ViewModel() {
    val state: StateFlow<BinListState> = repository.bins
        .map { BinListState(bins = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BinListState())
}
```

Views don't touch repositories, services, or Room directly. Views observe state, dispatch intents.

### Compose specifics

- One primary Composable per file, matching the filename (`BinListScreen.kt` → `BinListScreen`)
- Prefer stateless Composables with `onEvent: (Event) -> Unit` over stateful ones
- Use `remember { ... }` and `derivedStateOf` carefully — avoid recomposition thrash
- Theme and color pulled from `MaterialTheme.colorScheme` — do not hardcode colors
- Icons: Material Symbols via `androidx.compose.material.icons.extended`
- Previews on every screen Composable (`@Preview`)

### Room specifics

- Every `@Entity` class mirrors a Supabase table name (snake_case → camelCase via Room field mapping)
- Use `Flow<List<Entity>>` return types on DAOs for reactive queries
- Migrations live in `data/database/migrations/` — never destructive migrations in production
- UUIDs stored as `String` (lowercase) to match Supabase Postgres UUID format
- Relationships use foreign keys + `@Relation` on query result classes, not bidirectional graph relationships

### Supabase client

- One `SupabaseClient` instance, injected via Hilt, configured in `SupabaseModule`
- All sync code lives in `SyncService` — it handles push (local → remote) and pull (remote → local) with the same tombstone pattern as iOS
- URL/anon key from `BuildConfig` (populated from `local.properties`, not committed)
- UUIDs always sent lowercased to match Postgres `auth.uid()` case-sensitivity

### State management

- `StateFlow` for UI state that the Composable observes
- `SharedFlow` for one-shot events (navigation, snackbar)
- `collectAsStateWithLifecycle()` in Composables (not plain `collectAsState`) — respects lifecycle
- Never hold a `Context` in a ViewModel

### Navigation

- Jetpack Navigation Compose with typesafe routes (Kotlin serialization-based)
- Single-Activity architecture (no Fragment usage)
- Top-level destinations: Search, Bins, Zones, Scanner, Settings — matches iOS tab bar

### Image pipeline

1. User takes photo or picks from library → `Bitmap`
2. Compressed to JPEG, saved to app's internal storage
3. Path stored on the `Item` Room entity
4. For AI analysis: base64-encoded, posted to Anthropic/OpenAI endpoint
5. AI results presented to user for verification before save

### QR Code Flow

1. Each `Bin` has a unique UUID (matches iOS format exactly — uppercase or lowercase string both acceptable, normalize to lowercase for storage)
2. Scanner decodes QR → looks up bin by UUID → navigates to BinDetailScreen
3. App generates printable QR labels on-device (zxing-core, then Canvas for layout)

## Environment

`local.properties` (git-ignored) contains Supabase credentials:

```
supabase.url=https://xxxxx.supabase.co
supabase.anonKey=eyJhbGc...
```

These are injected into `BuildConfig` by `build.gradle.kts`:

```kotlin
buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("supabase.url")}\"")
```

Never hardcode keys or commit them. Users supply their own Claude/OpenAI API keys in-app (stored in DataStore, scoped per user ID).

Signing keys live in `keystore.properties` (git-ignored) and are referenced from `app/build.gradle.kts`. The actual `.keystore` file never enters the repo — keep it in 1Password/Keychain.

## Conventions

### Kotlin Style

- Follow the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- ktlint enforces formatting — run `./gradlew ktlintFormat` before committing
- detekt catches code smells — must pass in CI
- `val` over `var` always; `var` only when mutation is necessary and local
- Prefer data classes for DTOs and UI state
- Prefer sealed classes/interfaces for finite type hierarchies (navigation events, error states)
- Extension functions for adding behavior to types you don't own
- Null safety: no `!!` in production code; use `?.`, `?:`, `requireNotNull(x) { "msg" }`, or smart casts
- Visibility: default to `private` or `internal`. Only `public` (implicit) when the API surface requires it
- No `Any` or `Any?` unless you have a specific reason (JSON parsing boundaries)
- `Result<T>` over throwing when a call is expected to fail in normal flow (e.g. network)

### Compose Specifics

- Keep Composables under 40 lines — extract subcomposables aggressively
- Parameters ordered: modifier first (default `Modifier`), then data, then callbacks
- `onClick` callbacks take no params unless they need to — use `() -> Unit` not `(SomeEvent) -> Unit` when a single action
- Avoid `LocalContext.current` in ViewModels — pass what you need
- Don't launch coroutines from Composables — use ViewModel
- Use `key(...)` when animating lists with stable IDs
- Testable: accessibility content descriptions on every interactive element (doubles as UI test selector)

### Coroutines

- `viewModelScope` for ViewModel work
- `lifecycleScope` for Activity/Composable-bound work (rare — usually defer to ViewModel)
- `withContext(Dispatchers.IO)` for database, network, disk
- Never `runBlocking` outside of tests
- Flow over LiveData for new code
- `first()` / `firstOrNull()` when you need a single value from a Flow; otherwise collect

### Room Specifics

- DAO queries return `Flow<T>` for reactive, `suspend fun` for one-shot
- Transactions via `@Transaction` on DAO functions that do multiple writes
- Never expose Room entities as UI models — map to UI state classes in the repository or ViewModel
- Migrations are explicit — no `fallbackToDestructiveMigration()` in release builds
- Use `@Embedded` for composed fields, `@Relation` for joined fetches

## Code Standards

### Clean Code

- **DRY**: Extract reusable logic. If you see duplication in two places, it's a coincidence. Three or more — refactor.
- **SRP**: Every class and function does one thing. If a function name needs "and," split it.
- **Small, focused files**: One primary class per file. Split large Composables into subcomposable files.
- **Never over-engineer**: Minimum code to solve the problem. No speculative abstractions or "just in case" interfaces.
- **Naming**: Descriptive and intention-revealing. Code reads like prose. Minimize comments by making code self-documenting.
- **No dead code**: Remove unused imports, variables, functions, and commented-out code.

### Error Handling

- Use `Result<T>` or sealed `UiState` types for boundaries that can fail
- Never swallow exceptions. At minimum, log with `Log.e(TAG, "context", e)` and surface a user-visible error
- Network errors → retry logic with exponential backoff where appropriate
- Present user-facing errors with clear actionable messages, not raw stacktraces

### Security

- Never commit secrets, tokens, or keystores
- Validate and sanitize user input at system boundaries
- Use Supabase RLS for all tables — it's the final defense, not just "nice to have"
- Store sensitive user data in EncryptedSharedPreferences or the Android Keystore, not DataStore/prefs
- Request only the permissions needed, just-in-time
- Network security config pins HTTPS — no cleartext traffic in production builds

## Testing

No PR is mergeable without tests that cover the behavior introduced or changed.

### The Testing Pyramid

```
        /\
       /  \
      / UI  \
     /--------\
    /  Integra- \
   /   tion      \
  /----------------\
 /    Unit Tests    \
/--------------------\
```

### Unit Tests (JUnit)

- Every new ViewModel, service, and repository gets unit tests
- Test behavior, not implementation. A test that breaks on internal rename is wrong
- Tests must be fast — no network, no database, no filesystem. Mock at the boundary (MockK for Kotlin)
- Room queries: use `Room.inMemoryDatabaseBuilder()` for tests needing a real DB
- Name tests descriptively: `checkout_setsExpectedReturnDate()` not `test1()`

### Integration Tests

- Test service boundaries: Supabase interactions, image storage pipeline, notification scheduling
- Use real Room containers (in-memory) for data layer tests
- Test the contract at the boundary, not internals

### Instrumented / UI Tests

- Cover critical journeys: scan QR → view bin, add item with photo, check item out, check item back in
- Use accessibility content descriptions (or test tags) for element lookup — doubles as a11y compliance
- No `Thread.sleep()` — use Compose test `waitUntil` and idling resources
- Tests must be deterministic. A flaky UI test is a broken test

### What is not an acceptable excuse

- "It's just a small change." — Small tests for small changes
- "It's hard to test." — Make it testable. Difficulty is a design signal
- "I'll add tests in a follow-up PR." — Tests ship with the feature or the PR doesn't merge

## Git Workflow

- **Always work from a feature branch.** Never commit to `main`. Use descriptive names: `feat/qr-scanner`, `fix/checkout-date-bug`
- **Commit often.** Small, frequent commits, each a logical unit. No batched unrelated changes
- **Keep PRs reviewable.** Small enough for a reviewer to understand in one sitting. Break large features into incremental slices
- **Conventional commit messages:**
  - `feat:` — New feature
  - `fix:` — Bug fix
  - `refactor:` — Code restructure, no behavior change
  - `test:` — Tests only
  - `chore:` — Build, CI, deps, tooling
  - `docs:` — Documentation
  - `perf:` — Performance improvement
  - `style:` — Formatting only
- **Messages describe _what_ and _why_, not _how_.** `feat: add AI-powered item description from photo` not `update Item.kt`
- **Submit PRs via `gh pr create`.** Titles with conventional prefixes. Include summary and test plan
- **The user reviews all PRs before merge.** Do not merge autonomously
- **NEVER add `Co-Authored-By` or "Generated with Claude Code" lines to commits or PRs**

## iOS Parity Notes

When implementing a feature that already exists on iOS, reference the iOS implementation at `~/code/binthere` first. The data model names (Bin, Item, Zone, CheckoutRecord, CustomAttribute, Household, HouseholdMember, Invitation) and Supabase table schemas must match exactly — Android and iOS users share a household.

Don't re-design features from scratch. Port them. Surface-level platform idioms (navigation patterns, Material vs. Apple HIG) are the expected difference; core logic should be identical.

Session data that's stored remotely (households, bins, items, zones) must produce byte-identical payloads from either platform. UUIDs always lowercased. Timestamps always ISO8601 UTC.
