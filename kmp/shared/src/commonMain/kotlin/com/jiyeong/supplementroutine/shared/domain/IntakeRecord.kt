package com.jiyeong.supplementroutine.shared.domain

data class IntakeRecord(
    val id: String,
    val supplementId: String,
    val date: LocalDateValue,
    val scheduledTime: TimeOfDayValue,
    val isDone: Boolean,
    val takenAt: InstantValue? = null,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(supplementId.isNotBlank()) { "supplementId must not be blank" }
    }

    fun markDone(takenAt: InstantValue): IntakeRecord {
        return copy(isDone = true, takenAt = takenAt)
    }

    fun markUndone(): IntakeRecord {
        return copy(isDone = false, takenAt = null)
    }
}
