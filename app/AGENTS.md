# App Module Knowledge Base

## OVERVIEW
The main Android application module containing UI, Navigation, Playback Service, and Database integration. Implements MVVM with Jetpack Compose and Hilt.

## STRUCTURE
```
ui/
├── component/   # Atomic reusable UI (ListItem, GridItem)
├── screens/     # Feature screens & NavigationBuilder.kt
├── theme/       # Dynamic theming logic
viewmodels/      # Hilt ViewModels (State management)
playback/        # MusicService (Media3) & Queues
db/              # Room Database
```

## WHERE TO LOOK
- **New Screen**: Register in `NavigationBuilder.kt`, create ViewModel in `viewmodels/`.
- **State**: `viewmodels/` use `MutableStateFlow` exposed as `StateFlow`.
- **Lists**: Use `ListItem` or `GridItem` from `ui/component`.
- **Images**: Use `AsyncImage` (Coil).

## CONVENTIONS
- **Injection**: Use `@AndroidEntryPoint` on Activities/Services, `@HiltViewModel` on ViewModels.
- **Navigation**: Pass `NavController` only where strictly necessary; prefer passing lambdas.
- **Themes**: `MainActivity` extracts color from thumbnail -> `Theme.kt` applies it.

## ANTI-PATTERNS
- **Blocking UI**: Never perform DB/Network on Main thread.
- **Hardcoded Strings**: Use `stringResource(R.string...)`.
- **Logic in UI**: Composables should only render state; logic belongs in ViewModels.
