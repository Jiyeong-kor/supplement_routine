import 'package:flutter/material.dart';

class AppColors {
  static const seed = Color(0xFF6B36F6);

  static const success = Color(0xFF20B486);
  static const warning = Color(0xFFFFB020);
  static const error = Color(0xFFE5484D);

  static ColorScheme lightScheme =
      ColorScheme.fromSeed(
        seedColor: seed,
        brightness: Brightness.light,
      ).copyWith(
        primary: seed,
        onPrimary: Colors.white,
        primaryContainer: const Color(0xFFF0E9FF),
        onPrimaryContainer: const Color(0xFF26105F),
        secondary: const Color(0xFF7E7A8D),
        onSecondary: Colors.white,
        secondaryContainer: const Color(0xFFF5F2FF),
        onSecondaryContainer: const Color(0xFF2B2738),
        tertiary: const Color(0xFFFFB84D),
        onTertiary: Colors.white,
        tertiaryContainer: const Color(0xFFFFF3D8),
        onTertiaryContainer: const Color(0xFF4C3500),
        surface: Colors.white,
        onSurface: const Color(0xFF15131C),
        surfaceContainerLow: Colors.white,
        surfaceContainer: const Color(0xFFF8F7FB),
        surfaceContainerHigh: const Color(0xFFF2F0F7),
        surfaceContainerHighest: const Color(0xFFEAE7F2),
        onSurfaceVariant: const Color(0xFF8B8798),
        error: error,
        onError: Colors.white,
        outline: const Color(0xFFC9C4D7),
        outlineVariant: const Color(0xFFE8E5EF),
      );

  static ColorScheme darkScheme =
      ColorScheme.fromSeed(
        seedColor: seed,
        brightness: Brightness.dark,
      ).copyWith(
        primary: const Color(0xFFB8A0FF),
        onPrimary: const Color(0xFF26005D),
        primaryContainer: const Color(0xFF3C1A8F),
        onPrimaryContainer: const Color(0xFFF0E9FF),
        secondary: const Color(0xFFCFC9DE),
        onSecondary: const Color(0xFF302A3F),
        secondaryContainer: const Color(0xFF2E2939),
        onSecondaryContainer: const Color(0xFFF3EFFC),
        tertiary: const Color(0xFFFFD071),
        onTertiary: const Color(0xFF422C00),
        tertiaryContainer: const Color(0xFF5F4300),
        onTertiaryContainer: const Color(0xFFFFF0CA),
        surface: const Color(0xFF15131C),
        onSurface: const Color(0xFFF8F7FB),
        surfaceContainerLow: const Color(0xFF1E1B27),
        surfaceContainer: const Color(0xFF24202E),
        surfaceContainerHigh: const Color(0xFF302B3A),
        surfaceContainerHighest: const Color(0xFF3C3548),
        onSurfaceVariant: const Color(0xFFCFC9DE),
        error: const Color(0xFFFFB3B6),
        onError: const Color(0xFF680007),
        outline: const Color(0xFF958EA6),
        outlineVariant: const Color(0xFF4B4558),
      );
}
