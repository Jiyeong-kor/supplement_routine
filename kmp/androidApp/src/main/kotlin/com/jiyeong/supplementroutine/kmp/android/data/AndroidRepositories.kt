package com.jiyeong.supplementroutine.kmp.android.data

import com.jiyeong.supplementroutine.shared.data.IntakeRecordRepository
import com.jiyeong.supplementroutine.shared.data.SettingsRepository
import com.jiyeong.supplementroutine.shared.data.SupplementRepository
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class AndroidSupplementRepository(
    private val store: AndroidRoutineDataStore,
) : SupplementRepository {
    override fun getSupplements(): List<Supplement> = blockingRead {
        store.readSupplements()
    }

    override fun addSupplement(supplement: Supplement): List<Supplement> = blockingRead {
        val supplements = store.readSupplements()
            .filterNot { current -> current.id == supplement.id } + supplement
        store.writeSupplements(supplements)
        supplements
    }

    override fun updateSupplement(supplement: Supplement): List<Supplement> = blockingRead {
        val supplements = store.readSupplements().map { current ->
            if (current.id == supplement.id) supplement else current
        }
        store.writeSupplements(supplements)
        supplements
    }

    override fun removeSupplement(supplementId: String): List<Supplement> = blockingRead {
        val supplements = store.readSupplements().filterNot { supplement -> supplement.id == supplementId }
        store.writeSupplements(supplements)
        supplements
    }

    override fun clearSupplements(): List<Supplement> = blockingRead {
        store.writeSupplements(emptyList())
        emptyList()
    }
}

class AndroidIntakeRecordRepository(
    private val store: AndroidRoutineDataStore,
) : IntakeRecordRepository {
    override fun getRecords(): Map<String, IntakeRecord> = blockingRead {
        store.readRecords()
    }

    override fun upsertRecord(record: IntakeRecord): Map<String, IntakeRecord> = blockingRead {
        val records = store.readRecords() + (record.id to record)
        store.writeRecords(records)
        records
    }

    override fun clearRecordsForSupplement(supplementId: String): Map<String, IntakeRecord> = blockingRead {
        val records = store.readRecords().filterValues { record -> record.supplementId != supplementId }
        store.writeRecords(records)
        records
    }

    override fun clearAll(): Map<String, IntakeRecord> = blockingRead {
        store.writeRecords(emptyMap())
        emptyMap()
    }
}

class AndroidSettingsRepository(
    private val store: AndroidRoutineDataStore,
) : SettingsRepository {
    override fun getMealTimeSettings(): MealTimeSettings = blockingRead {
        store.readMealTimeSettings()
    }

    override fun updateBreakfastTime(time: TimeOfDayValue): MealTimeSettings = blockingRead {
        val settings = store.readMealTimeSettings().copy(breakfastTime = time)
        store.writeMealTimeSettings(settings)
        settings
    }

    override fun updateLunchTime(time: TimeOfDayValue): MealTimeSettings = blockingRead {
        val settings = store.readMealTimeSettings().copy(lunchTime = time)
        store.writeMealTimeSettings(settings)
        settings
    }

    override fun updateDinnerTime(time: TimeOfDayValue): MealTimeSettings = blockingRead {
        val settings = store.readMealTimeSettings().copy(dinnerTime = time)
        store.writeMealTimeSettings(settings)
        settings
    }

    override fun getNotificationEnabled(): Boolean = blockingRead {
        store.readNotificationEnabled()
    }

    override fun updateNotificationEnabled(isEnabled: Boolean): Boolean = blockingRead {
        store.writeNotificationEnabled(isEnabled)
        isEnabled
    }
}

private fun <T> blockingRead(block: suspend () -> T): T {
    return runBlocking(Dispatchers.IO) { block() }
}
