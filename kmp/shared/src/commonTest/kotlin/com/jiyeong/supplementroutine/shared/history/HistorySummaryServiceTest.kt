package com.jiyeong.supplementroutine.shared.history

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistorySummaryServiceTest {
    @Test
    fun emptySummaryHasZeroCompletionAndEmptyState() {
        val summary = DailyHistorySummary(
            date = LocalDateValue(year = 2026, month = 5, day = 18),
            doneCount = 0,
            totalCount = 0,
        )

        assertEquals(0.0, summary.completionRate)
        assertTrue(summary.isEmpty)
    }

    @Test
    fun savedDoneRecordIsReflectedInDailySummary() {
        val supplement = vitaminD()
        val date = LocalDateValue(year = 2026, month = 5, day = 18)
        val record = IntakeRecord(
            id = "r_vitamin_d_20260518_0",
            supplementId = "vitamin_d",
            date = date,
            scheduledTime = TimeOfDayValue(hour = 8, minute = 30),
            isDone = true,
        )

        val summary = HistorySummaryService.createSummary(
            date = date,
            supplements = listOf(supplement),
            records = mapOf(record.id to record),
            mealTimeSettings = MealTimeSettings(),
        )

        assertEquals(1, summary.doneCount)
        assertEquals(1, summary.totalCount)
        assertEquals(1.0, summary.completionRate)
    }

    @Test
    fun currentMonthSummaryLeavesFutureDaysEmpty() {
        val today = LocalDateValue(year = 2026, month = 5, day = 18)

        val summaries = HistorySummaryService.createCurrentMonthSummaries(
            today = today,
            supplements = listOf(vitaminD()),
            records = emptyMap(),
        )

        assertEquals(31, summaries.size)
        assertTrue(summaries.drop(18).all { it.doneCount == 0 && it.totalCount == 0 && it.isEmpty })
    }

    @Test
    fun viewStateIsEmptyWhenTodayAndRecentSummariesAreEmpty() {
        val state = HistorySummaryService.createViewState(
            today = LocalDateValue(year = 2026, month = 5, day = 18),
            supplements = emptyList(),
            records = emptyMap(),
        )

        assertTrue(state.isEmpty)
    }

    @Test
    fun recentSummariesContainTodayAndPreviousThirteenDays() {
        val summaries = HistorySummaryService.createRecentSummaries(
            today = LocalDateValue(year = 2026, month = 3, day = 1),
            supplements = emptyList(),
            records = emptyMap(),
        )

        assertEquals(14, summaries.size)
        assertEquals(LocalDateValue(year = 2026, month = 3, day = 1), summaries.first().date)
        assertEquals(LocalDateValue(year = 2026, month = 2, day = 16), summaries.last().date)
    }

    @Test
    fun leapYearMonthLengthIsHandled() {
        val summaries = HistorySummaryService.createCurrentMonthSummaries(
            today = LocalDateValue(year = 2024, month = 2, day = 29),
            supplements = emptyList(),
            records = emptyMap(),
        )

        assertEquals(29, summaries.size)
    }

    private fun vitaminD(): Supplement {
        return Supplement(
            id = "vitamin_d",
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
    }
}
