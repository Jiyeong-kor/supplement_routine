import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/app_config.dart';
import 'package:supplement_routine/core/utils/time_utils.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final mealTimeSettings = ref.watch(mealTimeSettingsProvider);
    final isNotificationEnabled = ref.watch(notificationSettingsProvider);
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(title: Text(l10n.settingsTitle)),
      body: ListView(
        children: [
          _SettingsSectionTitle(l10n.settingsDefaultSection),
          ListTile(
            leading: Icon(Icons.restaurant, color: colorScheme.primary),
            title: Text(l10n.settingsMealTimeTitle),
            subtitle: Text(
              l10n.settingsMealTimeSummary(
                mealTimeSettings.breakfastTime.to24hString(),
                mealTimeSettings.lunchTime.to24hString(),
                mealTimeSettings.dinnerTime.to24hString(),
              ),
            ),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              _showMealTimeSheet(context);
            },
          ),
          SwitchListTile(
            secondary: Icon(Icons.notifications, color: colorScheme.primary),
            title: Text(l10n.settingsNotificationTitle),
            subtitle: Text(
              isNotificationEnabled
                  ? l10n.settingsNotificationOn
                  : l10n.settingsNotificationOff,
            ),
            value: isNotificationEnabled,
            onChanged: (value) {
              ref
                  .read(notificationSettingsProvider.notifier)
                  .updateEnabled(value);
            },
          ),
          const Divider(),
          _SettingsSectionTitle(l10n.settingsDataSection),
          ListTile(
            leading: Icon(Icons.delete_forever, color: colorScheme.error),
            title: Text(
              l10n.settingsResetTitle,
              style: TextStyle(color: colorScheme.error),
            ),
            onTap: () {
              _showResetDialog(context, ref);
            },
          ),
          const Divider(),
          _SettingsSectionTitle(l10n.settingsInfoSection),
          ListTile(
            leading: Icon(Icons.help_outline, color: colorScheme.primary),
            title: Text(l10n.settingsUsageGuideTitle),
            onTap: () {
              _showUsageGuideDialog(context);
            },
          ),
          ListTile(
            leading: Icon(Icons.info_outline, color: colorScheme.primary),
            title: Text(l10n.settingsDisclaimerTitle),
            onTap: () {
              _showDisclaimerDialog(context);
            },
          ),
          ListTile(
            title: Text(l10n.settingsVersionTitle),
            trailing: Text(
              AppConfig.appVersion,
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
        return _MealTimeSheetContent(pickTime: _pickTime);
      },
    );
  }

  Future<TimeOfDay?> _pickTime(BuildContext context, TimeOfDay initialTime) {
    return showTimePicker(context: context, initialTime: initialTime);
  }

  void _showUsageGuideDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(AppLocalizations.of(context).settingsUsageGuideTitle),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _GuideItem(
                title: AppLocalizations.of(context).usageGuideStepRegisterTitle,
                description: AppLocalizations.of(
                  context,
                ).usageGuideStepRegisterDescription,
              ),
              _GuideItem(
                title: AppLocalizations.of(context).usageGuideStepTodayTitle,
                description: AppLocalizations.of(
                  context,
                ).usageGuideStepTodayDescription,
              ),
              _GuideItem(
                title: AppLocalizations.of(context).usageGuideStepCheckTitle,
                description: AppLocalizations.of(
                  context,
                ).usageGuideStepCheckDescription,
              ),
              _GuideItem(
                title: AppLocalizations.of(context).usageGuideStepHistoryTitle,
                description: AppLocalizations.of(
                  context,
                ).usageGuideStepHistoryDescription,
              ),
              const SizedBox(height: 8),
              Text(AppLocalizations.of(context).usageGuideDisclaimer),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(AppLocalizations.of(context).confirm),
          ),
        ],
      ),
    );
  }

  void _showResetDialog(BuildContext context, WidgetRef ref) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(AppLocalizations.of(context).settingsResetTitle),
        content: Text(AppLocalizations.of(context).settingsResetMessage),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(AppLocalizations.of(context).cancel),
          ),
          TextButton(
            onPressed: () {
              ref.read(supplementListProvider.notifier).clearSupplements();
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(AppLocalizations.of(context).settingsResetDone),
                ),
              );
            },
            child: Text(
              AppLocalizations.of(context).settingsResetConfirm,
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
        title: Text(AppLocalizations.of(context).settingsDisclaimerTitle),
        content: SingleChildScrollView(
          child: Text(AppLocalizations.of(context).disclaimerBody),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(AppLocalizations.of(context).confirm),
          ),
        ],
      ),
    );
  }
}

class _SettingsSectionTitle extends StatelessWidget {
  const _SettingsSectionTitle(this.title);

  final String title;

  @override
  Widget build(BuildContext context) {
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
}

class _MealTimeSheetContent extends ConsumerWidget {
  const _MealTimeSheetContent({required this.pickTime});

  final Future<TimeOfDay?> Function(BuildContext context, TimeOfDay initialTime)
  pickTime;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final currentSettings = ref.watch(mealTimeSettingsProvider);
    final colorScheme = Theme.of(context).colorScheme;

    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: Text(
                l10n.settingsMealTimeTitle,
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              subtitle: Text(
                l10n.settingsMealTimeDescription,
                style: TextStyle(color: colorScheme.onSurfaceVariant),
              ),
            ),
            _MealTimeTile(
              title: l10n.mealBreakfast,
              time: currentSettings.breakfastTime,
              onTap: () async {
                final picked = await pickTime(
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
              title: l10n.mealLunch,
              time: currentSettings.lunchTime,
              onTap: () async {
                final picked = await pickTime(
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
              title: l10n.mealDinner,
              time: currentSettings.dinnerTime,
              onTap: () async {
                final picked = await pickTime(
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
