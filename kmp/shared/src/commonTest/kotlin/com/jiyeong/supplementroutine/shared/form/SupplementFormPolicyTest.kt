package com.jiyeong.supplementroutine.shared.form

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SupplementFormPolicyTest {
    @Test
    fun nameCannotBeBlank() {
        assertEquals(
            SupplementFormValidationError.EmptyName,
            SupplementFormPolicy.validateName("  "),
        )
        assertNull(SupplementFormPolicy.validateName("비타민 D"))
    }

    @Test
    fun dosageMustBeGreaterThanZero() {
        assertNull(SupplementFormPolicy.parseDosage("0"))
        assertNull(SupplementFormPolicy.parseDosage("-1"))
        assertNull(SupplementFormPolicy.parseDosage("abc"))
        assertEquals(1.5, SupplementFormPolicy.parseDosage("1.5"))
    }

    @Test
    fun routineBasedInputRequiresAtLeastOneSlot() {
        assertEquals(
            SupplementFormValidationError.EmptyRoutineSlots,
            SupplementFormPolicy.validateRoutineSlots(emptySet()),
        )
        assertNull(
            SupplementFormPolicy.validateRoutineSlots(
                setOf(IntakeSlot(condition = IntakeCondition.Fasting)),
            ),
        )
    }

    @Test
    fun routineBasedInputCreatesMealBasedSupplement() {
        val supplement = SupplementFormMapper.toSupplement(
            input = formInput(
                name = "비타민 D",
                isRoutineBased = true,
                selectedSlots = setOf(IntakeSlot(condition = IntakeCondition.Fasting)),
            ),
            idProvider = { "generated" },
        )

        assertEquals("비타민 D", supplement.name)
        assertEquals(IntakeMethod.MealBased, supplement.method)
        assertEquals(1, supplement.dailyCount)
        assertNotNull(supplement.selectedSlots)
        assertNull(supplement.fixedTimes)
    }

    @Test
    fun editModeKeepsExistingSupplementId() {
        val initialSupplement = Supplement(
            id = "existing_id",
            name = "수정 전",
            dailyCount = 1,
            method = IntakeMethod.FixedTime,
            dosageUnit = "개",
            dosageValue = 1.0,
            fixedTimes = listOf(TimeOfDayValue(hour = 8, minute = 0)),
            isNotificationEnabled = true,
        )

        val supplement = SupplementFormMapper.toSupplement(
            input = formInput(
                name = "수정 후",
                dosageValue = 2.0,
                isRoutineBased = false,
                isSpecificTime = true,
                fixedCount = 2,
                fixedTimes = listOf(
                    TimeOfDayValue(hour = 8, minute = 0),
                    TimeOfDayValue(hour = 21, minute = 0),
                ),
                isNotificationEnabled = false,
                memo = "저녁에도 확인",
            ),
            initialSupplement = initialSupplement,
            idProvider = { "generated" },
        )

        assertEquals("existing_id", supplement.id)
        assertEquals("수정 후", supplement.name)
        assertEquals(IntakeMethod.FixedTime, supplement.method)
        assertEquals(2, supplement.dailyCount)
        assertEquals(2, supplement.fixedTimes?.size)
    }

    @Test
    fun intervalInputCreatesIntervalSupplement() {
        val supplement = SupplementFormMapper.toSupplement(
            input = formInput(
                name = "비타민 C",
                isRoutineBased = false,
                isSpecificTime = false,
                startTime = TimeOfDayValue(hour = 7, minute = 30),
                intervalHours = 6,
                intervalCount = 3,
            ),
            idProvider = { "generated" },
        )

        assertEquals(IntakeMethod.Interval, supplement.method)
        assertEquals(3, supplement.dailyCount)
        assertEquals(TimeOfDayValue(hour = 7, minute = 30), supplement.startTime)
        assertEquals(6, supplement.intervalHours)
        assertNull(supplement.fixedTimes)
    }

    private fun formInput(
        name: String = "오메가3",
        dosageValue: Double = 1.0,
        isRoutineBased: Boolean = false,
        isSpecificTime: Boolean = true,
        selectedSlots: Set<IntakeSlot> = emptySet(),
        fixedCount: Int = 1,
        fixedTimes: List<TimeOfDayValue> = listOf(TimeOfDayValue(hour = 8, minute = 0)),
        startTime: TimeOfDayValue = TimeOfDayValue(hour = 8, minute = 0),
        intervalHours: Int = 8,
        intervalCount: Int = 1,
        isNotificationEnabled: Boolean = true,
        memo: String? = null,
    ): SupplementFormInput {
        return SupplementFormInput(
            name = name,
            dosageUnit = "개",
            dosageValue = dosageValue,
            isRoutineBased = isRoutineBased,
            isSpecificTime = isSpecificTime,
            selectedSlots = selectedSlots,
            fixedCount = fixedCount,
            fixedTimes = fixedTimes,
            startTime = startTime,
            intervalHours = intervalHours,
            intervalCount = intervalCount,
            isNotificationEnabled = isNotificationEnabled,
            memo = memo,
        )
    }
}
