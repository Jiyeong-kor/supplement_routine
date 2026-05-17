import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supplement_routine/app/app_bootstrap.dart';

void main() {
  testWidgets('저장소 준비 전에도 첫 프레임용 화면을 즉시 렌더링한다', (tester) async {
    SharedPreferences.setMockInitialValues({});

    await tester.pumpWidget(const AppBootstrap());

    expect(find.byType(AppBootstrap), findsOneWidget);
  });
}
