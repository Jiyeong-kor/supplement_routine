import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/features/settings/data/settings_repository.dart';

class LocalSettingsRepository implements SettingsRepository {
  LocalSettingsRepository(this._preferences) {
    _mealTimeSettings = _loadMealTimeSettings();
    _isNotificationEnabled = _loadNotificationEnabled();
  }

  static const mealTimeSettingsKey = 'meal_time_settings';
  static const notificationEnabledKey = 'notification_enabled';

  final SharedPreferencesWithCache _preferences;
  late MealTimeSettings _mealTimeSettings;
  late bool _isNotificationEnabled;

  @override
  MealTimeSettings getMealTimeSettings() {
    return _mealTimeSettings;
  }

  @override
  MealTimeSettings updateBreakfastTime(TimeOfDay time) {
    _mealTimeSettings = _mealTimeSettings.copyWith(breakfastTime: time);
    _saveMealTimeSettings();
    return getMealTimeSettings();
  }

  @override
  MealTimeSettings updateLunchTime(TimeOfDay time) {
    _mealTimeSettings = _mealTimeSettings.copyWith(lunchTime: time);
    _saveMealTimeSettings();
    return getMealTimeSettings();
  }

  @override
  MealTimeSettings updateDinnerTime(TimeOfDay time) {
    _mealTimeSettings = _mealTimeSettings.copyWith(dinnerTime: time);
    _saveMealTimeSettings();
    return getMealTimeSettings();
  }

  @override
  bool getNotificationEnabled() {
    return _isNotificationEnabled;
  }

  @override
  bool updateNotificationEnabled(bool isEnabled) {
    _isNotificationEnabled = isEnabled;
    unawaited(_preferences.setBool(notificationEnabledKey, isEnabled));
    return getNotificationEnabled();
  }

  MealTimeSettings _loadMealTimeSettings() {
    final rawJson = _preferences.getString(mealTimeSettingsKey);

    if (rawJson == null) {
      return const MealTimeSettings();
    }

    return MealTimeSettings.fromJson(
      Map<String, Object?>.from(jsonDecode(rawJson) as Map),
    );
  }

  bool _loadNotificationEnabled() {
    return _preferences.getBool(notificationEnabledKey) ?? true;
  }

  void _saveMealTimeSettings() {
    unawaited(
      _preferences.setString(
        mealTimeSettingsKey,
        jsonEncode(_mealTimeSettings.toJson()),
      ),
    );
  }
}
