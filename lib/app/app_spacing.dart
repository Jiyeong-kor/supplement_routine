import 'package:flutter/material.dart';

class AppSpacing {
  static const double xxs = 4;
  static const double xs = 6;
  static const double sm = 8;
  static const double md = 12;
  static const double lg = 16;
  static const double xl = 20;
  static const double xxl = 24;
  static const double xxxl = 32;
  static const double screen = 16;

  static const EdgeInsets screenPadding = EdgeInsets.all(screen);
  static const EdgeInsets cardPadding = EdgeInsets.all(lg);
  static const EdgeInsets sectionTitlePadding = EdgeInsets.fromLTRB(
    lg,
    lg,
    lg,
    sm,
  );
}
