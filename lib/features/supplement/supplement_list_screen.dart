import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/supplement_add_screen.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class SupplementListScreen extends ConsumerWidget {
  const SupplementListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final supplements = ref.watch(supplementListProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('영양제')),
      body: supplements.isEmpty
          ? const _SupplementEmptyState()
          : ListView.separated(
              padding: const EdgeInsets.all(16),
              itemCount: supplements.length,
              separatorBuilder: (context, index) => const SizedBox(height: 12),
              itemBuilder: (context, index) {
                return _SupplementListItem(supplement: supplements[index]);
              },
            ),
      floatingActionButton: FloatingActionButton(
        heroTag: 'supplement_list_add',
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const SupplementAddScreen(),
            ),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}

class _SupplementListItem extends StatelessWidget {
  const _SupplementListItem({required this.supplement});

  final Supplement supplement;

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: Colors.grey[200]!),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    supplement.name,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Icon(
                  supplement.isNotificationEnabled
                      ? Icons.notifications_active_outlined
                      : Icons.notifications_off_outlined,
                  size: 20,
                  color: Colors.grey[600],
                ),
              ],
            ),
            const SizedBox(height: 6),
            Text(
              '${supplement.method.label} · 하루 ${supplement.dailyCount}회',
              style: TextStyle(fontSize: 13, color: Colors.grey[700]),
            ),
            const SizedBox(height: 4),
            Text(
              '1회 ${_formatDosage(supplement.dosageValue)} ${supplement.dosageUnit}',
              style: TextStyle(fontSize: 13, color: Colors.grey[600]),
            ),
            if (supplement.memo != null) ...[
              const SizedBox(height: 8),
              Text(
                supplement.memo!,
                style: const TextStyle(fontSize: 13, color: Colors.blueGrey),
              ),
            ],
          ],
        ),
      ),
    );
  }

  String _formatDosage(double value) {
    if (value == value.toInt()) {
      return value.toInt().toString();
    }

    return value.toString();
  }
}

class _SupplementEmptyState extends StatelessWidget {
  const _SupplementEmptyState();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.medication_outlined, size: 40, color: Colors.grey[500]),
            const SizedBox(height: 12),
            const Text(
              '등록된 영양제가 없습니다',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 6),
            Text(
              '오른쪽 아래 + 버튼으로 영양제를 등록해보세요.',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 13, color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }
}
