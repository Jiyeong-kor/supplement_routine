import 'package:flutter/material.dart';

class AppTypography {
  static const fontFamily = 'Pretendard';

  static TextTheme textTheme(ColorScheme colorScheme) {
    final base = Typography.material2021().black;

    return base
        .apply(
          fontFamily: fontFamily,
          bodyColor: colorScheme.onSurface,
          displayColor: colorScheme.onSurface,
        )
        .copyWith(
          titleLarge: base.titleLarge?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w700,
            letterSpacing: 0,
          ),
          titleMedium: base.titleMedium?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w700,
            letterSpacing: 0,
          ),
          titleSmall: base.titleSmall?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w600,
            letterSpacing: 0,
          ),
          labelLarge: base.labelLarge?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w600,
            letterSpacing: 0,
          ),
          bodyLarge: base.bodyLarge?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w500,
            letterSpacing: 0,
          ),
          bodyMedium: base.bodyMedium?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w400,
            letterSpacing: 0,
          ),
          bodySmall: base.bodySmall?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w400,
            letterSpacing: 0,
          ),
        );
  }
}
