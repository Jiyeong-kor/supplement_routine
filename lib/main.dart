import 'package:flutter/material.dart';

void main() {
  runApp(const SupplementRoutineApp());
}

class SupplementRoutineApp extends StatelessWidget {
  const SupplementRoutineApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Supplement Routine',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.green),
        useMaterial3: true,
      ),
      home: const Scaffold(
        body: Center(
          child: Text('Supplement Routine'),
        ),
      ),
    );
  }
}