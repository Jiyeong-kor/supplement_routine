import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/features/history/data/intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/mock_intake_record_repository.dart';

final intakeRecordRepositoryProvider = Provider<IntakeRecordRepository>((ref) {
  return MockIntakeRecordRepository();
});

class IntakeRecordNotifier extends Notifier<Map<String, IntakeRecord>> {
  @override
  Map<String, IntakeRecord> build() {
    return ref.read(intakeRecordRepositoryProvider).getRecords();
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

    state = ref
        .read(intakeRecordRepositoryProvider)
        .upsertRecord(updatedRecord);
  }

  void clearRecordsForSupplement(String supplementId) {
    state = ref
        .read(intakeRecordRepositoryProvider)
        .clearRecordsForSupplement(supplementId);
  }

  void clearAll() {
    state = ref.read(intakeRecordRepositoryProvider).clearAll();
  }
}

final intakeRecordProvider =
    NotifierProvider<IntakeRecordNotifier, Map<String, IntakeRecord>>(() {
      return IntakeRecordNotifier();
    });
