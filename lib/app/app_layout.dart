import 'package:flutter/material.dart';

class AppLayout {
  const AppLayout._();

  static const double expandedBreakpoint = 840;
  static const double readableContentWidth = 720;
  static const double wideContentWidth = 1040;

  static bool isExpanded(BuildContext context) {
    return MediaQuery.sizeOf(context).width >= expandedBreakpoint;
  }
}

class AppConstrainedContent extends StatelessWidget {
  const AppConstrainedContent({
    super.key,
    required this.child,
    this.maxWidth = AppLayout.readableContentWidth,
  });

  final Widget child;
  final double maxWidth;

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.topCenter,
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: maxWidth),
        child: child,
      ),
    );
  }
}
