package com.jiyeong.supplementroutine.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class SupplementRoutineInfoTest {
    @Test
    fun exposesFourTopLevelDestinations() {
        assertEquals(
            listOf("오늘", "영양제", "기록", "설정"),
            SupplementRoutineInfo.topLevelDestinations.map { it.koreanLabel },
        )
    }
}
