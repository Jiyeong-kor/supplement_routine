import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/intake_condition.dart';
import 'package:supplement_routine/core/models/meal_type.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class SupplementAddScreen extends ConsumerStatefulWidget {
  const SupplementAddScreen({super.key});

  @override
  ConsumerState<SupplementAddScreen> createState() =>
      _SupplementAddScreenState();
}

class _SupplementAddScreenState extends ConsumerState<SupplementAddScreen> {
  final _nameController = TextEditingController();
  final _memoController = TextEditingController();
  final _dosageValueController = TextEditingController(text: '1');

  String _unit = '개';

  // 큰 분류: 루틴 기준(true) vs 직접 시간 지정(false)
  bool _isRoutineBased = true;

  // 직접 시간 지정 내부 분류: 특정 시각(true) vs 일정 간격(false)
  bool _isSpecificTime = true;

  final Set<IntakeSlot> _selectedSlots = {};

  int _fixedCount = 1;
  List<TimeOfDay> _fixedTimes = [const TimeOfDay(hour: 8, minute: 0)];

  TimeOfDay _startTime = const TimeOfDay(hour: 8, minute: 0);
  int _intervalHours = 8;
  int _intervalCount = 1;

  bool _isNotificationEnabled = true;

  @override
  void initState() {
    super.initState();
    _isNotificationEnabled = ref.read(notificationSettingsProvider);
  }

  @override
  void dispose() {
    _nameController.dispose();
    _memoController.dispose();
    _dosageValueController.dispose();
    super.dispose();
  }

  final List<IntakeSlot> _allSlots = [
    const IntakeSlot(condition: IntakeCondition.fasting),
    const IntakeSlot(
      mealType: MealType.breakfast,
      condition: IntakeCondition.beforeMeal,
    ),
    const IntakeSlot(
      mealType: MealType.breakfast,
      condition: IntakeCondition.afterMeal,
    ),
    const IntakeSlot(
      mealType: MealType.breakfast,
      condition: IntakeCondition.betweenMeals,
    ),
    const IntakeSlot(
      mealType: MealType.lunch,
      condition: IntakeCondition.beforeMeal,
    ),
    const IntakeSlot(
      mealType: MealType.lunch,
      condition: IntakeCondition.afterMeal,
    ),
    const IntakeSlot(
      mealType: MealType.lunch,
      condition: IntakeCondition.betweenMeals,
    ),
    const IntakeSlot(
      mealType: MealType.dinner,
      condition: IntakeCondition.beforeMeal,
    ),
    const IntakeSlot(
      mealType: MealType.dinner,
      condition: IntakeCondition.afterMeal,
    ),
    const IntakeSlot(condition: IntakeCondition.beforeSleep),
  ];

  void _saveSupplement() {
    final name = _nameController.text.trim();
    if (name.isEmpty) {
      _showSnackBar('영양제 이름을 입력해주세요.');
      return;
    }

    final dosageValue = double.tryParse(_dosageValueController.text.trim());
    if (dosageValue == null || dosageValue <= 0) {
      _showSnackBar('1회 복용량은 0보다 큰 숫자로 입력해주세요.');
      return;
    }

    IntakeMethod method;
    int dailyCount;

    if (_isRoutineBased) {
      method = IntakeMethod.mealBased;
      dailyCount = _selectedSlots.length;
      if (dailyCount == 0) {
        _showSnackBar('복용 시간대를 하나 이상 선택해주세요.');
        return;
      }
    } else {
      if (_isSpecificTime) {
        method = IntakeMethod.fixedTime;
        dailyCount = _fixedCount;
      } else {
        method = IntakeMethod.interval;
        dailyCount = _intervalCount;
      }
    }

    final newSupplement = Supplement(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      name: name,
      dailyCount: dailyCount,
      method: method,
      dosageUnit: _unit,
      dosageValue: dosageValue,
      selectedSlots: method == IntakeMethod.mealBased
          ? _selectedSlots.toList()
          : null,
      fixedTimes: method == IntakeMethod.fixedTime ? _fixedTimes : null,
      startTime: method == IntakeMethod.interval ? _startTime : null,
      intervalHours: method == IntakeMethod.interval ? _intervalHours : null,
      isNotificationEnabled: _isNotificationEnabled,
      memo: _memoController.text.trim().isEmpty
          ? null
          : _memoController.text.trim(),
    );

    ref.read(supplementListProvider.notifier).addSupplement(newSupplement);
    Navigator.pop(context);
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(message)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: const Text(
          '영양제 등록',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSectionTitle('영양제 이름'),
            TextField(
              controller: _nameController,
              decoration: InputDecoration(
                hintText: '예: 비타민 D, 오메가3',
                filled: true,
                fillColor: Colors.grey[100],
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
            const SizedBox(height: 24),

            Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _buildSectionTitle('1회 복용량'),
                      TextField(
                        controller: _dosageValueController,
                        keyboardType: const TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        textAlign: TextAlign.center,
                        decoration: InputDecoration(
                          filled: true,
                          fillColor: Colors.grey[100],
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: BorderSide.none,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _buildSectionTitle('단위'),
                      DropdownButtonFormField<String>(
                        initialValue: _unit,
                        decoration: InputDecoration(
                          filled: true,
                          fillColor: Colors.grey[100],
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: BorderSide.none,
                          ),
                        ),
                        items: ['개', 'ml']
                            .map(
                              (u) => DropdownMenuItem(value: u, child: Text(u)),
                            )
                            .toList(),
                        onChanged: (v) => setState(() => _unit = v!),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),

            _buildSectionTitle('복용 방식 선택'),
            _buildTopMethodSelector(),
            const SizedBox(height: 24),

            _buildDetailSection(),

            const SizedBox(height: 24),
            _buildSectionTitle('기타 설정'),
            SwitchListTile(
              title: const Text('복용 알림 받기'),
              value: _isNotificationEnabled,
              contentPadding: EdgeInsets.zero,
              onChanged: (v) => setState(() => _isNotificationEnabled = v),
            ),
            const SizedBox(height: 12),
            _buildSectionTitle('메모 (선택)'),
            TextField(
              controller: _memoController,
              maxLines: 2,
              decoration: InputDecoration(
                hintText: '주의사항 등을 적어주세요',
                filled: true,
                fillColor: Colors.grey[100],
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
            const SizedBox(height: 40),

            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: _saveSupplement,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                  elevation: 0,
                ),
                child: const Text(
                  '등록 완료',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8.0),
      child: Text(
        title,
        style: const TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.bold,
          color: Colors.blueGrey,
        ),
      ),
    );
  }

  Widget _buildTopMethodSelector() {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          _buildTopMethodItem(
            '식사/생활 루틴',
            _isRoutineBased,
            () => setState(() => _isRoutineBased = true),
          ),
          _buildTopMethodItem(
            '직접 시간 지정',
            !_isRoutineBased,
            () => setState(() => _isRoutineBased = false),
          ),
        ],
      ),
    );
  }

  Widget _buildTopMethodItem(
    String title,
    bool isSelected,
    VoidCallback onTap,
  ) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 12),
          decoration: BoxDecoration(
            color: isSelected ? Colors.blue : Colors.transparent,
            borderRadius: BorderRadius.circular(12),
          ),
          child: Text(
            title,
            textAlign: TextAlign.center,
            style: TextStyle(
              color: isSelected ? Colors.white : Colors.grey[600],
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDetailSection() {
    if (_isRoutineBased) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildSectionTitle('언제 복용하시나요? (다중 선택)'),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: _allSlots.map((slot) {
              final isSelected = _selectedSlots.contains(slot);
              return FilterChip(
                label: Text(slot.label),
                selected: isSelected,
                onSelected: (v) {
                  setState(() {
                    if (v) {
                      _selectedSlots.add(slot);
                    } else {
                      _selectedSlots.remove(slot);
                    }
                  });
                },
              );
            }).toList(),
          ),
        ],
      );
    } else {
      return Column(
        children: [
          // 시간 지정 내의 서브 탭
          Container(
            padding: const EdgeInsets.all(4),
            decoration: BoxDecoration(
              color: Colors.grey[100],
              borderRadius: BorderRadius.circular(10),
            ),
            child: Row(
              children: [
                _buildSubMethodItem(
                  '특정 시각 지정',
                  _isSpecificTime,
                  () => setState(() => _isSpecificTime = true),
                ),
                _buildSubMethodItem(
                  '일정 간격 반복',
                  !_isSpecificTime,
                  () => setState(() => _isSpecificTime = false),
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),
          _isSpecificTime ? _buildFixedTimeUI() : _buildIntervalUI(),
        ],
      );
    }
  }

  Widget _buildSubMethodItem(
    String title,
    bool isSelected,
    VoidCallback onTap,
  ) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: isSelected ? Colors.white : Colors.transparent,
            borderRadius: BorderRadius.circular(8),
            boxShadow: isSelected
                ? [
                    BoxShadow(
                      color: Colors.black.withValues(alpha: 0.05),
                      blurRadius: 4,
                    ),
                  ]
                : [],
          ),
          child: Text(
            title,
            textAlign: TextAlign.center,
            style: TextStyle(
              color: isSelected ? Colors.black : Colors.grey[500],
              fontSize: 13,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildFixedTimeUI() {
    return Column(
      children: [
        _buildCounterRow(
          '하루 복용 횟수',
          _fixedCount,
          (v) => setState(() {
            _fixedCount = v;
            if (_fixedTimes.length < v) {
              _fixedTimes.add(_fixedTimes.last);
            } else if (_fixedTimes.length > v) {
              _fixedTimes = _fixedTimes.sublist(0, v);
            }
          }),
        ),
        const SizedBox(height: 12),
        ...List.generate(
          _fixedCount,
          (index) => Padding(
            padding: const EdgeInsets.only(bottom: 8.0),
            child: ListTile(
              title: Text('복용 시각 ${index + 1}'),
              trailing: Text(
                _fixedTimes[index].format(context),
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              onTap: () async {
                final picked = await showTimePicker(
                  context: context,
                  initialTime: _fixedTimes[index],
                );
                if (picked != null) {
                  setState(() => _fixedTimes[index] = picked);
                }
              },
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              tileColor: Colors.blue[50],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildIntervalUI() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[200]!),
      ),
      child: Column(
        children: [
          _buildCounterRow(
            '하루 복용 횟수',
            _intervalCount,
            (v) => setState(() => _intervalCount = v),
          ),
          const Divider(height: 24),
          ListTile(
            title: const Text('첫 복용 시작 시각', style: TextStyle(fontSize: 15)),
            trailing: Text(
              _startTime.format(context),
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            onTap: () async {
              final picked = await showTimePicker(
                context: context,
                initialTime: _startTime,
              );
              if (picked != null) {
                setState(() => _startTime = picked);
              }
            },
            contentPadding: EdgeInsets.zero,
          ),
          const Divider(height: 24),
          _buildCounterRow(
            '반복 간격(시간)',
            _intervalHours,
            (v) => setState(() => _intervalHours = v),
            min: 1,
            max: 24,
          ),
          const SizedBox(height: 12),
          const Text(
            '※ 오늘 자정 전까지만 일정이 생성됩니다.',
            style: TextStyle(fontSize: 12, color: Colors.grey),
          ),
        ],
      ),
    );
  }

  Widget _buildCounterRow(
    String title,
    int value,
    ValueChanged<int> onChanged, {
    int min = 1,
    int max = 10,
  }) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          title,
          style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w500),
        ),
        Row(
          children: [
            IconButton(
              onPressed: value > min ? () => onChanged(value - 1) : null,
              icon: const Icon(Icons.remove_circle_outline, color: Colors.blue),
            ),
            Container(
              constraints: const BoxConstraints(minWidth: 30),
              child: Text(
                '$value',
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            IconButton(
              onPressed: value < max ? () => onChanged(value + 1) : null,
              icon: const Icon(Icons.add_circle_outline, color: Colors.blue),
            ),
          ],
        ),
      ],
    );
  }
}
