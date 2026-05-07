import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/services/intake_notification_service.dart';

void main() {
  test('알림 문구는 복용할 영양제 이름을 포함한다', () {
    expect(IntakeNotificationService.reminderBody('오메가3'), '오메가3 복용할 시간이에요.');
  });
}
