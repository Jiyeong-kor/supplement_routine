import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/supplement.dart';
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
}
