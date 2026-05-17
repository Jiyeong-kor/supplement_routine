import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supplement_routine/app/app_config.dart';
import 'package:supplement_routine/app/app_theme.dart';
import 'package:supplement_routine/app/supplement_routine_app.dart';
import 'package:supplement_routine/features/history/data/local_intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/mock_intake_records.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/settings/data/local_settings_repository.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/data/local_supplement_repository.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplements.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class AppBootstrap extends StatefulWidget {
  const AppBootstrap({super.key});

  @override
  State<AppBootstrap> createState() => _AppBootstrapState();
}

class _AppBootstrapState extends State<AppBootstrap> {
  late final Future<_RepositoryBundle> _repositories = _createRepositories();

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<_RepositoryBundle>(
      future: _repositories,
      builder: (context, snapshot) {
        final repositories = snapshot.data;

        if (repositories == null) {
          return const _BootstrapLoadingApp();
        }

        return ProviderScope(
          overrides: [
            supplementRepositoryProvider.overrideWithValue(
              repositories.supplementRepository,
            ),
            intakeRecordRepositoryProvider.overrideWithValue(
              repositories.intakeRecordRepository,
            ),
            settingsRepositoryProvider.overrideWithValue(
              repositories.settingsRepository,
            ),
          ],
          child: const SupplementRoutineApp(),
        );
      },
    );
  }

  Future<_RepositoryBundle> _createRepositories() async {
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

    return _RepositoryBundle(
      supplementRepository: LocalSupplementRepository(
        preferences,
        seedSupplements: AppConfig.isMockDataEnabled
            ? mockSupplements
            : const [],
      ),
      intakeRecordRepository: LocalIntakeRecordRepository(
        preferences,
        seedRecords: AppConfig.isMockDataEnabled
            ? createMockIntakeRecords()
            : const {},
      ),
      settingsRepository: LocalSettingsRepository(preferences),
    );
  }
}

class _RepositoryBundle {
  const _RepositoryBundle({
    required this.supplementRepository,
    required this.intakeRecordRepository,
    required this.settingsRepository,
  });

  final LocalSupplementRepository supplementRepository;
  final LocalIntakeRecordRepository intakeRecordRepository;
  final LocalSettingsRepository settingsRepository;
}

class _BootstrapLoadingApp extends StatelessWidget {
  const _BootstrapLoadingApp();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      home: const Scaffold(),
    );
  }
}
