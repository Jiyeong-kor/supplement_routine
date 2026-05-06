import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';

class SupplementListNotifier extends Notifier<List<Supplement>> {
  @override
  List<Supplement> build() {
    return _mockSupplements;
  }

  void addSupplement(Supplement supplement) {
    state = [...state, supplement];
  }

  void updateSupplement(Supplement updatedSupplement) {
    state = [
      for (final supplement in state)
        if (supplement.id == updatedSupplement.id)
          updatedSupplement
        else
          supplement,
    ];
  }

  void removeSupplement(String supplementId) {
    state = [
      for (final supplement in state)
        if (supplement.id != supplementId) supplement,
    ];
    ref
        .read(intakeRecordProvider.notifier)
        .clearRecordsForSupplement(supplementId);
  }

  void clearSupplements() {
    state = [];
    ref.read(intakeRecordProvider.notifier).clearAll();
  }
}

final supplementListProvider =
    NotifierProvider<SupplementListNotifier, List<Supplement>>(() {
      return SupplementListNotifier();
    });

const _mockSupplements = [
  Supplement(
    id: 'mock_vitamin_d',
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
    memo: '아침 식사 후 챙기기',
  ),
  Supplement(
    id: 'mock_omega3',
    name: '오메가3',
    dailyCount: 1,
    method: IntakeMethod.fixedTime,
    dosageUnit: '개',
    dosageValue: 2,
    fixedTimes: [TimeOfDay(hour: 21, minute: 0)],
    isNotificationEnabled: true,
  ),
  Supplement(
    id: 'mock_magnesium',
    name: '마그네슘',
    dailyCount: 2,
    method: IntakeMethod.interval,
    dosageUnit: 'ml',
    dosageValue: 10,
    startTime: TimeOfDay(hour: 9, minute: 0),
    intervalHours: 8,
    isNotificationEnabled: false,
    memo: '간격 반복 UI 확인용',
  ),
];
