# Windows Support

## Goal

On Windows, the priority is a small panel-like experience that stays available for quick routine checks instead of stretching the mobile UI into a large desktop window.

## Current Behavior

- Added the Windows desktop target.
- The app starts in a compact `420 x 720` window.
- The window stays above other windows by default so it can behave like a quick routine panel.
- The window title is `Supplement Routine`.

## Build Note

Flutter Windows builds use plugin symlink generation, so Windows Developer Mode is required in the development environment. After enabling Developer Mode, verify with `flutter build windows` or `flutter run -d windows`.

## Remaining Decision

Auto-start changes how the user's device behaves at login, so it is safer to expose it as an explicit setting. It is intentionally not enabled by default yet and should be implemented together with future Windows settings work.
