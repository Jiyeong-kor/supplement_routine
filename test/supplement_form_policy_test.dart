import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/application/supplement_form_mapper.dart';
import 'package:supplement_routine/features/supplement/application/supplement_form_policy.dart';

void main() {
  test('영양제 이름은 공백일 수 없다', () {
    expect(
      SupplementFormPolicy.validateName('  '),
      SupplementFormValidationError.emptyName,
    );
    expect(SupplementFormPolicy.validateName('비타민 D'), isNull);
  });

  test('복용량은 0보다 큰 숫자만 허용한다', () {
    expect(SupplementFormPolicy.parseDosage('0'), isNull);
    expect(SupplementFormPolicy.parseDosage('-1'), isNull);
    expect(SupplementFormPolicy.parseDosage('abc'), isNull);
    expect(SupplementFormPolicy.parseDosage('1.5'), 1.5);
  });

  test('식사 기준 복용은 하나 이상의 시간대가 필요하다', () {
    expect(
      SupplementFormPolicy.validateRoutineSlots({}),
      SupplementFormValidationError.emptyRoutineSlots,
    );
    expect(
      SupplementFormPolicy.validateRoutineSlots({
        const IntakeSlot(condition: IntakeCondition.fasting),
      }),
      isNull,
    );
  });

  test('식사 기준 입력값으로 Supplement를 생성한다', () {
    final supplement = SupplementFormMapper.toSupplement(
      input: SupplementFormInput(
        name: '비타민 D',
        dosageUnit: '개',
        dosageValue: 1,
        isRoutineBased: true,
        isSpecificTime: true,
        selectedSlots: {const IntakeSlot(condition: IntakeCondition.fasting)},
        fixedCount: 1,
        fixedTimes: const [TimeOfDay(hour: 8, minute: 0)],
        startTime: const TimeOfDay(hour: 8, minute: 0),
        intervalHours: 8,
        intervalCount: 1,
        isNotificationEnabled: true,
        memo: null,
      ),
    );

    expect(supplement.name, '비타민 D');
    expect(supplement.method, IntakeMethod.mealBased);
    expect(supplement.dailyCount, 1);
    expect(supplement.selectedSlots, isNotNull);
    expect(supplement.fixedTimes, isNull);
  });

  test('수정 모드에서는 기존 Supplement ID를 유지한다', () {
    const initialSupplement = Supplement(
      id: 'existing_id',
      name: '수정 전',
      dailyCount: 1,
      method: IntakeMethod.fixedTime,
      dosageUnit: '개',
      dosageValue: 1,
      fixedTimes: [TimeOfDay(hour: 8, minute: 0)],
      isNotificationEnabled: true,
    );

    final supplement = SupplementFormMapper.toSupplement(
      initialSupplement: initialSupplement,
      input: SupplementFormInput(
        name: '수정 후',
        dosageUnit: '개',
        dosageValue: 2,
        isRoutineBased: false,
        isSpecificTime: true,
        selectedSlots: {},
        fixedCount: 2,
        fixedTimes: const [
          TimeOfDay(hour: 8, minute: 0),
          TimeOfDay(hour: 21, minute: 0),
        ],
        startTime: const TimeOfDay(hour: 8, minute: 0),
        intervalHours: 8,
        intervalCount: 1,
        isNotificationEnabled: false,
        memo: '저녁에도 확인',
      ),
    );

    expect(supplement.id, 'existing_id');
    expect(supplement.name, '수정 후');
    expect(supplement.method, IntakeMethod.fixedTime);
    expect(supplement.dailyCount, 2);
    expect(supplement.fixedTimes, hasLength(2));
  });
}
