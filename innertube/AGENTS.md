# Innertube Module Knowledge Base

## OVERVIEW
Client for the YouTube Music Internal API (InnerTube). Handles authentication, client spoofing, and response parsing.

## STRUCTURE
```
InnerTube.kt        # Low-level Ktor client & Headers
YouTube.kt          # High-level Orchestrator (Singleton)
models/
├── body/           # Request bodies (BrowseBody, SearchBody)
├── response/       # Raw JSON response models
└── ...             # Renderers (MusicShelfRenderer)
pages/              # Parsers (Renderer -> Domain Model)
```

## CONVENTIONS
- **Access**: Always use `YouTube` object (e.g., `YouTube.search(...)`).
- **Spoofing**: Defaults to `WEB_REMIX` client. Use `Context` to switch.
- **Parsing**: Responses are nested "Renderers". Use `pages/` parsers to flatten them.
- **Auth**: Uses `SAPISIDHASH` header generated from cookies.

## ANTI-PATTERNS
- **Direct Client Usage**: Do not call `InnerTube.kt` functions directly from the app.
- **Hardcoding IDs**: Use `YouTubeClient` constants for client IDs/Versions.
