import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/services/scheduling_service.dart';

void main() {
  final date = DateTime(2026, 5, 18);

  Supplement supplement({
    required String id,
    required IntakeMethod method,
    int dailyCount = 1,
    List<TimeOfDay>? fixedTimes,
    TimeOfDay? startTime,
    int? intervalHours,
    List<IntakeSlot>? selectedSlots,
  }) {
    return Supplement(
      id: id,
      name: id,
      dailyCount: dailyCount,
      method: method,
      dosageUnit: '개',
      dosageValue: 1,
      fixedTimes: fixedTimes,
      startTime: startTime,
      intervalHours: intervalHours,
      selectedSlots: selectedSlots,
      isNotificationEnabled: true,
    );
  }

  test('정해진 시간 방식은 입력한 시간을 그대로 사용한다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(
        id: 'fixed',
        method: IntakeMethod.fixedTime,
        fixedTimes: const [
          TimeOfDay(hour: 8, minute: 0),
          TimeOfDay(hour: 21, minute: 30),
        ],
      ),
    );

    expect(result.map((item) => item.time).toList(), [
      const TimeOfDay(hour: 8, minute: 0),
      const TimeOfDay(hour: 21, minute: 30),
    ]);
  });

  test('정해진 시간 방식은 값이 없으면 기본 시간을 사용한다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(id: 'fixed_default', method: IntakeMethod.fixedTime),
    );

    expect(result.single.time, const TimeOfDay(hour: 9, minute: 0));
  });

  test('일정 간격 방식은 자정을 넘는 일정을 생성하지 않는다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(
        id: 'interval',
        method: IntakeMethod.interval,
        dailyCount: 4,
        startTime: const TimeOfDay(hour: 20, minute: 0),
        intervalHours: 3,
      ),
    );

    expect(result.map((item) => item.time).toList(), [
      const TimeOfDay(hour: 20, minute: 0),
      const TimeOfDay(hour: 23, minute: 0),
    ]);
  });

  test('일정 간격 방식은 시작 시간과 간격 기본값을 사용한다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(
        id: 'interval_default',
        method: IntakeMethod.interval,
        dailyCount: 2,
      ),
    );

    expect(result.map((item) => item.time).toList(), [
      const TimeOfDay(hour: 8, minute: 0),
      const TimeOfDay(hour: 16, minute: 0),
    ]);
  });

  test('식사 기준 방식은 조건별 시간을 계산한다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(
        id: 'routine',
        method: IntakeMethod.mealBased,
        selectedSlots: const [
          IntakeSlot(condition: IntakeCondition.fasting),
          IntakeSlot(
            mealType: MealType.breakfast,
            condition: IntakeCondition.beforeMeal,
          ),
          IntakeSlot(
            mealType: MealType.breakfast,
            condition: IntakeCondition.afterMeal,
          ),
          IntakeSlot(
            mealType: MealType.breakfast,
            condition: IntakeCondition.betweenMeals,
          ),
          IntakeSlot(condition: IntakeCondition.beforeSleep),
        ],
      ),
      mealTimeSettings: const MealTimeSettings(
        breakfastTime: TimeOfDay(hour: 8, minute: 0),
        lunchTime: TimeOfDay(hour: 12, minute: 0),
        dinnerTime: TimeOfDay(hour: 18, minute: 0),
      ),
    );

    expect(result.map((item) => item.time).toList(), [
      const TimeOfDay(hour: 7, minute: 0),
      const TimeOfDay(hour: 7, minute: 30),
      const TimeOfDay(hour: 8, minute: 30),
      const TimeOfDay(hour: 10, minute: 0),
      const TimeOfDay(hour: 22, minute: 0),
    ]);
  });

  test('식간 루틴은 점심과 저녁 사이도 계산한다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(
        id: 'between_lunch_dinner',
        method: IntakeMethod.mealBased,
        selectedSlots: const [
          IntakeSlot(
            mealType: MealType.lunch,
            condition: IntakeCondition.betweenMeals,
          ),
        ],
      ),
      mealTimeSettings: const MealTimeSettings(
        lunchTime: TimeOfDay(hour: 12, minute: 0),
        dinnerTime: TimeOfDay(hour: 18, minute: 0),
      ),
    );

    expect(result.single.time, const TimeOfDay(hour: 15, minute: 0));
  });

  test('일일 기록은 시간순으로 정렬된다', () {
    final records = SchedulingService.createDailyIntakeRecords(
      date: date,
      supplements: [
        supplement(
          id: 'late',
          method: IntakeMethod.fixedTime,
          fixedTimes: const [TimeOfDay(hour: 21, minute: 0)],
        ),
        supplement(
          id: 'early',
          method: IntakeMethod.fixedTime,
          fixedTimes: const [TimeOfDay(hour: 8, minute: 0)],
        ),
      ],
    );

    expect(records.map((item) => item.supplement.id).toList(), [
      'early',
      'late',
    ]);
  });

  test('선택된 루틴 시간이 없으면 일정을 만들지 않는다', () {
    final result = SchedulingService.calculateIntakeTimes(
      supplement(id: 'empty', method: IntakeMethod.mealBased),
    );

    expect(result, isEmpty);
  });
}
