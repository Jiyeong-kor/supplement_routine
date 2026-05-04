import 'package:flutter/material.dart';

class TodayScreen extends StatelessWidget {
  const TodayScreen({super.key});

  @override
  Widget build(BuildContext context) {
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
            const Text(
              '2024년 5월 20일 월요일',
              style: TextStyle(fontSize: 16, color: Colors.grey),
            ),
            const SizedBox(height: 16),

            // 2. 오늘의 한 줄 카드 (사용자 메모 예시)
            _buildQuoteCard(
              icon: Icons.edit_note,
              text: '내 메모 · 비타민 D: 커피랑 같이 먹지 않기',
            ),
            const SizedBox(height: 24),

            // 3. 완료 현황
            _buildProgressSection(done: 1, total: 3),
            const SizedBox(height: 32),

            // 4. 시간순 복용 목록
            const Text(
              '복용 목록',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            _buildSupplementItem(
              time: '08:00',
              name: '비타민 D',
              condition: '식후',
              isDone: false,
              memo: '커피랑 같이 먹지 않기',
            ),
            _buildSupplementItem(
              time: '12:30',
              name: '오메가3',
              condition: '식후',
              isDone: true,
            ),
            _buildSupplementItem(
              time: '21:00',
              name: '마그네슘',
              condition: '취침 전',
              isDone: false,
            ),
          ],
        ),
      ),
      // 5. 영양제 추가 버튼
      floatingActionButton: FloatingActionButton(
        onPressed: () {}, // 7단계에서 구현 예정
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
    String? memo,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[200]!),
      ),
      child: Row(
        children: [
          // 시간 표시
          SizedBox(
            width: 50,
            child: Text(
              time,
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
            ),
          ),
          const SizedBox(width: 12),
          // 영양제 정보
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
          // 체크 아이콘 (임시)
          Icon(
            isDone ? Icons.check_circle : Icons.radio_button_unchecked,
            color: isDone ? Colors.blue : Colors.grey[400],
            size: 28,
          ),
        ],
      ),
    );
  }
}
