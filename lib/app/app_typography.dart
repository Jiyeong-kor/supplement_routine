import 'package:flutter/material.dart';

class AppTypography {
  static const fontFamily = 'Pretendard';

  static TextTheme textTheme(ColorScheme colorScheme) {
    TextStyle style(
      double size,
      double height,
      FontWeight weight, {
      Color? color,
    }) {
      return TextStyle(
        fontFamily: fontFamily,
        fontSize: size,
        height: height / size,
        fontWeight: weight,
        letterSpacing: 0,
        color: color ?? colorScheme.onSurface,
      );
    }

    return TextTheme(
      displayLarge: style(57, 64, FontWeight.w400),
      displayMedium: style(45, 52, FontWeight.w400),
      displaySmall: style(36, 44, FontWeight.w400),
      headlineLarge: style(32, 40, FontWeight.w700),
      headlineMedium: style(28, 36, FontWeight.w700),
      headlineSmall: style(24, 32, FontWeight.w700),
      titleLarge: style(22, 30, FontWeight.w700),
      titleMedium: style(16, 24, FontWeight.w700),
      titleSmall: style(14, 20, FontWeight.w700),
      bodyLarge: style(16, 24, FontWeight.w400),
      bodyMedium: style(14, 20, FontWeight.w400),
      bodySmall: style(12, 16, FontWeight.w400),
      labelLarge: style(14, 20, FontWeight.w500),
      labelMedium: style(12, 16, FontWeight.w500),
      labelSmall: style(11, 16, FontWeight.w500),
    );
  }
}
