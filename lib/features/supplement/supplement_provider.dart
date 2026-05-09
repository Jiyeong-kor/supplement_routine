import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/history/intake_record_provider.dart';
import 'package:supplement_routine/features/supplement/data/mock_supplement_repository.dart';
import 'package:supplement_routine/features/supplement/data/supplement_repository.dart';

final supplementRepositoryProvider = Provider<SupplementRepository>((ref) {
  return MockSupplementRepository();
});

class SupplementListNotifier extends Notifier<List<Supplement>> {
  @override
  List<Supplement> build() {
    return ref.read(supplementRepositoryProvider).getSupplements();
  }

  void addSupplement(Supplement supplement) {
    state = ref.read(supplementRepositoryProvider).addSupplement(supplement);
  }

  void updateSupplement(Supplement updatedSupplement) {
    state = ref
        .read(supplementRepositoryProvider)
        .updateSupplement(updatedSupplement);
  }

  void toggleNotification(String supplementId) {
    final supplement = state.firstWhere((s) => s.id == supplementId);
    final updated = supplement.copyWith(
      isNotificationEnabled: !supplement.isNotificationEnabled,
    );
    updateSupplement(updated);
  }

  void removeSupplement(String supplementId) {
    state = ref
        .read(supplementRepositoryProvider)
        .removeSupplement(supplementId);
    ref
        .read(intakeRecordProvider.notifier)
        .clearRecordsForSupplement(supplementId);
  }

  void clearSupplements() {
    state = ref.read(supplementRepositoryProvider).clearSupplements();
    ref.read(intakeRecordProvider.notifier).clearAll();
  }
}

final supplementListProvider =
    NotifierProvider<SupplementListNotifier, List<Supplement>>(() {
      return SupplementListNotifier();
    });
