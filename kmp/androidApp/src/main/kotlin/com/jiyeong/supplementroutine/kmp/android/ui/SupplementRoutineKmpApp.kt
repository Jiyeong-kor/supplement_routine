package com.jiyeong.supplementroutine.kmp.android.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayRoute
import com.jiyeong.supplementroutine.shared.SupplementRoutineInfo

@Composable
fun SupplementRoutineKmpApp() {
    var selectedDestinationKey by remember { mutableStateOf("today") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    DestinationItems(
                        selectedDestinationKey = selectedDestinationKey,
                        onDestinationSelected = { key -> selectedDestinationKey = key },
                    )
                }
            },
        ) { paddingValues ->
            when (selectedDestinationKey) {
                "supplements" -> SupplementsRoute(contentPadding = paddingValues)
                else -> TodayRoute(contentPadding = paddingValues)
            }
        }
    }
}

@Composable
private fun RowScope.DestinationItems(
    selectedDestinationKey: String,
    onDestinationSelected: (String) -> Unit,
) {
    val icons = listOf(
        DestinationIcon(Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
        DestinationIcon(Icons.Filled.Spa, Icons.Outlined.Spa),
        DestinationIcon(Icons.Filled.History, Icons.Outlined.History),
        DestinationIcon(Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    SupplementRoutineInfo.topLevelDestinations.forEachIndexed { index, destination ->
        val selected = destination.key == selectedDestinationKey
        NavigationBarItem(
            selected = selected,
            onClick = { onDestinationSelected(destination.key) },
            icon = {
                Icon(
                    imageVector = if (selected) icons[index].selected else icons[index].unselected,
                    contentDescription = destination.koreanLabel,
                )
            },
            label = { Text(destination.koreanLabel) },
        )
    }
}

private data class DestinationIcon(
    val selected: ImageVector,
    val unselected: ImageVector,
)
