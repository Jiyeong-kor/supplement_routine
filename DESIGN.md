---
version: alpha
name: Supplement Routine
description: A calm Korean supplement tracker for daily intake, records, and reminder setup.
colors:
  primary: "#006199"
  background: "#FFFFFF"
  surface: "#FFFFFF"
  surface-soft: "#F3F6F8"
  sky-blue: "#8ACFF8"
  lemon: "#F4EB6C"
  sun-yellow: "#FFD444"
  ink: "#172B3A"
  ink-muted: "#536675"
  line: "#D4DFE6"
  disabled: "#71818D"
  error: "#B3261E"
typography:
  display:
    fontFamily: Pretendard
    fontSize: 32px
    fontWeight: 800
    lineHeight: 40px
  title:
    fontFamily: Pretendard
    fontSize: 20px
    fontWeight: 600
    lineHeight: 28px
  body:
    fontFamily: Pretendard
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
  label:
    fontFamily: Pretendard
    fontSize: 12px
    fontWeight: 600
    lineHeight: 16px
rounded:
  xs: 8px
  sm: 12px
  md: 20px
  lg: 28px
  xl: 36px
  pill: 999px
spacing:
  xs: 6px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 20px
  xxl: 24px
  xxxl: 32px
components:
  app-screen:
    backgroundColor: "{colors.background}"
    textColor: "{colors.ink}"
  review-shell:
    backgroundColor: "{colors.surface-soft}"
    textColor: "{colors.ink-muted}"
  soft-surface:
    backgroundColor: "{colors.surface-soft}"
    textColor: "{colors.ink}"
  divider:
    backgroundColor: "{colors.line}"
    textColor: "{colors.ink}"
  primary-button:
    backgroundColor: "{colors.primary}"
    textColor: "#FFFFFF"
    typography: "{typography.label}"
    rounded: "{rounded.pill}"
    height: 48px
  card:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.lg}"
    padding: 18px
  progress-card:
    backgroundColor: "{colors.sky-blue}"
    textColor: "{colors.ink}"
    rounded: "{rounded.xl}"
    padding: 20px
  bottom-nav-selected:
    backgroundColor: "{colors.sky-blue}"
    textColor: "{colors.ink}"
    rounded: "{rounded.pill}"
  bottom-nav-disabled:
    backgroundColor: "{colors.disabled}"
    textColor: "{colors.ink}"
  dark-primary-action:
    backgroundColor: "{colors.sky-blue}"
    textColor: "{colors.ink}"
  success-badge:
    backgroundColor: "{colors.lemon}"
    textColor: "{colors.ink}"
  success-strong-badge:
    backgroundColor: "{colors.primary}"
    textColor: "#FFFFFF"
  warning-badge:
    backgroundColor: "{colors.sun-yellow}"
    textColor: "{colors.ink}"
  error-badge:
    backgroundColor: "{colors.error}"
    textColor: "{colors.ink}"
---

# Design System: Supplement Routine

## Overview

Supplement Routine is a quiet daily health utility, not a medical dashboard and not a wellness marketing page. The interface uses a pure white base with blue actions, sky-blue selections and yellow completion accents. It should feel friendly, legible and practical for small daily actions.

The product emotion is "I can check this quickly without pressure." Screens should keep a clear mobile app structure: title, status, next action, list or empty state, bottom navigation. Avoid decorative layout experiments when they slow down daily use.

Density is balanced. The app should show enough information to act immediately, but never feel crowded. Visual variance comes from card scale, spacing, and small botanical symbols, not from loud color changes.

## Colors

The approved palette is [Color Hunt 006199 / 8ACFF8 / F4EB6C / FFD444](https://colorhunt.co/palette/0061998acff8f4eb6cffd444). Preserve these four exact accent colors.

Use pure white **#FFFFFF** for the light app background, cards, sheets and bottom navigation. **Primary Blue #006199** is used for primary buttons, selected icons, focus rings and progress. **Sky Blue #8ACFF8** is used for the daily summary and selected containers. **Lemon #F4EB6C** marks completed rows and secondary containers. **Sun Yellow #FFD444** is reserved for small badges and calendar/status accents.

Use white labels on Primary Blue and dark Ink on the three light accent colors. Use Ink Muted for secondary text and Line for decorative separators. Error red is reserved for errors and destructive actions. Communicate completion with a check and label as well as color. Dark mode preserves the blue/yellow identity with dark containers and light-blue primary actions.

## Typography

Use Pretendard for Korean-first readability. Headlines are confident but compact. Body text should be short and broken into readable lines. Avoid long explanatory copy inside cards.

Display titles are 32sp/40sp extra bold. Card titles are 20sp/28sp semibold. Body copy is 14sp/20sp. Labels and navigation text are 12sp/16sp semibold.

Numbers such as completion percent may be larger, but should stay calm and left-aligned with related text. Do not create oversized hero typography that makes the app feel like a landing page.

## Layout

Design mobile-first. Primary screens use 20dp horizontal padding and 24dp vertical rhythm between major sections. Cards should align to a single vertical column. Bottom navigation remains 72dp high with stable icon and label positions.

Cards are useful only when they group one action or one record. Do not put cards inside cards. Do not make the entire screen look like one giant floating card in production app surfaces. Validate production screens in the Android emulator.

Empty states must immediately explain the next useful action. They should include one clear primary action and avoid generic "no data" wording.

## Elevation & Depth

Use depth sparingly. Most production surfaces should rely on color, border, and spacing. Heavy shadows, glass blur, and floating decorative panels are banned.

## Shapes

The shape language is soft but controlled. Use 28dp for main cards, 36dp for large progress surfaces, 20dp for medium controls, and pill radius for primary buttons and selected navigation indicators.

Avoid tiny sharp cards and avoid excessive bubble-like rounding on dense lists. If a component repeats many times, reduce visual weight before adding more radius or shadow.

## Components

Primary buttons are full-width or context-width, filled with Primary Blue, pill-shaped, and at least 48dp tall. Button text is short, direct, and action-oriented.

Progress cards use Sky Blue with one strong number, one plain explanation, and a small botanical badge. The badge is a supporting signal, not the content.

Medicine and settings rows use Surface fill, Line border, and one leading icon badge. Keep subtitles short. Long descriptions should move to detail screens or dialogs.

Bottom navigation uses three stable items: 오늘, 기록, 설정. Supplement management opens from the add action. Selected state uses Sky Blue background and Primary Blue icon/text. Unselected state uses Ink Muted.

## Do's and Don'ts

Do use Korean-first short labels. Do keep daily actions one tap away. Do preserve enough spacing for touch comfort. Verify the native Android screens and real interactions in the emulator.

Do not use purple or blue neon gradients. Do not use pink wellness app styling. Do not use marketing hero sections. Do not use emojis. Do not use generic stock illustrations. Do not add decorative orbs, blobs, bokeh, or glassmorphism. Do not make cards inside cards. Do not use long one-line explanatory text in constrained cards.

Native Android is the implementation and verification target. Browser previews are not the acceptance surface.
