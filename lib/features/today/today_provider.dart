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
    List<TodayDisplayItem> items = [];

    for (final s in supplements) {
      final scheduledIntakes = SchedulingService.calculateIntakeTimes(
        s,
        mealTimeSettings: mealTimeSettings,
      );

      for (int i = 0; i < scheduledIntakes.length; i++) {
        final intake = scheduledIntakes[i];
        final record = IntakeRecord(
          id: 'r_${s.id}_${now.day}_$i',
          supplementId: s.id,
          date: now,
          scheduledTime: intake.time,
          isDone: false,
        );
        items.add(
          TodayDisplayItem(
            supplement: s,
            label: intake.label,
            record: ref.read(intakeRecordProvider.notifier).getOrCreate(record),
          ),
        );
      }
    }

    items.sort((a, b) {
      final aTime =
          a.record.scheduledTime.hour * 60 + a.record.scheduledTime.minute;
      final bTime =
          b.record.scheduledTime.hour * 60 + b.record.scheduledTime.minute;
      return aTime.compareTo(bTime);
    });

    return items;
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
