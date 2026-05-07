package com.jiyeong.supplement_routine

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class SupplementProgressWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val prefsName = "supplement_progress_widget"
        private const val doneCountKey = "doneCount"
        private const val totalCountKey = "totalCount"
        private const val hasScheduleKey = "hasSchedule"
        private const val hasNextKey = "hasNext"
        private const val nextNameKey = "nextName"
        private const val nextHourKey = "nextHour"
        private const val nextMinuteKey = "nextMinute"

        fun saveSummary(
            context: Context,
            doneCount: Int,
            totalCount: Int,
            hasSchedule: Boolean,
            hasNext: Boolean,
            nextName: String?,
            nextHour: Int?,
            nextMinute: Int?,
        ) {
            context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                .edit()
                .putInt(doneCountKey, doneCount)
                .putInt(totalCountKey, totalCount)
                .putBoolean(hasScheduleKey, hasSchedule)
                .putBoolean(hasNextKey, hasNext)
                .putString(nextNameKey, nextName)
                .putInt(nextHourKey, nextHour ?: 0)
                .putInt(nextMinuteKey, nextMinute ?: 0)
                .apply()

            updateAllWidgets(context)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(
                context,
                SupplementProgressWidgetProvider::class.java,
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            appWidgetIds.forEach { appWidgetId ->
                updateWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            val doneCount = prefs.getInt(doneCountKey, 0)
            val totalCount = prefs.getInt(totalCountKey, 0)
            val hasSchedule = prefs.getBoolean(hasScheduleKey, false)
            val hasNext = prefs.getBoolean(hasNextKey, false)
            val nextName = prefs.getString(nextNameKey, null)
            val nextHour = prefs.getInt(nextHourKey, 0)
            val nextMinute = prefs.getInt(nextMinuteKey, 0)
            val percent = if (totalCount > 0) (doneCount * 100) / totalCount else 0

            val nextText = when {
                !hasSchedule -> context.getString(R.string.widget_empty)
                !hasNext -> context.getString(R.string.widget_done)
                else -> context.getString(
                    R.string.widget_next_format,
                    nextHour,
                    nextMinute,
                    nextName.orEmpty(),
                )
            }

            val views = RemoteViews(context.packageName, R.layout.supplement_progress_widget)
            views.setTextViewText(R.id.widgetTitle, context.getString(R.string.widget_title))
            views.setTextViewText(R.id.widgetPercent, context.getString(R.string.widget_percent, percent))
            views.setTextViewText(
                R.id.widgetCount,
                context.getString(R.string.widget_progress_count, doneCount, totalCount),
            )
            views.setTextViewText(R.id.widgetNext, nextText)
            views.setProgressBar(R.id.widgetProgress, 100, percent, false)
            views.setOnClickPendingIntent(R.id.widgetRoot, createLaunchPendingIntent(context))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun createLaunchPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
