import 'package:flutter/material.dart';

class AppColors {
  static const seed = Color(0xFF1A237E);

  static const success = Color(0xFF1B5E20);
  static const warning = Color(0xFFE65100);
  static const error = Color(0xFFB71C1C);

  static ColorScheme lightScheme =
      ColorScheme.fromSeed(
        seedColor: seed,
        brightness: Brightness.light,
      ).copyWith(
        primary: seed,
        onPrimary: Colors.white,
        primaryContainer: const Color(0xFFE8EAF6),
        onPrimaryContainer: const Color(0xFF101A5C),
        secondary: const Color(0xFF455A64),
        onSecondary: Colors.white,
        secondaryContainer: const Color(0xFFECEFF1),
        onSecondaryContainer: const Color(0xFF263238),
        surface: Colors.white,
        onSurface: const Color(0xFF1A1C1E),
        surfaceContainerLow: Colors.white,
        surfaceContainer: const Color(0xFFF2F2F4),
        surfaceContainerHigh: const Color(0xFFE8E8EA),
        error: error,
        onError: Colors.white,
        outline: const Color(0xFF9E9E9E),
        outlineVariant: const Color(0xFFEEEEEE),
      );

  static ColorScheme darkScheme = ColorScheme.fromSeed(
    seedColor: seed,
    brightness: Brightness.dark,
  ).copyWith(surface: const Color(0xFF121212), onSurface: Colors.white);
}
