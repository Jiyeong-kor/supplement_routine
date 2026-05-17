import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/schedule_label.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/presentation/supplement_display_text.dart';
import 'package:supplement_routine/l10n/generated/app_localizations_ko.dart';

void main() {
  final l10n = AppLocalizationsKo();

  test('복용 방식 라벨을 한글로 변환한다', () {
    expect(
      SupplementDisplayText.methodLabel(l10n, IntakeMethod.fixedTime),
      '정해진 시간',
    );
    expect(
      SupplementDisplayText.methodLabel(l10n, IntakeMethod.mealBased),
      '식사 기준',
    );
    expect(
      SupplementDisplayText.methodLabel(l10n, IntakeMethod.interval),
      '일정 간격',
    );
  });

  test('복용 슬롯 라벨을 조건별로 변환한다', () {
    expect(
      SupplementDisplayText.slotLabel(
        l10n,
        const IntakeSlot(condition: IntakeCondition.beforeSleep),
      ),
      '취침 전',
    );
    expect(
      SupplementDisplayText.slotLabel(
        l10n,
        const IntakeSlot(
          mealType: MealType.breakfast,
          condition: IntakeCondition.betweenMeals,
        ),
      ),
      '아침-점심 사이',
    );
    expect(
      SupplementDisplayText.slotLabel(
        l10n,
        const IntakeSlot(
          mealType: MealType.lunch,
          condition: IntakeCondition.afterMeal,
        ),
      ),
      '점심 식후',
    );
  });

  test('일정 라벨을 화면 문구로 변환한다', () {
    expect(
      SupplementDisplayText.scheduleLabel(
        l10n,
        const ScheduleLabel.fixedTime(),
      ),
      '정해진 시간',
    );
    expect(
      SupplementDisplayText.scheduleLabel(
        l10n,
        ScheduleLabel.routineSlot(
          const IntakeSlot(condition: IntakeCondition.fasting),
        ),
      ),
      '기상 직후(공복)',
    );
  });
}
