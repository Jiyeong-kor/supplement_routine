package com.jiyeong.supplementroutine.kmp.android.ui.common

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

fun sampleSupplements(): List<Supplement> {
    return listOf(
        Supplement(
            id = "vitamin_d",
            name = "비타민 D",
            dailyCount = 1,
            method = IntakeMethod.MealBased,
            selectedSlots = listOf(
                IntakeSlot(mealType = MealType.Breakfast, condition = IntakeCondition.AfterMeal),
            ),
            dosageUnit = "정",
            dosageValue = 1.0,
            isNotificationEnabled = true,
            memo = "아침 식사 후 물과 함께",
        ),
        Supplement(
            id = "omega_3",
            name = "오메가3",
            dailyCount = 2,
            method = IntakeMethod.FixedTime,
            fixedTimes = listOf(
                TimeOfDayValue(hour = 9, minute = 30),
                TimeOfDayValue(hour = 20, minute = 0),
            ),
            dosageUnit = "캡슐",
            dosageValue = 1.0,
            isNotificationEnabled = true,
        ),
        Supplement(
            id = "magnesium",
            name = "마그네슘",
            dailyCount = 1,
            method = IntakeMethod.MealBased,
            selectedSlots = listOf(
                IntakeSlot(mealType = null, condition = IntakeCondition.BeforeSleep),
            ),
            dosageUnit = "정",
            dosageValue = 1.0,
            isNotificationEnabled = false,
            memo = "잠들기 전 체크",
        ),
    )
}
