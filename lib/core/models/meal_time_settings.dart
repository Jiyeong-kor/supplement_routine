import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/time_of_day_json.dart';

class MealTimeSettings {
  final TimeOfDay breakfastTime;
  final TimeOfDay lunchTime;
  final TimeOfDay dinnerTime;

  const MealTimeSettings({
    this.breakfastTime = const TimeOfDay(hour: 8, minute: 0),
    this.lunchTime = const TimeOfDay(hour: 12, minute: 0),
    this.dinnerTime = const TimeOfDay(hour: 18, minute: 0),
  });

  factory MealTimeSettings.fromJson(Map<String, Object?> json) {
    return MealTimeSettings(
      breakfastTime: TimeOfDayJson.fromJson(
        Map<String, Object?>.from(json['breakfastTime'] as Map),
      ),
      lunchTime: TimeOfDayJson.fromJson(
        Map<String, Object?>.from(json['lunchTime'] as Map),
      ),
      dinnerTime: TimeOfDayJson.fromJson(
        Map<String, Object?>.from(json['dinnerTime'] as Map),
      ),
    );
  }

  Map<String, Object?> toJson() {
    return {
      'breakfastTime': TimeOfDayJson.toJson(breakfastTime),
      'lunchTime': TimeOfDayJson.toJson(lunchTime),
      'dinnerTime': TimeOfDayJson.toJson(dinnerTime),
    };
  }

  MealTimeSettings copyWith({
    TimeOfDay? breakfastTime,
    TimeOfDay? lunchTime,
    TimeOfDay? dinnerTime,
  }) {
    return MealTimeSettings(
      breakfastTime: breakfastTime ?? this.breakfastTime,
      lunchTime: lunchTime ?? this.lunchTime,
      dinnerTime: dinnerTime ?? this.dinnerTime,
    );
  }
}
