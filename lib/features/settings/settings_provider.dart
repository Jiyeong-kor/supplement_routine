import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/features/settings/data/memory_settings_repository.dart';
import 'package:supplement_routine/features/settings/data/settings_repository.dart';

final settingsRepositoryProvider = Provider<SettingsRepository>((ref) {
  return MemorySettingsRepository();
});

class MealTimeSettingsNotifier extends Notifier<MealTimeSettings> {
  @override
  MealTimeSettings build() {
    return ref.read(settingsRepositoryProvider).getMealTimeSettings();
  }

  void updateBreakfastTime(TimeOfDay time) {
    state = ref.read(settingsRepositoryProvider).updateBreakfastTime(time);
  }

  void updateLunchTime(TimeOfDay time) {
    state = ref.read(settingsRepositoryProvider).updateLunchTime(time);
  }

  void updateDinnerTime(TimeOfDay time) {
    state = ref.read(settingsRepositoryProvider).updateDinnerTime(time);
  }
}

final mealTimeSettingsProvider =
    NotifierProvider<MealTimeSettingsNotifier, MealTimeSettings>(() {
      return MealTimeSettingsNotifier();
    });

class NotificationSettingsNotifier extends Notifier<bool> {
  @override
  bool build() {
    return ref.read(settingsRepositoryProvider).getNotificationEnabled();
  }

  void updateEnabled(bool isEnabled) {
    state = ref
        .read(settingsRepositoryProvider)
        .updateNotificationEnabled(isEnabled);
  }
}

final notificationSettingsProvider =
    NotifierProvider<NotificationSettingsNotifier, bool>(() {
      return NotificationSettingsNotifier();
    });
