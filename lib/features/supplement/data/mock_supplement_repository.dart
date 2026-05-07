import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplements.dart';
import 'package:supplement_routine/features/supplement/data/supplement_repository.dart';

class MockSupplementRepository implements SupplementRepository {
  MockSupplementRepository({
    List<Supplement> initialSupplements = mockSupplements,
  }) : _supplements = [...initialSupplements];

  final List<Supplement> _supplements;

  @override
  List<Supplement> getSupplements() {
    return List.unmodifiable(_supplements);
  }

  @override
  List<Supplement> addSupplement(Supplement supplement) {
    _supplements.add(supplement);
    return getSupplements();
  }

  @override
  List<Supplement> updateSupplement(Supplement updatedSupplement) {
    final index = _supplements.indexWhere(
      (supplement) => supplement.id == updatedSupplement.id,
    );

    if (index == -1) {
      return getSupplements();
    }

    _supplements[index] = updatedSupplement;
    return getSupplements();
  }

  @override
  List<Supplement> removeSupplement(String supplementId) {
    _supplements.removeWhere((supplement) => supplement.id == supplementId);
    return getSupplements();
  }

  @override
  List<Supplement> clearSupplements() {
    _supplements.clear();
    return getSupplements();
  }
}
