import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/history/data/mock_intake_record_repository.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/settings/data/memory_settings_repository.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplement_repository.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

void main() {
  Supplement mealSupplement({String id = 'vitamin_d'}) {
    return Supplement(
      id: id,
      name: '비타민 D',
      dailyCount: 1,
      method: IntakeMethod.mealBased,
      dosageUnit: '개',
      dosageValue: 1,
      selectedSlots: const [
        IntakeSlot(
          mealType: MealType.breakfast,
          condition: IntakeCondition.afterMeal,
        ),
      ],
      isNotificationEnabled: true,
    );
  }

  ProviderContainer createContainer({
    List<Supplement> supplements = const [],
    Map<String, IntakeRecord> records = const {},
  }) {
    return ProviderContainer(
      overrides: [
        supplementRepositoryProvider.overrideWithValue(
          MockSupplementRepository(initialSupplements: supplements),
        ),
        intakeRecordRepositoryProvider.overrideWithValue(
          MockIntakeRecordRepository(initialRecords: records),
        ),
        settingsRepositoryProvider.overrideWithValue(
          MemorySettingsRepository(),
        ),
      ],
    );
  }

  test('오늘 목록은 저장된 복용 기록 상태를 복원한다', () {
    final tempContainer = createContainer(supplements: [mealSupplement()]);
    addTearDown(tempContainer.dispose);
    final scheduledRecord = tempContainer.read(todayListProvider).single.record;

    final container = createContainer(
      supplements: [mealSupplement()],
      records: {scheduledRecord.id: scheduledRecord.markDone()},
    );
    addTearDown(container.dispose);

    expect(container.read(todayListProvider).single.record.isDone, isTrue);
    expect(container.read(todayListProvider).single.record.takenAt, isNotNull);
  });

  test('없는 기록 ID 토글은 상태를 바꾸지 않는다', () {
    final container = createContainer(supplements: [mealSupplement()]);
    addTearDown(container.dispose);
    final before = container.read(intakeRecordProvider);

    container.read(todayListProvider.notifier).toggleRecord('missing');

    expect(container.read(intakeRecordProvider), before);
  });

  test('영양제 삭제 시 해당 영양제 기록도 함께 삭제된다', () {
    final tempContainer = createContainer(supplements: [mealSupplement()]);
    addTearDown(tempContainer.dispose);
    final scheduledRecord = tempContainer.read(todayListProvider).single.record;

    final container = createContainer(
      supplements: [mealSupplement()],
      records: {scheduledRecord.id: scheduledRecord.markDone()},
    );
    addTearDown(container.dispose);

    container
        .read(supplementListProvider.notifier)
        .removeSupplement('vitamin_d');

    expect(container.read(supplementListProvider), isEmpty);
    expect(container.read(intakeRecordProvider), isEmpty);
  });

  test('식사 시간과 기본 알림 설정 provider는 저장소 상태를 갱신한다', () {
    final container = createContainer();
    addTearDown(container.dispose);

    container
        .read(mealTimeSettingsProvider.notifier)
        .updateBreakfastTime(const TimeOfDay(hour: 7, minute: 15));
    container.read(notificationSettingsProvider.notifier).updateEnabled(false);

    expect(
      container.read(mealTimeSettingsProvider).breakfastTime,
      const TimeOfDay(hour: 7, minute: 15),
    );
    expect(container.read(notificationSettingsProvider), isFalse);
  });
}
