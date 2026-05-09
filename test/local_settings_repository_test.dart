import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:shared_preferences_platform_interface/in_memory_shared_preferences_async.dart';
import 'package:shared_preferences_platform_interface/shared_preferences_async_platform_interface.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/features/settings/data/local_settings_repository.dart';

void main() {
  const storageKeys = {
    LocalSettingsRepository.mealTimeSettingsKey,
    LocalSettingsRepository.notificationEnabledKey,
  };

  test('저장된 설정값을 로컬 저장소에서 불러온다', () async {
    final settings = MealTimeSettings(
      breakfastTime: const TimeOfDay(hour: 7, minute: 30),
      lunchTime: const TimeOfDay(hour: 12, minute: 20),
      dinnerTime: const TimeOfDay(hour: 19, minute: 10),
    );
    final store = InMemorySharedPreferencesAsync.empty();
    SharedPreferencesAsyncPlatform.instance = store;
    await store.setString(
      LocalSettingsRepository.mealTimeSettingsKey,
      jsonEncode(settings.toJson()),
      const SharedPreferencesOptions(),
    );
    await store.setBool(
      LocalSettingsRepository.notificationEnabledKey,
      false,
      const SharedPreferencesOptions(),
    );
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: storageKeys,
      ),
    );

    final repository = LocalSettingsRepository(preferences);

    expect(
      repository.getMealTimeSettings().breakfastTime,
      settings.breakfastTime,
    );
    expect(repository.getMealTimeSettings().lunchTime, settings.lunchTime);
    expect(repository.getMealTimeSettings().dinnerTime, settings.dinnerTime);
    expect(repository.getNotificationEnabled(), isFalse);
  });

  test('설정값 변경 사항을 로컬 저장소에 저장한다', () async {
    SharedPreferencesAsyncPlatform.instance =
        InMemorySharedPreferencesAsync.empty();
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: storageKeys,
      ),
    );
    final repository = LocalSettingsRepository(preferences);

    repository.updateBreakfastTime(const TimeOfDay(hour: 6, minute: 45));
    repository.updateNotificationEnabled(false);

    final rawJson = preferences.getString(
      LocalSettingsRepository.mealTimeSettingsKey,
    );
    final values = jsonDecode(rawJson!) as Map;

    expect(values['breakfastTime']['hour'], 6);
    expect(values['breakfastTime']['minute'], 45);
    expect(
      preferences.getBool(LocalSettingsRepository.notificationEnabledKey),
      isFalse,
    );
  });
}
