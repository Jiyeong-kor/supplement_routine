import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/supplement_routine_app.dart';

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
}
