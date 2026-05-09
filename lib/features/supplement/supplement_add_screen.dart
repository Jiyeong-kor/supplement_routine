import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supplement_routine/app/app_radius.dart';
import 'package:supplement_routine/app/app_spacing.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/features/settings/settings_provider.dart';
import 'package:supplement_routine/features/supplement/application/supplement_form_mapper.dart';
import 'package:supplement_routine/features/supplement/application/supplement_form_policy.dart';
import 'package:supplement_routine/features/supplement/presentation/supplement_display_text.dart';
import 'package:supplement_routine/features/supplement/supplement_provider.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

class SupplementAddScreen extends ConsumerStatefulWidget {
  const SupplementAddScreen({super.key, this.initialSupplement});

  final Supplement? initialSupplement;

  @override
  ConsumerState<SupplementAddScreen> createState() =>
      _SupplementAddScreenState();
}

class _SupplementAddScreenState extends ConsumerState<SupplementAddScreen> {
  final _nameController = TextEditingController();
  final _memoController = TextEditingController();
  final _dosageValueController = TextEditingController(
    text: SupplementFormPolicy.defaultDosageText,
  );

  String _unit = SupplementFormPolicy.defaultUnit;

  bool _isRoutineBased = true;

  bool _isSpecificTime = true;

  final Set<IntakeSlot> _selectedSlots = {};

  int _fixedCount = SupplementFormPolicy.defaultCount;
  List<TimeOfDay> _fixedTimes = [SupplementFormPolicy.defaultTime];

  TimeOfDay _startTime = SupplementFormPolicy.defaultTime;
  int _intervalHours = SupplementFormPolicy.defaultIntervalHours;
  int _intervalCount = SupplementFormPolicy.defaultCount;

  bool _isNotificationEnabled = true;

  bool get _isEditMode => widget.initialSupplement != null;

  @override
  void initState() {
    super.initState();
    final initialSupplement = widget.initialSupplement;
    if (initialSupplement == null) {
      _isNotificationEnabled = ref.read(notificationSettingsProvider);
      return;
    }

    _nameController.text = initialSupplement.name;
    _memoController.text = initialSupplement.memo ?? '';
    _dosageValueController.text = SupplementFormPolicy.formatDosage(
      initialSupplement.dosageValue,
    );
    _unit = initialSupplement.dosageUnit;
    _isNotificationEnabled = initialSupplement.isNotificationEnabled;
    _isRoutineBased = initialSupplement.method == IntakeMethod.mealBased;
    _isSpecificTime = initialSupplement.method != IntakeMethod.interval;
    _selectedSlots.addAll(initialSupplement.selectedSlots ?? []);
    _fixedTimes = initialSupplement.fixedTimes ?? _fixedTimes;
    _fixedCount = _fixedTimes.length;
    _startTime = initialSupplement.startTime ?? _startTime;
    _intervalHours = initialSupplement.intervalHours ?? _intervalHours;
    _intervalCount = initialSupplement.method == IntakeMethod.interval
        ? initialSupplement.dailyCount
        : _intervalCount;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _memoController.dispose();
    _dosageValueController.dispose();
    super.dispose();
  }

  void _saveSupplement() {
    final l10n = AppLocalizations.of(context);
    final name = _nameController.text.trim();
    final nameError = SupplementFormPolicy.validateName(name);
    if (nameError != null) {
      _showSnackBar(_messageForValidationError(l10n, nameError));
      return;
    }

    final dosageValue = SupplementFormPolicy.parseDosage(
      _dosageValueController.text,
    );
    if (dosageValue == null) {
      _showSnackBar(
        _messageForValidationError(
          l10n,
          SupplementFormValidationError.invalidDosage,
        ),
      );
      return;
    }

    if (_isRoutineBased) {
      final routineError = SupplementFormPolicy.validateRoutineSlots(
        _selectedSlots,
      );
      if (routineError != null) {
        _showSnackBar(_messageForValidationError(l10n, routineError));
        return;
      }
    }

    final formInput = SupplementFormInput(
      name: name,
      dosageUnit: _unit,
      dosageValue: dosageValue,
      isRoutineBased: _isRoutineBased,
      isSpecificTime: _isSpecificTime,
      selectedSlots: _selectedSlots,
      fixedCount: _fixedCount,
      fixedTimes: _fixedTimes,
      startTime: _startTime,
      intervalHours: _intervalHours,
      intervalCount: _intervalCount,
      isNotificationEnabled: _isNotificationEnabled,
      memo: _memoController.text.trim().isEmpty
          ? null
          : _memoController.text.trim(),
    );
    final supplement = SupplementFormMapper.toSupplement(
      input: formInput,
      initialSupplement: widget.initialSupplement,
    );

    if (_isEditMode) {
      ref.read(supplementListProvider.notifier).updateSupplement(supplement);
    } else {
      ref.read(supplementListProvider.notifier).addSupplement(supplement);
    }
    Navigator.pop(context);
  }

  String _messageForValidationError(
    AppLocalizations l10n,
    SupplementFormValidationError error,
  ) {
    return switch (error) {
      SupplementFormValidationError.emptyName => l10n.supplementNameRequired,
      SupplementFormValidationError.invalidDosage =>
        l10n.supplementDosageInvalid,
      SupplementFormValidationError.emptyRoutineSlots =>
        l10n.supplementTimingRequired,
    };
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(message)));
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final textTheme = Theme.of(context).textTheme;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          _isEditMode ? l10n.supplementEditTitle : l10n.supplementAddTitle,
          style: textTheme.titleLarge,
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(AppSpacing.xl),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _SectionTitle(l10n.supplementNameSection),
            _SupplementTextField(
              controller: _nameController,
              hintText: l10n.supplementNameHint,
            ),
            const SizedBox(height: AppSpacing.xxl),

            Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _SectionTitle(l10n.supplementDosageSection),
                      _SupplementTextField(
                        controller: _dosageValueController,
                        keyboardType: const TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        textAlign: TextAlign.center,
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: AppSpacing.md),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _SectionTitle(l10n.supplementUnitSection),
                      DropdownButtonFormField<String>(
                        initialValue: _unit,
                        decoration: InputDecoration(
                          filled: true,
                          border: OutlineInputBorder(
                            borderRadius: AppRadius.mdBorder,
                            borderSide: BorderSide.none,
                          ),
                        ),
                        items: SupplementFormPolicy.dosageUnits
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
            const SizedBox(height: AppSpacing.xxl),

            _SectionTitle(l10n.supplementMethodSection),
            _buildTopMethodSelector(),
            const SizedBox(height: AppSpacing.xxl),

            _buildDetailSection(),

            const SizedBox(height: AppSpacing.xxl),
            _SectionTitle(l10n.supplementOtherSettingsSection),
            SwitchListTile(
              title: Text(l10n.supplementNotificationSwitch),
              value: _isNotificationEnabled,
              contentPadding: EdgeInsets.zero,
              onChanged: (v) => setState(() => _isNotificationEnabled = v),
            ),
            const SizedBox(height: AppSpacing.md),
            _SectionTitle(l10n.supplementMemoSection),
            _SupplementTextField(
              controller: _memoController,
              maxLines: 2,
              hintText: l10n.supplementMemoHint,
            ),
            const SizedBox(height: AppSpacing.xxxl),

            _SaveSupplementButton(
              label: _isEditMode
                  ? l10n.supplementEditDone
                  : l10n.supplementAddDone,
              onPressed: _saveSupplement,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTopMethodSelector() {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;

    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerHigh,
        borderRadius: AppRadius.mdBorder,
      ),
      child: Row(
        children: [
          _buildTopMethodItem(
            l10n.supplementRoutineMethod,
            _isRoutineBased,
            () => setState(() => _isRoutineBased = true),
          ),
          _buildTopMethodItem(
            l10n.supplementManualTimeMethod,
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
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: AppSpacing.md),
          decoration: BoxDecoration(
            color: isSelected ? colorScheme.primary : Colors.transparent,
            borderRadius: AppRadius.mdBorder,
          ),
          child: Text(
            title,
            textAlign: TextAlign.center,
            style: textTheme.labelLarge?.copyWith(
              color: isSelected
                  ? colorScheme.onPrimary
                  : colorScheme.onSurfaceVariant,
              fontWeight: FontWeight.w700,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDetailSection() {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;

    if (_isRoutineBased) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _SectionTitle(l10n.supplementTimingSection),
          Wrap(
            spacing: AppSpacing.sm,
            runSpacing: AppSpacing.sm,
            children: SupplementFormPolicy.routineSlots.map((slot) {
              final isSelected = _selectedSlots.contains(slot);
              return FilterChip(
                label: Text(SupplementDisplayText.slotLabel(l10n, slot)),
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
          Container(
            padding: const EdgeInsets.all(AppSpacing.xxs),
            decoration: BoxDecoration(
              color: colorScheme.surfaceContainerHigh,
              borderRadius: AppRadius.mdBorder,
            ),
            child: Row(
              children: [
                _buildSubMethodItem(
                  l10n.supplementSpecificTimeMethod,
                  _isSpecificTime,
                  () => setState(() => _isSpecificTime = true),
                ),
                _buildSubMethodItem(
                  l10n.supplementIntervalMethod,
                  !_isSpecificTime,
                  () => setState(() => _isSpecificTime = false),
                ),
              ],
            ),
          ),
          const SizedBox(height: AppSpacing.xl),
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
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: AppSpacing.sm),
          decoration: BoxDecoration(
            color: isSelected ? colorScheme.surface : Colors.transparent,
            borderRadius: AppRadius.smBorder,
            boxShadow: isSelected
                ? [
                    BoxShadow(
                      color: colorScheme.shadow.withValues(alpha: 0.05),
                      blurRadius: AppSpacing.xxs,
                    ),
                  ]
                : [],
          ),
          child: Text(
            title,
            textAlign: TextAlign.center,
            style: textTheme.labelMedium?.copyWith(
              color: isSelected
                  ? colorScheme.onSurface
                  : colorScheme.onSurfaceVariant,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildFixedTimeUI() {
    final l10n = AppLocalizations.of(context);

    return Column(
      children: [
        _buildCounterRow(
          l10n.supplementDailyCountLabel,
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
        const SizedBox(height: AppSpacing.md),
        ...List.generate(
          _fixedCount,
          (index) => Padding(
            padding: const EdgeInsets.only(bottom: AppSpacing.sm),
            child: ListTile(
              title: Text(l10n.supplementScheduledTimeLabel(index + 1)),
              trailing: Text(
                _fixedTimes[index].format(context),
                style: Theme.of(context).textTheme.labelLarge,
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
              shape: RoundedRectangleBorder(borderRadius: AppRadius.mdBorder),
              tileColor: Theme.of(context).colorScheme.secondaryContainer,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildIntervalUI() {
    final l10n = AppLocalizations.of(context);
    final colorScheme = Theme.of(context).colorScheme;

    return Container(
      padding: AppSpacing.cardPadding,
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLow,
        borderRadius: AppRadius.mdBorder,
        border: Border.all(color: colorScheme.outlineVariant),
      ),
      child: Column(
        children: [
          _buildCounterRow(
            l10n.supplementDailyCountLabel,
            _intervalCount,
            (v) => setState(() => _intervalCount = v),
          ),
          const Divider(height: AppSpacing.xxl),
          ListTile(
            title: Text(
              l10n.supplementStartTimeLabel,
              style: Theme.of(context).textTheme.bodyLarge,
            ),
            trailing: Text(
              _startTime.format(context),
              style: Theme.of(context).textTheme.labelLarge,
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
          const Divider(height: AppSpacing.xxl),
          _buildCounterRow(
            l10n.supplementIntervalHoursLabel,
            _intervalHours,
            (v) => setState(() => _intervalHours = v),
            min: SupplementFormPolicy.minIntervalHours,
            max: SupplementFormPolicy.maxIntervalHours,
          ),
          const SizedBox(height: AppSpacing.md),
          Text(l10n.supplementIntervalNotice),
        ],
      ),
    );
  }

  Widget _buildCounterRow(
    String title,
    int value,
    ValueChanged<int> onChanged, {
    int min = SupplementFormPolicy.minCount,
    int max = SupplementFormPolicy.maxCount,
  }) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          title,
          style: textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.w500),
        ),
        Row(
          children: [
            IconButton(
              onPressed: value > min ? () => onChanged(value - 1) : null,
              icon: Icon(
                Icons.remove_circle_outline,
                color: value > min ? colorScheme.primary : colorScheme.outline,
              ),
            ),
            Container(
              constraints: const BoxConstraints(minWidth: 30),
              child: Text(
                '$value',
                textAlign: TextAlign.center,
                style: textTheme.titleMedium,
              ),
            ),
            IconButton(
              onPressed: value < max ? () => onChanged(value + 1) : null,
              icon: Icon(
                Icons.add_circle_outline,
                color: value < max ? colorScheme.primary : colorScheme.outline,
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class _SectionTitle extends StatelessWidget {
  const _SectionTitle(this.title);

  final String title;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.only(bottom: AppSpacing.sm),
      child: Text(
        title,
        style: Theme.of(context).textTheme.labelLarge?.copyWith(
          fontWeight: FontWeight.w700,
          color: colorScheme.primary,
        ),
      ),
    );
  }
}

class _SupplementTextField extends StatelessWidget {
  const _SupplementTextField({
    required this.controller,
    this.hintText,
    this.keyboardType,
    this.textAlign = TextAlign.start,
    this.maxLines = 1,
  });

  final TextEditingController controller;
  final String? hintText;
  final TextInputType? keyboardType;
  final TextAlign textAlign;
  final int maxLines;

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: controller,
      keyboardType: keyboardType,
      textAlign: textAlign,
      maxLines: maxLines,
      decoration: InputDecoration(hintText: hintText, filled: true),
    );
  }
}

class _SaveSupplementButton extends StatelessWidget {
  const _SaveSupplementButton({required this.label, required this.onPressed});

  final String label;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: double.infinity,
      child: FilledButton(onPressed: onPressed, child: Text(label)),
    );
  }
}
