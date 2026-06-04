package com.jiyeong.supplementroutine.shared

data class AppDestination(
    val key: String,
    val koreanLabel: String,
)

object SupplementRoutineInfo {
    const val appName = "Supplement Routine"
    const val koreanAppName = "영양제 루틴"

    val topLevelDestinations = listOf(
        AppDestination(key = "today", koreanLabel = "오늘"),
        AppDestination(key = "supplements", koreanLabel = "영양제"),
        AppDestination(key = "history", koreanLabel = "기록"),
        AppDestination(key = "settings", koreanLabel = "설정"),
    )
}

class SharedAppSummary {
    fun appName(): String = SupplementRoutineInfo.koreanAppName

    fun destinationLabelText(): String {
        return SupplementRoutineInfo.topLevelDestinations.joinToString(" · ") { destination ->
            destination.koreanLabel
        }
    }

    fun iosFallbackMessage(): String {
        return "iOS 알림과 로컬 저장소는 다음 단계에서 플랫폼 방식으로 연결됩니다."
    }
}
