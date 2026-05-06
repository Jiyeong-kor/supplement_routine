import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/utils/time_utils.dart';

class ScheduledIntake {
  final TimeOfDay time;
  final String label;

  ScheduledIntake(this.time, this.label);
}

class SchedulingService {
  static const wakeupTime = TimeOfDay(hour: 7, minute: 0);
  static const sleepTime = TimeOfDay(hour: 22, minute: 0);

  static List<ScheduledIntake> calculateIntakeTimes(
    Supplement supplement, {
    MealTimeSettings mealTimeSettings = const MealTimeSettings(),
  }) {
    if (supplement.method == IntakeMethod.fixedTime) {
      final times =
          supplement.fixedTimes ?? [const TimeOfDay(hour: 9, minute: 0)];
      // 정해진 시간은 별도 라벨 없이 '복용 시간' 정도로만 표시하거나 생략 가능
      return times.map((t) => ScheduledIntake(t, '정해진 시간')).toList();
    }

    if (supplement.method == IntakeMethod.interval) {
      var result = <ScheduledIntake>[];
      var currentTime =
          supplement.startTime ?? const TimeOfDay(hour: 8, minute: 0);
      final interval = supplement.intervalHours ?? 8;
      for (int i = 0; i < supplement.dailyCount; i++) {
        result.add(ScheduledIntake(currentTime, '일정 간격'));
        final nextMinutes =
            currentTime.hour * 60 + currentTime.minute + (interval * 60);
        if (nextMinutes >= 24 * 60) break;
        currentTime = currentTime.addMinutes(interval * 60);
      }
      return result;
    }

    // 루틴 기반 (가장 상세한 라벨 제공)
    if (supplement.selectedSlots == null || supplement.selectedSlots!.isEmpty) {
      return [];
    }

    return supplement.selectedSlots!.map((slot) {
      TimeOfDay scheduledTime;

      if (slot.condition == IntakeCondition.fasting) {
        scheduledTime = wakeupTime;
      } else if (slot.condition == IntakeCondition.beforeSleep) {
        scheduledTime = sleepTime;
      } else if (slot.condition == IntakeCondition.betweenMeals) {
        scheduledTime = slot.mealType == MealType.breakfast
            ? _midpoint(
                mealTimeSettings.breakfastTime,
                mealTimeSettings.lunchTime,
              )
            : _midpoint(
                mealTimeSettings.lunchTime,
                mealTimeSettings.dinnerTime,
              );
      } else {
        final base = slot.mealType == MealType.breakfast
            ? mealTimeSettings.breakfastTime
            : (slot.mealType == MealType.lunch
                  ? mealTimeSettings.lunchTime
                  : mealTimeSettings.dinnerTime);

        if (slot.condition == IntakeCondition.beforeMeal) {
          scheduledTime = base.addMinutes(-30);
        } else if (slot.condition == IntakeCondition.afterMeal) {
          scheduledTime = base.addMinutes(30);
        } else {
          scheduledTime = base;
        }
      }

      return ScheduledIntake(scheduledTime, slot.label);
    }).toList();
  }

  static TimeOfDay _midpoint(TimeOfDay start, TimeOfDay end) {
    final startMinutes = start.hour * 60 + start.minute;
    final endMinutes = end.hour * 60 + end.minute;
    final midpointMinutes = ((startMinutes + endMinutes) / 2).round();

    return TimeOfDay(
      hour: (midpointMinutes ~/ 60) % 24,
      minute: midpointMinutes % 60,
    );
  }
}
