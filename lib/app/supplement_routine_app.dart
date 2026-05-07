import 'package:flutter/material.dart';

import 'package:supplement_routine/app/app_theme.dart';

import '../features/main_navigation_screen.dart';

class SupplementRoutineApp extends StatelessWidget {
  const SupplementRoutineApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '영양제 루틴',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      home: const MainNavigationScreen(),
    );
  }
}
