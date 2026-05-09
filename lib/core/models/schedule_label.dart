import 'package:supplement_routine/core/models/supplement.dart';

enum ScheduleLabelType { fixedTime, interval, routineSlot }

class ScheduleLabel {
  const ScheduleLabel._({required this.type, this.slot});

  const ScheduleLabel.fixedTime() : this._(type: ScheduleLabelType.fixedTime);

  const ScheduleLabel.interval() : this._(type: ScheduleLabelType.interval);

  const ScheduleLabel.routineSlot(IntakeSlot slot)
    : this._(type: ScheduleLabelType.routineSlot, slot: slot);

  final ScheduleLabelType type;
  final IntakeSlot? slot;
}
