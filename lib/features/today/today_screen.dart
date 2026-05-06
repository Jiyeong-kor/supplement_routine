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
    final dateString = '${now.year}년 ${now.month}월 ${now.day}일 ${weekDays[now.weekday - 1]}요일';

    final memoItems = todayList.where(
      (item) => item.supplement.memo != null && item.supplement.memo!.trim().isNotEmpty,
    );

    final String quoteText;
    final IconData quoteIcon;

    if (memoItems.isNotEmpty) {
      final firstMemo = memoItems.first;
      quoteText = '내 메모 · ${firstMemo.supplement.name}: ${firstMemo.supplement.memo}';
      quoteIcon = Icons.edit_note;
    } else {
      final quoteIndex = now.day % HabitQuotes.quotes.length;
      quoteText = HabitQuotes.quotes[quoteIndex];
      quoteIcon = Icons.auto_awesome;
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('오늘의 복용'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              dateString,
              style: const TextStyle(fontSize: 16, color: Colors.grey),
            ),
            const SizedBox(height: 16),
            _buildQuoteCard(
              icon: quoteIcon,
              text: quoteText,
            ),
            const SizedBox(height: 24),
            _buildProgressSection(done: doneCount, total: totalCount),
            const SizedBox(height: 32),
            const Text(
              '복용 목록',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            ...todayList.map((item) => _buildSupplementItem(
                  time: item.record.scheduledTime.format(context),
                  name: item.supplement.name,
                  label: item.label,
                  dosageUnit: item.supplement.dosageUnit,
                  dosageValue: item.supplement.dosageValue,
                  isDone: item.record.isDone,
                  memo: item.supplement.memo,
                  onTap: () => ref.read(todayListProvider.notifier).toggleRecord(item.record.id),
                )),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const SupplementAddScreen()),
          );
        },
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
                    '$label | $dosageStr $dosageUnit',
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
