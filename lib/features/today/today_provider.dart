import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/services/scheduling_service.dart';
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
    final now = DateTime.now();
    List<TodayDisplayItem> items = [];

    for (final s in supplements) {
      final scheduledIntakes = SchedulingService.calculateIntakeTimes(s);

      for (int i = 0; i < scheduledIntakes.length; i++) {
        final intake = scheduledIntakes[i];
        items.add(
          TodayDisplayItem(
            supplement: s,
            label: intake.label,
            record: IntakeRecord(
              id: 'r_${s.id}_${now.day}_$i',
              supplementId: s.id,
              date: now,
              scheduledTime: intake.time,
              isDone: false,
            ),
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
    state = [
      for (final item in state)
        if (item.record.id == recordId)
          TodayDisplayItem(
            supplement: item.supplement,
            label: item.label,
            record: item.record.isDone
                ? item.record.markUndone()
                : item.record.markDone(),
          )
        else
          item,
    ];
  }
}

final todayListProvider =
    NotifierProvider<TodayListNotifier, List<TodayDisplayItem>>(() {
      return TodayListNotifier();
    });
