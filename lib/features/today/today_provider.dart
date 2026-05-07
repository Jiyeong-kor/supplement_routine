import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/services/scheduling_service.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class TodayDisplayItem {
  final Supplement supplement;
  final IntakeRecord record;
  final String label;

  TodayDisplayItem({
    required this.supplement,
    required this.record,
    required this.label,
  });
}

class TodayListNotifier extends Notifier<List<TodayDisplayItem>> {
  @override
  List<TodayDisplayItem> build() {
    final supplements = ref.watch(supplementListProvider);
    final mealTimeSettings = ref.watch(mealTimeSettingsProvider);
    ref.watch(intakeRecordProvider);
    final now = DateTime.now();

    return SchedulingService.createDailyIntakeRecords(
      supplements: supplements,
      date: now,
      mealTimeSettings: mealTimeSettings,
    ).map((item) {
      return TodayDisplayItem(
        supplement: item.supplement,
        label: item.label,
        record: ref
            .read(intakeRecordProvider.notifier)
            .getOrCreate(item.record),
      );
    }).toList();
  }

  void toggleRecord(String recordId) {
    IntakeRecord? record;
    for (final item in state) {
      if (item.record.id == recordId) {
        record = item.record;
        break;
      }
    }

    if (record == null) {
      return;
    }

    ref.read(intakeRecordProvider.notifier).toggleRecord(record);
  }
}

final todayListProvider =
    NotifierProvider<TodayListNotifier, List<TodayDisplayItem>>(() {
      return TodayListNotifier();
    });
