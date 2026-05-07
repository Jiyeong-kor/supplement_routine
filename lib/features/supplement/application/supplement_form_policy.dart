import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';

enum SupplementFormValidationError {
  emptyName,
  invalidDosage,
  emptyRoutineSlots,
}

class SupplementFormPolicy {
  static const defaultDosageText = '1';
  static const defaultUnit = '개';
  static const dosageUnits = ['개', 'ml'];

  static const defaultCount = 1;
  static const minCount = 1;
  static const maxCount = 10;
  static const minIntervalHours = 1;
  static const maxIntervalHours = 24;
  static const defaultIntervalHours = 8;
  static const defaultTime = TimeOfDay(hour: 8, minute: 0);

  static const routineSlots = [
    IntakeSlot(condition: IntakeCondition.fasting),
    IntakeSlot(
      mealType: MealType.breakfast,
      condition: IntakeCondition.beforeMeal,
    ),
    IntakeSlot(
      mealType: MealType.breakfast,
      condition: IntakeCondition.afterMeal,
    ),
    IntakeSlot(
      mealType: MealType.breakfast,
      condition: IntakeCondition.betweenMeals,
    ),
    IntakeSlot(mealType: MealType.lunch, condition: IntakeCondition.beforeMeal),
    IntakeSlot(mealType: MealType.lunch, condition: IntakeCondition.afterMeal),
    IntakeSlot(
      mealType: MealType.lunch,
      condition: IntakeCondition.betweenMeals,
    ),
    IntakeSlot(
      mealType: MealType.dinner,
      condition: IntakeCondition.beforeMeal,
    ),
    IntakeSlot(mealType: MealType.dinner, condition: IntakeCondition.afterMeal),
    IntakeSlot(condition: IntakeCondition.beforeSleep),
  ];

  static SupplementFormValidationError? validateName(String name) {
    if (name.trim().isEmpty) {
      return SupplementFormValidationError.emptyName;
    }

    return null;
  }

  static double? parseDosage(String value) {
    final dosageValue = double.tryParse(value.trim());
    if (dosageValue == null || dosageValue <= 0) {
      return null;
    }

    return dosageValue;
  }

  static SupplementFormValidationError? validateRoutineSlots(
    Set<IntakeSlot> slots,
  ) {
    if (slots.isEmpty) {
      return SupplementFormValidationError.emptyRoutineSlots;
    }

    return null;
  }

  static String formatDosage(double value) {
    if (value == value.toInt()) {
      return value.toInt().toString();
    }

    return value.toString();
  }
}
