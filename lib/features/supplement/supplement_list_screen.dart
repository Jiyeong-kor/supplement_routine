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
      body: _SupplementListBody(
        supplements: supplements,
        onEdit: (supplement) {
          _openEditScreen(context, supplement);
        },
        onDelete: (supplement) {
          _showDeleteDialog(context, ref, supplement);
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

  void _openEditScreen(BuildContext context, Supplement supplement) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) =>
            SupplementAddScreen(initialSupplement: supplement),
      ),
    );
  }

  void _showDeleteDialog(
    BuildContext context,
    WidgetRef ref,
    Supplement supplement,
  ) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('영양제 삭제'),
        content: Text('${supplement.name}을(를) 삭제하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () {
              ref
                  .read(supplementListProvider.notifier)
                  .removeSupplement(supplement.id);
              Navigator.pop(context);
            },
            child: const Text('삭제', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}

class _SupplementListBody extends StatelessWidget {
  const _SupplementListBody({
    required this.supplements,
    required this.onEdit,
    required this.onDelete,
  });

  final List<Supplement> supplements;
  final ValueChanged<Supplement> onEdit;
  final ValueChanged<Supplement> onDelete;

  @override
  Widget build(BuildContext context) {
    if (supplements.isEmpty) {
      return const _SupplementEmptyState();
    }

    return ListView.separated(
      padding: const EdgeInsets.all(16),
      itemCount: supplements.length,
      separatorBuilder: (context, index) => const SizedBox(height: 12),
      itemBuilder: (context, index) {
        final supplement = supplements[index];

        return _SupplementListItem(
          supplement: supplement,
          onEdit: () {
            onEdit(supplement);
          },
          onDelete: () {
            onDelete(supplement);
          },
        );
      },
    );
  }
}

class _SupplementListItem extends StatelessWidget {
  const _SupplementListItem({
    required this.supplement,
    required this.onEdit,
    required this.onDelete,
  });

  final Supplement supplement;
  final VoidCallback onEdit;
  final VoidCallback onDelete;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Card(
      margin: EdgeInsets.zero,
      color: colorScheme.surfaceContainerLow,
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
                    style: textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface,
                    ),
                  ),
                ),
                _SupplementItemActions(
                  isNotificationEnabled: supplement.isNotificationEnabled,
                  onEdit: onEdit,
                  onDelete: onDelete,
                ),
              ],
            ),
            const SizedBox(height: 6),
            Text(
              '${supplement.method.label} · 하루 ${supplement.dailyCount}회',
              style: textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              '1회 ${_formatDosage(supplement.dosageValue)} ${supplement.dosageUnit}',
              style: textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            if (supplement.memo != null) ...[
              const SizedBox(height: 8),
              Text(
                supplement.memo!,
                style: textTheme.bodySmall?.copyWith(
                  color: colorScheme.tertiary,
                ),
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

class _SupplementItemActions extends StatelessWidget {
  const _SupplementItemActions({
    required this.isNotificationEnabled,
    required this.onEdit,
    required this.onDelete,
  });

  final bool isNotificationEnabled;
  final VoidCallback onEdit;
  final VoidCallback onDelete;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(
          isNotificationEnabled
              ? Icons.notifications_active_outlined
              : Icons.notifications_off_outlined,
          size: 20,
          color: isNotificationEnabled
              ? colorScheme.primary
              : colorScheme.outline,
        ),
        const SizedBox(width: 4),
        IconButton(
          tooltip: '수정',
          onPressed: onEdit,
          icon: const Icon(Icons.edit_outlined),
          color: colorScheme.onSurfaceVariant,
        ),
        const SizedBox(width: 4),
        IconButton(
          tooltip: '삭제',
          onPressed: onDelete,
          icon: const Icon(Icons.delete_outline),
          color: colorScheme.error,
        ),
      ],
    );
  }
}

class _SupplementEmptyState extends StatelessWidget {
  const _SupplementEmptyState();

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.inventory_2_outlined,
              size: 40,
              color: colorScheme.outline,
            ),
            const SizedBox(height: 12),
            Text(
              '등록된 영양제가 없습니다',
              style: Theme.of(
                context,
              ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 6),
            Text(
              '오른쪽 아래 + 버튼으로 영양제를 등록해보세요.',
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
