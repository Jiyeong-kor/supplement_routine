package com.jiyeong.supplementroutine.kmp.android.ui.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import com.jiyeong.supplementroutine.shared.scheduling.ScheduledIntakeRecord
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatDosage
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatHourMinute
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatTime
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCheckButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineFab
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineIconBadge
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineMetaChip
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePillButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineProgressLeaves
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSectionLabel
import com.jiyeong.supplementroutine.kmp.android.ui.common.scheduleLabelText
import com.jiyeong.supplementroutine.kmp.android.ui.common.GardenUi
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun TodayRoute(
    contentPadding: PaddingValues,
    date: LocalDateValue,
    items: List<ScheduledIntakeRecord>,
    errorMessage: String?,
    notificationNeedsAttention: Boolean,
    notificationAttentionDismissed: Boolean,
    onDismissNotificationAttention: () -> Unit,
    highlightedSupplementId: String?,
    onHighlightedSupplementConsumed: () -> Unit,
    feedbackMessage: String?,
    onFeedbackMessageConsumed: () -> Unit,
    onAddSupplementClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onToggleRecord: (IntakeRecord) -> Unit,
) {
    TodayScreen(
        contentPadding = contentPadding,
        date = date,
        items = items,
        errorMessage = errorMessage,
        notificationNeedsAttention = notificationNeedsAttention,
        notificationAttentionDismissed = notificationAttentionDismissed,
        onDismissNotificationAttention = onDismissNotificationAttention,
        highlightedSupplementId = highlightedSupplementId,
        onHighlightedSupplementConsumed = onHighlightedSupplementConsumed,
        feedbackMessage = feedbackMessage,
        onFeedbackMessageConsumed = onFeedbackMessageConsumed,
        onAddSupplementClick = onAddSupplementClick,
        onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
        onToggleRecord = onToggleRecord,
    )
}

@Composable
private fun TodayScreen(
    contentPadding: PaddingValues,
    date: LocalDateValue,
    items: List<ScheduledIntakeRecord>,
    errorMessage: String?,
    notificationNeedsAttention: Boolean,
    notificationAttentionDismissed: Boolean,
    onDismissNotificationAttention: () -> Unit,
    highlightedSupplementId: String?,
    onHighlightedSupplementConsumed: () -> Unit,
    feedbackMessage: String?,
    onFeedbackMessageConsumed: () -> Unit,
    onAddSupplementClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onToggleRecord: (IntakeRecord) -> Unit,
) {
    var completionFeedback by remember { mutableStateOf<CompletionFeedback?>(null) }
    var showCompletedItems by remember { mutableStateOf(false) }
    val sortedItems = remember(items) {
        items.sortedWith(
            compareBy<ScheduledIntakeRecord> { it.record.isDone }
                .thenBy { it.record.scheduledTime.hour }
                .thenBy { it.record.scheduledTime.minute },
        )
    }
    val incompleteItems = sortedItems.filterNot { it.record.isDone }
    val completedItems = sortedItems.filter { it.record.isDone }
    val shouldShowCompletedItems = showCompletedItems
    val nextItem = sortedItems.firstOrNull { !it.record.isDone }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            completionFeedback = CompletionFeedback(message = it)
            onFeedbackMessageConsumed()
        }
    }

    LaunchedEffect(completionFeedback?.message) {
        if (completionFeedback != null) {
            delay(2200)
            completionFeedback = null
        }
    }

    LaunchedEffect(highlightedSupplementId) {
        if (highlightedSupplementId != null) {
            delay(3600)
            onHighlightedSupplementConsumed()
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
                bottom = contentPadding.calculateBottomPadding() + 104.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                TodayHeader(date = date)
            }
            item {
                TodayProgressCard(
                    done = items.count { it.record.isDone },
                    total = items.size,
                    nextItem = nextItem,
                )
            }
            if (notificationNeedsAttention && !notificationAttentionDismissed) {
                item {
                    NotificationAttentionCard(
                        onOpenSettingsClick = onOpenNotificationSettingsClick,
                        onDismissClick = onDismissNotificationAttention,
                    )
                }
            }
            completionFeedback?.let { feedback ->
                item {
                    CompletionFeedbackCard(
                        message = feedback.message,
                        actionLabel = feedback.actionLabel,
                        onActionClick = feedback.actionRecord?.let { record ->
                            {
                                completionFeedback = null
                                onToggleRecord(record)
                            }
                        },
                    )
                }
            }
            errorMessage?.let { message ->
                item { ErrorCard(message = message) }
            }
            item {
                RoutineSectionLabel(
                    title = "복용 목록",
                    trailing = "${items.count { it.record.isDone }}/${items.size}",
                )
            }
            if (items.isEmpty()) {
                item { TodayEmptyState(onAddSupplementClick = onAddSupplementClick) }
            } else {
                items(
                    items = incompleteItems,
                    key = { it.record.id },
                ) { item ->
                    TodaySupplementItem(
                        item = item,
                        highlighted = item.supplement.id == highlightedSupplementId,
                        onClick = {
                            if (!item.record.isDone) {
                                completionFeedback = CompletionFeedback(
                                    message = "${item.supplement.name} 기록에 반영됐어요.",
                                )
                            } else {
                                completionFeedback = CompletionFeedback(
                                    message = "${item.supplement.name} 기록을 되돌렸어요.",
                                    actionLabel = "다시 완료",
                                    actionRecord = item.record.markUndone(),
                                )
                            }
                            onToggleRecord(item.record)
                        },
                    )
                }
                if (completedItems.isNotEmpty()) {
                    item {
                        RoutinePillButton(
                            text = if (shouldShowCompletedItems) {
                                "완료한 복용 접기"
                            } else {
                                "완료한 복용 ${completedItems.size}개 보기"
                            },
                            onClick = { showCompletedItems = !showCompletedItems },
                            height = 38.dp,
                            containerColor = GardenUi.SurfaceSoft,
                            contentColor = GardenUi.Ink,
                        )
                    }
                    if (shouldShowCompletedItems) {
                        items(
                            items = completedItems,
                            key = { "done-${it.record.id}" },
                        ) { item ->
                            TodaySupplementItem(
                                item = item,
                                highlighted = item.supplement.id == highlightedSupplementId,
                                onClick = {
                                    completionFeedback = CompletionFeedback(
                                        message = "${item.supplement.name} 기록을 되돌렸어요.",
                                        actionLabel = "다시 완료",
                                        actionRecord = item.record.markUndone(),
                                    )
                                    onToggleRecord(item.record)
                                },
                            )
                        }
                    }
                }
            }
        }

        RoutineFab(
            onClick = onAddSupplementClick,
            icon = Icons.Filled.Add,
            contentDescription = "영양제 추가 화면으로 이동",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 20.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp,
                ),
        )
    }
}

@Composable
private fun NotificationAttentionCard(
    onOpenSettingsClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    RoutineCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoutineIconBadge(
                    icon = Icons.Outlined.NotificationsOff,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    tint = MaterialTheme.colorScheme.error,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "알림이 오지 않을 수 있어요",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "기기 설정에서 알림 권한을 허용해주세요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
            RoutinePillButton(
                text = "설정에서 허용하기",
                onClick = onOpenSettingsClick,
                height = 36.dp,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
            RoutinePillButton(
                text = "오늘은 숨기기",
                onClick = onDismissClick,
                height = 34.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    RoutineCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun TodayHeader(date: LocalDateValue) {
    RoutinePageHeader(
        title = "오늘 루틴",
        eyebrow = "${date.year}년 ${date.month}월 ${date.day}일 ${weekdayLabel(date)}요일",
    )
}

@Composable
private fun TodayProgressCard(
    done: Int,
    total: Int,
    nextItem: ScheduledIntakeRecord?,
) {
    val percent = if (total == 0) 0f else done.toFloat() / total.toFloat()
    val title = when {
        total == 0 -> "오늘 복용 일정 없음"
        nextItem == null -> "오늘 루틴 완료"
        else -> "다음 복용"
    }
    val nextText = when {
        total == 0 -> "영양제를 등록하면 일정이 여기에 보여요"
        nextItem == null -> "오늘 남은 복용이 없습니다"
        else -> "${formatTime(nextItem.record.scheduledTime)} · ${nextItem.supplement.name}"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = GardenUi.MistBlue,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = GardenUi.Ink,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = nextText,
                    style = MaterialTheme.typography.titleMedium,
                    color = GardenUi.Ink,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$done / $total 완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = GardenUi.InkMuted,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "${(percent * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    color = GardenUi.Ink,
                    fontWeight = FontWeight.ExtraBold,
                )
                RoutineProgressLeaves(done = done, total = total)
            }
        }
    }
}

@Composable
private fun TodaySupplementItem(
    item: ScheduledIntakeRecord,
    highlighted: Boolean = false,
    onClick: () -> Unit,
) {
    val record = item.record
    val supplement = item.supplement
    val statusText = if (record.isDone) "완료됨" else "미완료"

    RoutineCard(
        colors = CardDefaults.cardColors(
            containerColor = if (record.isDone) {
                MaterialTheme.colorScheme.surfaceContainerHighest
            } else if (highlighted) {
                GardenUi.SuccessSurface
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (record.isDone) {
                        Modifier
                    } else {
                        Modifier.clickable(role = Role.Checkbox, onClick = onClick)
                    },
                )
                .semantics {
                    contentDescription = "${supplement.name}, ${formatTime(record.scheduledTime)}, $statusText"
                }
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            TimeBadge(time = record.scheduledTime, isDone = record.isDone)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = supplement.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (record.isDone) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = FontWeight.SemiBold,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    RoutineMetaChip(text = formatTime(record.scheduledTime))
                    RoutineMetaChip(text = scheduleLabelText(item.label))
                    RoutineMetaChip(text = "${formatDosage(supplement.dosageValue)} ${supplement.dosageUnit}")
                    if (highlighted) {
                        RoutineMetaChip(
                            text = "방금 추가됨",
                            containerColor = GardenUi.LeafGreen,
                            contentColor = GardenUi.Ink,
                        )
                    }
                }
                supplement.memo?.let { memo ->
                    Text(
                        text = memo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(role = Role.Checkbox, onClick = onClick)
                    .semantics {
                        contentDescription = if (record.isDone) {
                            "${supplement.name}, 완료됨, 되돌리기"
                        } else {
                            "${supplement.name}, 미완료, 완료로 표시"
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                RoutineCheckButton(
                    checked = record.isDone,
                    contentDescription = statusText,
                )
            }
        }
    }
}

@Composable
private fun TimeBadge(time: TimeOfDayValue, isDone: Boolean) {
    Surface(
        modifier = Modifier.size(width = 58.dp, height = 52.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (isDone) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (time.hour < 12) "오전" else "오후",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatHourMinute(time),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CompletionFeedbackCard(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    RoutineCard(
        colors = CardDefaults.cardColors(containerColor = GardenUi.SuccessSurface),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBadge(
                icon = Icons.Outlined.EventAvailable,
                containerColor = GardenUi.LeafGreen,
                tint = GardenUi.Ink,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                if (actionLabel != null && onActionClick != null) {
                    RoutinePillButton(
                        text = actionLabel,
                        onClick = onActionClick,
                        height = 32.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private data class CompletionFeedback(
    val message: String,
    val actionLabel: String? = null,
    val actionRecord: IntakeRecord? = null,
)

@Composable
private fun TodayEmptyState(onAddSupplementClick: () -> Unit) {
    RoutineCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RoutineIconBadge(
                icon = Icons.Outlined.Spa,
                modifier = Modifier.size(56.dp),
                containerColor = GardenUi.MistBlue,
                tint = GardenUi.PrimaryBlue,
            )
            Text(
                text = "첫 영양제를 추가해볼까요?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "비타민 D처럼 매일 먹는 항목부터 등록해보세요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            RoutinePillButton(
                text = "첫 영양제 추가하기",
                icon = Icons.Filled.Add,
                onClick = onAddSupplementClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

private fun weekdayLabel(date: LocalDateValue): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.month - 1)
        set(Calendar.DAY_OF_MONTH, date.day)
    }
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "월"
        Calendar.TUESDAY -> "화"
        Calendar.WEDNESDAY -> "수"
        Calendar.THURSDAY -> "목"
        Calendar.FRIDAY -> "금"
        Calendar.SATURDAY -> "토"
        Calendar.SUNDAY -> "일"
        else -> ""
    }
}
