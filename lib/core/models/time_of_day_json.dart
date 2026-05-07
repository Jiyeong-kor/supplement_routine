import 'package:flutter/material.dart';

class TimeOfDayJson {
  const TimeOfDayJson._();

  static Map<String, int> toJson(TimeOfDay time) {
    return {'hour': time.hour, 'minute': time.minute};
  }

  static TimeOfDay fromJson(Map<String, Object?> json) {
    return TimeOfDay(hour: json['hour'] as int, minute: json['minute'] as int);
  }
}
