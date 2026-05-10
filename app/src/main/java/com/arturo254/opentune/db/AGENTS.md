# Database Knowledge Base

## OVERVIEW
Room Database (v19) managing the local music library, playback history, and cache.

## STRUCTURE
- `MusicDatabase.kt`: Main entry point, delegations, and migrations.
- `DatabaseDao.kt`: Centralized Data Access Object (Monolithic).
- `entities/`: Table definitions (`SongEntity`, `AlbumEntity`).

## CONVENTIONS
- **Reactivity**: Return `Flow<List<T>>` for UI observation.
- **Relationships**: Use `@Relation` and `Junction` tables (e.g., `SongArtistMap`) for M:N.
- **Transactions**: Use `@Transaction` for multi-table operations.
- **Migrations**: Prefer `AutoMigration`. See `MusicDatabase.kt` for spec classes.

## ANTI-PATTERNS
- **Splitting DAO**: `DatabaseDao` is intentionally monolithic; do not split without major refactor plan.
- **Main Thread**: Database ops must run on `Dispatchers.IO`.
