import 'package:flutter/material.dart';
import '../features/main_navigation_screen.dart';

class SupplementRoutineApp extends StatelessWidget {
  const SupplementRoutineApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '영양제 루틴',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorSchemeSeed: Colors.blue,
      ),
      home: const MainNavigationScreen(),
    );
  }
}
