import 'package:flutter/material.dart';

class AppColors {
  static const seed = Color(0xFF5C8A73);

  static const success = Color(0xFF466C59);
  static const warning = Color(0xFFB26A00);
  static const error = Color(0xFFB3261E);

  static ColorScheme lightScheme =
      ColorScheme.fromSeed(
        seedColor: seed,
        brightness: Brightness.light,
      ).copyWith(
        primary: seed,
        onPrimary: Colors.white,
        primaryContainer: const Color(0xFFE4ECE7),
        onPrimaryContainer: const Color(0xFF1B2922),
        secondary: const Color(0xFF6B756F),
        onSecondary: Colors.white,
        secondaryContainer: const Color(0xFFF4F2EB),
        onSecondaryContainer: const Color(0xFF2A2925),
        tertiary: const Color(0xFF7B6B54),
        onTertiary: Colors.white,
        tertiaryContainer: const Color(0xFFEDE3D6),
        onTertiaryContainer: const Color(0xFF2F2417),
        surface: const Color(0xFFFAF9F5),
        onSurface: const Color(0xFF1C1B19),
        surfaceContainerLow: const Color(0xFFFFFEFA),
        surfaceContainer: const Color(0xFFF4F2EB),
        surfaceContainerHigh: const Color(0xFFECE8DD),
        surfaceContainerHighest: const Color(0xFFE6E2D5),
        onSurfaceVariant: const Color(0xFF4A4842),
        error: error,
        onError: Colors.white,
        outline: const Color(0xFFA7A092),
        outlineVariant: const Color(0xFFD4CFC1),
      );

  static ColorScheme darkScheme =
      ColorScheme.fromSeed(
        seedColor: seed,
        brightness: Brightness.dark,
      ).copyWith(
        primary: const Color(0xFFA3C2B1),
        onPrimary: const Color(0xFF1B2922),
        primaryContainer: const Color(0xFF365345),
        onPrimaryContainer: const Color(0xFFE4ECE7),
        secondary: const Color(0xFFD4CFC1),
        onSecondary: const Color(0xFF2A2925),
        secondaryContainer: const Color(0xFF3A3832),
        onSecondaryContainer: const Color(0xFFF4F2EB),
        tertiary: const Color(0xFFD9C2A3),
        onTertiary: const Color(0xFF352716),
        tertiaryContainer: const Color(0xFF4B3924),
        onTertiaryContainer: const Color(0xFFF4E4CC),
        surface: const Color(0xFF1C1B19),
        onSurface: const Color(0xFFF4F2EB),
        surfaceContainerLow: const Color(0xFF24231F),
        surfaceContainer: const Color(0xFF2A2925),
        surfaceContainerHigh: const Color(0xFF34322D),
        surfaceContainerHighest: const Color(0xFF403D37),
        onSurfaceVariant: const Color(0xFFD4CFC1),
        error: const Color(0xFFFFB4AB),
        onError: const Color(0xFF690005),
        outline: const Color(0xFF938D81),
        outlineVariant: const Color(0xFF4A4842),
      );
}
