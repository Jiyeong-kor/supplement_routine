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
            _TodayQuoteCard(icon: quoteIcon, text: quoteText),
            const SizedBox(height: 24),
            _TodayProgressCard(done: doneCount, total: totalCount),
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
