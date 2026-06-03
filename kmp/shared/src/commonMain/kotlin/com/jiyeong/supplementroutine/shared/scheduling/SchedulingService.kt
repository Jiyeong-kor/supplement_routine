package com.jiyeong.supplementroutine.shared.scheduling

import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.ScheduleLabel
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

data class ScheduledIntake(
    val time: TimeOfDayValue,
    val label: ScheduleLabel,
)

data class ScheduledIntakeRecord(
    val supplement: Supplement,
    val record: IntakeRecord,
    val label: ScheduleLabel,
)

object SchedulingService {
    val wakeupTime = TimeOfDayValue(hour = 7, minute = 0)
    val sleepTime = TimeOfDayValue(hour = 22, minute = 0)

    fun calculateIntakeTimes(
        supplement: Supplement,
        mealTimeSettings: MealTimeSettings = MealTimeSettings(),
    ): List<ScheduledIntake> {
        return when (supplement.method) {
            IntakeMethod.FixedTime -> {
                val times = supplement.fixedTimes ?: listOf(TimeOfDayValue(hour = 9, minute = 0))
                times.map { time ->
                    ScheduledIntake(time = time, label = ScheduleLabel.FixedTime)
                }
            }
            IntakeMethod.Interval -> calculateIntervalTimes(supplement)
            IntakeMethod.MealBased -> calculateMealBasedTimes(
                supplement = supplement,
                mealTimeSettings = mealTimeSettings,
            )
        }
    }

    fun createDailyIntakeRecords(
        supplements: List<Supplement>,
        date: LocalDateValue,
        mealTimeSettings: MealTimeSettings = MealTimeSettings(),
    ): List<ScheduledIntakeRecord> {
        val records = supplements.flatMap { supplement ->
            calculateIntakeTimes(
                supplement = supplement,
                mealTimeSettings = mealTimeSettings,
            ).mapIndexed { index, intake ->
                ScheduledIntakeRecord(
                    supplement = supplement,
                    label = intake.label,
                    record = IntakeRecord(
                        id = "r_${supplement.id}_${date.toRecordDateKey()}_$index",
                        supplementId = supplement.id,
                        date = date,
                        scheduledTime = intake.time,
                        isDone = false,
                    ),
                )
            }
        }

        return records.sortedBy { it.record.scheduledTime.totalMinutes() }
    }

    private fun calculateIntervalTimes(supplement: Supplement): List<ScheduledIntake> {
        val result = mutableListOf<ScheduledIntake>()
        var currentTime = supplement.startTime ?: TimeOfDayValue(hour = 8, minute = 0)
        val interval = supplement.intervalHours ?: 8

        repeat(supplement.dailyCount) {
            result.add(ScheduledIntake(time = currentTime, label = ScheduleLabel.Interval))

            val nextMinutes = currentTime.totalMinutes() + interval * 60
            if (nextMinutes >= 24 * 60) {
                return result
            }
            currentTime = currentTime.addMinutes(interval * 60)
        }

        return result
    }

    private fun calculateMealBasedTimes(
        supplement: Supplement,
        mealTimeSettings: MealTimeSettings,
    ): List<ScheduledIntake> {
        val selectedSlots = supplement.selectedSlots.orEmpty()
        if (selectedSlots.isEmpty()) {
            return emptyList()
        }

        return selectedSlots.map { slot ->
            ScheduledIntake(
                time = scheduledTimeForSlot(slot, mealTimeSettings),
                label = ScheduleLabel.RoutineSlot(slot),
            )
        }
    }

    private fun scheduledTimeForSlot(
        slot: IntakeSlot,
        mealTimeSettings: MealTimeSettings,
    ): TimeOfDayValue {
        return when (slot.condition) {
            IntakeCondition.Fasting -> wakeupTime
            IntakeCondition.BeforeSleep -> sleepTime
            IntakeCondition.BetweenMeals -> {
                if (slot.mealType == MealType.Breakfast) {
                    midpoint(mealTimeSettings.breakfastTime, mealTimeSettings.lunchTime)
                } else {
                    midpoint(mealTimeSettings.lunchTime, mealTimeSettings.dinnerTime)
                }
            }
            else -> {
                val base = when (slot.mealType) {
                    MealType.Breakfast -> mealTimeSettings.breakfastTime
                    MealType.Lunch -> mealTimeSettings.lunchTime
                    MealType.Dinner, null -> mealTimeSettings.dinnerTime
                }

                when (slot.condition) {
                    IntakeCondition.BeforeMeal -> base.addMinutes(-30)
                    IntakeCondition.AfterMeal -> base.addMinutes(30)
                    else -> base
                }
            }
        }
    }

    private fun midpoint(start: TimeOfDayValue, end: TimeOfDayValue): TimeOfDayValue {
        val midpointMinutes = ((start.totalMinutes() + end.totalMinutes()).toDouble() / 2).roundToInt()

        return TimeOfDayValue(
            hour = (midpointMinutes / 60) % 24,
            minute = midpointMinutes % 60,
        )
    }

    private fun Double.roundToInt(): Int {
        return (this + 0.5).toInt()
    }
}
