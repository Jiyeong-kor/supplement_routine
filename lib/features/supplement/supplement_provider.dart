import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplements.dart';

class SupplementListNotifier extends Notifier<List<Supplement>> {
  @override
  List<Supplement> build() {
    return mockSupplements;
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
    ref
        .read(intakeRecordProvider.notifier)
        .clearRecordsForSupplement(supplementId);
  }

  void clearSupplements() {
    state = [];
    ref.read(intakeRecordProvider.notifier).clearAll();
  }
}

final supplementListProvider =
    NotifierProvider<SupplementListNotifier, List<Supplement>>(() {
      return SupplementListNotifier();
    });
