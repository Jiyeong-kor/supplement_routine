import 'dart:async';
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/features/history/data/intake_record_repository.dart';

class LocalIntakeRecordRepository implements IntakeRecordRepository {
  LocalIntakeRecordRepository(
    this._preferences, {
    Map<String, IntakeRecord> seedRecords = const {},
  }) : _seedRecords = seedRecords {
    _records = _loadRecords();
  }

  static const storageKey = 'intake_records';

  final SharedPreferencesWithCache _preferences;
  final Map<String, IntakeRecord> _seedRecords;
  late Map<String, IntakeRecord> _records;

  @override
  Map<String, IntakeRecord> getRecords() {
    return Map.unmodifiable(_records);
  }

  @override
  Map<String, IntakeRecord> upsertRecord(IntakeRecord record) {
    _records = {..._records, record.id: record};
    _save();
    return getRecords();
  }

  @override
  Map<String, IntakeRecord> clearRecordsForSupplement(String supplementId) {
    _records = {
      for (final entry in _records.entries)
        if (entry.value.supplementId != supplementId) entry.key: entry.value,
    };
    _save();
    return getRecords();
  }

  @override
  Map<String, IntakeRecord> clearAll() {
    _records = {};
    _save();
    return getRecords();
  }

  Map<String, IntakeRecord> _loadRecords() {
    final rawJson = _preferences.getString(storageKey);

    if (rawJson == null) {
      return {..._seedRecords};
    }

    final values = jsonDecode(rawJson) as List;
    final records = values.map(
      (value) => IntakeRecord.fromJson(Map<String, Object?>.from(value as Map)),
    );

    return {for (final record in records) record.id: record};
  }

  void _save() {
    final encoded = jsonEncode(
      _records.values.map((record) => record.toJson()).toList(),
    );

    unawaited(_preferences.setString(storageKey, encoded));
  }
}
