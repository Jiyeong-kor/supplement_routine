package com.jiyeong.supplementroutine.shared.scheduling

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SchedulingServiceTest {
    private val date = LocalDateValue(year = 2026, month = 5, day = 18)

    @Test
    fun fixedTimeMethodUsesProvidedTimes() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(
                id = "fixed",
                method = IntakeMethod.FixedTime,
                fixedTimes = listOf(
                    TimeOfDayValue(hour = 8, minute = 0),
                    TimeOfDayValue(hour = 21, minute = 30),
                ),
            ),
        )

        assertEquals(
            listOf(
                TimeOfDayValue(hour = 8, minute = 0),
                TimeOfDayValue(hour = 21, minute = 30),
            ),
            result.map { it.time },
        )
    }

    @Test
    fun fixedTimeMethodUsesDefaultTimeWhenMissing() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(id = "fixed_default", method = IntakeMethod.FixedTime),
        )

        assertEquals(TimeOfDayValue(hour = 9, minute = 0), result.single().time)
    }

    @Test
    fun intervalMethodDoesNotCreateSchedulesPastMidnight() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(
                id = "interval",
                method = IntakeMethod.Interval,
                dailyCount = 4,
                startTime = TimeOfDayValue(hour = 20, minute = 0),
                intervalHours = 3,
            ),
        )

        assertEquals(
            listOf(
                TimeOfDayValue(hour = 20, minute = 0),
                TimeOfDayValue(hour = 23, minute = 0),
            ),
            result.map { it.time },
        )
    }

    @Test
    fun intervalMethodUsesDefaultStartTimeAndInterval() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(
                id = "interval_default",
                method = IntakeMethod.Interval,
                dailyCount = 2,
            ),
        )

        assertEquals(
            listOf(
                TimeOfDayValue(hour = 8, minute = 0),
                TimeOfDayValue(hour = 16, minute = 0),
            ),
            result.map { it.time },
        )
    }

    @Test
    fun mealBasedMethodCalculatesConditionSpecificTimes() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(
                id = "routine",
                method = IntakeMethod.MealBased,
                selectedSlots = listOf(
                    IntakeSlot(condition = IntakeCondition.Fasting),
                    IntakeSlot(
                        mealType = MealType.Breakfast,
                        condition = IntakeCondition.BeforeMeal,
                    ),
                    IntakeSlot(
                        mealType = MealType.Breakfast,
                        condition = IntakeCondition.AfterMeal,
                    ),
                    IntakeSlot(
                        mealType = MealType.Breakfast,
                        condition = IntakeCondition.BetweenMeals,
                    ),
                    IntakeSlot(condition = IntakeCondition.BeforeSleep),
                ),
            ),
            mealTimeSettings = MealTimeSettings(
                breakfastTime = TimeOfDayValue(hour = 8, minute = 0),
                lunchTime = TimeOfDayValue(hour = 12, minute = 0),
                dinnerTime = TimeOfDayValue(hour = 18, minute = 0),
            ),
        )

        assertEquals(
            listOf(
                TimeOfDayValue(hour = 7, minute = 0),
                TimeOfDayValue(hour = 7, minute = 30),
                TimeOfDayValue(hour = 8, minute = 30),
                TimeOfDayValue(hour = 10, minute = 0),
                TimeOfDayValue(hour = 22, minute = 0),
            ),
            result.map { it.time },
        )
    }

    @Test
    fun betweenMealsSlotAlsoCalculatesLunchToDinnerMidpoint() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(
                id = "between_lunch_dinner",
                method = IntakeMethod.MealBased,
                selectedSlots = listOf(
                    IntakeSlot(
                        mealType = MealType.Lunch,
                        condition = IntakeCondition.BetweenMeals,
                    ),
                ),
            ),
            mealTimeSettings = MealTimeSettings(
                lunchTime = TimeOfDayValue(hour = 12, minute = 0),
                dinnerTime = TimeOfDayValue(hour = 18, minute = 0),
            ),
        )

        assertEquals(TimeOfDayValue(hour = 15, minute = 0), result.single().time)
    }

    @Test
    fun dailyRecordsAreSortedByScheduledTime() {
        val records = SchedulingService.createDailyIntakeRecords(
            date = date,
            supplements = listOf(
                supplement(
                    id = "late",
                    method = IntakeMethod.FixedTime,
                    fixedTimes = listOf(TimeOfDayValue(hour = 21, minute = 0)),
                ),
                supplement(
                    id = "early",
                    method = IntakeMethod.FixedTime,
                    fixedTimes = listOf(TimeOfDayValue(hour = 8, minute = 0)),
                ),
            ),
        )

        assertEquals(listOf("early", "late"), records.map { it.supplement.id })
    }

    @Test
    fun mealBasedMethodReturnsEmptyWhenNoSlotsAreSelected() {
        val result = SchedulingService.calculateIntakeTimes(
            supplement(id = "empty", method = IntakeMethod.MealBased),
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun dailyRecordIdsUseFlutterCompatibleDateKey() {
        val records = SchedulingService.createDailyIntakeRecords(
            date = date,
            supplements = listOf(
                supplement(id = "fixed", method = IntakeMethod.FixedTime),
            ),
        )

        assertEquals("r_fixed_20260518_0", records.single().record.id)
    }

    private fun supplement(
        id: String,
        method: IntakeMethod,
        dailyCount: Int = 1,
        fixedTimes: List<TimeOfDayValue>? = null,
        startTime: TimeOfDayValue? = null,
        intervalHours: Int? = null,
        selectedSlots: List<IntakeSlot>? = null,
    ): Supplement {
        return Supplement(
            id = id,
            name = id,
            dailyCount = dailyCount,
            method = method,
            dosageUnit = "개",
            dosageValue = 1.0,
            fixedTimes = fixedTimes,
            startTime = startTime,
            intervalHours = intervalHours,
            selectedSlots = selectedSlots,
            isNotificationEnabled = true,
        )
    }
}
