import 'package:flutter/material.dart';

class HistoryScreen extends StatelessWidget {
  const HistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 더미 기록 데이터
    final historyData = [
      {'date': '5월 19일 일요일', 'completion': 1.0, 'total': 3, 'done': 3},
      {'date': '5월 18일 토요일', 'completion': 0.66, 'total': 3, 'done': 2},
      {'date': '5월 17일 금요일', 'completion': 1.0, 'total': 3, 'done': 3},
      {'date': '5월 16일 목요일', 'completion': 0.33, 'total': 3, 'done': 1},
    ];

    return Scaffold(
      appBar: AppBar(
        title: const Text('기록'),
      ),
      body: ListView.separated(
        padding: const EdgeInsets.all(16),
        itemCount: historyData.length,
        separatorBuilder: (context, index) => const SizedBox(height: 12),
        itemBuilder: (context, index) {
          final item = historyData[index];
          final done = item['done'] as int;
          final total = item['total'] as int;
          final completion = item['completion'] as double;

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
                          item['date'] as String,
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
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
        },
      ),
    );
  }
}
