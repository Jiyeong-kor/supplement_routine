import 'package:flutter_test/flutter_test.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplement_repository.dart';

void main() {
  Supplement createSupplement({String id = 'test_supplement'}) {
    return Supplement(
      id: id,
      name: '테스트 영양제',
      dailyCount: 1,
      method: IntakeMethod.fixedTime,
      dosageUnit: '개',
      dosageValue: 1,
      isNotificationEnabled: true,
    );
  }

  test('초기 영양제 목록을 불변 리스트로 반환한다', () {
    final repository = MockSupplementRepository(
      initialSupplements: [createSupplement()],
    );

    final supplements = repository.getSupplements();

    expect(supplements, hasLength(1));
    expect(
      () => supplements.add(createSupplement(id: 'other')),
      throwsA(isA<UnsupportedError>()),
    );
  });

  test('영양제 추가, 수정, 삭제 결과를 반환한다', () {
    final repository = MockSupplementRepository(initialSupplements: []);
    final supplement = createSupplement();

    final addedSupplements = repository.addSupplement(supplement);
    final updatedSupplement = supplement.copyWith(name: '수정된 영양제');
    final updatedSupplements = repository.updateSupplement(updatedSupplement);
    final removedSupplements = repository.removeSupplement(supplement.id);

    expect(addedSupplements, hasLength(1));
    expect(updatedSupplements.single.name, '수정된 영양제');
    expect(removedSupplements, isEmpty);
  });
}
