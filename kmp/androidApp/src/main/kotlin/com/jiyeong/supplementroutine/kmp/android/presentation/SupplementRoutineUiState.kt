package com.jiyeong.supplementroutine.kmp.android.presentation

import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.history.HistorySummaryService
import com.jiyeong.supplementroutine.shared.history.HistoryViewState
import com.jiyeong.supplementroutine.shared.scheduling.ScheduledIntakeRecord
import com.jiyeong.supplementroutine.shared.scheduling.SchedulingService

data class SupplementRoutineUiState(
    val isLoading: Boolean = true,
    val today: LocalDateValue = currentLocalDateValue(),
    val supplements: List<Supplement> = emptyList(),
    val intakeRecords: Map<String, IntakeRecord> = emptyMap(),
    val mealTimeSettings: MealTimeSettings = MealTimeSettings(),
    val notificationEnabled: Boolean = true,
    val errorMessage: String? = null,
) {
    val todayItems: List<ScheduledIntakeRecord>
        get() = SchedulingService.createDailyIntakeRecords(
            supplements = supplements,
            date = today,
            mealTimeSettings = mealTimeSettings,
        ).map { item ->
            item.copy(record = intakeRecords[item.record.id] ?: item.record)
        }

    val historyViewState: HistoryViewState
        get() = HistorySummaryService.createViewState(
            today = today,
            supplements = supplements,
            records = intakeRecords,
            mealTimeSettings = mealTimeSettings,
        )
}
