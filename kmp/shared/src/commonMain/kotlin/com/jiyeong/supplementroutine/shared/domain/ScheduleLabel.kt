package com.jiyeong.supplementroutine.shared.domain

sealed interface ScheduleLabel {
    data object FixedTime : ScheduleLabel
    data object Interval : ScheduleLabel
    data class RoutineSlot(val slot: IntakeSlot) : ScheduleLabel
}
