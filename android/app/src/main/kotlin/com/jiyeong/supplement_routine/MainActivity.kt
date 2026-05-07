package com.jiyeong.supplement_routine

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            "supplement_routine/home_widget",
        ).setMethodCallHandler { call, result ->
            if (call.method != "updateTodaySummary") {
                result.notImplemented()
                return@setMethodCallHandler
            }

            val doneCount = call.argument<Int>("doneCount") ?: 0
            val totalCount = call.argument<Int>("totalCount") ?: 0
            val hasSchedule = call.argument<Boolean>("hasSchedule") ?: false
            val hasNext = call.argument<Boolean>("hasNext") ?: false
            val nextName = call.argument<String>("nextName")
            val nextHour = call.argument<Int>("nextHour")
            val nextMinute = call.argument<Int>("nextMinute")

            SupplementProgressWidgetProvider.saveSummary(
                context = this,
                doneCount = doneCount,
                totalCount = totalCount,
                hasSchedule = hasSchedule,
                hasNext = hasNext,
                nextName = nextName,
                nextHour = nextHour,
                nextMinute = nextMinute,
            )
            result.success(null)
        }
    }
}
