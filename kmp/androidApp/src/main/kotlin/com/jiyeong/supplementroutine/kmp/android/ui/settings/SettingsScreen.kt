package com.jiyeong.supplementroutine.kmp.android.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.jiyeong.supplementroutine.kmp.android.ui.common.routineGlassBorder
import com.jiyeong.supplementroutine.kmp.android.ui.common.routineGlassCardColors
import com.jiyeong.supplementroutine.kmp.android.ui.common.routineGlassCardElevation
import com.jiyeong.supplementroutine.kmp.android.ui.common.routineGlassSheen
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "설정",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "복용 시간, 기본 알림, 앱 정보를 관리합니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                SettingsCard(title = "기본값") {
                    SettingsRow(
                        icon = Icons.Outlined.Restaurant,
                        title = "식사 시간",
                        description = "식사 기준 복용 일정에 바로 반영됩니다.",
                    )
                    MealTimeEditorRow(
                        title = "아침",
                        time = mealTimeSettings.breakfastTime,
                        onTimeChanged = onBreakfastTimeChanged,
                    )
                    MealTimeEditorRow(
                        title = "점심",
                        time = mealTimeSettings.lunchTime,
                        onTimeChanged = onLunchTimeChanged,
                    )
                    MealTimeEditorRow(
                        title = "저녁",
                        time = mealTimeSettings.dinnerTime,
                        onTimeChanged = onDinnerTimeChanged,
                    )
                    NotificationDefaultRow(
                        enabled = notificationEnabled,
                        onEnabledChanged = onNotificationEnabledChanged,
                    )
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
                            "허용됨. 정해진 시간에 복용 알림을 예약할 수 있습니다."
                        } else {
                            "권한이 없어도 근처 시간에 알림을 예약합니다. 더 정확한 알림은 설정에서 허용하세요."
                        },
                        actionLabel = if (notificationPermissionState.canScheduleExactAlarms) "새로고침" else "정확 알림 설정",
                        onClick = if (notificationPermissionState.canScheduleExactAlarms) {
                            onRefreshNotificationPermissions
                        } else {
                            onRequestExactAlarmPermission
                        },
                    )
                    PermissionActionRow(
                        icon = Icons.Outlined.Notifications,
                        title = "테스트 알림",
                        description = "복용 알림이 실제로 표시되는지 바로 확인합니다.",
                        actionLabel = "보내기",
                        onClick = {
                            dialog = if (onSendTestNotification()) {
                                SettingsDialog.TestNotificationSent
                            } else {
                                SettingsDialog.TestNotificationBlocked
                            }
                        },
                    )
                    PermissionActionRow(
                        icon = Icons.Outlined.Alarm,
                        title = "예약 테스트 알림",
                        description = "15초 뒤 알림을 예약해 실제 발화 경로를 확인합니다.",
                        actionLabel = "예약",
                        onClick = {
                            dialog = if (onScheduleTestNotification()) {
                                SettingsDialog.TestNotificationScheduled
                            } else {
                                SettingsDialog.TestNotificationBlocked
                            }
                        },
                    )
                }
            }
            item {
                SettingsCard(title = "데이터") {
                    DestructiveRow(
                        title = "복용 데이터 초기화",
                        description = "등록한 영양제와 복용 기록을 삭제합니다.",
                        onClick = { dialog = SettingsDialog.Reset },
                    )
                }
            }
            item {
                SettingsCard(title = "정보") {
                    InfoActionRow(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = "사용 가이드",
                        onClick = { dialog = SettingsDialog.Guide },
                    )
                    InfoActionRow(
                        icon = Icons.Outlined.Info,
                        title = "면책 고지",
                        onClick = { dialog = SettingsDialog.Disclaimer },
                    )
                    SettingsRow(
                        icon = Icons.Outlined.Info,
                        title = "버전",
                        description = "1.0.0",
                    )
                }
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
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
    Card(
        colors = routineGlassCardColors(),
        elevation = routineGlassCardElevation(),
        border = routineGlassBorder(),
        modifier = Modifier.routineGlassSheen(),
    ) {
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteForever,
                contentDescription = null,
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
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
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
private fun ResetDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("복용 데이터 초기화") },
        text = { Text("등록한 영양제와 복용 기록을 삭제합니다. 이 작업은 되돌릴 수 없습니다.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
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
