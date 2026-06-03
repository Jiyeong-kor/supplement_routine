package com.jiyeong.supplementroutine.shared.data

import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings

data class MealTimeSettingsDto(
    val breakfastTime: TimeOfDayDto,
    val lunchTime: TimeOfDayDto,
    val dinnerTime: TimeOfDayDto,
)

fun MealTimeSettingsDto.toDomain(): MealTimeSettings {
    return MealTimeSettings(
        breakfastTime = breakfastTime.toDomain(),
        lunchTime = lunchTime.toDomain(),
        dinnerTime = dinnerTime.toDomain(),
    )
}

fun MealTimeSettings.toDto(): MealTimeSettingsDto {
    return MealTimeSettingsDto(
        breakfastTime = breakfastTime.toDto(),
        lunchTime = lunchTime.toDto(),
        dinnerTime = dinnerTime.toDto(),
    )
}
