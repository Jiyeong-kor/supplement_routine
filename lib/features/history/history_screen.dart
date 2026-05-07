import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/features/history/history_summary_provider.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class HistoryScreen extends ConsumerWidget {
  const HistoryScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final summaries = ref.watch(historySummariesProvider);
    final todaySummary = summaries.first;
    final recentSummaries = summaries.skip(1).toList();

    return Scaffold(
      appBar: AppBar(title: Text(l10n.historyTitle)),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _HistoryOverviewCard(summary: todaySummary),
          const SizedBox(height: 24),
          Text(
            l10n.historyRecentTitle,
            style: Theme.of(
              context,
            ).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 4),
          Text(
            l10n.historyRecentDescription,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
          ),
          const SizedBox(height: 12),
          if (summaries.every((summary) => summary.isEmpty))
            const _HistoryEmptyState()
          else
            ...recentSummaries.map(
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
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.historyTodayOverviewTitle,
              style: textTheme.titleSmall?.copyWith(
                color: colorScheme.onPrimaryContainer,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              l10n.historyPercent(percent),
              style: textTheme.displaySmall?.copyWith(
                color: colorScheme.onPrimaryContainer,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
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
            const SizedBox(height: 16),
            LinearProgressIndicator(
              value: summary.completionRate,
              minHeight: 10,
              borderRadius: BorderRadius.circular(999),
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
        padding: const EdgeInsets.all(16),
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
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 14),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    dateText,
                    style: textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface,
                    ),
                  ),
                  const SizedBox(height: 4),
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
                  const SizedBox(height: 10),
                  LinearProgressIndicator(
                    value: completion,
                    minHeight: 6,
                    borderRadius: BorderRadius.circular(999),
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
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
        child: Column(
          children: [
            Icon(
              Icons.calendar_month_outlined,
              size: 36,
              color: colorScheme.outline,
            ),
            const SizedBox(height: 12),
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
