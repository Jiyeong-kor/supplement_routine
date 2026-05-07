import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/supplement_routine_app.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/services/scheduling_service.dart';
import 'package:supplement_routine/features/history/history_summary_provider.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

void main() {
  testWidgets('앱 시작 화면에 오늘 탭이 표시된다', (WidgetTester tester) async {
    // ProviderScope로 감싸서 앱을 빌드합니다.
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    // 하단 내비게이션의 '오늘' 텍스트가 있는지 확인합니다.
    expect(find.text('오늘'), findsWidgets);

    // 초기 화면인 '오늘' 화면의 AppBar 타이틀과 mock 일정이 있는지 확인합니다.
    expect(find.text('오늘의 복용'), findsOneWidget);
    expect(find.text('비타민 D'), findsOneWidget);
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

    expect(find.text('비타민 D'), findsOneWidget);
    expect(find.text('오메가3'), findsOneWidget);

    await tester.tap(find.byIcon(Icons.today_outlined));
    await tester.pumpAndSettle();
    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '테스트 비타민 D');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('영양제'));
    await tester.pumpAndSettle();

    await tester.scrollUntilVisible(find.text('테스트 비타민 D'), 200);

    expect(find.text('테스트 비타민 D'), findsOneWidget);
    expect(find.text('식사 기준 · 하루 1회'), findsWidgets);
    expect(find.text('1회 1 개'), findsWidgets);

    await tester.tap(find.byTooltip('삭제').last);
    await tester.pumpAndSettle();

    expect(find.text('영양제 삭제'), findsOneWidget);

    await tester.tap(find.text('삭제'));
    await tester.pumpAndSettle();

    expect(find.text('테스트 비타민 D'), findsNothing);
    expect(find.text('비타민 D'), findsOneWidget);
  });

  testWidgets('영양제 목록에서 등록된 영양제를 수정한다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '수정 전 영양제');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('영양제'));
    await tester.pumpAndSettle();
    await tester.scrollUntilVisible(find.text('수정 전 영양제'), 200);
    await tester.tap(find.byTooltip('수정').last);
    await tester.pumpAndSettle();

    expect(find.text('영양제 수정'), findsOneWidget);

    await tester.enterText(find.byType(TextField).first, '수정 후 영양제');
    await tester.ensureVisible(find.text('수정 완료'));
    await tester.tap(find.text('수정 완료'));
    await tester.pumpAndSettle();

    expect(find.text('수정 후 영양제'), findsOneWidget);
    expect(find.text('수정 전 영양제'), findsNothing);
  });

  testWidgets('기록 화면은 오늘 복용 체크 상태를 완료율로 표시한다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '기록 테스트');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('기록 테스트'));
    await tester.pumpAndSettle();

    await tester.tap(find.byIcon(Icons.calendar_month_outlined));
    await tester.pumpAndSettle();

    expect(find.text('완료율 20% (1/5)'), findsOneWidget);
  });

  testWidgets('설정 화면 데이터 초기화는 등록된 영양제를 모두 삭제한다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField).first, '초기화 테스트');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    expect(find.text('초기화 테스트'), findsOneWidget);

    await tester.tap(find.text('설정'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('데이터 초기화'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('초기화'));
    await tester.pumpAndSettle();

    expect(find.text('데이터가 초기화되었습니다.'), findsOneWidget);

    await tester.tap(find.text('오늘'));
    await tester.pumpAndSettle();

    expect(find.text('등록된 복용 일정이 없습니다'), findsOneWidget);

    await tester.tap(find.text('영양제'));
    await tester.pumpAndSettle();

    expect(find.text('등록된 영양제가 없습니다'), findsOneWidget);
  });

  testWidgets('알림 설정은 새 영양제 등록 기본값에 반영된다', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: SupplementRoutineApp()));

    await tester.tap(find.text('설정'));
    await tester.pumpAndSettle();
    await tester.tap(find.widgetWithText(SwitchListTile, '알림 설정'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('오늘'));
    await tester.pumpAndSettle();
    await tester.tap(find.byIcon(Icons.add));
    await tester.pumpAndSettle();

    expect(
      tester
          .widget<SwitchListTile>(
            find.widgetWithText(SwitchListTile, '복용 알림 받기'),
          )
          .value,
      isFalse,
    );

    await tester.enterText(find.byType(TextField).first, '알림 테스트');
    await tester.tap(find.text('기상 직후(공복)'));
    await tester.ensureVisible(find.text('등록 완료'));
    await tester.tap(find.text('등록 완료'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('영양제'));
    await tester.pumpAndSettle();

    expect(find.byIcon(Icons.notifications_off_outlined), findsWidgets);
  });

  test('식사 시간 설정 변경은 오늘 루틴 일정에 반영된다', () {
    final container = ProviderContainer();
    addTearDown(container.dispose);
    container.read(supplementListProvider.notifier).clearSupplements();

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

  test('복용 체크 기록은 오늘 일정 재계산 후에도 유지된다', () {
    final container = ProviderContainer();
    addTearDown(container.dispose);
    container.read(supplementListProvider.notifier).clearSupplements();

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

    final recordId = container.read(todayListProvider).first.record.id;

    container.read(todayListProvider.notifier).toggleRecord(recordId);

    expect(container.read(todayListProvider).first.record.isDone, isTrue);
    expect(
      container.read(todayListProvider).first.record.scheduledTime.hour,
      8,
    );

    container
        .read(mealTimeSettingsProvider.notifier)
        .updateBreakfastTime(const TimeOfDay(hour: 9, minute: 0));

    expect(container.read(todayListProvider).first.record.isDone, isTrue);
    expect(
      container.read(todayListProvider).first.record.scheduledTime.hour,
      9,
    );
  });

  test('오늘 기록 요약은 완료 개수와 완료율을 계산한다', () {
    final container = ProviderContainer();
    addTearDown(container.dispose);
    container.read(supplementListProvider.notifier).clearSupplements();

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

    final recordId = container.read(todayListProvider).first.record.id;

    container.read(todayListProvider.notifier).toggleRecord(recordId);

    final summary = container.read(todayHistorySummaryProvider);

    expect(summary.doneCount, 1);
    expect(summary.totalCount, 1);
    expect(summary.completionRate, 1);
  });

  test('복용 기록 ID는 연월일을 포함해 날짜별로 구분된다', () {
    const supplement = Supplement(
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
    );

    final aprilRecord = SchedulingService.createDailyIntakeRecords(
      supplements: [supplement],
      date: DateTime(2026, 4, 7),
    ).first.record;
    final mayRecord = SchedulingService.createDailyIntakeRecords(
      supplements: [supplement],
      date: DateTime(2026, 5, 7),
    ).first.record;

    expect(aprilRecord.id, 'r_test_supplement_20260407_0');
    expect(mayRecord.id, 'r_test_supplement_20260507_0');
    expect(aprilRecord.id, isNot(mayRecord.id));
  });
}
