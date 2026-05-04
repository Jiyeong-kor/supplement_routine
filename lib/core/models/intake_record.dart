import 'package:flutter/material.dart';

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
}
