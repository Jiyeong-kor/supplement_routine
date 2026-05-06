import 'package:flutter/material.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';

class SupplementAddScreen extends StatefulWidget {
  const SupplementAddScreen({super.key});

  @override
  State<SupplementAddScreen> createState() => _SupplementAddScreenState();
}

class _SupplementAddScreenState extends State<SupplementAddScreen> {
  final _nameController = TextEditingController();
  final _memoController = TextEditingController();
  int _dailyCount = 1;
  IntakeMethod _method = IntakeMethod.fixedTime;
  IntakeCondition _condition = IntakeCondition.afterMeal;
  bool _isNotificationEnabled = true;

  @override
  void dispose() {
    _nameController.dispose();
    _memoController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('영양제 등록'),
        actions: [
          TextButton(
            onPressed: () {
              // 8단계에서 저장 로직 구현 예정
              Navigator.pop(context);
            },
            child: const Text('저장', style: TextStyle(fontWeight: FontWeight.bold)),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('영양제 이름', style: TextStyle(fontWeight: FontWeight.bold)),
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(
                hintText: '예: 비타민 D',
                border: UnderlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),
            
            const Text('하루 복용 횟수', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Row(
              children: [1, 2, 3].map((count) {
                return Padding(
                  padding: const EdgeInsets.only(right: 8.0),
                  child: ChoiceChip(
                    label: Text('$count회'),
                    selected: _dailyCount == count,
                    onSelected: (selected) {
                      if (selected) setState(() => _dailyCount = count);
                    },
                  ),
                );
              }).toList(),
            ),
            const SizedBox(height: 24),
            
            const Text('복용 방식', style: TextStyle(fontWeight: FontWeight.bold)),
            DropdownButton<IntakeMethod>(
              isExpanded: true,
              value: _method,
              items: IntakeMethod.values.map((method) {
                return DropdownMenuItem(
                  value: method,
                  child: Text(method.label),
                );
              }).toList(),
              onChanged: (value) {
                if (value != null) setState(() => _method = value);
              },
            ),
            const SizedBox(height: 24),
            
            const Text('복용 조건', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: IntakeCondition.values.map((condition) {
                return ChoiceChip(
                  label: Text(condition.label),
                  selected: _condition == condition,
                  onSelected: (selected) {
                    if (selected) setState(() => _condition = condition);
                  },
                );
              }).toList(),
            ),
            const SizedBox(height: 24),
            
            SwitchListTile(
              title: const Text('알림 받기', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              contentPadding: EdgeInsets.zero,
              value: _isNotificationEnabled,
              activeColor: Colors.blue,
              onChanged: (value) => setState(() => _isNotificationEnabled = value),
            ),
            const SizedBox(height: 12),
            
            const Text('메모', style: TextStyle(fontWeight: FontWeight.bold)),
            TextField(
              controller: _memoController,
              decoration: const InputDecoration(
                hintText: '커피와 함께 복용 금지 등',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
          ],
        ),
      ),
    );
  }
}
