# Material Color Utilities Knowledge Base

## OVERVIEW
Local module for Material 3 dynamic theming and HCT color extraction. Derived from Google's library.

## STRUCTURE
- `score/Score.java`: Algorithms to rank colors from an image.
- `scheme/SchemeTonalSpot.java`: Generates M3 tonal palettes.
- `hct/`: HCT (Hue, Chroma, Tone) color space logic.

## CONVENTIONS
- **Extraction**: Requires `Bitmap` config that is NOT hardware-accelerated (`allowHardware(false)`).
- **Usage**: Called via `Theme.kt` in the app module.

## ANTI-PATTERNS
- **EDITING FILES**: Most files here are auto-generated or library copies. **DO NOT MODIFY** logic in `blend/`, `hct/`, or `palettes/` unless fixing a critical bug in the library implementation itself.
