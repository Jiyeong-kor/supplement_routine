import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';

class SupplementListNotifier extends Notifier<List<Supplement>> {
  @override
  List<Supplement> build() {
    return [];
  }

  void addSupplement(Supplement supplement) {
    state = [...state, supplement];
  }

  void updateSupplement(Supplement updatedSupplement) {
    state = [
      for (final supplement in state)
        if (supplement.id == updatedSupplement.id)
          updatedSupplement
        else
          supplement,
    ];
  }

  void removeSupplement(String supplementId) {
    state = [
      for (final supplement in state)
        if (supplement.id != supplementId) supplement,
    ];
  }

  void clearSupplements() {
    state = [];
  }
}

final supplementListProvider =
    NotifierProvider<SupplementListNotifier, List<Supplement>>(() {
      return SupplementListNotifier();
    });
