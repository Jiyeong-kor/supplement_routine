package com.jiyeong.supplementroutine.kmp.android.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jiyeong.supplementroutine.kmp.android.notification.AndroidNotificationPermissionController
import com.jiyeong.supplementroutine.kmp.android.notification.AndroidReminderScheduler
import com.jiyeong.supplementroutine.kmp.android.notification.NotificationPermissionState
import com.jiyeong.supplementroutine.kmp.android.presentation.SupplementRoutineViewModel
import com.jiyeong.supplementroutine.kmp.android.ui.haptic.rememberRoutineHapticFeedback
import com.jiyeong.supplementroutine.kmp.android.ui.history.HistoryRoute
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayRoute
import com.jiyeong.supplementroutine.shared.SupplementRoutineInfo

@Composable
fun SupplementRoutineKmpApp(
    permissionController: AndroidNotificationPermissionController,
    reminderScheduler: AndroidReminderScheduler,
    viewModel: SupplementRoutineViewModel = hiltViewModel(),
) {
    var selectedDestinationKey by remember { mutableStateOf("today") }
    val uiState by viewModel.uiState.collectAsState()
    val hapticFeedback = rememberRoutineHapticFeedback()
    var notificationPermissionState by remember {
        mutableStateOf(permissionController.currentState())
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        notificationPermissionState = permissionController.currentState()
    }

    LaunchedEffect(selectedDestinationKey) {
        if (selectedDestinationKey == "settings") {
            notificationPermissionState = permissionController.currentState()
        }
    }

    LaunchedEffect(
        uiState.todayItems,
        uiState.notificationEnabled,
        notificationPermissionState,
    ) {
        if (uiState.isLoading || !uiState.notificationEnabled) {
            reminderScheduler.cancelStoredReminders()
        } else {
            reminderScheduler.syncTodayReminders(uiState.todayItems)
        }
    }
    val colorScheme = MaterialTheme.colorScheme
    val glassHighlight = colorScheme.surface.copy(alpha = 0.72f)
    val glassWarmTint = colorScheme.secondaryContainer.copy(alpha = 0.54f)
    val glassCoolTint = colorScheme.tertiaryContainer.copy(alpha = 0.42f)
    val navigationTopLine = colorScheme.outline.copy(alpha = 0.82f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(routineGlassBackground())
            .drawBehind {
                drawCircle(
                    color = glassHighlight,
                    radius = size.minDimension * 0.72f,
                    center = Offset(size.width * 0.12f, size.height * 0.08f),
                )
                drawCircle(
                    color = glassWarmTint,
                    radius = size.minDimension * 0.64f,
                    center = Offset(size.width * 0.92f, size.height * 0.28f),
                )
                drawCircle(
                    color = glassCoolTint,
                    radius = size.minDimension * 0.82f,
                    center = Offset(size.width * 0.28f, size.height * 0.86f),
                )
            },
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = navigationTopLine,
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx(),
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.58f),
                    tonalElevation = NavigationBarDefaults.Elevation / 2,
                ) {
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
                        notificationPermissionState = notificationPermissionState,
                        onBreakfastTimeChanged = viewModel::updateBreakfastTime,
                        onLunchTimeChanged = viewModel::updateLunchTime,
                        onDinnerTimeChanged = viewModel::updateDinnerTime,
                        onNotificationEnabledChanged = viewModel::updateNotificationEnabled,
                        onRequestNotificationPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                notificationPermissionState = permissionController.currentState()
                            }
                        },
                        onRequestExactAlarmPermission = {
                            permissionController.openExactAlarmSettings()
                            notificationPermissionState = permissionController.currentState()
                        },
                        onRefreshNotificationPermissions = {
                            notificationPermissionState = permissionController.currentState()
                        },
                        onSendTestNotification = {
                            notificationPermissionState = permissionController.currentState()
                            reminderScheduler.sendTestReminder()
                        },
                        onScheduleTestNotification = {
                            notificationPermissionState = permissionController.currentState()
                            reminderScheduler.scheduleTestReminder()
                        },
                        onResetRoutineData = {
                            hapticFeedback.destructiveConfirmed()
                            viewModel.resetRoutineData()
                        },
                    )
                    else -> TodayRoute(
                        contentPadding = paddingValues,
                        date = uiState.today,
                        items = uiState.todayItems,
                        errorMessage = uiState.errorMessage,
                        onAddSupplementClick = { selectedDestinationKey = "supplements" },
                        onToggleRecord = { record ->
                            if (!record.isDone) {
                                hapticFeedback.intakeCompleted()
                            }
                            viewModel.toggleRecord(record)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun routineGlassBackground(): Brush {
    val colorScheme = MaterialTheme.colorScheme
    return Brush.verticalGradient(
        colors = listOf(
            colorScheme.background,
            colorScheme.primaryContainer.copy(alpha = 0.82f),
            colorScheme.tertiaryContainer.copy(alpha = 0.58f),
            colorScheme.secondaryContainer.copy(alpha = 0.64f),
            colorScheme.surfaceVariant.copy(alpha = 0.72f),
        ),
    )
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
