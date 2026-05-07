import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/time_of_day_json.dart';

class IntakeSlot {
  final MealType? mealType;
  final IntakeCondition condition;

  const IntakeSlot({this.mealType, required this.condition});

  factory IntakeSlot.fromJson(Map<String, Object?> json) {
    final mealTypeName = json['mealType'] as String?;

    return IntakeSlot(
      mealType: mealTypeName == null
          ? null
          : MealType.values.byName(mealTypeName),
      condition: IntakeCondition.values.byName(json['condition'] as String),
    );
  }

  Map<String, Object?> toJson() {
    return {'mealType': mealType?.name, 'condition': condition.name};
  }

  String get label {
    if (condition == IntakeCondition.beforeSleep) return '취침 전';
    if (condition == IntakeCondition.fasting) return '기상 직후(공복)';

    if (condition == IntakeCondition.betweenMeals) {
      if (mealType == MealType.breakfast) return '아침-점심 사이';
      if (mealType == MealType.lunch) return '점심-저녁 사이';
      return '식간(식사 사이)';
    }

    final mealLabel = mealType == MealType.breakfast
        ? '아침'
        : (mealType == MealType.lunch ? '점심' : '저녁');
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

  factory Supplement.fromJson(Map<String, Object?> json) {
    return Supplement(
      id: json['id'] as String,
      name: json['name'] as String,
      dailyCount: json['dailyCount'] as int,
      method: IntakeMethod.values.byName(json['method'] as String),
      generalCondition: IntakeCondition.values.byName(
        json['generalCondition'] as String? ?? IntakeCondition.none.name,
      ),
      dosageUnit: json['dosageUnit'] as String,
      dosageValue: (json['dosageValue'] as num).toDouble(),
      selectedSlots: (json['selectedSlots'] as List?)
          ?.map(
            (slot) =>
                IntakeSlot.fromJson(Map<String, Object?>.from(slot as Map)),
          )
          .toList(),
      fixedTimes: (json['fixedTimes'] as List?)
          ?.map(
            (time) =>
                TimeOfDayJson.fromJson(Map<String, Object?>.from(time as Map)),
          )
          .toList(),
      startTime: json['startTime'] == null
          ? null
          : TimeOfDayJson.fromJson(
              Map<String, Object?>.from(json['startTime'] as Map),
            ),
      intervalHours: json['intervalHours'] as int?,
      isNotificationEnabled: json['isNotificationEnabled'] as bool,
      memo: json['memo'] as String?,
    );
  }

  Map<String, Object?> toJson() {
    return {
      'id': id,
      'name': name,
      'dailyCount': dailyCount,
      'method': method.name,
      'generalCondition': generalCondition.name,
      'dosageUnit': dosageUnit,
      'dosageValue': dosageValue,
      'selectedSlots': selectedSlots?.map((slot) => slot.toJson()).toList(),
      'fixedTimes': fixedTimes?.map(TimeOfDayJson.toJson).toList(),
      'startTime': startTime == null ? null : TimeOfDayJson.toJson(startTime!),
      'intervalHours': intervalHours,
      'isNotificationEnabled': isNotificationEnabled,
      'memo': memo,
    };
  }

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
      isNotificationEnabled:
          isNotificationEnabled ?? this.isNotificationEnabled,
      memo: memo ?? this.memo,
    );
  }
}
