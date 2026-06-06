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
    const val DEFAULT_UNIT = "정"
    val dosageUnits = listOf("정", "캡슐", "포", "방울", "mg", "g", "mcg", "IU", "ml")

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
        val match = Regex("""^([0-9]+(?:[.,][0-9]+)?)([A-Za-z가-힣µμ]+)?$""").matchEntire(compact)
            ?: return null
        val dosageValue = match.groupValues[1].replace(",", ".").toDoubleOrNull()
            ?.takeIf { it > 0.0 }
            ?: return null
        val rawUnit = match.groupValues.getOrNull(2)
            ?.takeIf { it.isNotBlank() }
        val parsedUnit = rawUnit?.let(::normalizeDosageUnit)
        if (rawUnit != null && parsedUnit == null) {
            return null
        }

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

    fun normalizeDosageUnitForSelection(unit: String): String {
        return normalizeDosageUnit(unit) ?: DEFAULT_UNIT
    }

    private fun normalizeDosageUnit(unit: String): String? {
        val aliases = mapOf(
            "개" to "정",
            "알" to "정",
            "tablet" to "정",
            "tablets" to "정",
            "tab" to "정",
            "tabs" to "정",
            "capsule" to "캡슐",
            "capsules" to "캡슐",
            "cap" to "캡슐",
            "caps" to "캡슐",
            "pack" to "포",
            "packs" to "포",
            "packet" to "포",
            "packets" to "포",
            "drop" to "방울",
            "drops" to "방울",
            "ug" to "mcg",
            "µg" to "mcg",
            "μg" to "mcg",
        )
        aliases[unit.lowercase()]?.let { return it }

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
