import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

class HomeWidgetSummary {
  const HomeWidgetSummary({
    required this.doneCount,
    required this.totalCount,
    required this.hasNext,
    required this.nextName,
    required this.nextHour,
    required this.nextMinute,
  });

  final int doneCount;
  final int totalCount;
  final bool hasNext;
  final String? nextName;
  final int? nextHour;
  final int? nextMinute;

  bool get hasSchedule => totalCount > 0;

  Map<String, Object?> toMap() {
    return {
      'doneCount': doneCount,
      'totalCount': totalCount,
      'hasNext': hasNext,
      'hasSchedule': hasSchedule,
      'nextName': nextName,
      'nextHour': nextHour,
      'nextMinute': nextMinute,
    };
  }

  factory HomeWidgetSummary.fromTodayList(List<TodayDisplayItem> todayList) {
    final doneCount = todayList.where((item) => item.record.isDone).length;
    final nextItem = todayList.where((item) => !item.record.isDone).firstOrNull;

    return HomeWidgetSummary(
      doneCount: doneCount,
      totalCount: todayList.length,
      hasNext: nextItem != null,
      nextName: nextItem?.supplement.name,
      nextHour: nextItem?.record.scheduledTime.hour,
      nextMinute: nextItem?.record.scheduledTime.minute,
    );
  }
}

class HomeWidgetService {
  const HomeWidgetService._();

  static const _channel = MethodChannel('supplement_routine/home_widget');

  static Future<void> updateTodaySummary(HomeWidgetSummary summary) async {
    if (kIsWeb) {
      return;
    }

    try {
      await _channel.invokeMethod<void>('updateTodaySummary', summary.toMap());
    } on MissingPluginException {
      return;
    } on PlatformException {
      return;
    }
  }
}
