import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'app/supplement_routine_app.dart';
import 'core/services/intake_notification_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await IntakeNotificationService.initialize();

  runApp(const ProviderScope(child: SupplementRoutineApp()));
}
