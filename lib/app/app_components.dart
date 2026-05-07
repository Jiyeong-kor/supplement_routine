import 'package:flutter/material.dart';

import 'app_radius.dart';
import 'app_spacing.dart';

class AppComponents {
  static ButtonStyle filledButtonStyle(ColorScheme colorScheme) {
    return FilledButton.styleFrom(
      minimumSize: const Size.fromHeight(52),
      shape: RoundedRectangleBorder(borderRadius: AppRadius.mdBorder),
      textStyle: const TextStyle(fontWeight: FontWeight.w600),
    );
  }

  static ButtonStyle textButtonStyle(ColorScheme colorScheme) {
    return TextButton.styleFrom(
      shape: RoundedRectangleBorder(borderRadius: AppRadius.mdBorder),
      textStyle: const TextStyle(fontWeight: FontWeight.w600),
    );
  }

  static OutlinedBorder cardShape(ColorScheme colorScheme) {
    return RoundedRectangleBorder(
      borderRadius: AppRadius.mdBorder,
      side: BorderSide(color: colorScheme.outlineVariant),
    );
  }

  static InputBorder inputBorder(ColorScheme colorScheme) {
    return OutlineInputBorder(
      borderRadius: AppRadius.mdBorder,
      borderSide: BorderSide(color: colorScheme.outlineVariant),
    );
  }

  static EdgeInsets get dialogInsetPadding {
    return const EdgeInsets.symmetric(
      horizontal: AppSpacing.xxl,
      vertical: AppSpacing.xxl,
    );
  }
}
