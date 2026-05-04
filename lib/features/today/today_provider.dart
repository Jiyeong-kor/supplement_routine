import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/models/supplement.dart';
import '../../core/models/intake_record.dart';
import '../../core/models/intake_method.dart';
import '../../core/models/intake_condition.dart';

// UI 표시를 위한 편의용 클래스
class TodayDisplayItem {
  final Supplement supplement;
  final IntakeRecord record;

  TodayDisplayItem({required this.supplement, required this.record});
}

class TodayListNotifier extends Notifier<List<TodayDisplayItem>> {
  @override
  List<TodayDisplayItem> build() {
    // 4단계: 더미 데이터 생성
    final now = DateTime.now();
    
    final dummySupplements = [
      Supplement(
        id: '1',
        name: '비타민 D',
        dailyCount: 1,
        method: IntakeMethod.mealBased,
        condition: IntakeCondition.afterMeal,
        isNotificationEnabled: true,
        memo: '커피랑 같이 먹지 않기',
      ),
      Supplement(
        id: '2',
        name: '오메가3',
        dailyCount: 1,
        method: IntakeMethod.mealBased,
        condition: IntakeCondition.afterMeal,
        isNotificationEnabled: true,
      ),
      Supplement(
        id: '3',
        name: '마그네슘',
        dailyCount: 1,
        method: IntakeMethod.fixedTime,
        condition: IntakeCondition.beforeSleep,
        isNotificationEnabled: true,
      ),
    ];

    return [
      TodayDisplayItem(
        supplement: dummySupplements[0],
        record: IntakeRecord(
          id: 'r1',
          supplementId: '1',
          date: now,
          scheduledTime: const TimeOfDay(hour: 8, minute: 0),
          isDone: false,
        ),
      ),
      TodayDisplayItem(
        supplement: dummySupplements[1],
        record: IntakeRecord(
          id: 'r2',
          supplementId: '2',
          date: now,
          scheduledTime: const TimeOfDay(hour: 12, minute: 30),
          isDone: true,
          takenAt: now,
        ),
      ),
      TodayDisplayItem(
        supplement: dummySupplements[2],
        record: IntakeRecord(
          id: 'r3',
          supplementId: '3',
          date: now,
          scheduledTime: const TimeOfDay(hour: 21, minute: 0),
          isDone: false,
        ),
      ),
    ];
  }
}

final todayListProvider = NotifierProvider<TodayListNotifier, List<TodayDisplayItem>>(() {
  return TodayListNotifier();
});
