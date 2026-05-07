import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/core/utils/time_utils.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mealTimeSettings = ref.watch(mealTimeSettingsProvider);
    final isNotificationEnabled = ref.watch(notificationSettingsProvider);
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(title: const Text('설정')),
      body: ListView(
        children: [
          _buildSectionTitle(context, '기본 설정'),
          ListTile(
            leading: Icon(Icons.restaurant, color: colorScheme.primary),
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
          SwitchListTile(
            secondary: Icon(Icons.notifications, color: colorScheme.primary),
            title: const Text('알림 설정'),
            subtitle: Text(
              isNotificationEnabled
                  ? '새 영양제 등록 시 알림을 기본으로 켭니다'
                  : '새 영양제 등록 시 알림을 기본으로 끕니다',
            ),
            value: isNotificationEnabled,
            onChanged: (value) {
              ref
                  .read(notificationSettingsProvider.notifier)
                  .updateEnabled(value);
            },
          ),
          const Divider(),
          _buildSectionTitle(context, '데이터 관리'),
          ListTile(
            leading: Icon(Icons.delete_forever, color: colorScheme.error),
            title: Text('데이터 초기화', style: TextStyle(color: colorScheme.error)),
            onTap: () {
              _showResetDialog(context, ref);
            },
          ),
          const Divider(),
          _buildSectionTitle(context, '정보'),
          ListTile(
            leading: Icon(Icons.help_outline, color: colorScheme.primary),
            title: const Text('앱 사용 가이드'),
            onTap: () {
              _showUsageGuideDialog(context);
            },
          ),
          ListTile(
            leading: Icon(Icons.info_outline, color: colorScheme.primary),
            title: const Text('면책 고지'),
            onTap: () {
              _showDisclaimerDialog(context);
            },
          ),
          ListTile(
            title: const Text('앱 버전'),
            trailing: Text(
              '1.0.0',
              style: TextStyle(color: colorScheme.onSurfaceVariant),
            ),
          ),
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
                    ListTile(
                      title: const Text(
                        '식사 시간 설정',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                      subtitle: Text(
                        '오늘 일정 계산에 사용할 기본 식사 시간입니다',
                        style: TextStyle(
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        ),
                      ),
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

  Widget _buildSectionTitle(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
      child: Text(
        title,
        style: Theme.of(context).textTheme.labelLarge?.copyWith(
          fontWeight: FontWeight.bold,
          color: Theme.of(context).colorScheme.primary,
        ),
      ),
    );
  }

  void _showUsageGuideDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('앱 사용 가이드'),
        content: const SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _GuideItem(
                title: '1. 영양제를 등록하세요',
                description: '이름, 복용 방식, 복용 조건, 알림 여부, 메모를 직접 입력합니다.',
              ),
              _GuideItem(
                title: '2. 오늘 화면에서 확인하세요',
                description: '입력한 규칙을 기준으로 오늘의 복용 일정과 진행률을 확인합니다.',
              ),
              _GuideItem(
                title: '3. 복용 후 체크하세요',
                description: '복용을 완료하면 오늘 목록에서 해당 일정을 체크합니다.',
              ),
              _GuideItem(
                title: '4. 기록 화면에서 돌아보세요',
                description: '오늘 완료 개수와 완료율을 확인해 복용 기록을 관리합니다.',
              ),
              SizedBox(height: 8),
              Text('Supplement Routine은 영양제를 추천하거나 의료 조언을 제공하지 않습니다.'),
            ],
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

  void _showResetDialog(BuildContext context, WidgetRef ref) {
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
            onPressed: () {
              ref.read(supplementListProvider.notifier).clearSupplements();
              Navigator.pop(context);
              ScaffoldMessenger.of(
                context,
              ).showSnackBar(const SnackBar(content: Text('데이터가 초기화되었습니다.')));
            },
            child: Text(
              '초기화',
              style: TextStyle(color: Theme.of(context).colorScheme.error),
            ),
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

class _GuideItem extends StatelessWidget {
  const _GuideItem({required this.title, required this.description});

  final String title;
  final String description;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: Theme.of(
              context,
            ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 4),
          Text(
            description,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
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
    final colorScheme = Theme.of(context).colorScheme;

    return ListTile(
      title: Text(title),
      trailing: Text(
        time.to24hString(),
        style: TextStyle(
          color: colorScheme.primary,
          fontWeight: FontWeight.bold,
        ),
      ),
      onTap: onTap,
    );
  }
}
