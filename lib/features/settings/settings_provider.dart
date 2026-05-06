import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';

class MealTimeSettingsNotifier extends Notifier<MealTimeSettings> {
  @override
  MealTimeSettings build() {
    return const MealTimeSettings();
  }

  void updateBreakfastTime(TimeOfDay time) {
    state = state.copyWith(breakfastTime: time);
  }

  void updateLunchTime(TimeOfDay time) {
    state = state.copyWith(lunchTime: time);
  }

  void updateDinnerTime(TimeOfDay time) {
    state = state.copyWith(dinnerTime: time);
  }
}

final mealTimeSettingsProvider =
    NotifierProvider<MealTimeSettingsNotifier, MealTimeSettings>(() {
      return MealTimeSettingsNotifier();
    });

class NotificationSettingsNotifier extends Notifier<bool> {
  @override
  bool build() {
    return true;
  }

  void updateEnabled(bool isEnabled) {
    state = isEnabled;
  }
}

final notificationSettingsProvider =
    NotifierProvider<NotificationSettingsNotifier, bool>(() {
      return NotificationSettingsNotifier();
    });
