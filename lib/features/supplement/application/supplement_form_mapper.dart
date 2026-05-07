import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/supplement.dart';

class SupplementFormInput {
  const SupplementFormInput({
    required this.name,
    required this.dosageUnit,
    required this.dosageValue,
    required this.isRoutineBased,
    required this.isSpecificTime,
    required this.selectedSlots,
    required this.fixedCount,
    required this.fixedTimes,
    required this.startTime,
    required this.intervalHours,
    required this.intervalCount,
    required this.isNotificationEnabled,
    required this.memo,
  });

  final String name;
  final String dosageUnit;
  final double dosageValue;
  final bool isRoutineBased;
  final bool isSpecificTime;
  final Set<IntakeSlot> selectedSlots;
  final int fixedCount;
  final List<TimeOfDay> fixedTimes;
  final TimeOfDay startTime;
  final int intervalHours;
  final int intervalCount;
  final bool isNotificationEnabled;
  final String? memo;
}

class SupplementFormMapper {
  static Supplement toSupplement({
    required SupplementFormInput input,
    Supplement? initialSupplement,
  }) {
    final method = _methodFor(input);
    final dailyCount = _dailyCountFor(input, method);

    return Supplement(
      id: initialSupplement?.id ?? _createSupplementId(),
      name: input.name,
      dailyCount: dailyCount,
      method: method,
      dosageUnit: input.dosageUnit,
      dosageValue: input.dosageValue,
      selectedSlots: method == IntakeMethod.mealBased
          ? input.selectedSlots.toList()
          : null,
      fixedTimes: method == IntakeMethod.fixedTime ? input.fixedTimes : null,
      startTime: method == IntakeMethod.interval ? input.startTime : null,
      intervalHours: method == IntakeMethod.interval
          ? input.intervalHours
          : null,
      isNotificationEnabled: input.isNotificationEnabled,
      memo: input.memo,
    );
  }

  static IntakeMethod _methodFor(SupplementFormInput input) {
    if (input.isRoutineBased) {
      return IntakeMethod.mealBased;
    }

    return input.isSpecificTime
        ? IntakeMethod.fixedTime
        : IntakeMethod.interval;
  }

  static int _dailyCountFor(SupplementFormInput input, IntakeMethod method) {
    return switch (method) {
      IntakeMethod.mealBased => input.selectedSlots.length,
      IntakeMethod.fixedTime => input.fixedCount,
      IntakeMethod.interval => input.intervalCount,
    };
  }

  static String _createSupplementId() {
    return DateTime.now().millisecondsSinceEpoch.toString();
  }
}
