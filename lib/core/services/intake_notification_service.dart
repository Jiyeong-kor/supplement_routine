import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_timezone/flutter_timezone.dart';
import 'package:supplement_routine/core/services/intake_notification_copy.dart';
import 'package:supplement_routine/features/today/today_provider.dart';
import 'package:timezone/data/latest_all.dart' as tz_data;
import 'package:timezone/timezone.dart' as tz;

class IntakeNotificationService {
  IntakeNotificationService._();

  static final FlutterLocalNotificationsPlugin _notifications =
      FlutterLocalNotificationsPlugin();

  static var _isInitialized = false;
  static var _copy = IntakeNotificationCopy.ko();

  static const _channelId = 'intake_reminders';

  static Future<void> initialize({IntakeNotificationCopy? copy}) async {
    configureCopy(copy ?? IntakeNotificationCopy.ko());

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

  static void configureCopy(IntakeNotificationCopy copy) {
    _copy = copy;
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
          title: _copy.notificationTitle,
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
    return _copy.reminderBody(supplementName);
  }

  static NotificationDetails get _notificationDetails {
    return NotificationDetails(
      android: AndroidNotificationDetails(
        _channelId,
        _copy.channelName,
        channelDescription: _copy.channelDescription,
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
