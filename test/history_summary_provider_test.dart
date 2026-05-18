import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/history/data/mock_intake_record_repository.dart';
import 'package:supplement_routine/features/history/history_summary_provider.dart';
import 'package:supplement_routine/features/history/history_view_model.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/settings/data/memory_settings_repository.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplement_repository.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

void main() {
  ProviderContainer createContainer() {
    return ProviderContainer(
      overrides: [
        supplementRepositoryProvider.overrideWithValue(
          MockSupplementRepository(initialSupplements: const []),
        ),
        intakeRecordRepositoryProvider.overrideWithValue(
          MockIntakeRecordRepository(initialRecords: const {}),
        ),
        settingsRepositoryProvider.overrideWithValue(
          MemorySettingsRepository(),
        ),
      ],
    );
  }

  test('빈 기록 요약은 완료율 0과 empty 상태를 반환한다', () {
    final summary = DailyHistorySummary(
      date: DateTime(2026, 5, 18),
      doneCount: 0,
      totalCount: 0,
    );

    expect(summary.completionRate, 0);
    expect(summary.isEmpty, isTrue);
  });

  test('현재 달 요약은 미래 날짜를 비워 둔다', () {
    final container = createContainer();
    addTearDown(container.dispose);

    final summaries = container.read(currentMonthHistorySummariesProvider);
    final today = DateTime.now();
    final futureSummaries = summaries.where(
      (summary) =>
          summary.date.isAfter(DateTime(today.year, today.month, today.day)),
    );

    expect(
      futureSummaries.every(
        (summary) =>
            summary.doneCount == 0 &&
            summary.totalCount == 0 &&
            summary.isEmpty,
      ),
      isTrue,
    );
  });

  test('기록 ViewModel은 모든 날짜가 비어 있으면 empty 상태다', () {
    final container = createContainer();
    addTearDown(container.dispose);

    expect(container.read(historyViewModelProvider).isEmpty, isTrue);
  });

  test('저장된 완료 기록은 날짜별 요약에 반영된다', () {
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
    );
    final today = DateTime.now();
    final date = DateTime(today.year, today.month, today.day);
    final record = IntakeRecord(
      id: 'r_vitamin_d_${date.year.toString().padLeft(4, '0')}${date.month.toString().padLeft(2, '0')}${date.day.toString().padLeft(2, '0')}_0',
      supplementId: 'vitamin_d',
      date: date,
      scheduledTime: const TimeOfDay(hour: 8, minute: 30),
      isDone: true,
    );
    final container = ProviderContainer(
      overrides: [
        supplementRepositoryProvider.overrideWithValue(
          MockSupplementRepository(initialSupplements: const [supplement]),
        ),
        intakeRecordRepositoryProvider.overrideWithValue(
          MockIntakeRecordRepository(initialRecords: {record.id: record}),
        ),
        settingsRepositoryProvider.overrideWithValue(
          MemorySettingsRepository(),
        ),
      ],
    );
    addTearDown(container.dispose);

    final summary = container.read(todayHistorySummaryProvider);

    expect(summary.doneCount, 1);
    expect(summary.totalCount, 1);
    expect(summary.completionRate, 1);
  });
}
