# Windows Support

## Goal

On Windows, the priority is a small panel-like experience that stays available for quick routine checks instead of stretching the mobile UI into a large desktop window.

## Current Behavior

- Added the Windows desktop target.
- The app starts in a compact `420 x 720` window.
- The window stays above other windows by default so it can behave like a quick routine panel.
- The window title is `Supplement Routine`.
- Users can explicitly enable or disable `Run at Windows startup` from Settings.

## Build Note

Flutter Windows builds use plugin symlink generation, so Windows Developer Mode is required in the development environment. After enabling Developer Mode, verify with `flutter build windows` or `flutter run -d windows`.

## Auto-start

Auto-start is not forced by default. Users opt in from Settings, and the app launches after Windows login only when that setting is enabled.
