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

  void removeSupplement(String supplementId) {
    state = [
      for (final supplement in state)
        if (supplement.id != supplementId) supplement,
    ];
  }
}

final supplementListProvider =
    NotifierProvider<SupplementListNotifier, List<Supplement>>(() {
      return SupplementListNotifier();
    });
