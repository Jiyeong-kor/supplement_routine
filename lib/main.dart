import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'app/supplement_routine_app.dart';
import 'core/services/intake_notification_service.dart';
import 'features/history/data/local_intake_record_repository.dart';
import 'features/history/intake_record_provider.dart';
import 'features/settings/data/local_settings_repository.dart';
import 'features/settings/settings_provider.dart';
import 'features/supplement/data/local_supplement_repository.dart';
import 'features/supplement/supplement_provider.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await IntakeNotificationService.initialize();
  final preferences = await SharedPreferencesWithCache.create(
    cacheOptions: const SharedPreferencesWithCacheOptions(
      allowList: {
        LocalSupplementRepository.storageKey,
        LocalIntakeRecordRepository.storageKey,
        LocalSettingsRepository.mealTimeSettingsKey,
        LocalSettingsRepository.notificationEnabledKey,
      },
    ),
  );

  runApp(
    ProviderScope(
      overrides: [
        supplementRepositoryProvider.overrideWithValue(
          LocalSupplementRepository(preferences),
        ),
        intakeRecordRepositoryProvider.overrideWithValue(
          LocalIntakeRecordRepository(preferences),
        ),
        settingsRepositoryProvider.overrideWithValue(
          LocalSettingsRepository(preferences),
        ),
      ],
      child: const SupplementRoutineApp(),
    ),
  );
}
