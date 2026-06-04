package com.jiyeong.supplementroutine.kmp.android.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.jiyeong.supplementroutine.kmp.android.MainActivity

class SupplementReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        ensureNotificationChannel(context)

        val supplementName = intent.getStringExtra(EXTRA_SUPPLEMENT_NAME).orEmpty()
        val scheduledTime = intent.getStringExtra(EXTRA_SCHEDULED_TIME).orEmpty()
        val title = if (supplementName.isBlank()) "영양제 복용 시간" else "$supplementName 복용 시간"
        val body = if (scheduledTime.isBlank()) {
            "오늘 복용 항목을 확인하고 체크해보세요."
        } else {
            "$scheduledTime 복용 항목을 확인하고 체크해보세요."
        }

        val builder = notificationBuilder(context)
        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(openAppPendingIntent(context))
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_REMINDER)
            .build()

        context.getSystemService(NotificationManager::class.java).notify(
            NOTIFICATION_ID_BASE + intent.hashCode(),
            notification,
        )
    }

    private fun openAppPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    @Suppress("DEPRECATION")
    private fun notificationBuilder(context: Context): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }
    }

    companion object {
        const val EXTRA_SUPPLEMENT_NAME = "supplement_name"
        const val EXTRA_SCHEDULED_TIME = "scheduled_time"
        private const val CHANNEL_ID = "supplement_reminders"
        private const val CHANNEL_NAME = "복용 알림"
        private const val OPEN_APP_REQUEST_CODE = 1001
        private const val NOTIFICATION_ID_BASE = 5000

        fun ensureNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return
            }

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "영양제 복용 시간을 알려줍니다."
            }

            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
