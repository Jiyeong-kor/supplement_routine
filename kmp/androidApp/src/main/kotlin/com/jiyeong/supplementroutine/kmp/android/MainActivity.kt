package com.jiyeong.supplementroutine.kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jiyeong.supplementroutine.kmp.android.ui.SupplementRoutineKmpApp
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SupplementRoutineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SupplementRoutineTheme {
                SupplementRoutineKmpApp()
            }
        }
    }
}
