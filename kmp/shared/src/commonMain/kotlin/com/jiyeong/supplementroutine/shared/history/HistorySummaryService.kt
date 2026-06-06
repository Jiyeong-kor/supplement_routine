package com.jiyeong.supplementroutine.shared.history

import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import com.jiyeong.supplementroutine.shared.scheduling.SchedulingService

enum class MissedIntakePeriod {
    Morning,
    Afternoon,
    Evening,
}

data class DailyHistorySummary(
    val date: LocalDateValue,
    val doneCount: Int,
    val totalCount: Int,
    val missedPeriodCounts: Map<MissedIntakePeriod, Int> = emptyMap(),
) {
    val completionRate: Double
        get() = if (totalCount == 0) 0.0 else doneCount.toDouble() / totalCount

    val isEmpty: Boolean
        get() = totalCount == 0
}

data class HistoryViewState(
    val todaySummary: DailyHistorySummary,
    val monthSummaries: List<DailyHistorySummary>,
    val recentSummaries: List<DailyHistorySummary>,
) {
    val isEmpty: Boolean
        get() = todaySummary.isEmpty && recentSummaries.all { it.isEmpty }
}

object HistorySummaryService {
    fun createSummary(
        date: LocalDateValue,
        supplements: List<Supplement>,
        records: Map<String, IntakeRecord>,
        mealTimeSettings: MealTimeSettings = MealTimeSettings(),
    ): DailyHistorySummary {
        val scheduledRecords = SchedulingService.createDailyIntakeRecords(
            supplements = supplements,
            date = date,
            mealTimeSettings = mealTimeSettings,
        )

        val doneCount = scheduledRecords.count { item ->
            records[item.record.id]?.isDone == true
        }
        val missedPeriodCounts = scheduledRecords
            .filterNot { item -> records[item.record.id]?.isDone == true }
            .groupingBy { item -> missedPeriodFor(item.record.scheduledTime) }
            .eachCount()

        return DailyHistorySummary(
            date = date,
            doneCount = doneCount,
            totalCount = scheduledRecords.size,
            missedPeriodCounts = missedPeriodCounts,
        )
    }

    fun createRecentSummaries(
        today: LocalDateValue,
        supplements: List<Supplement>,
        records: Map<String, IntakeRecord>,
        mealTimeSettings: MealTimeSettings = MealTimeSettings(),
        days: Int = 14,
    ): List<DailyHistorySummary> {
        return (0 until days).map { offset ->
            createSummary(
                date = today.minusDays(offset),
                supplements = supplements,
                records = records,
                mealTimeSettings = mealTimeSettings,
            )
        }
    }

    fun createCurrentMonthSummaries(
        today: LocalDateValue,
        supplements: List<Supplement>,
        records: Map<String, IntakeRecord>,
        mealTimeSettings: MealTimeSettings = MealTimeSettings(),
    ): List<DailyHistorySummary> {
        return (1..today.daysInMonth()).map { day ->
            val date = today.copy(day = day)

            if (date.isAfter(today)) {
                DailyHistorySummary(date = date, doneCount = 0, totalCount = 0)
            } else {
                createSummary(
                    date = date,
                    supplements = supplements,
                    records = records,
                    mealTimeSettings = mealTimeSettings,
                )
            }
        }
    }

    fun createViewState(
        today: LocalDateValue,
        supplements: List<Supplement>,
        records: Map<String, IntakeRecord>,
        mealTimeSettings: MealTimeSettings = MealTimeSettings(),
    ): HistoryViewState {
        val recent = createRecentSummaries(
            today = today,
            supplements = supplements,
            records = records,
            mealTimeSettings = mealTimeSettings,
        )

        return HistoryViewState(
            todaySummary = recent.first(),
            monthSummaries = createCurrentMonthSummaries(
                today = today,
                supplements = supplements,
                records = records,
                mealTimeSettings = mealTimeSettings,
            ),
            recentSummaries = recent.drop(1),
        )
    }
}

private fun missedPeriodFor(time: TimeOfDayValue): MissedIntakePeriod {
    return when {
        time.hour < 12 -> MissedIntakePeriod.Morning
        time.hour < 18 -> MissedIntakePeriod.Afternoon
        else -> MissedIntakePeriod.Evening
    }
}

fun LocalDateValue.isAfter(other: LocalDateValue): Boolean {
    return compareTo(other) > 0
}

fun LocalDateValue.minusDays(days: Int): LocalDateValue {
    require(days >= 0) { "days must be greater than or equal to 0" }

    var year = this.year
    var month = this.month
    var day = this.day - days

    while (day <= 0) {
        month -= 1
        if (month == 0) {
            year -= 1
            month = 12
        }
        day += daysInMonth(year, month)
    }

    return LocalDateValue(year = year, month = month, day = day)
}

fun LocalDateValue.daysInMonth(): Int {
    return daysInMonth(year, month)
}

private fun LocalDateValue.compareTo(other: LocalDateValue): Int {
    return when {
        year != other.year -> year.compareTo(other.year)
        month != other.month -> month.compareTo(other.month)
        else -> day.compareTo(other.day)
    }
}

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> error("month must be between 1 and 12")
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}
