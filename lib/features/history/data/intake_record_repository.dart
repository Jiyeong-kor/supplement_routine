import 'package:supplement_routine/core/models/intake_record.dart';

abstract interface class IntakeRecordRepository {
  Map<String, IntakeRecord> getRecords();

  Map<String, IntakeRecord> upsertRecord(IntakeRecord record);

  Map<String, IntakeRecord> clearRecordsForSupplement(String supplementId);

  Map<String, IntakeRecord> clearAll();
}
