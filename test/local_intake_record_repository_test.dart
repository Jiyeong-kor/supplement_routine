import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:shared_preferences_platform_interface/in_memory_shared_preferences_async.dart';
import 'package:shared_preferences_platform_interface/shared_preferences_async_platform_interface.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/features/history/data/local_intake_record_repository.dart';
import 'package:supplement_routine/features/history/data/mock_intake_records.dart';

void main() {
  IntakeRecord createRecord({String id = 'record_1'}) {
    return IntakeRecord(
      id: id,
      supplementId: 'supplement_1',
      date: DateTime(2026, 5, 9),
      scheduledTime: const TimeOfDay(hour: 8, minute: 30),
      isDone: true,
      takenAt: DateTime(2026, 5, 9, 8, 35),
    );
  }

  test('저장된 복용 기록을 로컬 저장소에서 불러온다', () async {
    final record = createRecord();
    final store = InMemorySharedPreferencesAsync.empty();
    SharedPreferencesAsyncPlatform.instance = store;
    await store.setString(
      LocalIntakeRecordRepository.storageKey,
      jsonEncode([record.toJson()]),
      const SharedPreferencesOptions(),
    );
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: {LocalIntakeRecordRepository.storageKey},
      ),
    );

    final repository = LocalIntakeRecordRepository(preferences);

    expect(repository.getRecords()[record.id]?.isDone, isTrue);
    expect(repository.getRecords()[record.id]?.takenAt, record.takenAt);
  });

  test('저장된 값이 없으면 기본적으로 빈 기록을 반환한다', () async {
    SharedPreferencesAsyncPlatform.instance =
        InMemorySharedPreferencesAsync.empty();
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: {LocalIntakeRecordRepository.storageKey},
      ),
    );

    final repository = LocalIntakeRecordRepository(preferences);

    expect(repository.getRecords(), isEmpty);
  });

  test('개발용 seed 복용 기록을 명시적으로 주입할 수 있다', () async {
    final seedRecords = createMockIntakeRecords();
    SharedPreferencesAsyncPlatform.instance =
        InMemorySharedPreferencesAsync.empty();
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: {LocalIntakeRecordRepository.storageKey},
      ),
    );

    final repository = LocalIntakeRecordRepository(
      preferences,
      seedRecords: seedRecords,
    );

    expect(repository.getRecords(), hasLength(seedRecords.length));
  });

  test('복용 기록 변경 사항을 로컬 저장소에 저장한다', () async {
    SharedPreferencesAsyncPlatform.instance =
        InMemorySharedPreferencesAsync.empty();
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: {LocalIntakeRecordRepository.storageKey},
      ),
    );
    final repository = LocalIntakeRecordRepository(preferences);
    final record = createRecord();

    repository.clearAll();
    repository.upsertRecord(record);

    final rawJson = preferences.getString(
      LocalIntakeRecordRepository.storageKey,
    );
    final values = jsonDecode(rawJson!) as List;

    expect(values, hasLength(1));
    expect(values.first['id'], record.id);
  });
}
