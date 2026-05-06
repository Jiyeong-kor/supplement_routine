import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/supplement_routine_app.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

void main() {
  testWidgets('앱 시작 화면에 오늘 탭이 표시된다', (WidgetTester tester) async {
    // ProviderScope로 감싸서 앱을 빌드합니다.
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    // 하단 내비게이션의 '오늘' 텍스트가 있는지 확인합니다.
    expect(find.text('오늘'), findsWidgets);

    // 초기 화면인 '오늘' 화면의 AppBar 타이틀과 빈 상태 안내가 있는지 확인합니다.
    expect(find.text('오늘의 복용'), findsOneWidget);
    expect(find.text('등록된 복용 일정이 없습니다'), findsOneWidget);
  });

  testWidgets('영양제 등록 시 복용량은 0보다 큰 숫자여야 한다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '비타민 D');
    await tester.enterText(find.byType(TextField).at(1), '0');
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pump();

    expect(find.text('1회 복용량은 0보다 큰 숫자로 입력해주세요.'), findsOneWidget);
  });

  testWidgets('영양제 화면은 등록된 영양제 목록을 표시한다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.text('영양제'));
    await tester.pumpAndSettle();

    expect(find.text('등록된 영양제가 없습니다'), findsOneWidget);

    await tester.tap(find.byIcon(Icons.today_outlined));
    await tester.pumpAndSettle();
    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '비타민 D');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('영양제'));
    await tester.pumpAndSettle();

    expect(find.text('비타민 D'), findsOneWidget);
    expect(find.text('식사 기준 · 하루 1회'), findsOneWidget);
    expect(find.text('1회 1 개'), findsOneWidget);
  });

  testWidgets('기록 화면은 오늘 복용 체크 상태를 완료율로 표시한다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '비타민 D');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('비타민 D'));
    await tester.pumpAndSettle();

    await tester.tap(find.byIcon(Icons.calendar_month_outlined));
    await tester.pumpAndSettle();

    expect(find.text('완료율 100% (1/1)'), findsOneWidget);
  });

  test('식사 시간 설정 변경은 오늘 루틴 일정에 반영된다', () {
    final container = ProviderContainer();
    addTearDown(container.dispose);

    container
        .read(supplementListProvider.notifier)
        .addSupplement(
          const Supplement(
            id: 'test_supplement',
            name: '비타민 D',
            dailyCount: 1,
            method: IntakeMethod.mealBased,
            dosageUnit: '개',
            dosageValue: 1,
            selectedSlots: [
              IntakeSlot(
                mealType: MealType.breakfast,
                condition: IntakeCondition.afterMeal,
              ),
            ],
            isNotificationEnabled: true,
          ),
        );

    expect(
      container.read(todayListProvider).first.record.scheduledTime.hour,
      8,
    );
    expect(
      container.read(todayListProvider).first.record.scheduledTime.minute,
      30,
    );

    container
        .read(mealTimeSettingsProvider.notifier)
        .updateBreakfastTime(const TimeOfDay(hour: 9, minute: 0));

    expect(
      container.read(todayListProvider).first.record.scheduledTime.hour,
      9,
    );
    expect(
      container.read(todayListProvider).first.record.scheduledTime.minute,
      30,
    );
  });
}
