package com.jiyeong.supplementroutine.shared.data

import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

interface SupplementRepository {
    fun getSupplements(): List<Supplement>
    fun addSupplement(supplement: Supplement): List<Supplement>
    fun updateSupplement(supplement: Supplement): List<Supplement>
    fun removeSupplement(supplementId: String): List<Supplement>
    fun clearSupplements(): List<Supplement>
}

interface IntakeRecordRepository {
    fun getRecords(): Map<String, IntakeRecord>
    fun upsertRecord(record: IntakeRecord): Map<String, IntakeRecord>
    fun clearRecordsForSupplement(supplementId: String): Map<String, IntakeRecord>
    fun clearAll(): Map<String, IntakeRecord>
}

interface SettingsRepository {
    fun getMealTimeSettings(): MealTimeSettings
    fun updateBreakfastTime(time: TimeOfDayValue): MealTimeSettings
    fun updateLunchTime(time: TimeOfDayValue): MealTimeSettings
    fun updateDinnerTime(time: TimeOfDayValue): MealTimeSettings
    fun getNotificationEnabled(): Boolean
    fun updateNotificationEnabled(isEnabled: Boolean): Boolean
}
