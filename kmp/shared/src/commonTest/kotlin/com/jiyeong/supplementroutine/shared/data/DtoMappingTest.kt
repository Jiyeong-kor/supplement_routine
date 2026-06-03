package com.jiyeong.supplementroutine.shared.data

import com.jiyeong.supplementroutine.shared.domain.InstantValue
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DtoMappingTest {
    @Test
    fun supplementDtoUsesFlutterEnumNamesAndRoundTripsDomainMeaning() {
        val supplement = Supplement(
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
            memo = "아침 식사 후 체크",
        )

        val dto = supplement.toDto()

        assertEquals("mealBased", dto.method)
        assertEquals("none", dto.generalCondition)
        assertEquals("breakfast", dto.selectedSlots?.single()?.mealType)
        assertEquals("afterMeal", dto.selectedSlots?.single()?.condition)
        assertEquals(supplement, dto.toDomain())
    }

    @Test
    fun supplementDtoKeepsFixedAndIntervalTimes() {
        val dto = SupplementDto(
            id = "magnesium",
            name = "마그네슘",
            dailyCount = 2,
            method = "interval",
            dosageUnit = "정",
            dosageValue = 1.5,
            fixedTimes = listOf(TimeOfDayDto(hour = 9, minute = 0)),
            startTime = TimeOfDayDto(hour = 8, minute = 15),
            intervalHours = 6,
            isNotificationEnabled = false,
        )

        val domain = dto.toDomain()

        assertEquals(listOf(TimeOfDayValue(hour = 9, minute = 0)), domain.fixedTimes)
        assertEquals(TimeOfDayValue(hour = 8, minute = 15), domain.startTime)
        assertEquals(6, domain.intervalHours)
        assertFalse(domain.isNotificationEnabled)
        assertEquals(dto, domain.toDto())
    }

    @Test
    fun intakeSlotDtoAllowsMissingMealType() {
        val slot = IntakeSlot(condition = IntakeCondition.Fasting)

        val dto = slot.toDto()

        assertNull(dto.mealType)
        assertEquals("fasting", dto.condition)
        assertEquals(slot, dto.toDomain())
    }

    @Test
    fun intakeRecordDtoKeepsFlutterDateAndTimeShape() {
        val record = IntakeRecord(
            id = "record_1",
            supplementId = "vitamin_d",
            date = LocalDateValue(year = 2026, month = 5, day = 8),
            scheduledTime = TimeOfDayValue(hour = 8, minute = 30),
            isDone = true,
            takenAt = InstantValue(epochMilliseconds = 1_778_185_300_000),
        )

        val dto = record.toDto { "2026-05-08T08:35:00.000" }

        assertEquals("2026-05-08", dto.date)
        assertEquals(TimeOfDayDto(hour = 8, minute = 30), dto.scheduledTime)
        assertEquals("2026-05-08T08:35:00.000", dto.takenAt)

        val restored = dto.toDomain {
            assertEquals("2026-05-08T08:35:00.000", it)
            InstantValue(epochMilliseconds = 1_778_185_300_000)
        }

        assertEquals(record, restored)
    }

    @Test
    fun mealTimeSettingsDtoRoundTrips() {
        val settings = MealTimeSettings(
            breakfastTime = TimeOfDayValue(hour = 7, minute = 30),
            lunchTime = TimeOfDayValue(hour = 12, minute = 15),
            dinnerTime = TimeOfDayValue(hour = 18, minute = 45),
        )

        val dto = settings.toDto()

        assertEquals(TimeOfDayDto(hour = 7, minute = 30), dto.breakfastTime)
        assertEquals(settings, dto.toDomain())
    }

    @Test
    fun notificationEnabledDefaultIsOwnedByRepositoryImplementation() {
        val repository = MemorySettingsRepositoryContractProbe()

        assertTrue(repository.getNotificationEnabled())
        assertFalse(repository.updateNotificationEnabled(false))
    }
}

private class MemorySettingsRepositoryContractProbe : SettingsRepository {
    private var mealTimeSettings = MealTimeSettings()
    private var notificationEnabled = true

    override fun getMealTimeSettings(): MealTimeSettings = mealTimeSettings

    override fun updateBreakfastTime(time: TimeOfDayValue): MealTimeSettings {
        mealTimeSettings = mealTimeSettings.copy(breakfastTime = time)
        return mealTimeSettings
    }

    override fun updateLunchTime(time: TimeOfDayValue): MealTimeSettings {
        mealTimeSettings = mealTimeSettings.copy(lunchTime = time)
        return mealTimeSettings
    }

    override fun updateDinnerTime(time: TimeOfDayValue): MealTimeSettings {
        mealTimeSettings = mealTimeSettings.copy(dinnerTime = time)
        return mealTimeSettings
    }

    override fun getNotificationEnabled(): Boolean = notificationEnabled

    override fun updateNotificationEnabled(isEnabled: Boolean): Boolean {
        notificationEnabled = isEnabled
        return notificationEnabled
    }
}
