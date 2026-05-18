import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/app_layout.dart';
import 'package:supplement_routine/app/app_radius.dart';
import 'package:supplement_routine/app/app_spacing.dart';
import 'package:supplement_routine/features/supplement/presentation/supplement_display_text.dart';
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

    final quotes = [
      l10n.habitQuoteCheckAfterTaking,
      l10n.habitQuoteFixedRoutine,
      l10n.habitQuoteReviewToday,
    ];
    final quoteIndex = now.day % quotes.length;
    final quoteText = quotes[quoteIndex];

    return Scaffold(
      appBar: AppBar(title: Text(l10n.todayAppBarTitle)),
      body: AppConstrainedContent(
        child: SingleChildScrollView(
          padding: AppSpacing.screenPadding,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _TodayHeader(dateText: dateString, quoteText: quoteText),
              const SizedBox(height: AppSpacing.lg),
              _TodayProgressCard(done: doneCount, total: totalCount),
              const SizedBox(height: AppSpacing.xxxl),
              Text(
                l10n.todayListTitle,
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: AppSpacing.md),
              if (todayList.isEmpty)
                const _TodayEmptyState()
              else
                ...todayList.map(
                  (item) => _TodaySupplementItem(
                    time: item.record.scheduledTime.format(context),
                    name: item.supplement.name,
                    label: SupplementDisplayText.scheduleLabel(
                      l10n,
                      item.label,
                    ),
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

class _TodayHeader extends StatelessWidget {
  const _TodayHeader({required this.dateText, required this.quoteText});

  final String dateText;
  final String quoteText;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          dateText,
          style: textTheme.labelLarge?.copyWith(
            color: colorScheme.primary,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: AppSpacing.sm),
        Card.filled(
          margin: EdgeInsets.zero,
          color: colorScheme.primaryContainer,
          child: Padding(
            padding: const EdgeInsets.all(AppSpacing.xl),
            child: Row(
              children: [
                DecoratedBox(
                  decoration: BoxDecoration(
                    color: colorScheme.primary.withValues(alpha: 0.12),
                    borderRadius: AppRadius.lgBorder,
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(AppSpacing.md),
                    child: Icon(Icons.auto_awesome, color: colorScheme.primary),
                  ),
                ),
                const SizedBox(width: AppSpacing.md),
                Expanded(
                  child: Text(
                    quoteText,
                    style: textTheme.bodyLarge?.copyWith(
                      color: colorScheme.onPrimaryContainer,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
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
    final textTheme = Theme.of(context).textTheme;

    return Card(
      margin: EdgeInsets.zero,
      color: colorScheme.surfaceContainerLow,
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.end,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(l10n.todayRoutineTitle, style: textTheme.titleSmall),
                    const SizedBox(height: AppSpacing.xxs),
                    Text(
                      l10n.todayProgressCount(done, total),
                      style: textTheme.bodySmall?.copyWith(
                        color: colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ],
                ),
                Text(
                  '${(percent * 100).round()}%',
                  style: textTheme.headlineMedium?.copyWith(
                    color: colorScheme.primary,
                    fontWeight: FontWeight.w700,
                  ),
                ),
              ],
            ),
            const SizedBox(height: AppSpacing.lg),
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
    final dosageStr = dosageValue == dosageValue.toInt()
        ? dosageValue.toInt().toString()
        : dosageValue.toString();
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    final memoText = memo;

    return Card(
      margin: const EdgeInsets.only(bottom: AppSpacing.md),
      color: isDone ? colorScheme.surfaceContainerHighest : null,
      child: InkWell(
        borderRadius: AppRadius.xxlBorder,
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.lg),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              DecoratedBox(
                decoration: BoxDecoration(
                  color: colorScheme.primaryContainer,
                  borderRadius: AppRadius.lgBorder,
                ),
                child: SizedBox(
                  width: 64,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: AppSpacing.sm,
                      vertical: AppSpacing.md,
                    ),
                    child: Column(
                      children: [
                        Text(
                          time.split(' ').first,
                          style: textTheme.labelSmall?.copyWith(
                            color: colorScheme.primary,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(height: AppSpacing.xxs),
                        Text(
                          time.split(' ').skip(1).join(' '),
                          textAlign: TextAlign.center,
                          style: textTheme.labelLarge?.copyWith(
                            color: colorScheme.onPrimaryContainer,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              const SizedBox(width: AppSpacing.md),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      name,
                      style: textTheme.titleSmall?.copyWith(
                        color: isDone
                            ? colorScheme.onSurfaceVariant
                            : colorScheme.onSurface,
                      ),
                    ),
                    Wrap(
                      spacing: AppSpacing.xs,
                      runSpacing: AppSpacing.xs,
                      children: [
                        _MetaChip(label: label),
                        _MetaChip(label: '$dosageStr $dosageUnit'),
                      ],
                    ),
                    if (memoText != null)
                      Padding(
                        padding: const EdgeInsets.only(top: AppSpacing.sm),
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

class _MetaChip extends StatelessWidget {
  const _MetaChip({required this.label});

  final String label;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return DecoratedBox(
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerHighest,
        borderRadius: AppRadius.pillBorder,
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.sm,
          vertical: AppSpacing.xxs,
        ),
        child: Text(
          label,
          style: Theme.of(
            context,
          ).textTheme.labelSmall?.copyWith(color: colorScheme.onSurfaceVariant),
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
        padding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.lg,
          vertical: AppSpacing.xxxl,
        ),
        child: Column(
          children: [
            Icon(
              Icons.event_available_outlined,
              size: 36,
              color: colorScheme.outline,
            ),
            const SizedBox(height: AppSpacing.md),
            Text(
              l10n.todayEmptyTitle,
              style: Theme.of(context).textTheme.titleSmall,
            ),
            const SizedBox(height: AppSpacing.xs),
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
