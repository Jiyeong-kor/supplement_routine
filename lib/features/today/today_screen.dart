import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/features/today/today_provider.dart';

class TodayScreen extends ConsumerWidget {
  const TodayScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final todayList = ref.watch(todayListProvider);
    
    // 완료 개수 계산
    final doneCount = todayList.where((item) => item.record.isDone).length;
    final totalCount = todayList.length;

    // 날짜 포맷 (예: 2024년 5월 20일 월요일)
    final now = DateTime.now();
    final weekDays = ['월', '화', '수', '목', '금', '토', '일'];
    final dateString = '${now.year}년 ${now.month}월 ${now.day}일 ${weekDays[now.weekday - 1]}요일';

    // 오늘의 한 줄 로직 개선
    final memoItems = todayList.where(
      (item) => item.supplement.memo != null && item.supplement.memo!.trim().isNotEmpty,
    );

    final quoteText = memoItems.isNotEmpty
        ? '내 메모 · ${memoItems.first.supplement.name}: ${memoItems.first.supplement.memo}'
        : '복용 후 바로 체크하면 오늘 기록이 더 정확해집니다.';

    return Scaffold(
      appBar: AppBar(
        title: const Text('오늘의 복용'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 1. 오늘 날짜
            Text(
              dateString,
              style: const TextStyle(fontSize: 16, color: Colors.grey),
            ),
            const SizedBox(height: 16),

            // 2. 오늘의 한 줄 카드
            _buildQuoteCard(
              icon: Icons.edit_note,
              text: quoteText,
            ),
            const SizedBox(height: 24),

            // 3. 완료 현황
            _buildProgressSection(done: doneCount, total: totalCount),
            const SizedBox(height: 32),

            // 4. 시간순 복용 목록
            const Text(
              '복용 목록',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            ...todayList.map((item) => _buildSupplementItem(
                  time: item.record.scheduledTime.format(context),
                  name: item.supplement.name,
                  condition: item.supplement.condition.label,
                  isDone: item.record.isDone,
                  memo: item.supplement.memo,
                  onTap: () => ref.read(todayListProvider.notifier).toggleRecord(item.record.id),
                )),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildQuoteCard({required IconData icon, required String text}) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: Row(
        children: [
          Icon(icon, color: Colors.grey[700]),
          const SizedBox(width: 12),
          Expanded(
            child: Text(
              text,
              style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildProgressSection({required int done, required int total}) {
    final double percent = total > 0 ? done / total : 0;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text('오늘의 성취도', style: TextStyle(fontWeight: FontWeight.w600)),
            Text('$done / $total 완료', style: const TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
        const SizedBox(height: 8),
        LinearProgressIndicator(
          value: percent,
          minHeight: 8,
          backgroundColor: Colors.grey[200],
          valueColor: const AlwaysStoppedAnimation<Color>(Colors.blue),
          borderRadius: BorderRadius.circular(4),
        ),
      ],
    );
  }

  Widget _buildSupplementItem({
    required String time,
    required String name,
    required String condition,
    required bool isDone,
    required VoidCallback onTap,
    String? memo,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.grey[200]!),
        ),
        child: Row(
          children: [
            SizedBox(
              width: 60,
              child: Text(
                time,
                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    name,
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      decoration: isDone ? TextDecoration.lineThrough : null,
                      color: isDone ? Colors.grey : Colors.black,
                    ),
                  ),
                  Text(
                    condition,
                    style: TextStyle(fontSize: 13, color: Colors.grey[600]),
                  ),
                  if (memo != null)
                    Padding(
                      padding: const EdgeInsets.only(top: 4),
                      child: Text(
                        'ㄴ $memo',
                        style: const TextStyle(fontSize: 12, color: Colors.blueGrey),
                      ),
                    ),
                ],
              ),
            ),
            Icon(
              isDone ? Icons.check_circle : Icons.radio_button_unchecked,
              color: isDone ? Colors.blue : Colors.grey[400],
              size: 28,
            ),
          ],
        ),
      ),
    );
  }
}
