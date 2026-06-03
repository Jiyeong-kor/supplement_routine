package com.jiyeong.supplementroutine.shared.domain

data class MealTimeSettings(
    val breakfastTime: TimeOfDayValue = TimeOfDayValue(hour = 8, minute = 0),
    val lunchTime: TimeOfDayValue = TimeOfDayValue(hour = 12, minute = 0),
    val dinnerTime: TimeOfDayValue = TimeOfDayValue(hour = 18, minute = 0),
)
