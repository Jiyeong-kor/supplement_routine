package com.jiyeong.supplementroutine.kmp.android.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatTime
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineIconBadge
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePillButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSectionLabel
import com.jiyeong.supplementroutine.kmp.android.notification.NotificationPermissionState
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue

@Composable
fun SettingsRoute(
    contentPadding: PaddingValues,
    mealTimeSettings: MealTimeSettings,
    notificationEnabled: Boolean,
    notificationPermissionState: NotificationPermissionState,
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
) {
    var dialog by remember { mutableStateOf<SettingsDialog?>(null) }
    var mealTimesEditing by remember { mutableStateOf(false) }
    var draftMealTimeSettings by remember { mutableStateOf(mealTimeSettings) }

    LaunchedEffect(mealTimeSettings, mealTimesEditing) {
        if (!mealTimesEditing) {
            draftMealTimeSettings = mealTimeSettings
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                RoutinePageHeader(
                    title = "설정",
                    subtitle = "루틴 기준과 알림을 조정해요",
                )
            }
            item {
                RoutineSectionLabel(title = "식사 시간")
            }
            item {
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
            item {
                RoutineSectionLabel(title = "알림")
            }
            item {
                NotificationSettingsCard(
                    notificationEnabled = notificationEnabled,
                    notificationPermissionState = notificationPermissionState,
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
                dialog = null
            },
        )
        SettingsDialog.Guide -> InfoDialog(
            title = "사용 가이드",
            body = "1. 영양제 화면에서 복용할 영양제를 등록합니다.\n\n2. 오늘 화면에서 복용할 항목을 확인하고 완료 여부를 체크합니다.\n\n3. 기록 화면에서 최근 완료율을 확인합니다.\n\n이 앱은 복용 기록을 돕는 도구이며, 의학적 조언을 제공하지 않습니다.",
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
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MealTimeDisplayRow(
                icon = Icons.Outlined.LightMode,
                title = "아침",
                time = mealTimeSettings.breakfastTime,
                editing = editing,
                onTimeChanged = onBreakfastTimeChanged,
            )
            MealTimeDisplayRow(
                icon = Icons.Outlined.Coffee,
                title = "점심",
                time = mealTimeSettings.lunchTime,
                editing = editing,
                onTimeChanged = onLunchTimeChanged,
            )
            MealTimeDisplayRow(
                icon = Icons.Outlined.ModeNight,
                title = "저녁",
                time = mealTimeSettings.dinnerTime,
                editing = editing,
                onTimeChanged = onDinnerTimeChanged,
            )
            RoutinePillButton(
                text = if (editing) "식사 시간 저장" else "식사 시간 편집",
                icon = Icons.Outlined.Restaurant,
                onClick = if (editing) onSave else onStartEditing,
                height = 38.dp,
            )
            if (editing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
    icon: ImageVector,
    title: String,
    time: TimeOfDayValue,
    editing: Boolean,
    onTimeChanged: (TimeOfDayValue) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineIconBadge(
            icon = icon,
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.62f),
            tint = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        if (editing) {
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = { onTimeChanged(time.plusMinutes(-10)) }) {
                Text("-10")
            }
            TextButton(onClick = { onTimeChanged(time.plusMinutes(10)) }) {
                Text("+10")
            }
        } else {
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    notificationEnabled: Boolean,
    notificationPermissionState: NotificationPermissionState,
    onNotificationEnabledChanged: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onRefreshNotificationPermissions: () -> Unit,
    onSendTestNotification: () -> Unit,
    onScheduleTestNotification: () -> Unit,
) {
    var showTroubleshooting by remember { mutableStateOf(false) }

    RoutineCard {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NotificationDefaultRow(
                enabled = notificationEnabled,
                onEnabledChanged = onNotificationEnabledChanged,
            )
            RoutinePillButton(
                text = if (showTroubleshooting) "알림 문제 해결 접기" else "알림 문제 해결",
                icon = Icons.Outlined.Alarm,
                onClick = { showTroubleshooting = !showTroubleshooting },
                height = 38.dp,
            )
            if (showTroubleshooting) {
                PermissionActionRow(
                    icon = Icons.Outlined.Notifications,
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
                    icon = Icons.Outlined.Alarm,
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
                    icon = Icons.Outlined.Notifications,
                    title = "테스트 알림",
                    description = "복용 알림 표시를 바로 확인합니다.",
                    actionLabel = "보내기",
                    onClick = onSendTestNotification,
                )
                PermissionActionRow(
                    icon = Icons.Outlined.Alarm,
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
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            InfoActionRow(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = "앱 사용 가이드",
                onClick = onGuideClick,
            )
            InfoActionRow(
                icon = Icons.Outlined.Info,
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
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineIconBadge(icon = icon)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    RoutineCard {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineIconBadge(icon = icon)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
    }
}

@Composable
private fun MealTimeEditorRow(
    title: String,
    time: TimeOfDayValue,
    onTimeChanged: (TimeOfDayValue) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Row {
            IconButton(onClick = { onTimeChanged(time.plusMinutes(-10)) }) {
                Text("-10")
            }
            IconButton(onClick = { onTimeChanged(time.plusMinutes(10)) }) {
                Text("+10")
            }
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
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBadge(icon = Icons.Outlined.Notifications)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "새 영양제 알림 기본값",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (enabled) "새로 등록하는 영양제 알림을 기본으로 켭니다." else "새로 등록하는 영양제 알림을 기본으로 끕니다.",
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
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBadge(
                icon = Icons.Outlined.DeleteForever,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                tint = MaterialTheme.colorScheme.error,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
    icon: ImageVector,
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
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            .padding(horizontal = 14.dp, vertical = 10.dp),
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
        title = { Text("복용 데이터 초기화") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            ) {
                Text("초기화", color = MaterialTheme.colorScheme.error)
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
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
    )
}

private enum class SettingsDialog {
    Reset,
    Guide,
    Disclaimer,
    TestNotificationSent,
    TestNotificationBlocked,
    TestNotificationScheduled,
}

private fun TimeOfDayValue.plusMinutes(minutes: Int): TimeOfDayValue {
    val totalMinutes = ((hour * 60 + minute + minutes) % (24 * 60) + (24 * 60)) % (24 * 60)
    return TimeOfDayValue(
        hour = totalMinutes / 60,
        minute = totalMinutes % 60,
    )
}
