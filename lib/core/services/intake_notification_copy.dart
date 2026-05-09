class IntakeNotificationCopy {
  const IntakeNotificationCopy({
    required this.channelName,
    required this.channelDescription,
    required this.notificationTitle,
    required this.reminderBodyBuilder,
  });

  factory IntakeNotificationCopy.ko() {
    return IntakeNotificationCopy(
      channelName: '복용 일정 알림',
      channelDescription: '사용자가 입력한 복용 일정에 맞춰 체크 알림을 보냅니다.',
      notificationTitle: '복용 일정 확인',
      reminderBodyBuilder: (supplementName) => '$supplementName 복용할 시간이에요.',
    );
  }

  final String channelName;
  final String channelDescription;
  final String notificationTitle;
  final String Function(String supplementName) reminderBodyBuilder;

  String reminderBody(String supplementName) {
    return reminderBodyBuilder(supplementName);
  }
}
