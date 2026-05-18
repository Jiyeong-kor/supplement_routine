import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/utils/time_utils.dart';

void main() {
  test('시간 더하기는 자정을 넘어가면 다음 날 시각으로 순환한다', () {
    expect(
      const TimeOfDay(hour: 23, minute: 45).addMinutes(30),
      const TimeOfDay(hour: 0, minute: 15),
    );
  });

  test('시간 더하기는 음수 분도 처리한다', () {
    expect(
      const TimeOfDay(hour: 8, minute: 0).addMinutes(-30),
      const TimeOfDay(hour: 7, minute: 30),
    );
  });

  test('시간 더하기는 자정 이전으로도 순환한다', () {
    expect(
      const TimeOfDay(hour: 0, minute: 15).addMinutes(-30),
      const TimeOfDay(hour: 23, minute: 45),
    );
  });

  test('24시간 문자열은 두 자리로 포맷한다', () {
    expect(const TimeOfDay(hour: 7, minute: 5).to24hString(), '07:05');
  });
}
