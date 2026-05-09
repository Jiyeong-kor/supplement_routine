import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/features/history/data/intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/mock_intake_records.dart';

class MockIntakeRecordRepository implements IntakeRecordRepository {
  MockIntakeRecordRepository({Map<String, IntakeRecord>? initialRecords})
    : _records = {...(initialRecords ?? createMockIntakeRecords())};

  Map<String, IntakeRecord> _records;

  @override
  Map<String, IntakeRecord> getRecords() {
    return Map.unmodifiable(_records);
  }

  @override
  Map<String, IntakeRecord> upsertRecord(IntakeRecord record) {
    _records = {..._records, record.id: record};
    return getRecords();
  }

  @override
  Map<String, IntakeRecord> clearRecordsForSupplement(String supplementId) {
    _records = {
      for (final entry in _records.entries)
        if (entry.value.supplementId != supplementId) entry.key: entry.value,
    };
    return getRecords();
  }

  @override
  Map<String, IntakeRecord> clearAll() {
    _records = {};
    return getRecords();
  }
}
