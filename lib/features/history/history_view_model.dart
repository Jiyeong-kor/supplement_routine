import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/features/history/history_summary_provider.dart';

class HistoryViewState {
  const HistoryViewState({
    required this.todaySummary,
    required this.monthSummaries,
    required this.recentSummaries,
  });

  final DailyHistorySummary todaySummary;
  final List<DailyHistorySummary> monthSummaries;
  final List<DailyHistorySummary> recentSummaries;

  bool get isEmpty {
    return todaySummary.isEmpty &&
        recentSummaries.every((summary) => summary.isEmpty);
  }
}

final historyViewModelProvider = Provider<HistoryViewState>((ref) {
  final summaries = ref.watch(historySummariesProvider);
  final monthSummaries = ref.watch(currentMonthHistorySummariesProvider);

  return HistoryViewState(
    todaySummary: summaries.first,
    monthSummaries: monthSummaries,
    recentSummaries: summaries.skip(1).toList(),
  );
});
