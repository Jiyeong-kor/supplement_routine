package com.jiyeong.supplementroutine.kmp.android.notification

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

data class NotificationPermissionState(
    val canPostNotifications: Boolean,
    val canScheduleExactAlarms: Boolean,
) {
    val canScheduleReminders: Boolean
        get() = canPostNotifications && canScheduleExactAlarms
}

class AndroidNotificationPermissionController(
    private val context: Context,
) {
    fun currentState(): NotificationPermissionState {
        return NotificationPermissionState(
            canPostNotifications = canPostNotifications(),
            canScheduleExactAlarms = canScheduleExactAlarms(),
        )
    }

    fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun canScheduleExactAlarms(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    }

    fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return
        }

        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.parse("package:${context.packageName}"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }
}
