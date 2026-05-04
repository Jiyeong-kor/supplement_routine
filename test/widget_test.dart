import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/main.dart';

void main() {
  testWidgets('앱 시작 화면에 제목이 표시된다', (WidgetTester tester) async {
    await tester.pumpWidget(const SupplementRoutineApp());

    expect(find.text('Supplement Routine'), findsOneWidget);
    expect(find.byType(MaterialApp), findsOneWidget);
  });
}