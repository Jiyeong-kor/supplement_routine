import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/schedule_label.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class SupplementDisplayText {
  const SupplementDisplayText._();

  static String methodLabel(AppLocalizations l10n, IntakeMethod method) {
    return switch (method) {
      IntakeMethod.fixedTime => l10n.intakeMethodFixedTime,
      IntakeMethod.mealBased => l10n.intakeMethodMealBased,
      IntakeMethod.interval => l10n.intakeMethodInterval,
    };
  }

  static String conditionLabel(
    AppLocalizations l10n,
    IntakeCondition condition,
  ) {
    return switch (condition) {
      IntakeCondition.beforeMeal => l10n.intakeConditionBeforeMeal,
      IntakeCondition.afterMeal => l10n.intakeConditionAfterMeal,
      IntakeCondition.betweenMeals => l10n.intakeConditionBetweenMeals,
      IntakeCondition.fasting => l10n.intakeConditionFasting,
      IntakeCondition.beforeSleep => l10n.intakeConditionBeforeSleep,
      IntakeCondition.none => l10n.intakeConditionNone,
    };
  }

  static String mealLabel(AppLocalizations l10n, MealType mealType) {
    return switch (mealType) {
      MealType.breakfast => l10n.mealBreakfast,
      MealType.lunch => l10n.mealLunch,
      MealType.dinner => l10n.mealDinner,
    };
  }

  static String slotLabel(AppLocalizations l10n, IntakeSlot slot) {
    if (slot.condition == IntakeCondition.beforeSleep) {
      return l10n.intakeSlotBeforeSleep;
    }

    if (slot.condition == IntakeCondition.fasting) {
      return l10n.intakeSlotFasting;
    }

    if (slot.condition == IntakeCondition.betweenMeals) {
      return switch (slot.mealType) {
        MealType.breakfast => l10n.intakeSlotBetweenBreakfastLunch,
        MealType.lunch => l10n.intakeSlotBetweenLunchDinner,
        _ => l10n.intakeSlotBetweenMeals,
      };
    }

    final mealType = slot.mealType ?? MealType.dinner;
    return l10n.intakeSlotMealCondition(
      mealLabel(l10n, mealType),
      conditionLabel(l10n, slot.condition),
    );
  }

  static String scheduleLabel(AppLocalizations l10n, ScheduleLabel label) {
    return switch (label.type) {
      ScheduleLabelType.fixedTime => l10n.intakeMethodFixedTime,
      ScheduleLabelType.interval => l10n.intakeMethodInterval,
      ScheduleLabelType.routineSlot => slotLabel(l10n, label.slot!),
    };
  }
}
