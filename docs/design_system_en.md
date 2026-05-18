# Supplement Routine Design System

## UI/UX Direction

Supplement Routine is a routine and record management app, not a medical advice app. The UI should feel calm, predictable, and scan-friendly. The primary user flow is:

1. Check today's scheduled intakes.
2. Mark completed intakes.
3. Add or edit supplement rules.
4. Review completion history.
5. Adjust routine settings.

## Material Design 3 Decisions

- Color scheme: Sage green seed color with warm neutral surfaces for a calm routine-management tone. Success, warning, and error colors are explicit app tokens.
- Typography: Pretendard static font files, weights 400, 500, 600, and 700. The official Material 3 type scale is declared explicitly (`titleLarge 22/28`, `bodyLarge 16/24`, `bodyMedium 14/20`, `bodySmall 12/16`), body copy stays light, and emphasis increases gradually across title roles.
- Spacing: 4-point scale with common screen and card padding tokens.
- Shape: 8px for compact controls, 12px for cards and inputs, 16px for prominent actions, and 28px for larger containers.
- Elevation: Mostly zero elevation with outlined/tonal surfaces. This keeps the app quiet and suitable for repeated daily use.
- Icons: Material outlined icons by default, filled icons for selected navigation state.
- Buttons: Filled buttons for primary form actions, text buttons for dialogs.
- Text fields: Filled M3 fields with clear focused and error borders.
- Cards: Tonal low surfaces with outline variant borders.
- Dialogs and bottom sheets: Rounded 24px containers using M3 surface containers.
- Navigation: NavigationBar on compact widths and NavigationRail on expanded widths.
- Layout: Keep content within readable maximum widths, and let list/history screens use expanded layouts on larger screens.

## Tokens

| Category | Token | Value | Preview |
| --- | --- | --- | --- |
| Color | `AppColors.seed` | `#5C8A73` | ![Seed](assets/colors/seed.svg) |
| Color | `AppColors.success` | `#466C59` | ![Success](assets/colors/success.svg) |
| Color | `AppColors.warning` | `#B26A00` | ![Warning](assets/colors/warning.svg) |
| Color | `AppColors.error` | `#B3261E` | ![Error](assets/colors/error.svg) |
| Spacing | `xxs / xs / sm / md / lg / xl / xxl / xxxl` | `4 / 6 / 8 / 12 / 16 / 20 / 24 / 32` |
| Radius | `sm / md / lg / xl / xxl / pill` | `8 / 12 / 16 / 24 / 28 / 999` |
| Typography | `Pretendard` | `400 / 500 / 600 / 700` |

## Component Rules

| Component | Rule |
| --- | --- |
| AppBar | `centerTitle: false`, surface background, low scrolled-under elevation |
| Navigation | NavigationBar on compact widths, NavigationRail on expanded widths |
| Layout | `720px` for readable content, `1040px` for wide content |
| Card | Low tonal surface, zero elevation, outline variant border |
| FilledButton | Primary actions such as save/complete |
| TextButton | Secondary dialog actions |
| TextField | Filled field, 12px radius, distinct focus/error borders |
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
