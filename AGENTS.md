# OpenTune Knowledge Base

**Generated:** 2026-01-29
**Platform:** Android 15 (API 35)
**Stack:** Kotlin, Compose, Hilt, Room, Media3, Ktor

## OVERVIEW
OpenTune is an open-source YouTube Music client for Android. It uses a multi-module architecture to separate the Android app (`app`) from the API client (`innertube`) and other utilities. It emphasizes privacy, no ads, and Material Design 3.

## STRUCTURE
```
.
├── app/                      # Main Android Application (UI, DB, Service)
├── innertube/                # YouTube Music API Client (Ktor)
├── kugou/ & lrclib/          # Lyrics Providers
├── kizzy/                    # Discord RPC Integration
├── material-color-utilities/ # Dynamic Theming Engine (Google)
├── fastlane/                 # Metadata & Release Automation
└── gradle/                   # Build Config & Version Catalog
```

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| **UI Components** | `app/.../ui/component/` | Reusable Compose elements |
| **Screens** | `app/.../ui/screens/` | Feature screens & ViewModels |
| **Navigation** | `app/.../NavigationBuilder.kt` | NavGraph definitions |
| **Playback** | `app/.../playback/MusicService.kt` | Media3 Service |
| **Database** | `app/.../db/` | Room entities & DAO |
| **YouTube API** | `innertube/.../YouTube.kt` | High-level API wrapper |
| **Theming** | `app/.../ui/theme/Theme.kt` | Dynamic color logic |
| **Build** | `gradle/libs.versions.toml` | Dependencies |

## CONVENTIONS
- **Logging**: Use `Timber` exclusively. `Timber.e(e)` for errors.
- **Async**: `viewModelScope` for UI, `Dispatchers.IO` for DB/Network.
- **UI**: Visual changes **MUST** be delegated to `frontend-ui-ux-engineer`.
- **Commits**: Conventional Commits (`feat:`, `fix:`, `refactor:`).
- **Lint**: Run `./gradlew lint` before commits.

## ANTI-PATTERNS
- **DO NOT** use `Log.d` or `println`.
- **DO NOT** modify files in `material-color-utilities` (Generated).
- **DO NOT** use wildcard imports.
- **NEVER** handle UI styling manually if you are not the UI agent.
- **AVOID** hardcoded colors; use `MaterialTheme.colorScheme`.

## COMMANDS
```bash
./gradlew assembleDebug      # Build Debug APK
./gradlew lint               # Run Lint checks
./gradlew test               # Run Unit Tests (currently disabled/empty)
./gradlew clean              # Clean Build
```
