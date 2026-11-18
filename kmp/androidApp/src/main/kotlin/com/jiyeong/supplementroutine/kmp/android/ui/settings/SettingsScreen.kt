package com.jiyeong.supplementroutine.kmp.android.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatTime
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineIconBadge
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePillButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSectionLabel
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrIcon
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSize
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSpacing
import com.jiyeong.supplementroutine.kmp.android.notification.NotificationPermissionState
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlinx.coroutines.delay

internal class SettingsScreenState(mealTimeSettings: MealTimeSettings = MealTimeSettings()) {
    var dialog by mutableStateOf<SettingsDialog?>(null)
    var mealTimesEditing by mutableStateOf(false)
    var draftMealTimeSettings by mutableStateOf(mealTimeSettings)
    var resetFeedbackVisible by mutableStateOf(false)
    var showTroubleshooting by mutableStateOf(false)
}

@Composable
internal fun SettingsRoute(
    contentPadding: PaddingValues,
    mealTimeSettings: MealTimeSettings,
    notificationEnabled: Boolean,
    notificationPermissionState: NotificationPermissionState,
    expandNotificationTroubleshooting: Boolean,
    onNotificationTroubleshootingExpanded: () -> Unit,
    onBreakfastTimeChanged: (TimeOfDayValue) -> Unit,
    onLunchTimeChanged: (TimeOfDayValue) -> Unit,
    onDinnerTimeChanged: (TimeOfDayValue) -> Unit,
    onNotificationEnabledChanged: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onRefreshNotificationPermissions: () -> Unit,
    onSendTestNotification: () -> Boolean,
    onScheduleTestNotification: () -> Boolean,
    onResetRoutineData: () -> Unit,
    state: SettingsScreenState = remember { SettingsScreenState(mealTimeSettings) },
    listState: LazyListState = rememberLazyListState(),
) = with(state) {
    val inspectionMode = LocalInspectionMode.current

    LaunchedEffect(mealTimeSettings, mealTimesEditing) {
        if (!mealTimesEditing) {
            draftMealTimeSettings = mealTimeSettings
        }
    }

    LaunchedEffect(expandNotificationTroubleshooting) {
        if (expandNotificationTroubleshooting) {
            listState.animateScrollToItem(3)
        }
    }

    LaunchedEffect(resetFeedbackVisible) {
        if (resetFeedbackVisible && !inspectionMode) {
            delay(2400)
            resetFeedbackVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = SrSpacing.ScreenHorizontal,
                top = SrSpacing.Lg,
                end = SrSpacing.ScreenHorizontal,
                bottom = contentPadding.calculateBottomPadding() + SrSpacing.Xxl,
            ),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Lg),
        ) {
            item(key = "settings-header") {
                RoutinePageHeader(
                    title = "설정",
                )
            }
            if (resetFeedbackVisible) {
                item(key = "reset-feedback") {
                    ResetFeedbackCard()
                }
            }
            item(key = "meal-section") {
                RoutineSectionLabel(title = "식사 시간")
            }
            item(key = "meal-card") {
                MealTimesCard(
                    mealTimeSettings = if (mealTimesEditing) draftMealTimeSettings else mealTimeSettings,
                    editing = mealTimesEditing,
                    changed = mealTimesEditing && draftMealTimeSettings != mealTimeSettings,
                    onStartEditing = {
                        draftMealTimeSettings = mealTimeSettings
                        mealTimesEditing = true
                    },
                    onSave = {
                        onBreakfastTimeChanged(draftMealTimeSettings.breakfastTime)
                        onLunchTimeChanged(draftMealTimeSettings.lunchTime)
                        onDinnerTimeChanged(draftMealTimeSettings.dinnerTime)
                        mealTimesEditing = false
                    },
                    onCancel = {
                        draftMealTimeSettings = mealTimeSettings
                        mealTimesEditing = false
                    },
                    onBreakfastTimeChanged = {
                        draftMealTimeSettings = draftMealTimeSettings.copy(breakfastTime = it)
                    },
                    onLunchTimeChanged = {
                        draftMealTimeSettings = draftMealTimeSettings.copy(lunchTime = it)
                    },
                    onDinnerTimeChanged = {
                        draftMealTimeSettings = draftMealTimeSettings.copy(dinnerTime = it)
                    },
                )
            }
            item(key = "notification-section") {
                RoutineSectionLabel(title = "알림")
            }
            item(key = "notification-card") {
                NotificationSettingsCard(
                    notificationEnabled = notificationEnabled,
                    notificationPermissionState = notificationPermissionState,
                    showTroubleshooting = showTroubleshooting,
                    onShowTroubleshootingChanged = { showTroubleshooting = it },
                    expandTroubleshooting = expandNotificationTroubleshooting,
                    onTroubleshootingExpanded = onNotificationTroubleshootingExpanded,
                    onNotificationEnabledChanged = onNotificationEnabledChanged,
                    onRequestNotificationPermission = onRequestNotificationPermission,
                    onRequestExactAlarmPermission = onRequestExactAlarmPermission,
                    onRefreshNotificationPermissions = onRefreshNotificationPermissions,
                    onSendTestNotification = {
                        dialog = if (onSendTestNotification()) {
                            SettingsDialog.TestNotificationSent
                        } else {
                            SettingsDialog.TestNotificationBlocked
                        }
                    },
                    onScheduleTestNotification = {
                        dialog = if (onScheduleTestNotification()) {
                            SettingsDialog.TestNotificationScheduled
                        } else {
                            SettingsDialog.TestNotificationBlocked
                        }
                    },
                )
            }
            item {
                RoutineSectionLabel(title = "앱 정보")
            }
            item {
                AppInfoCard(
                    onGuideClick = { dialog = SettingsDialog.Guide },
                    onDisclaimerClick = { dialog = SettingsDialog.Disclaimer },
                )
            }
            item {
                RoutineSectionLabel(title = "데이터")
            }
            item {
                DataResetCard(onClick = { dialog = SettingsDialog.Reset })
            }
        }
    }

    when (dialog) {
        SettingsDialog.Reset -> ResetDialog(
            onDismiss = { dialog = null },
            onConfirm = {
                onResetRoutineData()
                resetFeedbackVisible = true
                dialog = null
            },
        )
        SettingsDialog.Guide -> InfoDialog(
            title = "사용 가이드",
            body = "1. 오늘 화면의 + 버튼으로 복용할 영양제를 등록합니다.\n\n2. 오늘 화면에서 복용할 항목을 확인하고 완료 여부를 체크합니다.\n\n3. 기록 화면에서 최근 완료율을 확인합니다.\n\n이 앱은 복용 기록을 돕는 도구이며, 의학적 조언을 제공하지 않습니다.",
            onDismiss = { dialog = null },
        )
        SettingsDialog.Disclaimer -> InfoDialog(
            title = "면책 고지",
            body = "Supplement Routine은 영양제 복용 루틴을 관리하기 위한 기록 도구입니다. 특정 제품, 성분, 복용량, 치료 효과를 추천하지 않습니다. 건강 상태나 복용량에 관한 결정은 의료 전문가와 상의하세요.",
            onDismiss = { dialog = null },
        )
        SettingsDialog.TestNotificationSent -> InfoDialog(
            title = "테스트 알림 전송",
            body = "알림이 표시되었는지 알림 패널에서 확인해보세요.",
            onDismiss = { dialog = null },
        )
        SettingsDialog.TestNotificationBlocked -> InfoDialog(
            title = "알림 권한 필요",
            body = "테스트 알림을 보내려면 알림 표시 권한을 먼저 허용해야 합니다.",
            onDismiss = { dialog = null },
        )
        SettingsDialog.TestNotificationScheduled -> InfoDialog(
            title = "테스트 알림 예약",
            body = "15초 뒤 알림이 표시되는지 확인해보세요.",
            onDismiss = { dialog = null },
        )
        null -> Unit
    }
}

@Composable
private fun MealTimesCard(
    mealTimeSettings: MealTimeSettings,
    editing: Boolean,
    changed: Boolean,
    onStartEditing: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBreakfastTimeChanged: (TimeOfDayValue) -> Unit,
    onLunchTimeChanged: (TimeOfDayValue) -> Unit,
    onDinnerTimeChanged: (TimeOfDayValue) -> Unit,
) {
    RoutineCard {
        Column(
            modifier = Modifier.padding(
                horizontal = if (editing) SrSpacing.Md else 0.dp,
                vertical = SrSpacing.Xs,
            ),
        ) {
            if (!editing) {
                CompactSettingsRow(
                    icon = SrIcon.Clock,
                    title = "식사 시간 설정",
                    description = "아침 ${formatTime(mealTimeSettings.breakfastTime)}\n점심 ${formatTime(mealTimeSettings.lunchTime)}\n저녁 ${formatTime(mealTimeSettings.dinnerTime)}",
                    trailing = "›",
                    onClick = onStartEditing,
                )
            } else {
                MealTimeDisplayRow(
                    title = "아침",
                    time = mealTimeSettings.breakfastTime,
                    onTimeChanged = onBreakfastTimeChanged,
                )
                MealTimeDisplayRow(
                    title = "점심",
                    time = mealTimeSettings.lunchTime,
                    onTimeChanged = onLunchTimeChanged,
                )
                MealTimeDisplayRow(
                    title = "저녁",
                    time = mealTimeSettings.dinnerTime,
                    onTimeChanged = onDinnerTimeChanged,
                )
                RoutinePillButton(
                    text = "식사 시간 저장",
                    icon = SrIcon.Clock,
                    onClick = onSave,
                    modifier = Modifier.padding(horizontal = SrSpacing.Md),
                    height = SrSize.ControlSmall,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (changed) "변경된 시간이 아직 저장되지 않았습니다." else "시간을 10분 단위로 조정할 수 있습니다.",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onCancel) {
                        Text("취소")
                    }
                }
            }
        }
    }
}

@Composable
private fun MealTimeDisplayRow(
    title: String,
    time: TimeOfDayValue,
    onTimeChanged: (TimeOfDayValue) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SrSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        TextButton(onClick = { onTimeChanged(time.plusMinutes(-10)) }) {
            Text("-10")
        }
        TextButton(onClick = { onTimeChanged(time.plusMinutes(10)) }) {
            Text("+10")
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    notificationEnabled: Boolean,
    notificationPermissionState: NotificationPermissionState,
    showTroubleshooting: Boolean,
    onShowTroubleshootingChanged: (Boolean) -> Unit,
    expandTroubleshooting: Boolean,
    onTroubleshootingExpanded: () -> Unit,
    onNotificationEnabledChanged: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onRefreshNotificationPermissions: () -> Unit,
    onSendTestNotification: () -> Unit,
    onScheduleTestNotification: () -> Unit,
) {
    LaunchedEffect(expandTroubleshooting) {
        if (expandTroubleshooting) {
            onShowTroubleshootingChanged(true)
            onTroubleshootingExpanded()
        }
    }

    RoutineCard {
        Column(
            modifier = Modifier.padding(vertical = SrSpacing.Xs),
        ) {
            NotificationDefaultRow(
                enabled = notificationEnabled,
                onEnabledChanged = onNotificationEnabledChanged,
            )
            CompactSettingsRow(
                icon = SrIcon.Bell,
                title = "정확한 알림 권한",
                description = if (notificationPermissionState.canScheduleExactAlarms) "허용됨" else "설정 필요",
                trailing = "›",
                onClick = if (notificationPermissionState.canScheduleExactAlarms) {
                    onRefreshNotificationPermissions
                } else {
                    onRequestExactAlarmPermission
                },
            )
            CompactSettingsRow(
                icon = SrIcon.Clock,
                title = if (showTroubleshooting) "알림 문제 해결 접기" else "알림 문제 해결",
                description = "테스트 알림과 권한 상태를 확인합니다.",
                trailing = "›",
                onClick = { onShowTroubleshootingChanged(!showTroubleshooting) },
            )
            if (showTroubleshooting) {
                PermissionActionRow(
                    icon = SrIcon.Bell,
                    title = "알림 표시 권한",
                    description = if (notificationPermissionState.canPostNotifications) {
                        "허용됨. 복용 알림을 표시할 수 있습니다."
                    } else {
                        "Android 13 이상에서는 알림 표시 권한이 필요합니다."
                    },
                    actionLabel = if (notificationPermissionState.canPostNotifications) "새로고침" else "권한 요청",
                    onClick = if (notificationPermissionState.canPostNotifications) {
                        onRefreshNotificationPermissions
                    } else {
                        onRequestNotificationPermission
                    },
                )
                PermissionActionRow(
                    icon = SrIcon.Clock,
                    title = "정확한 알림 권한",
                    description = if (notificationPermissionState.canScheduleExactAlarms) {
                        "허용됨. 정해진 시간에 정확히 알립니다."
                    } else {
                        "정확한 시간 알림은 설정에서 허용할 수 있습니다."
                    },
                    actionLabel = if (notificationPermissionState.canScheduleExactAlarms) "새로고침" else "설정",
                    onClick = if (notificationPermissionState.canScheduleExactAlarms) {
                        onRefreshNotificationPermissions
                    } else {
                        onRequestExactAlarmPermission
                    },
                )
                PermissionActionRow(
                    icon = SrIcon.Bell,
                    title = "테스트 알림",
                    description = "복용 알림 표시를 바로 확인합니다.",
                    actionLabel = "보내기",
                    onClick = onSendTestNotification,
                )
                PermissionActionRow(
                    icon = SrIcon.Clock,
                    title = "예약 테스트 알림",
                    description = "15초 뒤 알림을 예약합니다.",
                    actionLabel = "예약",
                    onClick = onScheduleTestNotification,
                )
            }
        }
    }
}

@Composable
private fun AppInfoCard(
    onGuideClick: () -> Unit,
    onDisclaimerClick: () -> Unit,
) {
    RoutineCard {
        Column(modifier = Modifier.padding(vertical = SrSpacing.Sm)) {
            InfoActionRow(
                icon = SrIcon.Chevron,
                title = "앱 사용 가이드",
                onClick = onGuideClick,
            )
            InfoActionRow(
                icon = SrIcon.Calendar,
                title = "면책 고지",
                onClick = onDisclaimerClick,
            )
            StaticInfoRow(title = "버전", value = "1.0.0")
        }
    }
}

@Composable
private fun DataResetCard(onClick: () -> Unit) {
    RoutineCard {
        DestructiveRow(
            title = "데이터 초기화",
            description = "등록한 영양제와 기록을 삭제합니다.",
            onClick = onClick,
        )
    }
}

@Composable
private fun PermissionActionRow(
    icon: SrIcon,
    title: String,
    description: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SrSpacing.Xxs),
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineIconBadge(icon = icon)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = onClick) {
            Text(actionLabel)
        }
    }
}

@Composable
private fun NotificationDefaultRow(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = SrSpacing.Md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBadge(icon = SrIcon.Bell, modifier = Modifier.height(40.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "새 영양제 알림 기본값",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (enabled) "기본 켜짐" else "기본 꺼짐",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChanged,
        )
    }
}

@Composable
private fun CompactSettingsRow(
    icon: SrIcon,
    title: String,
    description: String,
    trailing: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = SrSpacing.Lg, vertical = SrSpacing.Md),
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineIconBadge(icon = icon, modifier = Modifier.height(40.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = trailing,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DestructiveRow(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SrSpacing.Md, vertical = SrSpacing.Md),
            horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBadge(
                icon = SrIcon.Trash,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                tint = MaterialTheme.colorScheme.error,
            )
            Column(verticalArrangement = Arrangement.spacedBy(SrSpacing.Xxs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InfoActionRow(
    icon: SrIcon,
    title: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SrSpacing.Md, vertical = SrSpacing.Md),
            horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBadge(icon = icon)
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun StaticInfoRow(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SrSpacing.Md, vertical = SrSpacing.Md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ResetDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var confirmText by remember { mutableStateOf("") }
    val canReset = confirmText.trim() == "초기화"

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = { Text("복용 데이터 초기화") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SrSpacing.Md)) {
                Text("등록한 영양제와 복용 기록을 삭제합니다. 이 작업은 되돌릴 수 없습니다.")
                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    label = { Text("초기화 입력") },
                    placeholder = { Text("초기화") },
                    supportingText = { Text("계속하려면 아래에 초기화를 입력해주세요.") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = canReset,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("초기화")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
    )
}

@Composable
private fun InfoDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
    )
}

internal enum class SettingsDialog {
    Reset,
    Guide,
    Disclaimer,
    TestNotificationSent,
    TestNotificationBlocked,
    TestNotificationScheduled,
}

@Composable
private fun ResetFeedbackCard() {
    RoutineCard(
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Text(
            text = "데이터를 초기화했어요.",
            modifier = Modifier.padding(SrSpacing.Lg),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun TimeOfDayValue.plusMinutes(minutes: Int): TimeOfDayValue {
    val totalMinutes = ((hour * 60 + minute + minutes) % (24 * 60) + (24 * 60)) % (24 * 60)
    return TimeOfDayValue(
        hour = totalMinutes / 60,
        minute = totalMinutes % 60,
    )
}
