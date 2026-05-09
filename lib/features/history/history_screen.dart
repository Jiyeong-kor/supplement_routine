import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/app_radius.dart';
import 'package:supplement_routine/app/app_spacing.dart';
import 'package:supplement_routine/features/history/history_summary_provider.dart';
import 'package:supplement_routine/features/history/history_view_model.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class HistoryScreen extends ConsumerWidget {
  const HistoryScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final state = ref.watch(historyViewModelProvider);

    return Scaffold(
      appBar: AppBar(title: Text(l10n.historyTitle)),
      body: ListView(
        padding: AppSpacing.screenPadding,
        children: [
          _HistoryOverviewCard(summary: state.todaySummary),
          const SizedBox(height: AppSpacing.xxl),
          Text(
            l10n.historyRecentTitle,
            style: Theme.of(
              context,
            ).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w700),
          ),
          const SizedBox(height: AppSpacing.xxs),
          Text(
            l10n.historyRecentDescription,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
          ),
          const SizedBox(height: AppSpacing.md),
          if (state.isEmpty)
            const _HistoryEmptyState()
          else
            ...state.recentSummaries.map(
              (summary) => _HistoryItem(
                dateText: _dateLabel(l10n, summary.date),
                done: summary.doneCount,
                total: summary.totalCount,
                completion: summary.completionRate,
              ),
            ),
        ],
      ),
    );
  }

  String _dateLabel(AppLocalizations l10n, DateTime date) {
    return l10n.historyDate(
      date.month,
      date.day,
      _weekdayLabel(l10n, date.weekday),
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

class _HistoryOverviewCard extends StatelessWidget {
  const _HistoryOverviewCard({required this.summary});

  final DailyHistorySummary summary;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    final percent = (summary.completionRate * 100).round();

    return Card.filled(
      margin: EdgeInsets.zero,
      color: colorScheme.primaryContainer,
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.historyTodayOverviewTitle,
              style: textTheme.titleSmall?.copyWith(
                color: colorScheme.onPrimaryContainer,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: AppSpacing.sm),
            Text(
              l10n.historyPercent(percent),
              style: textTheme.displaySmall?.copyWith(
                color: colorScheme.onPrimaryContainer,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: AppSpacing.sm),
            Text(
              summary.isEmpty
                  ? l10n.historyEmptyToday
                  : l10n.historyCompletion(
                      percent,
                      summary.doneCount,
                      summary.totalCount,
                    ),
              style: textTheme.bodyMedium?.copyWith(
                color: colorScheme.onPrimaryContainer,
              ),
            ),
            const SizedBox(height: AppSpacing.lg),
            LinearProgressIndicator(
              value: summary.completionRate,
              minHeight: 10,
              borderRadius: AppRadius.pillBorder,
              backgroundColor: colorScheme.primary.withValues(alpha: 0.18),
            ),
          ],
        ),
      ),
    );
  }
}

class _HistoryItem extends StatelessWidget {
  const _HistoryItem({
    required this.dateText,
    required this.done,
    required this.total,
    required this.completion,
  });

  final String dateText;
  final int done;
  final int total;
  final double completion;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Card.outlined(
      margin: const EdgeInsets.only(bottom: 10),
      color: colorScheme.surfaceContainerLow,
      child: Padding(
        padding: AppSpacing.cardPadding,
        child: Row(
          children: [
            SizedBox(
              width: 44,
              height: 44,
              child: DecoratedBox(
                decoration: BoxDecoration(
                  color: _statusColor(colorScheme, completion),
                  shape: BoxShape.circle,
                ),
                child: Center(
                  child: Text(
                    '${(completion * 100).round()}',
                    style: textTheme.labelLarge?.copyWith(
                      color: colorScheme.onPrimary,
                      fontWeight: FontWeight.w700,
                    ),
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
                    dateText,
                    style: textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.w700,
                      color: colorScheme.onSurface,
                    ),
                  ),
                  const SizedBox(height: AppSpacing.xxs),
                  Text(
                    l10n.historyCompletion(
                      (completion * 100).toInt(),
                      done,
                      total,
                    ),
                    style: textTheme.bodySmall?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                  const SizedBox(height: AppSpacing.sm),
                  LinearProgressIndicator(
                    value: completion,
                    minHeight: 6,
                    borderRadius: AppRadius.pillBorder,
                    backgroundColor: colorScheme.surfaceContainerHighest,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Color _statusColor(ColorScheme colorScheme, double completion) {
    if (completion >= 0.8) {
      return colorScheme.primary;
    }
    if (completion >= 0.4) {
      return colorScheme.tertiary;
    }
    return colorScheme.outline;
  }
}

class _HistoryEmptyState extends StatelessWidget {
  const _HistoryEmptyState();

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
          vertical: AppSpacing.xxl,
        ),
        child: Column(
          children: [
            Icon(
              Icons.calendar_month_outlined,
              size: 36,
              color: colorScheme.outline,
            ),
            const SizedBox(height: AppSpacing.md),
            Text(
              l10n.historyEmptyToday,
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
