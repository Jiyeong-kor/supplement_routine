package com.jiyeong.supplementroutine.kmp.android.ui.common

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.ScheduleLabel
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

fun formatTime(time: TimeOfDayValue): String {
    return "${if (time.hour < 12) "오전" else "오후"} ${formatHourMinute(time)}"
}

fun formatHourMinute(time: TimeOfDayValue): String {
    val displayHour = when {
        time.hour == 0 -> 12
        time.hour > 12 -> time.hour - 12
        else -> time.hour
    }
    return "$displayHour:${time.minute.toString().padStart(2, '0')}"
}

fun formatDosage(value: Double): String {
    return if (value == value.toInt().toDouble()) {
        value.toInt().toString()
    } else {
        value.toString()
    }
}

fun methodLabelText(method: IntakeMethod): String {
    return when (method) {
        IntakeMethod.FixedTime -> "정해진 시간"
        IntakeMethod.MealBased -> "식사 기준"
        IntakeMethod.Interval -> "일정 간격"
    }
}

fun scheduleLabelText(label: ScheduleLabel): String {
    return when (label) {
        ScheduleLabel.FixedTime -> "정해진 시간"
        ScheduleLabel.Interval -> "일정 간격"
        is ScheduleLabel.RoutineSlot -> slotLabelText(label.slot)
    }
}

fun slotLabelText(slot: IntakeSlot): String {
    return when (slot.condition) {
        IntakeCondition.BeforeMeal -> "${mealText(slot.mealType)} 전"
        IntakeCondition.AfterMeal -> "${mealText(slot.mealType)} 후"
        IntakeCondition.BetweenMeals -> {
            if (slot.mealType == MealType.Breakfast) "아침-점심 사이" else "점심-저녁 사이"
        }
        IntakeCondition.Fasting -> "공복"
        IntakeCondition.BeforeSleep -> "취침 전"
        IntakeCondition.None -> "복용"
    }
}

fun mealText(mealType: MealType?): String {
    return when (mealType) {
        MealType.Breakfast -> "아침"
        MealType.Lunch -> "점심"
        MealType.Dinner, null -> "저녁"
    }
}
