import 'package:flutter/material.dart';

import 'package:supplement_routine/app/app_config.dart';
import 'package:supplement_routine/app/app_theme.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

import '../features/main_navigation_screen.dart';

class SupplementRoutineApp extends StatelessWidget {
  const SupplementRoutineApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: AppConfig.appName,
      onGenerateTitle: (context) => AppLocalizations.of(context).appTitle,
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      home: const MainNavigationScreen(),
    );
  }
}
