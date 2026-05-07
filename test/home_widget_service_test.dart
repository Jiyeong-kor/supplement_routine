import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/services/home_widget_service.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

void main() {
  TodayDisplayItem createTodayItem({
    required String id,
    required String supplementName,
    required TimeOfDay scheduledTime,
    required bool isDone,
  }) {
    return TodayDisplayItem(
      supplement: Supplement(
        id: id,
        name: supplementName,
        dailyCount: 1,
        method: IntakeMethod.fixedTime,
        dosageUnit: '개',
        dosageValue: 1,
        isNotificationEnabled: true,
      ),
      record: IntakeRecord(
        id: 'record_$id',
        supplementId: id,
        date: DateTime(2026, 5, 8),
        scheduledTime: scheduledTime,
        isDone: isDone,
      ),
      label: '정해진 시간',
    );
  }

  test('홈 위젯 요약은 완료 수와 다음 복용 항목을 계산한다', () {
    final summary = HomeWidgetSummary.fromTodayList([
      createTodayItem(
        id: 'vitamin_d',
        supplementName: '비타민 D',
        scheduledTime: const TimeOfDay(hour: 9, minute: 0),
        isDone: true,
      ),
      createTodayItem(
        id: 'omega3',
        supplementName: '오메가3',
        scheduledTime: const TimeOfDay(hour: 21, minute: 0),
        isDone: false,
      ),
    ]);

    expect(summary.doneCount, 1);
    expect(summary.totalCount, 2);
    expect(summary.hasSchedule, isTrue);
    expect(summary.hasNext, isTrue);
    expect(summary.nextName, '오메가3');
    expect(summary.nextHour, 21);
    expect(summary.nextMinute, 0);
  });

  test('홈 위젯 요약은 모든 일정 완료 상태를 계산한다', () {
    final summary = HomeWidgetSummary.fromTodayList([
      createTodayItem(
        id: 'vitamin_d',
        supplementName: '비타민 D',
        scheduledTime: const TimeOfDay(hour: 9, minute: 0),
        isDone: true,
      ),
    ]);

    expect(summary.doneCount, 1);
    expect(summary.totalCount, 1);
    expect(summary.hasSchedule, isTrue);
    expect(summary.hasNext, isFalse);
    expect(summary.nextName, isNull);
  });
}
