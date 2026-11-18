package com.jiyeong.supplementroutine.kmp.android.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jiyeong.supplementroutine.kmp.android.notification.AndroidNotificationPermissionController
import com.jiyeong.supplementroutine.kmp.android.notification.AndroidReminderScheduler
import com.jiyeong.supplementroutine.kmp.android.presentation.SupplementRoutineViewModel
import com.jiyeong.supplementroutine.kmp.android.ui.haptic.rememberRoutineHapticFeedback
import com.jiyeong.supplementroutine.kmp.android.ui.history.HistoryRoute
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrIcon
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSize
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSpacing
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrStroke
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayRoute
import com.jiyeong.supplementroutine.shared.SupplementRoutineInfo
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue

@Composable
fun SupplementRoutineKmpApp(
    permissionController: AndroidNotificationPermissionController,
    reminderScheduler: AndroidReminderScheduler,
    viewModel: SupplementRoutineViewModel = hiltViewModel(),
) {
    var selectedDestinationKey by remember { mutableStateOf("today") }
    var isSupplementManagementOpen by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val hapticFeedback = rememberRoutineHapticFeedback()
    var todayFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var todayHighlightedSupplementId by remember { mutableStateOf<String?>(null) }
    var notificationAttentionDismissedDate by remember { mutableStateOf<LocalDateValue?>(null) }
    var expandNotificationTroubleshooting by remember { mutableStateOf(false) }
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
    RoutineAppScaffold(
        selectedDestinationKey = selectedDestinationKey,
        onDestinationSelected = { key ->
            isSupplementManagementOpen = false
            selectedDestinationKey = key
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(paddingValues))
        } else {
            when {
                isSupplementManagementOpen -> SupplementsRoute(
                    contentPadding = paddingValues,
                    supplements = uiState.supplements,
                    defaultNotificationEnabled = uiState.notificationEnabled,
                    onAddSupplement = { supplement, onSuccess, onFailure ->
                        val isFirstSupplement = uiState.supplements.isEmpty()
                        viewModel.addSupplement(
                            supplement = supplement,
                            onSuccess = {
                                onSuccess()
                                todayHighlightedSupplementId = supplement.id
                                if (isFirstSupplement) {
                                    todayFeedbackMessage = "오늘 일정에 추가됐어요."
                                    notificationAttentionDismissedDate = uiState.today
                                }
                                isSupplementManagementOpen = false
                                selectedDestinationKey = "today"
                            },
                            onFailure = onFailure,
                        )
                    },
                    onUpdateSupplement = { supplement, onSuccess, onFailure ->
                        viewModel.updateSupplement(
                            supplement = supplement,
                            onSuccess = onSuccess,
                            onFailure = onFailure,
                        )
                    },
                    onRemoveSupplement = viewModel::removeSupplement,
                    onToggleNotification = viewModel::toggleSupplementNotification,
                )
                selectedDestinationKey == "history" -> HistoryRoute(
                    contentPadding = paddingValues,
                    today = uiState.today,
                    historyViewState = uiState.historyViewState,
                    onOpenNotificationSettingsClick = {
                        expandNotificationTroubleshooting = true
                        isSupplementManagementOpen = false
                        selectedDestinationKey = "settings"
                    },
                )
                selectedDestinationKey == "settings" -> SettingsRoute(
                    contentPadding = paddingValues,
                    mealTimeSettings = uiState.mealTimeSettings,
                    notificationEnabled = uiState.notificationEnabled,
                    notificationPermissionState = notificationPermissionState,
                    expandNotificationTroubleshooting = expandNotificationTroubleshooting,
                    onNotificationTroubleshootingExpanded = {
                        expandNotificationTroubleshooting = false
                    },
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
                    notificationNeedsAttention = uiState.notificationEnabled &&
                        uiState.supplements.any { it.isNotificationEnabled } &&
                        (!notificationPermissionState.canPostNotifications ||
                            !notificationPermissionState.canScheduleExactAlarms),
                    notificationAttentionDismissed = notificationAttentionDismissedDate == uiState.today,
                    onDismissNotificationAttention = {
                        notificationAttentionDismissedDate = uiState.today
                    },
                    highlightedSupplementId = todayHighlightedSupplementId,
                    onHighlightedSupplementConsumed = { todayHighlightedSupplementId = null },
                    feedbackMessage = todayFeedbackMessage,
                    onFeedbackMessageConsumed = { todayFeedbackMessage = null },
                    onAddSupplementClick = { isSupplementManagementOpen = true },
                    onOpenNotificationSettingsClick = {
                        expandNotificationTroubleshooting = true
                        isSupplementManagementOpen = false
                        selectedDestinationKey = "settings"
                    },
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

@Composable
internal fun RoutineAppScaffold(
    selectedDestinationKey: String,
    onDestinationSelected: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val navigationTopLine = colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            containerColor = Color.Transparent,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SrSize.BottomNavigationHeight)
                        .background(MaterialTheme.colorScheme.surface)
                        .drawBehind {
                            drawLine(
                                color = navigationTopLine,
                                start = Offset.Zero,
                                end = Offset(size.width, 0f),
                                strokeWidth = SrStroke.Thin.toPx(),
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(modifier = Modifier.widthIn(max = 720.dp).fillMaxSize()) {
                        DestinationItems(
                            selectedDestinationKey = selectedDestinationKey,
                            onDestinationSelected = onDestinationSelected,
                        )
                    }
                }
            },
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(modifier = Modifier.widthIn(max = 720.dp).fillMaxSize()) {
                    content(paddingValues)
                }
            }
        }
    }
}

@Composable
internal fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = SrSpacing.ScreenHorizontal,
                top = SrSpacing.Lg,
                end = SrSpacing.ScreenHorizontal,
            ),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(SrSpacing.Md),
    ) {
        Text(
            text = "루틴을 불러오는 중입니다.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        repeat(5) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SrSize.SkeletonRowHeight),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(SrStroke.Thin, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(
                    modifier = Modifier.padding(SrSpacing.Md),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(SrSpacing.Md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(SrSize.SkeletonAvatar),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        content = {},
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SrSize.IconSmall),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        content = {},
                    )
                }
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
        DestinationIcon(SrIcon.Home, SrIcon.Home),
        DestinationIcon(SrIcon.History, SrIcon.History),
        DestinationIcon(SrIcon.Settings, SrIcon.Settings),
    )

    SupplementRoutineInfo.topLevelDestinations.filter { it.key != "supplements" }.forEachIndexed { index, destination ->
        val selected = destination.key == selectedDestinationKey
        DestinationItem(
            selected = selected,
            selectedIcon = icons[index].selected,
            unselectedIcon = icons[index].unselected,
            label = destination.koreanLabel,
            onClick = { onDestinationSelected(destination.key) },
        )
    }
}

@Composable
private fun RowScope.DestinationItem(
    selected: Boolean,
    selectedIcon: SrIcon,
    unselectedIcon: SrIcon,
    label: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .weight(1f)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Tab,
            )
            .padding(top = SrSpacing.Sm, bottom = SrSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.size(
                    width = SrSize.BottomNavigationIndicatorWidth,
                    height = SrSize.BottomNavigationIndicatorHeight,
                ),
                shape = CircleShape,
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                border = if (selected) null else BorderStroke(SrStroke.None, Color.Transparent),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(if (selected) selectedIcon.drawableRes else unselectedIcon.drawableRes),
                        contentDescription = null,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(SrSize.IconMedium),
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private data class DestinationIcon(
    val selected: SrIcon,
    val unselected: SrIcon,
)
