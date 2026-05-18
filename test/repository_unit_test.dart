import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/history/data/mock_intake_record_repository.dart';
import 'package:supplement_routine/features/settings/data/memory_settings_repository.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplement_repository.dart';

void main() {
  test('메모리 설정 저장소는 세 식사 시간을 각각 갱신한다', () {
    final repository = MemorySettingsRepository();

    repository.updateBreakfastTime(const TimeOfDay(hour: 7, minute: 0));
    repository.updateLunchTime(const TimeOfDay(hour: 12, minute: 30));
    repository.updateDinnerTime(const TimeOfDay(hour: 19, minute: 0));

    expect(
      repository.getMealTimeSettings().breakfastTime,
      const TimeOfDay(hour: 7, minute: 0),
    );
    expect(
      repository.getMealTimeSettings().lunchTime,
      const TimeOfDay(hour: 12, minute: 30),
    );
    expect(
      repository.getMealTimeSettings().dinnerTime,
      const TimeOfDay(hour: 19, minute: 0),
    );
  });

  test('메모리 설정 저장소는 알림 활성화 상태를 갱신한다', () {
    final repository = MemorySettingsRepository();

    expect(repository.getNotificationEnabled(), isTrue);
    expect(repository.updateNotificationEnabled(false), isFalse);
    expect(repository.getNotificationEnabled(), isFalse);
  });

  test('mock 기록 저장소는 upsert와 개별 영양제 삭제를 처리한다', () {
    final repository = MockIntakeRecordRepository(initialRecords: const {});
    final vitaminRecord = IntakeRecord(
      id: 'record_vitamin',
      supplementId: 'vitamin',
      date: DateTime(2026, 5, 18),
      scheduledTime: const TimeOfDay(hour: 8, minute: 0),
      isDone: false,
    );
    final omegaRecord = IntakeRecord(
      id: 'record_omega',
      supplementId: 'omega',
      date: DateTime(2026, 5, 18),
      scheduledTime: const TimeOfDay(hour: 21, minute: 0),
      isDone: false,
    );

    repository.upsertRecord(vitaminRecord);
    repository.upsertRecord(omegaRecord.markDone());
    final remaining = repository.clearRecordsForSupplement('vitamin');

    expect(remaining, hasLength(1));
    expect(remaining.values.single.id, 'record_omega');
    expect(remaining.values.single.isDone, isTrue);
  });

  test('mock 기록 저장소는 전체 삭제를 처리한다', () {
    final repository = MockIntakeRecordRepository(
      initialRecords: {
        'record': IntakeRecord(
          id: 'record',
          supplementId: 'vitamin',
          date: DateTime(2026, 5, 18),
          scheduledTime: const TimeOfDay(hour: 8, minute: 0),
          isDone: false,
        ),
      },
    );

    expect(repository.clearAll(), isEmpty);
  });

  test('mock 영양제 저장소는 없는 ID 수정 시 기존 목록을 유지한다', () {
    final repository = MockSupplementRepository(
      initialSupplements: const [
        Supplement(
          id: 'vitamin',
          name: '비타민 D',
          dailyCount: 1,
          method: IntakeMethod.fixedTime,
          dosageUnit: '개',
          dosageValue: 1,
          isNotificationEnabled: true,
        ),
      ],
    );

    final result = repository.updateSupplement(
      const Supplement(
        id: 'missing',
        name: '없는 영양제',
        dailyCount: 1,
        method: IntakeMethod.fixedTime,
        dosageUnit: '개',
        dosageValue: 1,
        isNotificationEnabled: true,
      ),
    );

    expect(result, hasLength(1));
    expect(result.single.id, 'vitamin');
  });
}
