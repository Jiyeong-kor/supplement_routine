import 'package:flutter/material.dart';

class AppTypography {
  static const fontFamily = 'Pretendard';

  static TextTheme textTheme(ColorScheme colorScheme) {
    final base = Typography.material2021().black;
    final themedBase = base.apply(
      fontFamily: fontFamily,
      bodyColor: colorScheme.onSurface,
      displayColor: colorScheme.onSurface,
    );

    return themedBase.copyWith(
      titleLarge: themedBase.titleLarge?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
      ),
      titleMedium: themedBase.titleMedium?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
      ),
      titleSmall: themedBase.titleSmall?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
      ),
      labelLarge: themedBase.labelLarge?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w500,
        letterSpacing: 0,
      ),
      bodyLarge: themedBase.bodyLarge?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w400,
        letterSpacing: 0,
      ),
      bodyMedium: themedBase.bodyMedium?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w400,
        letterSpacing: 0,
      ),
      bodySmall: themedBase.bodySmall?.copyWith(
        fontFamily: fontFamily,
        fontWeight: FontWeight.w400,
        letterSpacing: 0,
      ),
    );
  }
}
