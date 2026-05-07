import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/app_radius.dart';
import 'package:supplement_routine/features/supplement/supplement_add_screen.dart';
import 'package:supplement_routine/features/today/today_provider.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class TodayScreen extends ConsumerWidget {
  const TodayScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final todayList = ref.watch(todayListProvider);

    final doneCount = todayList.where((item) => item.record.isDone).length;
    final totalCount = todayList.length;

    final now = DateTime.now();
    final dateString = l10n.todayDate(
      now.year,
      now.month,
      now.day,
      _weekdayLabel(l10n, now.weekday),
    );

    final memoItems = todayList.where(
      (item) =>
          item.supplement.memo != null &&
          item.supplement.memo!.trim().isNotEmpty,
    );

    final String quoteText;
    final IconData quoteIcon;

    if (memoItems.isNotEmpty) {
      final firstMemo = memoItems.first;
      quoteText = l10n.todayMemoQuote(
        firstMemo.supplement.name,
        firstMemo.supplement.memo!,
      );
      quoteIcon = Icons.edit_note;
    } else {
      final quotes = [
        l10n.habitQuoteCheckAfterTaking,
        l10n.habitQuoteFixedRoutine,
        l10n.habitQuoteReviewToday,
      ];
      final quoteIndex = now.day % quotes.length;
      quoteText = quotes[quoteIndex];
      quoteIcon = Icons.auto_awesome;
    }

    return Scaffold(
      appBar: AppBar(title: Text(l10n.todayAppBarTitle)),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(dateString, style: Theme.of(context).textTheme.bodyMedium),
            const SizedBox(height: 16),
            _TodayQuoteCard(icon: quoteIcon, text: quoteText),
            const SizedBox(height: 24),
            _TodayProgressCard(done: doneCount, total: totalCount),
            const SizedBox(height: 32),
            Text(
              l10n.todayListTitle,
              style: Theme.of(
                context,
              ).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            if (todayList.isEmpty)
              const _TodayEmptyState()
            else
              ...todayList.map(
                (item) => _TodaySupplementItem(
                  time: item.record.scheduledTime.format(context),
                  name: item.supplement.name,
                  label: item.label,
                  dosageUnit: item.supplement.dosageUnit,
                  dosageValue: item.supplement.dosageValue,
                  isDone: item.record.isDone,
                  memo: item.supplement.memo,
                  onTap: () => ref
                      .read(todayListProvider.notifier)
                      .toggleRecord(item.record.id),
                ),
              ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        heroTag: 'today_add_supplement',
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

  String _weekdayLabel(AppLocalizations l10n, int weekday) {
    return switch (weekday) {
      DateTime.monday => l10n.weekdayMonday,
      DateTime.tuesday => l10n.weekdayTuesday,
      DateTime.wednesday => l10n.weekdayWednesday,
      DateTime.thursday => l10n.weekdayThursday,
      DateTime.friday => l10n.weekdayFriday,
      DateTime.saturday => l10n.weekdaySaturday,
      DateTime.sunday => l10n.weekdaySunday,
      _ => '',
    };
  }
}

class _TodayQuoteCard extends StatelessWidget {
  const _TodayQuoteCard({required this.icon, required this.text});

  final IconData icon;
  final String text;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Card.filled(
      margin: EdgeInsets.zero,
      color: colorScheme.secondaryContainer,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(icon, color: colorScheme.onSecondaryContainer),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                text,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onSecondaryContainer,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _TodayProgressCard extends StatelessWidget {
  const _TodayProgressCard({required this.done, required this.total});

  final int done;
  final int total;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final double percent = total > 0 ? done / total : 0;
    final colorScheme = Theme.of(context).colorScheme;

    return Card(
      margin: EdgeInsets.zero,
      color: colorScheme.surfaceContainerLow,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  l10n.todayRoutineTitle,
                  style: Theme.of(
                    context,
                  ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
                ),
                Text(
                  l10n.todayProgressCount(done, total),
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                    color: colorScheme.primary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            ClipRRect(
              borderRadius: AppRadius.pillBorder,
              child: LinearProgressIndicator(
                value: percent,
                minHeight: 10,
                backgroundColor: colorScheme.surfaceContainerHighest,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _TodaySupplementItem extends StatelessWidget {
  const _TodaySupplementItem({
    required this.time,
    required this.name,
    required this.label,
    required this.dosageUnit,
    required this.dosageValue,
    required this.isDone,
    required this.onTap,
    this.memo,
  });

  final String time;
  final String name;
  final String label;
  final String dosageUnit;
  final double dosageValue;
  final bool isDone;
  final VoidCallback onTap;
  final String? memo;

  @override
  Widget build(BuildContext context) {
    // 1.5 개 -> 1.5개, 1.0 개 -> 1개 처리
    final dosageStr = dosageValue == dosageValue.toInt()
        ? dosageValue.toInt().toString()
        : dosageValue.toString();
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    final memoText = memo;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      color: isDone ? colorScheme.surfaceContainerHighest : null,
      child: InkWell(
        borderRadius: AppRadius.mdBorder,
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              SizedBox(
                width: 60,
                child: Text(
                  time,
                  style: textTheme.labelLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: colorScheme.primary,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      name,
                      style: textTheme.titleSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: isDone
                            ? colorScheme.onSurfaceVariant
                            : colorScheme.onSurface,
                      ),
                    ),
                    Text(
                      '$label | $dosageStr $dosageUnit',
                      style: textTheme.bodySmall?.copyWith(
                        color: colorScheme.onSurfaceVariant,
                      ),
                    ),
                    if (memoText != null)
                      Padding(
                        padding: const EdgeInsets.only(top: 4),
                        child: Text(
                          memoText,
                          style: textTheme.bodySmall?.copyWith(
                            color: colorScheme.tertiary,
                          ),
                        ),
                      ),
                  ],
                ),
              ),
              Icon(
                isDone ? Icons.check_circle : Icons.radio_button_unchecked,
                color: isDone ? colorScheme.primary : colorScheme.outline,
                size: 28,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _TodayEmptyState extends StatelessWidget {
  const _TodayEmptyState();

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;

    return Card(
      margin: EdgeInsets.zero,
      color: colorScheme.surfaceContainerLow,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 28),
        child: Column(
          children: [
            Icon(
              Icons.event_available_outlined,
              size: 36,
              color: colorScheme.outline,
            ),
            const SizedBox(height: 12),
            Text(
              l10n.todayEmptyTitle,
              style: Theme.of(
                context,
              ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 6),
            Text(
              l10n.todayEmptyDescription,
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
