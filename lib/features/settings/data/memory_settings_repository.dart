import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/features/settings/data/settings_repository.dart';

class MemorySettingsRepository implements SettingsRepository {
  MealTimeSettings _mealTimeSettings = const MealTimeSettings();
  bool _isNotificationEnabled = true;

  @override
  MealTimeSettings getMealTimeSettings() {
    return _mealTimeSettings;
  }

  @override
  MealTimeSettings updateBreakfastTime(TimeOfDay time) {
    _mealTimeSettings = _mealTimeSettings.copyWith(breakfastTime: time);
    return getMealTimeSettings();
  }

  @override
  MealTimeSettings updateLunchTime(TimeOfDay time) {
    _mealTimeSettings = _mealTimeSettings.copyWith(lunchTime: time);
    return getMealTimeSettings();
  }

  @override
  MealTimeSettings updateDinnerTime(TimeOfDay time) {
    _mealTimeSettings = _mealTimeSettings.copyWith(dinnerTime: time);
    return getMealTimeSettings();
  }

  @override
  bool getNotificationEnabled() {
    return _isNotificationEnabled;
  }

  @override
  bool updateNotificationEnabled(bool isEnabled) {
    _isNotificationEnabled = isEnabled;
    return getNotificationEnabled();
  }
}
