package com.jiyeong.supplementroutine.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SupplementRoutineInfoTest {
    @Test
    fun exposesFourTopLevelDestinations() {
        assertEquals(
            listOf("오늘", "영양제", "기록", "설정"),
            SupplementRoutineInfo.topLevelDestinations.map { it.koreanLabel },
        )
    }

    @Test
    fun sharedAppSummaryProvidesIosShellSmokeValues() {
        val summary = SharedAppSummary()

        assertEquals("영양제 루틴", summary.appName())
        assertEquals("오늘 · 영양제 · 기록 · 설정", summary.destinationLabelText())
        assertTrue(summary.iosFallbackMessage().contains("iOS 알림"))
    }
}
