package com.jiyeong.supplementroutine.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DomainModelTest {
    @Test
    fun mealTimeSettingsUseFlutterDefaults() {
        val settings = MealTimeSettings()

        assertEquals(TimeOfDayValue(hour = 8, minute = 0), settings.breakfastTime)
        assertEquals(TimeOfDayValue(hour = 12, minute = 0), settings.lunchTime)
        assertEquals(TimeOfDayValue(hour = 18, minute = 0), settings.dinnerTime)
    }

    @Test
    fun supplementKeepsMealBasedSlotsAndDefaultsGeneralConditionToNone() {
        val supplement = Supplement(
            id = "vitamin-d",
            name = "비타민 D",
            dailyCount = 1,
            method = IntakeMethod.MealBased,
            dosageUnit = "개",
            dosageValue = 1.0,
            selectedSlots = listOf(
                IntakeSlot(
                    mealType = MealType.Breakfast,
                    condition = IntakeCondition.AfterMeal,
                ),
            ),
            isNotificationEnabled = true,
        )

        assertEquals(IntakeCondition.None, supplement.generalCondition)
        assertEquals(MealType.Breakfast, supplement.selectedSlots?.single()?.mealType)
        assertEquals(IntakeCondition.AfterMeal, supplement.selectedSlots?.single()?.condition)
    }

    @Test
    fun intervalSupplementKeepsStartTimeAndIntervalHours() {
        val supplement = Supplement(
            id = "magnesium",
            name = "마그네슘",
            dailyCount = 2,
            method = IntakeMethod.Interval,
            dosageUnit = "ml",
            dosageValue = 10.0,
            startTime = TimeOfDayValue(hour = 9, minute = 0),
            intervalHours = 8,
            isNotificationEnabled = false,
        )

        assertEquals(TimeOfDayValue(hour = 9, minute = 0), supplement.startTime)
        assertEquals(8, supplement.intervalHours)
    }

    @Test
    fun intakeRecordDoneStateCanBeClearedLikeFlutterModel() {
        val doneRecord = IntakeRecord(
            id = "2026-06-03-vitamin-d-0830",
            supplementId = "vitamin-d",
            date = LocalDateValue(year = 2026, month = 6, day = 3),
            scheduledTime = TimeOfDayValue(hour = 8, minute = 30),
            isDone = false,
        ).markDone(InstantValue(epochMilliseconds = 1_780_430_400_000))

        assertTrue(doneRecord.isDone)
        assertEquals(1_780_430_400_000, doneRecord.takenAt?.epochMilliseconds)

        val undoneRecord = doneRecord.markUndone()

        assertEquals(false, undoneRecord.isDone)
        assertNull(undoneRecord.takenAt)
    }

    @Test
    fun invalidTimeValuesFailFast() {
        assertFailsWith<IllegalArgumentException> {
            TimeOfDayValue(hour = 24, minute = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            TimeOfDayValue(hour = 23, minute = 60)
        }
    }

    @Test
    fun scheduleLabelCanRepresentRoutineSlot() {
        val slot = IntakeSlot(
            mealType = MealType.Lunch,
            condition = IntakeCondition.BeforeMeal,
        )

        val label = ScheduleLabel.RoutineSlot(slot)

        assertEquals(slot, label.slot)
    }
}
