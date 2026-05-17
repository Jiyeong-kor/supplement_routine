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

  static ColorScheme darkScheme =
      ColorScheme.fromSeed(
        seedColor: seed,
        brightness: Brightness.dark,
      ).copyWith(
        primary: const Color(0xFFBEC2FF),
        onPrimary: const Color(0xFF202B78),
        primaryContainer: const Color(0xFF313A8F),
        onPrimaryContainer: const Color(0xFFE0E0FF),
        secondary: const Color(0xFFC4C7D0),
        onSecondary: const Color(0xFF2D3038),
        secondaryContainer: const Color(0xFF424650),
        onSecondaryContainer: const Color(0xFFE0E2EC),
        tertiary: const Color(0xFFE0B8FF),
        onTertiary: const Color(0xFF44235E),
        tertiaryContainer: const Color(0xFF5D3A77),
        onTertiaryContainer: const Color(0xFFF3DAFF),
        surface: const Color(0xFF141318),
        onSurface: const Color(0xFFE6E1E9),
        surfaceContainerLow: const Color(0xFF1D1B20),
        surfaceContainer: const Color(0xFF211F26),
        surfaceContainerHigh: const Color(0xFF2B2930),
        surfaceContainerHighest: const Color(0xFF36343B),
        onSurfaceVariant: const Color(0xFFC9C5D0),
        error: const Color(0xFFFFB4AB),
        onError: const Color(0xFF690005),
        outline: const Color(0xFF938F99),
        outlineVariant: const Color(0xFF49454F),
      );
}
