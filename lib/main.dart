import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'app/supplement_routine_app.dart';
import 'core/services/intake_notification_service.dart';
import 'features/supplement/data/local_supplement_repository.dart';
import 'features/supplement/supplement_provider.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await IntakeNotificationService.initialize();
  final preferences = await SharedPreferencesWithCache.create(
    cacheOptions: const SharedPreferencesWithCacheOptions(
      allowList: {LocalSupplementRepository.storageKey},
    ),
  );

  runApp(
    ProviderScope(
      overrides: [
        supplementRepositoryProvider.overrideWithValue(
          LocalSupplementRepository(preferences),
        ),
      ],
      child: const SupplementRoutineApp(),
    ),
  );
}
