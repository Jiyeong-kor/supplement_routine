import 'package:flutter/material.dart';

class MealTimeSettings {
  final TimeOfDay breakfastTime;
  final TimeOfDay lunchTime;
  final TimeOfDay dinnerTime;

  const MealTimeSettings({
    this.breakfastTime = const TimeOfDay(hour: 8, minute: 0),
    this.lunchTime = const TimeOfDay(hour: 12, minute: 0),
    this.dinnerTime = const TimeOfDay(hour: 18, minute: 0),
  });

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
