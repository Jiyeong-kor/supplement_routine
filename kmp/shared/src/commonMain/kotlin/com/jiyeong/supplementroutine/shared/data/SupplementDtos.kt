package com.jiyeong.supplementroutine.shared.data

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement

data class IntakeSlotDto(
    val mealType: String?,
    val condition: String,
)

data class SupplementDto(
    val id: String,
    val name: String,
    val dailyCount: Int,
    val method: String,
    val generalCondition: String = "none",
    val dosageUnit: String,
    val dosageValue: Double,
    val selectedSlots: List<IntakeSlotDto>? = null,
    val fixedTimes: List<TimeOfDayDto>? = null,
    val startTime: TimeOfDayDto? = null,
    val intervalHours: Int? = null,
    val isNotificationEnabled: Boolean,
    val memo: String? = null,
)

fun IntakeSlotDto.toDomain(): IntakeSlot {
    return IntakeSlot(
        mealType = mealType?.toMealType(),
        condition = condition.toIntakeCondition(),
    )
}

fun IntakeSlot.toDto(): IntakeSlotDto {
    return IntakeSlotDto(
        mealType = mealType?.toFlutterName(),
        condition = condition.toFlutterName(),
    )
}

fun SupplementDto.toDomain(): Supplement {
    return Supplement(
        id = id,
        name = name,
        dailyCount = dailyCount,
        method = method.toIntakeMethod(),
        generalCondition = generalCondition.toIntakeCondition(),
        dosageUnit = dosageUnit,
        dosageValue = dosageValue,
        selectedSlots = selectedSlots?.map { it.toDomain() },
        fixedTimes = fixedTimes?.map { it.toDomain() },
        startTime = startTime?.toDomain(),
        intervalHours = intervalHours,
        isNotificationEnabled = isNotificationEnabled,
        memo = memo,
    )
}

fun Supplement.toDto(): SupplementDto {
    return SupplementDto(
        id = id,
        name = name,
        dailyCount = dailyCount,
        method = method.toFlutterName(),
        generalCondition = generalCondition.toFlutterName(),
        dosageUnit = dosageUnit,
        dosageValue = dosageValue,
        selectedSlots = selectedSlots?.map { it.toDto() },
        fixedTimes = fixedTimes?.map { it.toDto() },
        startTime = startTime?.toDto(),
        intervalHours = intervalHours,
        isNotificationEnabled = isNotificationEnabled,
        memo = memo,
    )
}

fun String.toIntakeMethod(): IntakeMethod {
    return when (this) {
        "fixedTime" -> IntakeMethod.FixedTime
        "mealBased" -> IntakeMethod.MealBased
        "interval" -> IntakeMethod.Interval
        else -> error("Unknown intake method: $this")
    }
}

fun IntakeMethod.toFlutterName(): String {
    return when (this) {
        IntakeMethod.FixedTime -> "fixedTime"
        IntakeMethod.MealBased -> "mealBased"
        IntakeMethod.Interval -> "interval"
    }
}

fun String.toIntakeCondition(): IntakeCondition {
    return when (this) {
        "beforeMeal" -> IntakeCondition.BeforeMeal
        "afterMeal" -> IntakeCondition.AfterMeal
        "betweenMeals" -> IntakeCondition.BetweenMeals
        "fasting" -> IntakeCondition.Fasting
        "beforeSleep" -> IntakeCondition.BeforeSleep
        "none" -> IntakeCondition.None
        else -> error("Unknown intake condition: $this")
    }
}

fun IntakeCondition.toFlutterName(): String {
    return when (this) {
        IntakeCondition.BeforeMeal -> "beforeMeal"
        IntakeCondition.AfterMeal -> "afterMeal"
        IntakeCondition.BetweenMeals -> "betweenMeals"
        IntakeCondition.Fasting -> "fasting"
        IntakeCondition.BeforeSleep -> "beforeSleep"
        IntakeCondition.None -> "none"
    }
}

fun String.toMealType(): MealType {
    return when (this) {
        "breakfast" -> MealType.Breakfast
        "lunch" -> MealType.Lunch
        "dinner" -> MealType.Dinner
        else -> error("Unknown meal type: $this")
    }
}

fun MealType.toFlutterName(): String {
    return when (this) {
        MealType.Breakfast -> "breakfast"
        MealType.Lunch -> "lunch"
        MealType.Dinner -> "dinner"
    }
}
