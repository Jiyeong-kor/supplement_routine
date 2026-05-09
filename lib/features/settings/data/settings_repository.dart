import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';

abstract interface class SettingsRepository {
  MealTimeSettings getMealTimeSettings();

  MealTimeSettings updateBreakfastTime(TimeOfDay time);

  MealTimeSettings updateLunchTime(TimeOfDay time);

  MealTimeSettings updateDinnerTime(TimeOfDay time);

  bool getNotificationEnabled();

  bool updateNotificationEnabled(bool isEnabled);
}
