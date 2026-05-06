import 'package:flutter/material.dart';

extension TimeOfDayExtension on TimeOfDay {
  TimeOfDay addMinutes(int minutes) {
    final totalMinutes = hour * 60 + minute + minutes;
    return TimeOfDay(
      hour: (totalMinutes ~/ 60) % 24,
      minute: totalMinutes % 60,
    );
  }

  String to24hString() {
    final hourStr = hour.toString().padLeft(2, '0');
    final minuteStr = minute.toString().padLeft(2, '0');
    return '$hourStr:$minuteStr';
  }
}
