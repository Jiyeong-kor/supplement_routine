package com.jiyeong.supplementroutine.shared.scheduling

import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

private const val MinutesPerDay = 24 * 60

fun TimeOfDayValue.addMinutes(minutes: Int): TimeOfDayValue {
    val totalMinutes = hour * 60 + minute + minutes
    val normalizedMinutes = totalMinutes.floorMod(MinutesPerDay)

    return TimeOfDayValue(
        hour = normalizedMinutes / 60,
        minute = normalizedMinutes % 60,
    )
}

fun TimeOfDayValue.to24hString(): String {
    return "${hour.twoDigits()}:${minute.twoDigits()}"
}

internal fun TimeOfDayValue.totalMinutes(): Int {
    return hour * 60 + minute
}

internal fun LocalDateValue.toRecordDateKey(): String {
    return "${year.toString().padStart(4, '0')}${month.twoDigits()}${day.twoDigits()}"
}

private fun Int.twoDigits(): String {
    return toString().padStart(2, '0')
}

private fun Int.floorMod(other: Int): Int {
    return ((this % other) + other) % other
}
