import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_record.dart';
import 'package:supplement_routine/core/services/scheduling_service.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplements.dart';

Map<String, IntakeRecord> createMockIntakeRecords({DateTime? baseDate}) {
  final today = baseDate ?? DateTime.now();
  final records = <String, IntakeRecord>{};

  for (var dayOffset = 1; dayOffset <= 14; dayOffset++) {
    final date = DateTime(today.year, today.month, today.day - dayOffset);
    final scheduledRecords = SchedulingService.createDailyIntakeRecords(
      supplements: mockSupplements,
      date: date,
    );
    final doneCount = _mockDoneCount(
      totalCount: scheduledRecords.length,
      dayOffset: dayOffset,
    );

    for (var index = 0; index < scheduledRecords.length; index++) {
      final record = scheduledRecords[index].record;
      final isDone = index < doneCount;
      records[record.id] = record.copyWith(
        isDone: isDone,
        takenAt: isDone ? _takenAt(date, record.scheduledTime) : null,
      );
    }
  }

  return records;
}

int _mockDoneCount({required int totalCount, required int dayOffset}) {
  if (totalCount == 0) {
    return 0;
  }

  return switch (dayOffset % 5) {
    0 => totalCount,
    1 => (totalCount * 0.8).round(),
    2 => (totalCount * 0.6).round(),
    3 => (totalCount * 0.4).round(),
    _ => 0,
  };
}

DateTime _takenAt(DateTime date, TimeOfDay scheduledTime) {
  return DateTime(
    date.year,
    date.month,
    date.day,
    scheduledTime.hour,
    scheduledTime.minute,
  );
}
