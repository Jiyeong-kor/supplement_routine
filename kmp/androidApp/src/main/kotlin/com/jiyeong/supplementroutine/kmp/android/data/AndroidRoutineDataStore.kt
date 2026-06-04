package com.jiyeong.supplementroutine.kmp.android.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jiyeong.supplementroutine.shared.data.IntakeRecordDto
import com.jiyeong.supplementroutine.shared.data.IntakeSlotDto
import com.jiyeong.supplementroutine.shared.data.MealTimeSettingsDto
import com.jiyeong.supplementroutine.shared.data.SupplementDto
import com.jiyeong.supplementroutine.shared.data.TimeOfDayDto
import com.jiyeong.supplementroutine.shared.data.toDomain
import com.jiyeong.supplementroutine.shared.data.toDto
import com.jiyeong.supplementroutine.shared.domain.InstantValue
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.Supplement
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.supplementRoutineDataStore by preferencesDataStore(name = "supplement_routine")

class AndroidRoutineDataStore(context: Context) {
    private val dataStore = context.applicationContext.supplementRoutineDataStore

    suspend fun readSupplements(): List<Supplement> {
        val preferences = dataStore.data.first()
        return preferences[SUPPLEMENTS_JSON]
            ?.let(::decodeSupplements)
            .orEmpty()
    }

    suspend fun writeSupplements(supplements: List<Supplement>) {
        dataStore.edit { preferences ->
            preferences[SUPPLEMENTS_JSON] = encodeSupplements(supplements)
        }
    }

    suspend fun readRecords(): Map<String, IntakeRecord> {
        val preferences = dataStore.data.first()
        return preferences[INTAKE_RECORDS_JSON]
            ?.let(::decodeRecords)
            .orEmpty()
    }

    suspend fun writeRecords(records: Map<String, IntakeRecord>) {
        dataStore.edit { preferences ->
            preferences[INTAKE_RECORDS_JSON] = encodeRecords(records.values)
        }
    }

    suspend fun readMealTimeSettings(): MealTimeSettings {
        val preferences = dataStore.data.first()
        return preferences[MEAL_TIME_SETTINGS_JSON]
            ?.let(::decodeMealTimeSettings)
            ?: MealTimeSettings()
    }

    suspend fun writeMealTimeSettings(settings: MealTimeSettings) {
        dataStore.edit { preferences ->
            preferences[MEAL_TIME_SETTINGS_JSON] = encodeMealTimeSettings(settings)
        }
    }

    suspend fun readNotificationEnabled(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[NOTIFICATION_ENABLED] ?: true
    }

    suspend fun writeNotificationEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = isEnabled
        }
    }

    private fun decodeSupplements(json: String): List<Supplement> {
        val array = JSONArray(json)
        return List(array.length()) { index ->
            array.getJSONObject(index).toSupplementDto().toDomain()
        }
    }

    private fun encodeSupplements(supplements: List<Supplement>): String {
        val array = JSONArray()
        supplements.map(Supplement::toDto).forEach { dto ->
            array.put(dto.toJsonObject())
        }
        return array.toString()
    }

    private fun decodeRecords(json: String): Map<String, IntakeRecord> {
        val array = JSONArray(json)
        return List(array.length()) { index ->
            array.getJSONObject(index).toIntakeRecordDto().toDomain(
                parseTakenAt = { value -> InstantValue(value.toLong()) },
            )
        }.associateBy { record -> record.id }
    }

    private fun encodeRecords(records: Collection<IntakeRecord>): String {
        val array = JSONArray()
        records.map { record ->
            record.toDto(formatTakenAt = { takenAt -> takenAt.epochMilliseconds.toString() })
        }.forEach { dto ->
            array.put(dto.toJsonObject())
        }
        return array.toString()
    }

    private fun decodeMealTimeSettings(json: String): MealTimeSettings {
        return JSONObject(json).toMealTimeSettingsDto().toDomain()
    }

    private fun encodeMealTimeSettings(settings: MealTimeSettings): String {
        return settings.toDto().toJsonObject().toString()
    }

    private fun SupplementDto.toJsonObject(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("name", name)
            .put("dailyCount", dailyCount)
            .put("method", method)
            .put("generalCondition", generalCondition)
            .put("dosageUnit", dosageUnit)
            .put("dosageValue", dosageValue)
            .putNullableArray("selectedSlots", selectedSlots?.map { it.toJsonObject() })
            .putNullableArray("fixedTimes", fixedTimes?.map { it.toJsonObject() })
            .putNullableObject("startTime", startTime?.toJsonObject())
            .putNullableInt("intervalHours", intervalHours)
            .put("isNotificationEnabled", isNotificationEnabled)
            .putNullableString("memo", memo)
    }

    private fun JSONObject.toSupplementDto(): SupplementDto {
        return SupplementDto(
            id = getString("id"),
            name = getString("name"),
            dailyCount = getInt("dailyCount"),
            method = getString("method"),
            generalCondition = optString("generalCondition", "none"),
            dosageUnit = getString("dosageUnit"),
            dosageValue = getDouble("dosageValue"),
            selectedSlots = optJsonArray("selectedSlots")?.toObjectList { it.toIntakeSlotDto() },
            fixedTimes = optJsonArray("fixedTimes")?.toObjectList { it.toTimeOfDayDto() },
            startTime = optJsonObject("startTime")?.toTimeOfDayDto(),
            intervalHours = optIntOrNull("intervalHours"),
            isNotificationEnabled = getBoolean("isNotificationEnabled"),
            memo = optStringOrNull("memo"),
        )
    }

    private fun IntakeRecordDto.toJsonObject(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("supplementId", supplementId)
            .put("date", date)
            .put("scheduledTime", scheduledTime.toJsonObject())
            .put("isDone", isDone)
            .putNullableString("takenAt", takenAt)
    }

    private fun JSONObject.toIntakeRecordDto(): IntakeRecordDto {
        return IntakeRecordDto(
            id = getString("id"),
            supplementId = getString("supplementId"),
            date = getString("date"),
            scheduledTime = getJSONObject("scheduledTime").toTimeOfDayDto(),
            isDone = getBoolean("isDone"),
            takenAt = optStringOrNull("takenAt"),
        )
    }

    private fun MealTimeSettingsDto.toJsonObject(): JSONObject {
        return JSONObject()
            .put("breakfastTime", breakfastTime.toJsonObject())
            .put("lunchTime", lunchTime.toJsonObject())
            .put("dinnerTime", dinnerTime.toJsonObject())
    }

    private fun JSONObject.toMealTimeSettingsDto(): MealTimeSettingsDto {
        return MealTimeSettingsDto(
            breakfastTime = getJSONObject("breakfastTime").toTimeOfDayDto(),
            lunchTime = getJSONObject("lunchTime").toTimeOfDayDto(),
            dinnerTime = getJSONObject("dinnerTime").toTimeOfDayDto(),
        )
    }

    private fun IntakeSlotDto.toJsonObject(): JSONObject {
        return JSONObject()
            .putNullableString("mealType", mealType)
            .put("condition", condition)
    }

    private fun JSONObject.toIntakeSlotDto(): IntakeSlotDto {
        return IntakeSlotDto(
            mealType = optStringOrNull("mealType"),
            condition = getString("condition"),
        )
    }

    private fun TimeOfDayDto.toJsonObject(): JSONObject {
        return JSONObject()
            .put("hour", hour)
            .put("minute", minute)
    }

    private fun JSONObject.toTimeOfDayDto(): TimeOfDayDto {
        return TimeOfDayDto(
            hour = getInt("hour"),
            minute = getInt("minute"),
        )
    }

    private fun JSONObject.putNullableArray(key: String, values: List<JSONObject>?): JSONObject {
        if (values == null) {
            put(key, JSONObject.NULL)
        } else {
            put(key, JSONArray().apply { values.forEach(::put) })
        }
        return this
    }

    private fun JSONObject.putNullableObject(key: String, value: JSONObject?): JSONObject {
        put(key, value ?: JSONObject.NULL)
        return this
    }

    private fun JSONObject.putNullableString(key: String, value: String?): JSONObject {
        put(key, value ?: JSONObject.NULL)
        return this
    }

    private fun JSONObject.putNullableInt(key: String, value: Int?): JSONObject {
        put(key, value ?: JSONObject.NULL)
        return this
    }

    private fun JSONObject.optJsonArray(key: String): JSONArray? {
        return if (has(key) && !isNull(key)) getJSONArray(key) else null
    }

    private fun JSONObject.optJsonObject(key: String): JSONObject? {
        return if (has(key) && !isNull(key)) getJSONObject(key) else null
    }

    private fun JSONObject.optStringOrNull(key: String): String? {
        return if (has(key) && !isNull(key)) getString(key) else null
    }

    private fun JSONObject.optIntOrNull(key: String): Int? {
        return if (has(key) && !isNull(key)) getInt(key) else null
    }

    private fun <T> JSONArray.toObjectList(mapper: (JSONObject) -> T): List<T> {
        return List(length()) { index -> mapper(getJSONObject(index)) }
    }

    private companion object {
        val SUPPLEMENTS_JSON = stringPreferencesKey("supplements_json")
        val INTAKE_RECORDS_JSON = stringPreferencesKey("intake_records_json")
        val MEAL_TIME_SETTINGS_JSON = stringPreferencesKey("meal_time_settings_json")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }
}
