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
          ),
          titleMedium: base.titleMedium?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w600,
          ),
          titleSmall: base.titleSmall?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w600,
          ),
          labelLarge: base.labelLarge?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w600,
          ),
          bodyMedium: base.bodyMedium?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w400,
          ),
          bodySmall: base.bodySmall?.copyWith(
            fontFamily: fontFamily,
            fontWeight: FontWeight.w400,
          ),
        );
  }
}
