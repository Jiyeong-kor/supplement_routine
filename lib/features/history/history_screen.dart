import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

class HistoryScreen extends ConsumerWidget {
  const HistoryScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final todayList = ref.watch(todayListProvider);
    final done = todayList.where((item) => item.record.isDone).length;
    final total = todayList.length;
    final completion = total > 0 ? done / total : 0.0;
    final today = DateTime.now();
    final weekDays = ['월', '화', '수', '목', '금', '토', '일'];
    final dateText =
        '${today.month}월 ${today.day}일 ${weekDays[today.weekday - 1]}요일';

    return Scaffold(
      appBar: AppBar(title: const Text('기록')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _HistoryItem(
            dateText: '오늘 · $dateText',
            done: done,
            total: total,
            completion: completion,
          ),
          if (total == 0) ...[
            const SizedBox(height: 12),
            const _HistoryEmptyState(),
          ],
        ],
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
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: Colors.grey[200]!),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    dateText,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '완료율 ${(completion * 100).toInt()}% ($done/$total)',
                    style: TextStyle(color: Colors.grey[600], fontSize: 14),
                  ),
                ],
              ),
            ),
            SizedBox(
              width: 40,
              height: 40,
              child: CircularProgressIndicator(
                value: completion,
                backgroundColor: Colors.grey[200],
                strokeWidth: 6,
                strokeCap: StrokeCap.round,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _HistoryEmptyState extends StatelessWidget {
  const _HistoryEmptyState();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[200]!),
      ),
      child: Text(
        '오늘 등록된 복용 일정이 없습니다.',
        textAlign: TextAlign.center,
        style: TextStyle(color: Colors.grey[600]),
      ),
    );
  }
}
