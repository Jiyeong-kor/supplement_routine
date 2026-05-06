import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
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
  final supplements = ref.watch(supplementListProvider);
  final mealTimeSettings = ref.watch(mealTimeSettingsProvider);
  final records = ref.watch(intakeRecordProvider);
  final today = DateTime.now();

  var doneCount = 0;
  var totalCount = 0;

  for (final supplement in supplements) {
    final scheduledIntakes = SchedulingService.calculateIntakeTimes(
      supplement,
      mealTimeSettings: mealTimeSettings,
    );

    for (int i = 0; i < scheduledIntakes.length; i++) {
      final intake = scheduledIntakes[i];
      final record = IntakeRecord(
        id: 'r_${supplement.id}_${today.day}_$i',
        supplementId: supplement.id,
        date: today,
        scheduledTime: intake.time,
        isDone: false,
      );
      final savedRecord = records[record.id];

      totalCount++;
      if (savedRecord?.isDone ?? false) {
        doneCount++;
      }
    }
  }

  return DailyHistorySummary(
    date: today,
    doneCount: doneCount,
    totalCount: totalCount,
  );
});
