import 'package:flutter/material.dart';

class AppColors {
  static const seed = Color(0xFF4355B9);

  static const success = Color(0xFF2E7D5B);
  static const warning = Color(0xFF9A6700);
  static const error = Color(0xFFBA1A1A);

  static ColorScheme lightScheme = ColorScheme.fromSeed(
    seedColor: seed,
    brightness: Brightness.light,
  ).copyWith(error: error);

  static ColorScheme darkScheme = ColorScheme.fromSeed(
    seedColor: seed,
    brightness: Brightness.dark,
  ).copyWith(error: const Color(0xFFFFB4AB));
}
