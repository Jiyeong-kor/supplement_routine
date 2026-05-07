import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_timezone/flutter_timezone.dart';
import 'package:supplement_routine/features/today/today_provider.dart';
import 'package:timezone/data/latest_all.dart' as tz_data;
import 'package:timezone/timezone.dart' as tz;

class IntakeNotificationService {
  IntakeNotificationService._();

  static final FlutterLocalNotificationsPlugin _notifications =
      FlutterLocalNotificationsPlugin();

  static var _isInitialized = false;

  static const _channelId = 'intake_reminders';
  static const _channelName = '복용 일정 알림';
  static const _channelDescription = '사용자가 입력한 복용 일정에 맞춰 체크 알림을 보냅니다.';
  static const _notificationTitle = '복용 일정 확인';

  static Future<void> initialize() async {
    if (kIsWeb) {
      return;
    }

    try {
      tz_data.initializeTimeZones();
      final timeZoneInfo = await FlutterTimezone.getLocalTimezone();
      tz.setLocalLocation(tz.getLocation(timeZoneInfo.identifier));

      const androidSettings = AndroidInitializationSettings(
        'ic_stat_notification',
      );
      const initializationSettings = InitializationSettings(
        android: androidSettings,
      );

      await _notifications.initialize(settings: initializationSettings);
      await _requestAndroidNotificationPermission();
      _isInitialized = true;
    } on MissingPluginException {
      return;
    } on PlatformException {
      return;
    }
  }

  static Future<void> syncTodayReminders(List<TodayDisplayItem> items) async {
    if (kIsWeb) {
      return;
    }

    if (!_isInitialized) {
      return;
    }

    try {
      await _notifications.cancelAll();

      final notificationItems = items.where(
        (item) => item.supplement.isNotificationEnabled && !item.record.isDone,
      );

      for (final item in notificationItems) {
        await _notifications.zonedSchedule(
          id: _notificationId(item.record.id),
          title: _notificationTitle,
          body: reminderBody(item.supplement.name),
          scheduledDate: _nextSchedule(item),
          notificationDetails: _notificationDetails,
          androidScheduleMode: AndroidScheduleMode.inexactAllowWhileIdle,
          matchDateTimeComponents: DateTimeComponents.time,
          payload: item.record.id,
        );
      }
    } on MissingPluginException {
      return;
    } on PlatformException {
      return;
    }
  }

  static Future<void> _requestAndroidNotificationPermission() async {
    final androidImplementation = _notifications
        .resolvePlatformSpecificImplementation<
          AndroidFlutterLocalNotificationsPlugin
        >();

    await androidImplementation?.requestNotificationsPermission();
  }

  static String reminderBody(String supplementName) {
    return '$supplementName 복용할 시간이에요.';
  }

  static NotificationDetails get _notificationDetails {
    return const NotificationDetails(
      android: AndroidNotificationDetails(
        _channelId,
        _channelName,
        channelDescription: _channelDescription,
        importance: Importance.high,
        priority: Priority.high,
      ),
    );
  }

  static tz.TZDateTime _nextSchedule(TodayDisplayItem item) {
    final now = tz.TZDateTime.now(tz.local);
    var scheduledDate = tz.TZDateTime(
      tz.local,
      now.year,
      now.month,
      now.day,
      item.record.scheduledTime.hour,
      item.record.scheduledTime.minute,
    );

    if (!scheduledDate.isAfter(now)) {
      scheduledDate = scheduledDate.add(const Duration(days: 1));
    }

    return scheduledDate;
  }

  static int _notificationId(String recordId) {
    return recordId.codeUnits.fold<int>(
      0,
      (value, codeUnit) => ((value * 31) + codeUnit) & 0x7fffffff,
    );
  }
}
