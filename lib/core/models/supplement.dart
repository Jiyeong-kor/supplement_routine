import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/meal_type.dart';

class IntakeSlot {
  final MealType? mealType;
  final IntakeCondition condition;

  const IntakeSlot({this.mealType, required this.condition});

  String get label {
    if (condition == IntakeCondition.beforeSleep) return '취침 전';
    if (condition == IntakeCondition.fasting) return '기상 직후(공복)';
    
    if (condition == IntakeCondition.betweenMeals) {
      if (mealType == MealType.breakfast) return '아침-점심 사이';
      if (mealType == MealType.lunch) return '점심-저녁 사이';
      return '식간(식사 사이)';
    }
    
    final mealLabel = mealType == MealType.breakfast ? '아침' : (mealType == MealType.lunch ? '점심' : '저녁');
    return '$mealLabel ${condition == IntakeCondition.beforeMeal ? "식전" : "식후"}';
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is IntakeSlot &&
          runtimeType == other.runtimeType &&
          mealType == other.mealType &&
          condition == other.condition;

  @override
  int get hashCode => mealType.hashCode ^ condition.hashCode;
}

class Supplement {
  final String id;
  final String name;
  final int dailyCount;
  final IntakeMethod method;
  final IntakeCondition generalCondition;
  final String dosageUnit;
  final double dosageValue;
  final List<IntakeSlot>? selectedSlots;
  final List<TimeOfDay>? fixedTimes;
  final TimeOfDay? startTime;
  final int? intervalHours;
  final bool isNotificationEnabled;
  final String? memo;

  const Supplement({
    required this.id,
    required this.name,
    required this.dailyCount,
    required this.method,
    this.generalCondition = IntakeCondition.none,
    required this.dosageUnit,
    required this.dosageValue,
    this.selectedSlots,
    this.fixedTimes,
    this.startTime,
    this.intervalHours,
    required this.isNotificationEnabled,
    this.memo,
  });

  Supplement copyWith({
    String? id,
    String? name,
    int? dailyCount,
    IntakeMethod? method,
    IntakeCondition? generalCondition,
    String? dosageUnit,
    double? dosageValue,
    List<IntakeSlot>? selectedSlots,
    List<TimeOfDay>? fixedTimes,
    TimeOfDay? startTime,
    int? intervalHours,
    bool? isNotificationEnabled,
    String? memo,
  }) {
    return Supplement(
      id: id ?? this.id,
      name: name ?? this.name,
      dailyCount: dailyCount ?? this.dailyCount,
      method: method ?? this.method,
      generalCondition: generalCondition ?? this.generalCondition,
      dosageUnit: dosageUnit ?? this.dosageUnit,
      dosageValue: dosageValue ?? this.dosageValue,
      selectedSlots: selectedSlots ?? this.selectedSlots,
      fixedTimes: fixedTimes ?? this.fixedTimes,
      startTime: startTime ?? this.startTime,
      intervalHours: intervalHours ?? this.intervalHours,
      isNotificationEnabled: isNotificationEnabled ?? this.isNotificationEnabled,
      memo: memo ?? this.memo,
    );
  }
}
