import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/presentation/supplement_display_text.dart';
import 'package:supplement_routine/features/supplement/supplement_add_screen.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class SupplementListScreen extends ConsumerWidget {
  const SupplementListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final supplements = ref.watch(supplementListProvider);

    return Scaffold(
      appBar: AppBar(title: Text(l10n.supplementListTitle)),
      body: _SupplementListBody(
        supplements: supplements,
        onEdit: (supplement) {
          _openEditScreen(context, supplement);
        },
        onDelete: (supplement) {
          _showDeleteDialog(context, ref, supplement);
        },
        onToggleNotification: (supplement) {
          ref
              .read(supplementListProvider.notifier)
              .toggleNotification(supplement.id);
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
        title: Text(AppLocalizations.of(context).deleteSupplementTitle),
        content: Text(
          AppLocalizations.of(context).deleteSupplementMessage(supplement.name),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(AppLocalizations.of(context).cancel),
          ),
          TextButton(
            onPressed: () {
              ref
                  .read(supplementListProvider.notifier)
                  .removeSupplement(supplement.id);
              Navigator.pop(context);
            },
            child: Text(
              AppLocalizations.of(context).delete,
              style: TextStyle(color: Theme.of(context).colorScheme.error),
            ),
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
    required this.onToggleNotification,
  });

  final List<Supplement> supplements;
  final ValueChanged<Supplement> onEdit;
  final ValueChanged<Supplement> onDelete;
  final ValueChanged<Supplement> onToggleNotification;

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
          onToggleNotification: () {
            onToggleNotification(supplement);
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
    required this.onToggleNotification,
  });

  final Supplement supplement;
  final VoidCallback onEdit;
  final VoidCallback onDelete;
  final VoidCallback onToggleNotification;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
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
                  onToggleNotification: onToggleNotification,
                ),
              ],
            ),
            const SizedBox(height: 6),
            Text(
              l10n.supplementDailyCount(
                SupplementDisplayText.methodLabel(l10n, supplement.method),
                supplement.dailyCount,
              ),
              style: textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              l10n.supplementDosage(
                _formatDosage(supplement.dosageValue),
                supplement.dosageUnit,
              ),
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
    required this.onToggleNotification,
  });

  final bool isNotificationEnabled;
  final VoidCallback onEdit;
  final VoidCallback onDelete;
  final VoidCallback onToggleNotification;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          tooltip: isNotificationEnabled
              ? l10n.notificationsEnabled
              : l10n.notificationsDisabled,
          onPressed: onToggleNotification,
          icon: Icon(
            isNotificationEnabled
                ? Icons.notifications_active_outlined
                : Icons.notifications_off_outlined,
            size: 20,
            color: isNotificationEnabled
                ? colorScheme.primary
                : colorScheme.outline,
          ),
        ),
        const SizedBox(width: 4),
        IconButton(
          tooltip: l10n.edit,
          onPressed: onEdit,
          icon: const Icon(Icons.edit_outlined),
          color: colorScheme.onSurfaceVariant,
        ),
        const SizedBox(width: 4),
        IconButton(
          tooltip: l10n.delete,
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
    final l10n = AppLocalizations.of(context);
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
              l10n.supplementEmptyTitle,
              style: Theme.of(
                context,
              ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 6),
            Text(
              l10n.supplementEmptyDescription,
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
