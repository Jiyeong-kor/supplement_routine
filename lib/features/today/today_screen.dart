import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/features/today/today_provider.dart';
import 'package:supplement_routine/core/constants/habit_quotes.dart';
import 'package:supplement_routine/features/supplement/supplement_add_screen.dart';

class TodayScreen extends ConsumerWidget {
  const TodayScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final todayList = ref.watch(todayListProvider);

    final doneCount = todayList.where((item) => item.record.isDone).length;
    final totalCount = todayList.length;

    final now = DateTime.now();
    final weekDays = ['월', '화', '수', '목', '금', '토', '일'];
    final dateString =
        '${now.year}년 ${now.month}월 ${now.day}일 ${weekDays[now.weekday - 1]}요일';

    final memoItems = todayList.where(
      (item) =>
          item.supplement.memo != null &&
          item.supplement.memo!.trim().isNotEmpty,
    );

    final String quoteText;
    final IconData quoteIcon;

    if (memoItems.isNotEmpty) {
      final firstMemo = memoItems.first;
      quoteText =
          '내 메모 · ${firstMemo.supplement.name}: ${firstMemo.supplement.memo}';
      quoteIcon = Icons.edit_note;
    } else {
      final quoteIndex = now.day % HabitQuotes.quotes.length;
      quoteText = HabitQuotes.quotes[quoteIndex];
      quoteIcon = Icons.auto_awesome;
    }

    return Scaffold(
      appBar: AppBar(title: const Text('오늘의 복용')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(dateString, style: Theme.of(context).textTheme.bodyMedium),
            const SizedBox(height: 16),
            _buildQuoteCard(context, icon: quoteIcon, text: quoteText),
            const SizedBox(height: 24),
            _buildProgressSection(context, done: doneCount, total: totalCount),
            const SizedBox(height: 32),
            Text(
              '복용 목록',
              style: Theme.of(
                context,
              ).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            if (todayList.isEmpty)
              const _TodayEmptyState()
            else
              ...todayList.map(
                (item) => _buildSupplementItem(
                  context: context,
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

  Widget _buildQuoteCard(
    BuildContext context, {
    required IconData icon,
    required String text,
  }) {
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

  Widget _buildProgressSection(
    BuildContext context, {
    required int done,
    required int total,
  }) {
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
                  '오늘의 루틴',
                  style: Theme.of(
                    context,
                  ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
                ),
                Text(
                  '$done / $total 완료',
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                    color: colorScheme.primary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            ClipRRect(
              borderRadius: BorderRadius.circular(999),
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

  Widget _buildSupplementItem({
    required BuildContext context,
    required String time,
    required String name,
    required String label,
    required String dosageUnit,
    required double dosageValue,
    required bool isDone,
    required VoidCallback onTap,
    String? memo,
  }) {
    // 1.5 개 -> 1.5개, 1.0 개 -> 1개 처리
    final dosageStr = dosageValue == dosageValue.toInt()
        ? dosageValue.toInt().toString()
        : dosageValue.toString();
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      color: isDone ? colorScheme.surfaceContainerHighest : null,
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
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
                    if (memo != null)
                      Padding(
                        padding: const EdgeInsets.only(top: 4),
                        child: Text(
                          memo,
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
              '등록된 복용 일정이 없습니다',
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
