package com.jiyeong.supplementroutine.shared.domain

data class IntakeSlot(
    val mealType: MealType? = null,
    val condition: IntakeCondition,
)

data class Supplement(
    val id: String,
    val name: String,
    val dailyCount: Int,
    val method: IntakeMethod,
    val generalCondition: IntakeCondition = IntakeCondition.None,
    val dosageUnit: String,
    val dosageValue: Double,
    val selectedSlots: List<IntakeSlot>? = null,
    val fixedTimes: List<TimeOfDayValue>? = null,
    val startTime: TimeOfDayValue? = null,
    val intervalHours: Int? = null,
    val isNotificationEnabled: Boolean,
    val memo: String? = null,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(name.isNotBlank()) { "name must not be blank" }
        require(dailyCount > 0) { "dailyCount must be greater than 0" }
        require(dosageUnit.isNotBlank()) { "dosageUnit must not be blank" }
        require(dosageValue > 0.0) { "dosageValue must be greater than 0" }
        require(intervalHours == null || intervalHours > 0) {
            "intervalHours must be greater than 0 when present"
        }
    }
}
