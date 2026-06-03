package com.jiyeong.supplementroutine.kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.shared.SupplementRoutineInfo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SupplementRoutineKmpApp()
        }
    }
}

@Composable
private fun SupplementRoutineKmpApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold { paddingValues ->
                KmpScaffoldScreen(paddingValues = paddingValues)
            }
        }
    }
}

@Composable
private fun KmpScaffoldScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = SupplementRoutineInfo.koreanAppName,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "KMP 전환을 위한 Android Compose shell입니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SupplementRoutineInfo.topLevelDestinations.forEach { destination ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = destination.koreanLabel,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
