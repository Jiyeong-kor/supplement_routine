import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/time_of_day_json.dart';

void main() {
  test('Supplement는 JSON 저장 후 복원할 수 있다', () {
    const supplement = Supplement(
      id: 'vitamin_d',
      name: '비타민 D',
      dailyCount: 1,
      method: IntakeMethod.mealBased,
      dosageUnit: '개',
      dosageValue: 1,
      selectedSlots: [
        IntakeSlot(
          mealType: MealType.breakfast,
          condition: IntakeCondition.afterMeal,
        ),
      ],
      isNotificationEnabled: true,
      memo: '아침 식사 후 체크',
    );

    final json = jsonDecode(jsonEncode(supplement.toJson()));
    final restored = Supplement.fromJson(
      Map<String, Object?>.from(json as Map),
    );

    expect(restored.id, supplement.id);
    expect(restored.name, supplement.name);
    expect(restored.method, supplement.method);
    expect(restored.selectedSlots, supplement.selectedSlots);
    expect(restored.memo, supplement.memo);
  });

  test('Supplement는 수동 시간과 간격 설정도 JSON 저장 후 복원한다', () {
    const supplement = Supplement(
      id: 'magnesium',
      name: '마그네슘',
      dailyCount: 2,
      method: IntakeMethod.interval,
      dosageUnit: '정',
      dosageValue: 1.5,
      fixedTimes: [TimeOfDay(hour: 9, minute: 0)],
      startTime: TimeOfDay(hour: 8, minute: 15),
      intervalHours: 6,
      isNotificationEnabled: false,
    );

    final restored = Supplement.fromJson(supplement.toJson());

    expect(restored.fixedTimes, supplement.fixedTimes);
    expect(restored.startTime, supplement.startTime);
    expect(restored.intervalHours, 6);
    expect(restored.isNotificationEnabled, isFalse);
  });

  test('IntakeSlot은 mealType이 없어도 JSON 저장 후 복원할 수 있다', () {
    const slot = IntakeSlot(condition: IntakeCondition.fasting);

    expect(IntakeSlot.fromJson(slot.toJson()), slot);
  });

  test('IntakeRecord는 JSON 저장 후 takenAt까지 복원할 수 있다', () {
    final record = IntakeRecord(
      id: 'record_1',
      supplementId: 'vitamin_d',
      date: DateTime(2026, 5, 8),
      scheduledTime: const TimeOfDay(hour: 8, minute: 30),
      isDone: true,
      takenAt: DateTime(2026, 5, 8, 8, 35),
    );

    final json = jsonDecode(jsonEncode(record.toJson()));
    final restored = IntakeRecord.fromJson(
      Map<String, Object?>.from(json as Map),
    );

    expect(restored.id, record.id);
    expect(restored.supplementId, record.supplementId);
    expect(restored.date, record.date);
    expect(restored.scheduledTime, record.scheduledTime);
    expect(restored.isDone, isTrue);
    expect(restored.takenAt, record.takenAt);
  });

  test('MealTimeSettings는 JSON 저장 후 식사 시간을 복원할 수 있다', () {
    const settings = MealTimeSettings(
      breakfastTime: TimeOfDay(hour: 7, minute: 30),
      lunchTime: TimeOfDay(hour: 12, minute: 15),
      dinnerTime: TimeOfDay(hour: 18, minute: 45),
    );

    final json = jsonDecode(jsonEncode(settings.toJson()));
    final restored = MealTimeSettings.fromJson(
      Map<String, Object?>.from(json as Map),
    );

    expect(restored.breakfastTime, settings.breakfastTime);
    expect(restored.lunchTime, settings.lunchTime);
    expect(restored.dinnerTime, settings.dinnerTime);
  });

  test('TimeOfDayJson은 시각을 직렬화하고 복원한다', () {
    const time = TimeOfDay(hour: 23, minute: 45);

    expect(TimeOfDayJson.toJson(time), {'hour': 23, 'minute': 45});
    expect(TimeOfDayJson.fromJson(TimeOfDayJson.toJson(time)), time);
  });

  test('IntakeRecord는 완료 해제 시 takenAt을 비운다', () {
    final record = IntakeRecord(
      id: 'record_2',
      supplementId: 'vitamin_d',
      date: DateTime(2026, 5, 8),
      scheduledTime: const TimeOfDay(hour: 8, minute: 30),
      isDone: true,
      takenAt: DateTime(2026, 5, 8, 8, 35),
    );

    final undone = record.markUndone();

    expect(undone.isDone, isFalse);
    expect(undone.takenAt, isNull);
  });
}
