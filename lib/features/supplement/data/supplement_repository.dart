import 'package:supplement_routine/core/models/supplement.dart';

abstract interface class SupplementRepository {
  List<Supplement> getSupplements();

  List<Supplement> addSupplement(Supplement supplement);

  List<Supplement> updateSupplement(Supplement supplement);

  List<Supplement> removeSupplement(String supplementId);

  List<Supplement> clearSupplements();
}
