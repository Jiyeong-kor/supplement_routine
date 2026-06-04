package com.jiyeong.supplementroutine.kmp.android.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jiyeong.supplementroutine.shared.scheduling.ScheduledIntakeRecord
import java.util.Calendar

class AndroidReminderScheduler(
    private val context: Context,
    private val permissionController: AndroidNotificationPermissionController,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun syncTodayReminders(items: List<ScheduledIntakeRecord>) {
        cancelStoredReminders()

        if (!permissionController.currentState().canScheduleReminders) {
            saveReminderIds(emptySet())
            return
        }

        val scheduledIds = mutableSetOf<Int>()
        items
            .filter { item -> item.supplement.isNotificationEnabled && !item.record.isDone }
            .forEach { item ->
                val triggerAtMillis = triggerAtMillisForToday(item.record.scheduledTime.hour, item.record.scheduledTime.minute)
                if (triggerAtMillis > System.currentTimeMillis()) {
                    val requestCode = requestCodeFor(item.record.id)
                    val pendingIntent = pendingIntentFor(
                        requestCode = requestCode,
                        supplementName = item.supplement.name,
                        scheduledTimeText = "${item.record.scheduledTime.hour.toString().padStart(2, '0')}:${item.record.scheduledTime.minute.toString().padStart(2, '0')}",
                    )
                    scheduleExact(triggerAtMillis, pendingIntent)
                    scheduledIds.add(requestCode)
                }
            }

        saveReminderIds(scheduledIds.map(Int::toString).toSet())
    }

    fun cancelStoredReminders() {
        preferences.getStringSet(KEY_REMINDER_IDS, emptySet()).orEmpty().forEach { id ->
            alarmManager.cancel(pendingIntentFor(id.toInt()))
        }
        saveReminderIds(emptySet())
    }

    private fun scheduleExact(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }
    }

    private fun triggerAtMillisForToday(hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun pendingIntentFor(
        requestCode: Int,
        supplementName: String = "",
        scheduledTimeText: String = "",
    ): PendingIntent {
        val intent = Intent(context, SupplementReminderReceiver::class.java).apply {
            putExtra(SupplementReminderReceiver.EXTRA_SUPPLEMENT_NAME, supplementName)
            putExtra(SupplementReminderReceiver.EXTRA_SCHEDULED_TIME, scheduledTimeText)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun saveReminderIds(ids: Set<String>) {
        preferences.edit().putStringSet(KEY_REMINDER_IDS, ids).apply()
    }

    private fun requestCodeFor(recordId: String): Int {
        return recordId.hashCode()
    }

    private companion object {
        const val PREFERENCES_NAME = "supplement_reminder_scheduler"
        const val KEY_REMINDER_IDS = "scheduled_reminder_ids"
    }
}
