import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:supplement_routine/app/app_config.dart';
import 'package:supplement_routine/app/app_theme.dart';
import 'package:supplement_routine/core/services/home_widget_service.dart';
import 'package:supplement_routine/core/services/intake_notification_copy.dart';
import 'package:supplement_routine/core/services/intake_notification_service.dart';
import 'package:supplement_routine/features/today/today_provider.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';
import 'package:supplement_routine/l10n/generated/app_localizations_ko.dart';

import '../features/main_navigation_screen.dart';

class SupplementRoutineApp extends ConsumerStatefulWidget {
  const SupplementRoutineApp({super.key});

  @override
  ConsumerState<SupplementRoutineApp> createState() =>
      _SupplementRoutineAppState();
}

class _SupplementRoutineAppState extends ConsumerState<SupplementRoutineApp> {
  late final ProviderSubscription<List<TodayDisplayItem>>
  _todayWidgetSubscription;
  var _areDeferredServicesReady = false;

  @override
  void initState() {
    super.initState();
    _todayWidgetSubscription = ref.listenManual<List<TodayDisplayItem>>(
      todayListProvider,
      (_, todayList) {
        if (!_areDeferredServicesReady) {
          return;
        }

        unawaited(_syncTodaySideEffects(todayList));
      },
    );
    WidgetsBinding.instance.addPostFrameCallback((_) {
      unawaited(_initializeDeferredServices());
    });
  }

  Future<void> _initializeDeferredServices() async {
    await IntakeNotificationService.initialize(
      copy: IntakeNotificationCopy.fromLocalizations(AppLocalizationsKo()),
    );
    _areDeferredServicesReady = true;
    await _syncTodaySideEffects(ref.read(todayListProvider));
  }

  Future<void> _syncTodaySideEffects(List<TodayDisplayItem> todayList) async {
    await HomeWidgetService.updateTodaySummary(
      HomeWidgetSummary.fromTodayList(todayList),
    );
    await IntakeNotificationService.syncTodayReminders(todayList);
  }

  @override
  void dispose() {
    _todayWidgetSubscription.close();
    super.dispose();
  }

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
