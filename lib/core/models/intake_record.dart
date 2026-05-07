import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/time_of_day_json.dart';

class IntakeRecord {
  final String id;
  final String supplementId;
  final DateTime date; // 예정 날짜
  final TimeOfDay scheduledTime; // 예정 시간
  final bool isDone;
  final DateTime? takenAt; // 실제 복용 완료 일시

  const IntakeRecord({
    required this.id,
    required this.supplementId,
    required this.date,
    required this.scheduledTime,
    required this.isDone,
    this.takenAt,
  });

  factory IntakeRecord.fromJson(Map<String, Object?> json) {
    return IntakeRecord(
      id: json['id'] as String,
      supplementId: json['supplementId'] as String,
      date: DateTime.parse(json['date'] as String),
      scheduledTime: TimeOfDayJson.fromJson(
        Map<String, Object?>.from(json['scheduledTime'] as Map),
      ),
      isDone: json['isDone'] as bool,
      takenAt: json['takenAt'] == null
          ? null
          : DateTime.parse(json['takenAt'] as String),
    );
  }

  Map<String, Object?> toJson() {
    return {
      'id': id,
      'supplementId': supplementId,
      'date': date.toIso8601String(),
      'scheduledTime': TimeOfDayJson.toJson(scheduledTime),
      'isDone': isDone,
      'takenAt': takenAt?.toIso8601String(),
    };
  }

  IntakeRecord copyWith({
    String? id,
    String? supplementId,
    DateTime? date,
    TimeOfDay? scheduledTime,
    bool? isDone,
    DateTime? takenAt,
  }) {
    return IntakeRecord(
      id: id ?? this.id,
      supplementId: supplementId ?? this.supplementId,
      date: date ?? this.date,
      scheduledTime: scheduledTime ?? this.scheduledTime,
      isDone: isDone ?? this.isDone,
      takenAt: takenAt ?? this.takenAt,
    );
  }

  IntakeRecord markDone() {
    return copyWith(isDone: true, takenAt: DateTime.now());
  }

  IntakeRecord markUndone() {
    return IntakeRecord(
      id: id,
      supplementId: supplementId,
      date: date,
      scheduledTime: scheduledTime,
      isDone: false,
      takenAt: null,
    );
  }
}
