package com.jiyeong.supplementroutine.shared.data

import com.jiyeong.supplementroutine.shared.domain.InstantValue
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord

data class IntakeRecordDto(
    val id: String,
    val supplementId: String,
    val date: String,
    val scheduledTime: TimeOfDayDto,
    val isDone: Boolean,
    val takenAt: String? = null,
)

fun IntakeRecordDto.toDomain(
    parseTakenAt: (String) -> InstantValue? = { null },
): IntakeRecord {
    return IntakeRecord(
        id = id,
        supplementId = supplementId,
        date = date.toLocalDateValue(),
        scheduledTime = scheduledTime.toDomain(),
        isDone = isDone,
        takenAt = takenAt?.let(parseTakenAt),
    )
}

fun IntakeRecord.toDto(
    formatTakenAt: (InstantValue) -> String? = { null },
): IntakeRecordDto {
    return IntakeRecordDto(
        id = id,
        supplementId = supplementId,
        date = date.toIsoDateString(),
        scheduledTime = scheduledTime.toDto(),
        isDone = isDone,
        takenAt = takenAt?.let(formatTakenAt),
    )
}
