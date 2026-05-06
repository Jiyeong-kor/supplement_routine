import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/utils/time_utils.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mealTimeSettings = ref.watch(mealTimeSettingsProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('설정')),
      body: ListView(
        children: [
          _buildSectionTitle('기본 설정'),
          ListTile(
            leading: const Icon(Icons.restaurant),
            title: const Text('식사 시간 설정'),
            subtitle: Text(
              '아침 ${mealTimeSettings.breakfastTime.to24hString()} · '
              '점심 ${mealTimeSettings.lunchTime.to24hString()} · '
              '저녁 ${mealTimeSettings.dinnerTime.to24hString()}',
            ),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              _showMealTimeSheet(context);
            },
          ),
          ListTile(
            leading: const Icon(Icons.notifications),
            title: const Text('알림 설정'),
            subtitle: const Text('푸시 알림 및 소리를 설정합니다'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {},
          ),
          const Divider(),
          _buildSectionTitle('데이터 관리'),
          ListTile(
            leading: const Icon(Icons.delete_forever, color: Colors.red),
            title: const Text('데이터 초기화', style: TextStyle(color: Colors.red)),
            onTap: () {
              _showResetDialog(context);
            },
          ),
          const Divider(),
          _buildSectionTitle('정보'),
          ListTile(
            leading: const Icon(Icons.info_outline),
            title: const Text('면책 고지'),
            onTap: () {
              _showDisclaimerDialog(context);
            },
          ),
          const ListTile(title: Text('앱 버전'), trailing: Text('1.0.0')),
        ],
      ),
    );
  }

  void _showMealTimeSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      showDragHandle: true,
      builder: (context) {
        return Consumer(
          builder: (context, ref, child) {
            final currentSettings = ref.watch(mealTimeSettingsProvider);

            return SafeArea(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const ListTile(
                      title: Text(
                        '식사 시간 설정',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                      subtitle: Text('오늘 일정 계산에 사용할 기본 식사 시간입니다'),
                    ),
                    _MealTimeTile(
                      title: '아침',
                      time: currentSettings.breakfastTime,
                      onTap: () async {
                        final picked = await _pickTime(
                          context,
                          currentSettings.breakfastTime,
                        );
                        if (picked != null) {
                          ref
                              .read(mealTimeSettingsProvider.notifier)
                              .updateBreakfastTime(picked);
                        }
                      },
                    ),
                    _MealTimeTile(
                      title: '점심',
                      time: currentSettings.lunchTime,
                      onTap: () async {
                        final picked = await _pickTime(
                          context,
                          currentSettings.lunchTime,
                        );
                        if (picked != null) {
                          ref
                              .read(mealTimeSettingsProvider.notifier)
                              .updateLunchTime(picked);
                        }
                      },
                    ),
                    _MealTimeTile(
                      title: '저녁',
                      time: currentSettings.dinnerTime,
                      onTap: () async {
                        final picked = await _pickTime(
                          context,
                          currentSettings.dinnerTime,
                        );
                        if (picked != null) {
                          ref
                              .read(mealTimeSettingsProvider.notifier)
                              .updateDinnerTime(picked);
                        }
                      },
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }

  Future<TimeOfDay?> _pickTime(BuildContext context, TimeOfDay initialTime) {
    return showTimePicker(context: context, initialTime: initialTime);
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
      child: Text(
        title,
        style: const TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.bold,
          color: Colors.blue,
        ),
      ),
    );
  }

  void _showResetDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('데이터 초기화'),
        content: const Text('모든 영양제 정보와 복용 기록이 삭제됩니다. 정말 초기화하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('초기화', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  void _showDisclaimerDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('면책 고지'),
        content: const SingleChildScrollView(
          child: Text(
            '본 앱은 사용자가 입력한 정보를 기반으로 복용 일정을 관리해주는 도구일 뿐, 의료적 조언이나 진단을 제공하지 않습니다.\n\n'
            '영양제 복용에 관한 결정은 전문의와 상담하시기 바랍니다. 앱에서 제공하는 정보의 오류나 누락으로 인한 책임은 사용자에게 있습니다.',
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('확인'),
          ),
        ],
      ),
    );
  }
}

class _MealTimeTile extends StatelessWidget {
  const _MealTimeTile({
    required this.title,
    required this.time,
    required this.onTap,
  });

  final String title;
  final TimeOfDay time;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(title),
      trailing: Text(
        time.to24hString(),
        style: const TextStyle(fontWeight: FontWeight.bold),
      ),
      onTap: onTap,
    );
  }
}
