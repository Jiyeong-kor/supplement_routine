package com.jiyeong.supplementroutine.shared.form

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

enum class SupplementFormValidationError {
    EmptyName,
    InvalidDosage,
    EmptyRoutineSlots,
}

data class SupplementFormInput(
    val name: String,
    val dosageUnit: String,
    val dosageValue: Double,
    val isRoutineBased: Boolean,
    val isSpecificTime: Boolean,
    val selectedSlots: Set<IntakeSlot>,
    val fixedCount: Int,
    val fixedTimes: List<TimeOfDayValue>,
    val startTime: TimeOfDayValue,
    val intervalHours: Int,
    val intervalCount: Int,
    val isNotificationEnabled: Boolean,
    val memo: String?,
)

data class ParsedDosage(
    val value: Double,
    val unit: String?,
)

object SupplementFormPolicy {
    const val DEFAULT_DOSAGE_TEXT = "1"
    const val DEFAULT_UNIT = "개"
    val dosageUnits = listOf("개", "정", "캡슐", "mg", "IU", "ml")

    const val DEFAULT_COUNT = 1
    const val MIN_COUNT = 1
    const val MAX_COUNT = 10
    const val MIN_INTERVAL_HOURS = 1
    const val MAX_INTERVAL_HOURS = 24
    const val DEFAULT_INTERVAL_HOURS = 8
    val defaultTime = TimeOfDayValue(hour = 8, minute = 0)
    val defaultRoutineSlots = setOf(
        IntakeSlot(mealType = MealType.Breakfast, condition = IntakeCondition.AfterMeal),
    )

    val routineSlots = listOf(
        IntakeSlot(condition = IntakeCondition.Fasting),
        IntakeSlot(mealType = MealType.Breakfast, condition = IntakeCondition.BeforeMeal),
        IntakeSlot(mealType = MealType.Breakfast, condition = IntakeCondition.AfterMeal),
        IntakeSlot(mealType = MealType.Breakfast, condition = IntakeCondition.BetweenMeals),
        IntakeSlot(mealType = MealType.Lunch, condition = IntakeCondition.BeforeMeal),
        IntakeSlot(mealType = MealType.Lunch, condition = IntakeCondition.AfterMeal),
        IntakeSlot(mealType = MealType.Lunch, condition = IntakeCondition.BetweenMeals),
        IntakeSlot(mealType = MealType.Dinner, condition = IntakeCondition.BeforeMeal),
        IntakeSlot(mealType = MealType.Dinner, condition = IntakeCondition.AfterMeal),
        IntakeSlot(condition = IntakeCondition.BeforeSleep),
    )

    fun validateName(name: String): SupplementFormValidationError? {
        return if (name.trim().isEmpty()) {
            SupplementFormValidationError.EmptyName
        } else {
            null
        }
    }

    fun parseDosage(value: String): Double? {
        return parseDosageInput(value)?.value
    }

    fun parseDosageInput(value: String): ParsedDosage? {
        val compact = value.trim().replace(" ", "")
        val match = Regex("""^([0-9]+(?:[.,][0-9]+)?)([A-Za-z가-힣]+)?$""").matchEntire(compact)
            ?: return null
        val dosageValue = match.groupValues[1].replace(",", ".").toDoubleOrNull()
            ?.takeIf { it > 0.0 }
            ?: return null
        val parsedUnit = match.groupValues.getOrNull(2)
            ?.takeIf { it.isNotBlank() }
            ?.let(::normalizeDosageUnit)

        return ParsedDosage(
            value = dosageValue,
            unit = parsedUnit,
        )
    }

    fun validateRoutineSlots(slots: Set<IntakeSlot>): SupplementFormValidationError? {
        return if (slots.isEmpty()) {
            SupplementFormValidationError.EmptyRoutineSlots
        } else {
            null
        }
    }

    fun formatDosage(value: Double): String {
        return if (value == value.toInt().toDouble()) {
            value.toInt().toString()
        } else {
            value.toString()
        }
    }

    private fun normalizeDosageUnit(unit: String): String? {
        return dosageUnits.firstOrNull { candidate ->
            candidate.equals(unit, ignoreCase = true)
        }
    }
}

object SupplementFormMapper {
    fun toSupplement(
        input: SupplementFormInput,
        initialSupplement: Supplement? = null,
        idProvider: () -> String,
    ): Supplement {
        val method = methodFor(input)

        return Supplement(
            id = initialSupplement?.id ?: idProvider(),
            name = input.name.trim(),
            dailyCount = dailyCountFor(input, method),
            method = method,
            dosageUnit = input.dosageUnit,
            dosageValue = input.dosageValue,
            selectedSlots = if (method == IntakeMethod.MealBased) input.selectedSlots.toList() else null,
            fixedTimes = if (method == IntakeMethod.FixedTime) input.fixedTimes.take(input.fixedCount) else null,
            startTime = if (method == IntakeMethod.Interval) input.startTime else null,
            intervalHours = if (method == IntakeMethod.Interval) input.intervalHours else null,
            isNotificationEnabled = input.isNotificationEnabled,
            memo = input.memo?.trim()?.takeIf { it.isNotEmpty() },
        )
    }

    private fun methodFor(input: SupplementFormInput): IntakeMethod {
        return if (input.isRoutineBased) {
            IntakeMethod.MealBased
        } else if (input.isSpecificTime) {
            IntakeMethod.FixedTime
        } else {
            IntakeMethod.Interval
        }
    }

    private fun dailyCountFor(input: SupplementFormInput, method: IntakeMethod): Int {
        return when (method) {
            IntakeMethod.MealBased -> input.selectedSlots.size
            IntakeMethod.FixedTime -> input.fixedCount
            IntakeMethod.Interval -> input.intervalCount
        }
    }
}
