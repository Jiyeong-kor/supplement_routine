import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/meal_time_settings.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/services/scheduling_service.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class DailyHistorySummary {
  final DateTime date;
  final int doneCount;
  final int totalCount;

  const DailyHistorySummary({
    required this.date,
    required this.doneCount,
    required this.totalCount,
  });

  double get completionRate {
    if (totalCount == 0) {
      return 0;
    }

    return doneCount / totalCount;
  }

  bool get isEmpty => totalCount == 0;
}

final todayHistorySummaryProvider = Provider<DailyHistorySummary>((ref) {
  return ref.watch(historySummariesProvider).first;
});

final historySummariesProvider = Provider<List<DailyHistorySummary>>((ref) {
  final supplements = ref.watch(supplementListProvider);
  final mealTimeSettings = ref.watch(mealTimeSettingsProvider);
  final records = ref.watch(intakeRecordProvider);
  final today = DateTime.now();

  return List.generate(14, (index) {
    final date = DateTime(today.year, today.month, today.day - index);
    return _createSummary(
      date: date,
      supplements: supplements,
      records: records,
      mealTimeSettings: mealTimeSettings,
    );
  });
});

final currentMonthHistorySummariesProvider =
    Provider<List<DailyHistorySummary>>((ref) {
      final supplements = ref.watch(supplementListProvider);
      final mealTimeSettings = ref.watch(mealTimeSettingsProvider);
      final records = ref.watch(intakeRecordProvider);
      final today = DateTime.now();
      final lastDay = DateTime(today.year, today.month + 1, 0).day;

      return List.generate(lastDay, (index) {
        final date = DateTime(today.year, today.month, index + 1);

        if (date.isAfter(DateTime(today.year, today.month, today.day))) {
          return DailyHistorySummary(date: date, doneCount: 0, totalCount: 0);
        }

        return _createSummary(
          date: date,
          supplements: supplements,
          records: records,
          mealTimeSettings: mealTimeSettings,
        );
      });
    });

DailyHistorySummary _createSummary({
  required DateTime date,
  required List<Supplement> supplements,
  required Map<String, IntakeRecord> records,
  required MealTimeSettings mealTimeSettings,
}) {
  var doneCount = 0;
  var totalCount = 0;

  final scheduledRecords = SchedulingService.createDailyIntakeRecords(
    supplements: supplements,
    date: date,
    mealTimeSettings: mealTimeSettings,
  );

  for (final item in scheduledRecords) {
    final savedRecord = records[item.record.id];

    totalCount++;
    if (savedRecord?.isDone ?? false) {
      doneCount++;
    }
  }

  return DailyHistorySummary(
    date: date,
    doneCount: doneCount,
    totalCount: totalCount,
  );
}
