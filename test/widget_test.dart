import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/supplement_routine_app.dart';

void main() {
  testWidgets('앱 시작 화면에 오늘 탭이 표시된다', (WidgetTester tester) async {
    // ProviderScope로 감싸서 앱을 빌드합니다.
    await tester.pumpWidget(
      const ProviderScope(
        child: SupplementRoutineApp(),
      ),
    );

    // 하단 내비게이션의 '오늘' 텍스트가 있는지 확인합니다.
    expect(find.text('오늘'), findsWidgets);
    
    // 초기 화면인 '오늘' 화면의 AppBar 타이틀이 있는지 확인합니다.
    expect(find.text('오늘의 복용 관리 화면'), findsOneWidget);
  });
}
