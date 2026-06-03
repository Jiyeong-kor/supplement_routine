package com.jiyeong.supplementroutine.shared.scheduling

import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeUtilsTest {
    @Test
    fun addMinutesWrapsPastMidnight() {
        assertEquals(
            TimeOfDayValue(hour = 0, minute = 15),
            TimeOfDayValue(hour = 23, minute = 45).addMinutes(30),
        )
    }

    @Test
    fun addMinutesSupportsNegativeValues() {
        assertEquals(
            TimeOfDayValue(hour = 7, minute = 30),
            TimeOfDayValue(hour = 8, minute = 0).addMinutes(-30),
        )
    }

    @Test
    fun addMinutesWrapsBeforeMidnight() {
        assertEquals(
            TimeOfDayValue(hour = 23, minute = 45),
            TimeOfDayValue(hour = 0, minute = 15).addMinutes(-30),
        )
    }

    @Test
    fun to24hStringPadsBothParts() {
        assertEquals("07:05", TimeOfDayValue(hour = 7, minute = 5).to24hString())
    }
}
