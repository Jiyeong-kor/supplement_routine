# Supplement Routine Design System

`docs/design_system.md` is the source of truth for the refreshed brand direction. This English document keeps a lightweight summary for reference.

## UI/UX Direction

Supplement Routine is a routine and record management app, not a medical advice app. The refreshed UI should feel cute, friendly, scan-friendly, and useful for daily repetition without becoming childish or overly decorative. The primary user flow is:

1. Check today's scheduled intakes.
2. Mark completed intakes.
3. Add or edit supplement rules.
4. Review completion history.
5. Adjust routine settings.

## Material Design 3 Decisions

- Color scheme: Warm white surfaces, berry/coral accents, mint success states, and dark ink text keep routine information friendly and easy to scan. Success, warning, and error colors are explicit app tokens.
- Typography: Pretendard static font files, weights 400, 500, 600, and 700. The scale follows Material 3, with title roles promoted to 700 weight for scan-friendly daily routine screens.
- Spacing: 4-point scale with common screen and card padding tokens.
- Shape: 8px for compact controls, 16px for cards and inputs, pill-shaped primary actions, and 28px for larger containers.
- Elevation: Mostly zero elevation with white surfaces, soft outlines, and sticker-like capsule states.
- Icons: Material outlined icons by default, filled icons for selected navigation state.
- Buttons: Filled buttons for primary form actions, text buttons for dialogs.
- Text fields: Filled M3 fields with clear focused and error borders.
- Cards: White surfaces with light outline variant borders.
- Dialogs and bottom sheets: Rounded 24px containers using M3 surface containers.
- Navigation: NavigationBar on compact widths and NavigationRail on expanded widths.
- Layout: Keep content within readable maximum widths, and let list/history screens use expanded layouts on larger screens.

## Tokens

| Category | Token | Value | Preview |
| --- | --- | --- | --- |
| Color | `AppColors.seed` | `#E95E7B` | ![Seed](assets/colors/seed.svg) |
| Color | `AppColors.coral` | `#FF9A76` | ![Coral](assets/colors/coral.svg) |
| Color | `AppColors.success` | `#24B88A` | ![Success](assets/colors/success.svg) |
| Color | `AppColors.warning` | `#F5A524` | ![Warning](assets/colors/warning.svg) |
| Color | `AppColors.error` | `#D6455D` | ![Error](assets/colors/error.svg) |
| Spacing | `xxs / xs / sm / md / lg / xl / xxl / xxxl` | `4 / 6 / 8 / 12 / 16 / 20 / 24 / 32` |
| Radius | `sm / md / lg / xl / xxl / pill` | `8 / 12 / 16 / 24 / 28 / 999` |
| Typography | `Pretendard` | `400 / 500 / 600 / 700` |

## Component Rules

| Component | Rule |
| --- | --- |
| AppBar | `centerTitle: false`, surface background, low scrolled-under elevation |
| Navigation | NavigationBar on compact widths, NavigationRail on expanded widths |
| Layout | `720px` for readable content, `1040px` for wide content |
| Card | White surface, zero elevation, light outline variant border |
| FilledButton | Primary actions such as save/complete |
| TextButton | Secondary dialog actions |
| TextField | Filled field, 16px radius, distinct focus/error borders |
| BottomSheet | 28px top radius, drag handle |
| Dialog | 28px radius, clear CTA |
| Empty State | Icon + title + short recovery copy |

## Implementation Files

- `lib/app/app_colors.dart`
- `lib/app/app_typography.dart`
- `lib/app/app_spacing.dart`
- `lib/app/app_radius.dart`
- `lib/app/app_components.dart`
- `lib/app/app_theme.dart`

## States

- Loading: Use Material progress indicators with the active ColorScheme primary color.
- Empty: Use a low surface Card, neutral outlined icon, title, and short recovery action text.
- Error: Use ColorScheme.error and short user-actionable copy.
- Disabled: Use default Material disabled treatment from ThemeData.
- Success: Use `AppColors.success` when a dedicated success signal is needed; completion checks primarily use ColorScheme.primary.

## Accessibility

- Do not communicate status by color alone; pair icons and text.
- Keep primary tap targets at Material defaults.
- Use TextTheme roles instead of fixed font sizes so user font scaling remains viable.
- Prefer ColorScheme pairs such as `primary/onPrimary` and `surface/onSurface` for contrast.

## Dark Mode

Dark mode keeps the same seed family while tuning `surface`, `surfaceContainer*`, `onSurface*`, and `primaryContainer` values separately so hierarchy and contrast remain clear. Components rely on ColorScheme surfaces instead of hardcoded white or black values.
