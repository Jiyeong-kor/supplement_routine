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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayRoute
import com.jiyeong.supplementroutine.shared.SupplementRoutineInfo

@Composable
fun SupplementRoutineKmpApp() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    DestinationItems()
                }
            },
        ) { paddingValues ->
            TodayRoute(contentPadding = paddingValues)
        }
    }
}

@Composable
private fun RowScope.DestinationItems() {
    val icons = listOf(
        DestinationIcon(Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
        DestinationIcon(Icons.Filled.Spa, Icons.Outlined.Spa),
        DestinationIcon(Icons.Filled.History, Icons.Outlined.History),
        DestinationIcon(Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    SupplementRoutineInfo.topLevelDestinations.forEachIndexed { index, destination ->
        val selected = destination.key == "today"
        NavigationBarItem(
            selected = selected,
            onClick = {},
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
