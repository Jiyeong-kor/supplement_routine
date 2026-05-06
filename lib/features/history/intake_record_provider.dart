import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/intake_record.dart';

class IntakeRecordNotifier extends Notifier<Map<String, IntakeRecord>> {
  @override
  Map<String, IntakeRecord> build() {
    return {};
  }

  IntakeRecord getOrCreate(IntakeRecord record) {
    final savedRecord = state[record.id];
    if (savedRecord == null) {
      return record;
    }

    return record.copyWith(
      isDone: savedRecord.isDone,
      takenAt: savedRecord.takenAt,
    );
  }

  void toggleRecord(IntakeRecord record) {
    final currentRecord = state[record.id] ?? record;
    final updatedRecord = currentRecord.isDone
        ? currentRecord.markUndone()
        : currentRecord.markDone();

    state = {...state, record.id: updatedRecord};
  }

  void clearRecordsForSupplement(String supplementId) {
    state = {
      for (final entry in state.entries)
        if (entry.value.supplementId != supplementId) entry.key: entry.value,
    };
  }

  void clearAll() {
    state = {};
  }
}

final intakeRecordProvider =
    NotifierProvider<IntakeRecordNotifier, Map<String, IntakeRecord>>(() {
      return IntakeRecordNotifier();
    });
