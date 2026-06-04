package com.jiyeong.supplementroutine.kmp.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jiyeong.supplementroutine.kmp.android.presentation.SupplementRoutineViewModel
import com.jiyeong.supplementroutine.kmp.android.ui.history.HistoryRoute
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayRoute
import com.jiyeong.supplementroutine.shared.SupplementRoutineInfo

@Composable
fun SupplementRoutineKmpApp() {
    var selectedDestinationKey by remember { mutableStateOf("today") }
    val context = LocalContext.current
    val viewModel: SupplementRoutineViewModel = viewModel(
        factory = SupplementRoutineViewModel.factory(context),
    )
    val uiState by viewModel.uiState.collectAsState()

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
            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.padding(paddingValues))
            } else {
                when (selectedDestinationKey) {
                    "supplements" -> SupplementsRoute(
                        contentPadding = paddingValues,
                        supplements = uiState.supplements,
                        defaultNotificationEnabled = uiState.notificationEnabled,
                        onAddSupplement = viewModel::addSupplement,
                        onUpdateSupplement = viewModel::updateSupplement,
                        onRemoveSupplement = viewModel::removeSupplement,
                        onToggleNotification = viewModel::toggleSupplementNotification,
                    )
                    "history" -> HistoryRoute(
                        contentPadding = paddingValues,
                        today = uiState.today,
                        historyViewState = uiState.historyViewState,
                    )
                    "settings" -> SettingsRoute(
                        contentPadding = paddingValues,
                        mealTimeSettings = uiState.mealTimeSettings,
                        notificationEnabled = uiState.notificationEnabled,
                        onBreakfastTimeChanged = viewModel::updateBreakfastTime,
                        onLunchTimeChanged = viewModel::updateLunchTime,
                        onDinnerTimeChanged = viewModel::updateDinnerTime,
                        onNotificationEnabledChanged = viewModel::updateNotificationEnabled,
                        onResetRoutineData = viewModel::resetRoutineData,
                    )
                    else -> TodayRoute(
                        contentPadding = paddingValues,
                        date = uiState.today,
                        items = uiState.todayItems,
                        errorMessage = uiState.errorMessage,
                        onAddSupplementClick = { selectedDestinationKey = "supplements" },
                        onToggleRecord = viewModel::toggleRecord,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "루틴을 불러오는 중입니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
