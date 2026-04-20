# Roadmap — binthere (Android)

Living plan for building the Android app to parity with iOS. Each row below is one pull request, sized to stay reviewable in a single sitting. Order may shift as we learn — update this file when it does.

Status legend: ☐ not started · ◐ in progress · ☑ merged.

## Near-term: feature parity foundation

| PR | Status | Slice | Scope | Unblocks |
|---:|:---:|---|---|---|
| 1 | ◐ | Initial Android project scaffold | Gradle wrapper, version catalog, Compose + Hilt + Room + supabase-kt wired, 5-tab bottom-nav shell, CI (build/test/lint/detekt/ktlint), placeholder stubs + Preview on every screen. | Everything |
| 2 | ☐ | Room schema + Supabase DTOs | Entities (`Bin`, `Item`, `Zone`, `CheckoutRecord`, `CustomAttribute`, `Household`, `HouseholdMember`, `Invitation`), DAOs returning `Flow`, `RoomDatabase` + v1 migration, `@Serializable` DTOs with entity mappers, repository interfaces (pure data, no network yet). Unit tests: in-memory Room round-trips + mapper coverage. | Everything below |
| 3 | ☐ | Auth + onboarding | Supabase email/password, session persistence in `EncryptedSharedPreferences`, `AuthViewModel` state machine (`SignedOut` / `Loading` / `SignedIn`), SignIn / SignUp / Onboarding screens, nav gate for unauthenticated users. | Anything needing `user_id` |
| 4 | ☐ | Bin list + detail (read path) | `BinListScreen`, `BinDetailScreen` (items inside), typed nav with Bin UUID, `BinRepository.bins()` `Flow`, `SyncService` pull-only path. | QR scan target, Add Bin |
| 5 | ☐ | QR scanner → bin detail | CameraX + zxing scanner, camera permission rationale, UUID lookup, unknown-QR handling, instrumented test with a fixture QR in assets. | — |
| 6 | ☐ | Create / edit bins + zones | `AddBinScreen`, edit flow on detail, `ZonesGridScreen` + `ZoneDetailScreen`, upsert + soft-delete tombstones, `SyncService` push path, on-device printable QR generation (Canvas + zxing-core) with a share Intent. | Items |
| 7 | ☐ | Add / edit items (no AI) | `AddItemScreen`, `ItemDetailScreen`, `ImageStorageService` (capture → compress → internal storage → Supabase Storage), custom fields + tags, move-between-bins action. | AI, checkout |
| 8 | ☐ | AI auto-description | `ImageAnalysisService` with Anthropic + OpenAI routing; user keys stored in `EncryptedSharedPreferences`; `AIAnalysisScreen` flow (shoot → loading → verify/edit → save). Payloads byte-identical to iOS. | — |
| 9 | ☐ | Checkout + check-in + local reminders | Checkout on items (borrower + due date), `CheckoutRecord` writes, WorkManager-scheduled `NotificationCompat` reminders, tap → ItemDetail. | — |
| 10 | ☐ | Households + invitations | Household screen, invite-code generation + share, accept flow, end-to-end verified with iOS. | Cross-device push |
| 11 | ☐ | FCM push | Firebase token registration, server trigger (likely a Supabase edge function shared with iOS), notify on checkout-by-someone-else and overdue events. | — |
| 12+ | ☐ | Polish | Emulator in CI for `connectedAndroidTest`, Crashlytics, a11y audit, Play signing + first internal-track release. | — |

## Guiding principles

- **Each PR ships with tests.** Per CLAUDE.md, no "tests in a follow-up PR."
- **Parity over redesign.** When porting a feature, open the matching iOS file at `~/code/binthere` first and mirror the logic. Only platform idioms should differ.
- **Byte-identical remote payloads.** Lowercase UUIDs, ISO8601 UTC timestamps. Bake this into the DTO layer in PR #2 so it doesn't drift later.
- **Small PRs over big ones.** If a slice above is growing past ~600 LOC net, split it.
- **Local toolchain first.** Install JDK 21 + Android Studio before PR #2 — Room schema work really needs KSP running locally for a tolerable feedback loop. CI alone is too slow.

## Open decisions

These affect slice ordering or scope. Answer before starting the corresponding PR.

1. **Slice 2 ↔ 3 ordering.** Data-first (as above) lets us wire UI against seeded fakes; auth-first means no screen renders without a real user. Default: data-first.
2. **AI provider for v1.** iOS routes Anthropic + OpenAI. Keep both on Android, or Anthropic-only for v1 with OpenAI as a follow-up?
3. **Reminder delivery.** Local `WorkManager` notifications only in PR #9, then FCM in PR #11? Or skip local and go straight to FCM?
4. **Household creation semantics.** First user at signup auto-creates a household (and later invites others), or explicit post-signup setup step? Nail down before PR #3.

## How to update this file

- Flip status from ☐ → ◐ when a branch opens, ◐ → ☑ when the PR merges.
- Add new rows for slices discovered mid-flight — don't silently expand an existing one.
- Decisions once made: move from "Open decisions" into the slice they affected, as a one-line note.
