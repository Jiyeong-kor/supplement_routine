import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/core/models/schedule_label.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/utils/time_utils.dart';

class ScheduledIntake {
  final TimeOfDay time;
  final ScheduleLabel label;

  ScheduledIntake(this.time, this.label);
}

class ScheduledIntakeRecord {
  final Supplement supplement;
  final IntakeRecord record;
  final ScheduleLabel label;

  ScheduledIntakeRecord({
    required this.supplement,
    required this.record,
    required this.label,
  });
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
      return times
          .map((time) => ScheduledIntake(time, const ScheduleLabel.fixedTime()))
          .toList();
    }

    if (supplement.method == IntakeMethod.interval) {
      var result = <ScheduledIntake>[];
      var currentTime =
          supplement.startTime ?? const TimeOfDay(hour: 8, minute: 0);
      final interval = supplement.intervalHours ?? 8;
      for (int i = 0; i < supplement.dailyCount; i++) {
        result.add(
          ScheduledIntake(currentTime, const ScheduleLabel.interval()),
        );
        final nextMinutes =
            currentTime.hour * 60 + currentTime.minute + (interval * 60);
        if (nextMinutes >= 24 * 60) break;
        currentTime = currentTime.addMinutes(interval * 60);
      }
      return result;
    }

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

      return ScheduledIntake(scheduledTime, ScheduleLabel.routineSlot(slot));
    }).toList();
  }

  static List<ScheduledIntakeRecord> createDailyIntakeRecords({
    required List<Supplement> supplements,
    required DateTime date,
    MealTimeSettings mealTimeSettings = const MealTimeSettings(),
  }) {
    final records = <ScheduledIntakeRecord>[];

    for (final supplement in supplements) {
      final scheduledIntakes = calculateIntakeTimes(
        supplement,
        mealTimeSettings: mealTimeSettings,
      );

      for (int i = 0; i < scheduledIntakes.length; i++) {
        final intake = scheduledIntakes[i];
        records.add(
          ScheduledIntakeRecord(
            supplement: supplement,
            label: intake.label,
            record: IntakeRecord(
              id: 'r_${supplement.id}_${_dateKey(date)}_$i',
              supplementId: supplement.id,
              date: date,
              scheduledTime: intake.time,
              isDone: false,
            ),
          ),
        );
      }
    }

    records.sort((a, b) {
      final aTime =
          a.record.scheduledTime.hour * 60 + a.record.scheduledTime.minute;
      final bTime =
          b.record.scheduledTime.hour * 60 + b.record.scheduledTime.minute;
      return aTime.compareTo(bTime);
    });

    return records;
  }

  static String _dateKey(DateTime date) {
    final year = date.year.toString().padLeft(4, '0');
    final month = date.month.toString().padLeft(2, '0');
    final day = date.day.toString().padLeft(2, '0');

    return '$year$month$day';
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
