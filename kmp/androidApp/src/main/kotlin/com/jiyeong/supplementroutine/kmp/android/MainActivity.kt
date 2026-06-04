package com.jiyeong.supplementroutine.kmp.android

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jiyeong.supplementroutine.kmp.android.notification.SupplementReminderReceiver
import com.jiyeong.supplementroutine.kmp.android.ui.SupplementRoutineKmpApp
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SupplementRoutineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureSystemBars()
        SupplementReminderReceiver.ensureNotificationChannel(this)
        setContent {
            SupplementRoutineTheme {
                SupplementRoutineKmpApp()
            }
        }
    }

    private fun configureSystemBars() {
        val isDarkTheme = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val systemBarStyle = if (isDarkTheme) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
        enableEdgeToEdge(
            statusBarStyle = systemBarStyle,
            navigationBarStyle = systemBarStyle,
        )
    }
}
