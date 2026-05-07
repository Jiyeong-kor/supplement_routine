# Supplement Routine Design System

## UI/UX Direction

Supplement Routine is a routine and record management app, not a medical advice app. The UI should feel calm, predictable, and scan-friendly. The primary user flow is:

1. Check today's scheduled intakes.
2. Mark completed intakes.
3. Add or edit supplement rules.
4. Review completion history.
5. Adjust routine settings.

## Material Design 3 Decisions

- Color scheme: Indigo seed color for trust and routine management. Success, warning, and error colors are explicit app tokens.
- Typography: Pretendard static font files, weights 400, 500, 600, and 700. Material 3 text roles are preserved and tuned with heavier title/label weights.
- Spacing: 4-point scale with common screen and card padding tokens.
- Shape: 8px for compact controls, 12px for cards and inputs, 16px for prominent actions, 24px for dialogs and bottom sheets.
- Elevation: Mostly zero elevation with outlined/tonal surfaces. This keeps the app quiet and suitable for repeated daily use.
- Icons: Material outlined icons by default, filled icons for selected navigation state.
- Buttons: Filled buttons for primary form actions, text buttons for dialogs.
- Text fields: Filled M3 fields with clear focused and error borders.
- Cards: Tonal low surfaces with outline variant borders.
- Dialogs and bottom sheets: Rounded 24px containers using M3 surface containers.
- Navigation: Bottom NavigationBar with always visible labels for 4 primary destinations.

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

Dark mode uses the same seed color with dark brightness. Components rely on ColorScheme surface containers, not hardcoded white/black values.
