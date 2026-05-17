import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/app/app_colors.dart';
import 'package:supplement_routine/app/app_typography.dart';

void main() {
  test('다크 모드 typography는 onSurface 색상을 유지한다', () {
    final textTheme = AppTypography.textTheme(AppColors.darkScheme);

    expect(textTheme.titleLarge?.color, AppColors.darkScheme.onSurface);
    expect(textTheme.titleMedium?.color, AppColors.darkScheme.onSurface);
    expect(textTheme.bodyMedium?.color, AppColors.darkScheme.onSurface);
  });
}
