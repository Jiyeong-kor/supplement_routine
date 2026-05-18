import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supplement_routine/app/app_config.dart';
import 'package:supplement_routine/app/supplement_routine_app.dart';
import 'package:supplement_routine/features/history/data/intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/local_intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/mock_intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/mock_intake_records.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/settings/data/local_settings_repository.dart';
import 'package:supplement_routine/features/settings/data/memory_settings_repository.dart';
import 'package:supplement_routine/features/settings/data/settings_repository.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/data/local_supplement_repository.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplement_repository.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplements.dart';
import 'package:supplement_routine/features/supplement/data/supplement_repository.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class AppBootstrap extends StatefulWidget {
  const AppBootstrap({super.key});

  @override
  State<AppBootstrap> createState() => _AppBootstrapState();
}

class _AppBootstrapState extends State<AppBootstrap> {
  late _RepositoryBundle _repositories = _RepositoryBundle.fallback();

  @override
  void initState() {
    super.initState();
    _loadRepositories();
  }

  @override
  Widget build(BuildContext context) {
    return ProviderScope(
      key: ValueKey(_repositories),
      overrides: [
        supplementRepositoryProvider.overrideWithValue(
          _repositories.supplementRepository,
        ),
        intakeRecordRepositoryProvider.overrideWithValue(
          _repositories.intakeRecordRepository,
        ),
        settingsRepositoryProvider.overrideWithValue(
          _repositories.settingsRepository,
        ),
      ],
      child: const SupplementRoutineApp(),
    );
  }

  Future<void> _loadRepositories() async {
    final repositories = await _createRepositories();

    if (!mounted) {
      return;
    }

    setState(() {
      _repositories = repositories;
    });
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

  factory _RepositoryBundle.fallback() {
    return _RepositoryBundle(
      supplementRepository: MockSupplementRepository(
        initialSupplements: AppConfig.isMockDataEnabled
            ? mockSupplements
            : const [],
      ),
      intakeRecordRepository: MockIntakeRecordRepository(
        initialRecords: AppConfig.isMockDataEnabled
            ? createMockIntakeRecords()
            : const {},
      ),
      settingsRepository: MemorySettingsRepository(),
    );
  }

  final SupplementRepository supplementRepository;
  final IntakeRecordRepository intakeRecordRepository;
  final SettingsRepository settingsRepository;
}
