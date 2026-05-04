import 'package:flutter/material.dart';
import 'intake_method.dart';
import 'intake_condition.dart';
import 'meal_type.dart';

class Supplement {
  final String id;
  final String name;
  final int dailyCount; // 1, 2, 3
  final IntakeMethod method;
  final IntakeCondition condition;
  
  // fixedTime 방식용
  final TimeOfDay? fixedTime;
  
  // mealBased 방식용
  final List<MealType>? selectedMeals;
  
  // interval 방식용
  final TimeOfDay? startTime;
  final int? intervalHours;

  final bool isNotificationEnabled;
  final String? memo;

  const Supplement({
    required this.id,
    required this.name,
    required this.dailyCount,
    required this.method,
    required this.condition,
    this.fixedTime,
    this.selectedMeals,
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
    IntakeCondition? condition,
    TimeOfDay? fixedTime,
    List<MealType>? selectedMeals,
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
      condition: condition ?? this.condition,
      fixedTime: fixedTime ?? this.fixedTime,
      selectedMeals: selectedMeals ?? this.selectedMeals,
      startTime: startTime ?? this.startTime,
      intervalHours: intervalHours ?? this.intervalHours,
      isNotificationEnabled: isNotificationEnabled ?? this.isNotificationEnabled,
      memo: memo ?? this.memo,
    );
  }
}
