import 'package:flutter/material.dart';

import 'app_radius.dart';
import 'app_spacing.dart';

class AppComponents {
  static ButtonStyle filledButtonStyle(ColorScheme colorScheme) {
    return FilledButton.styleFrom(
      minimumSize: const Size.fromHeight(56),
      shape: const StadiumBorder(),
      textStyle: const TextStyle(fontWeight: FontWeight.w700),
    );
  }

  static ButtonStyle textButtonStyle(ColorScheme colorScheme) {
    return TextButton.styleFrom(
      shape: const StadiumBorder(),
      textStyle: const TextStyle(fontWeight: FontWeight.w700),
    );
  }

  static OutlinedBorder cardShape(ColorScheme colorScheme) {
    return RoundedRectangleBorder(
      borderRadius: AppRadius.lgBorder,
      side: BorderSide(
        color: colorScheme.outlineVariant.withValues(alpha: 0.8),
      ),
    );
  }

  static InputBorder inputBorder(ColorScheme colorScheme) {
    return OutlineInputBorder(
      borderRadius: AppRadius.lgBorder,
      borderSide: BorderSide.none,
    );
  }

  static EdgeInsets get dialogInsetPadding {
    return const EdgeInsets.symmetric(
      horizontal: AppSpacing.xxl,
      vertical: AppSpacing.xxl,
    );
  }
}
