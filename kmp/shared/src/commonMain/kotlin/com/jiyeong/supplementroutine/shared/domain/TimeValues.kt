package com.jiyeong.supplementroutine.shared.domain

data class TimeOfDayValue(
    val hour: Int,
    val minute: Int,
) {
    init {
        require(hour in 0..23) { "hour must be between 0 and 23" }
        require(minute in 0..59) { "minute must be between 0 and 59" }
    }
}

data class LocalDateValue(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    init {
        require(month in 1..12) { "month must be between 1 and 12" }
        require(day in 1..31) { "day must be between 1 and 31" }
    }
}

data class InstantValue(
    val epochMilliseconds: Long,
)
