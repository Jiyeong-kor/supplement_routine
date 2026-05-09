import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/services/intake_notification_copy.dart';
import 'package:supplement_routine/core/services/intake_notification_service.dart';

void main() {
  test('알림 문구는 복용할 영양제 이름을 포함한다', () {
    expect(IntakeNotificationService.reminderBody('오메가3'), '오메가3 복용할 시간이에요.');
  });

  test('알림 문구 copy를 교체할 수 있다', () {
    addTearDown(
      () =>
          IntakeNotificationService.configureCopy(IntakeNotificationCopy.ko()),
    );
    IntakeNotificationService.configureCopy(
      IntakeNotificationCopy(
        channelName: '테스트 채널',
        channelDescription: '테스트 설명',
        notificationTitle: '테스트 제목',
        reminderBodyBuilder: (name) => '$name 테스트 알림',
      ),
    );

    expect(IntakeNotificationService.reminderBody('비타민 D'), '비타민 D 테스트 알림');
  });
}
