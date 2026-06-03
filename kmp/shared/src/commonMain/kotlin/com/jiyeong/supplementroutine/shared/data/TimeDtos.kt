package com.jiyeong.supplementroutine.shared.data

import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

data class TimeOfDayDto(
    val hour: Int,
    val minute: Int,
)

fun TimeOfDayDto.toDomain(): TimeOfDayValue {
    return TimeOfDayValue(hour = hour, minute = minute)
}

fun TimeOfDayValue.toDto(): TimeOfDayDto {
    return TimeOfDayDto(hour = hour, minute = minute)
}

fun LocalDateValue.toIsoDateString(): String {
    return "${year.toString().padStart(4, '0')}-${month.twoDigits()}-${day.twoDigits()}"
}

fun String.toLocalDateValue(): LocalDateValue {
    val datePart = substringBefore("T")
    val parts = datePart.split("-")
    require(parts.size == 3) { "date must use yyyy-MM-dd or ISO-8601 format" }

    return LocalDateValue(
        year = parts[0].toInt(),
        month = parts[1].toInt(),
        day = parts[2].toInt(),
    )
}

private fun Int.twoDigits(): String {
    return toString().padStart(2, '0')
}
