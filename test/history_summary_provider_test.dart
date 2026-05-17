import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
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
}
