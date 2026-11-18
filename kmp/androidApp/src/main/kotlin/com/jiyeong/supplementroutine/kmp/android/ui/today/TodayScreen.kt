package com.jiyeong.supplementroutine.kmp.android.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.scheduling.ScheduledIntakeRecord
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatDosage
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatTime
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineFab
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineIconBadge
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePillButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSectionLabel
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSlotCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.scheduleLabelText
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrIcon
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSize
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSpacing
import kotlinx.coroutines.delay
import java.util.Calendar
import com.jiyeong.supplementroutine.kmp.android.ui.common.DailyIntakeRow
import com.jiyeong.supplementroutine.kmp.android.ui.common.DailyProgressSummary
import com.jiyeong.supplementroutine.kmp.android.ui.common.DailyWelcomeCard

internal class TodayScreenState {
    var completionFeedback by mutableStateOf<CompletionFeedback?>(null)
}

@Composable
internal fun TodayRoute(
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
    state: TodayScreenState = remember { TodayScreenState() },
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
        state = state,
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
    state: TodayScreenState,
) = with(state) {
    val inspectionMode = LocalInspectionMode.current
    val sortedItems = remember(items) {
        items.sortedWith(
            compareBy<ScheduledIntakeRecord> { it.record.scheduledTime.hour }
                .thenBy { it.record.scheduledTime.minute },
        )
    }
    val incompleteItems = sortedItems.filterNot { it.record.isDone }
    val nextItem = sortedItems.firstOrNull { !it.record.isDone }
    val listState = rememberLazyListState()
    val willShowFeedback = completionFeedback != null || feedbackMessage != null
    val highlightedItemKey = incompleteItems
        .firstOrNull { it.supplement.id == highlightedSupplementId }
        ?.record
        ?.id
        ?.let { id -> "intake-$id" }
    val renderedItemKeys = buildList {
        add("today-header")
        add("today-progress")
        if (notificationNeedsAttention && !notificationAttentionDismissed) add("notification-attention")
        if (willShowFeedback) add("completion-feedback")
        if (errorMessage != null) add("error-message")
        if (items.isNotEmpty()) add("routine-section")
        sortedItems.forEach { item -> add("intake-${item.record.id}") }
    }
    val highlightedItemIndex = highlightedItemKey?.let(renderedItemKeys::indexOf) ?: -1

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            completionFeedback = CompletionFeedback(
                message = it,
                durationMillis = 1200,
            )
            onFeedbackMessageConsumed()
        }
    }

    LaunchedEffect(completionFeedback) {
        completionFeedback?.takeUnless { inspectionMode }?.let { feedback ->
            delay(feedback.durationMillis)
            completionFeedback = null
        }
    }

    LaunchedEffect(highlightedSupplementId, highlightedItemIndex) {
        if (highlightedSupplementId != null && highlightedItemIndex >= 0) {
            listState.animateScrollToItem(highlightedItemIndex)
            delay(3600)
            onHighlightedSupplementConsumed()
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
                bottom = contentPadding.calculateBottomPadding() + SrSize.BottomNavigationHeight + SrSpacing.ScreenBottomBuffer,
            ),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Lg),
        ) {
            item(key = "today-header") {
                TodayHeader(date = date)
            }
            item(key = "today-progress") {
                if (items.isEmpty()) {
                    DailyWelcomeCard(onAddClick = onAddSupplementClick)
                } else {
                    DailyProgressSummary(
                        done = items.count { it.record.isDone },
                        total = items.size,
                        nextIntake = nextItem?.let {
                            "${formatTime(it.record.scheduledTime)}  ${it.supplement.name}"
                        },
                    )
                }
            }
            if (notificationNeedsAttention && !notificationAttentionDismissed) {
                item(key = "notification-attention") {
                    NotificationAttentionCard(
                        onOpenSettingsClick = onOpenNotificationSettingsClick,
                        onDismissClick = onDismissNotificationAttention,
                    )
                }
            }
            completionFeedback?.let { feedback ->
                item(key = "completion-feedback") {
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
                item(key = "error-message") { ErrorCard(message = message) }
            }
            if (items.isNotEmpty()) {
                item(key = "routine-section") {
                    RoutineSectionLabel(
                        title = "복용 일정",
                        trailing = "${incompleteItems.size}개 남음",
                    )
                }
                items(
                    items = sortedItems,
                    key = { "intake-${it.record.id}" },
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
            }
        }

        if (items.isNotEmpty()) {
            RoutineFab(
                onClick = onAddSupplementClick,
                icon = SrIcon.Plus,
                contentDescription = "영양제 관리 열기",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = SrSpacing.ScreenHorizontal,
                        bottom = contentPadding.calculateBottomPadding() + SrSpacing.Lg,
                    ),
            )
        }
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
            modifier = Modifier.padding(SrSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Md),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoutineIconBadge(
                    icon = SrIcon.Bell,
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
                height = SrSize.ControlSmall,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
            RoutinePillButton(
                text = "오늘은 숨기기",
                onClick = onDismissClick,
                height = SrSize.ControlCompact,
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
            modifier = Modifier.padding(SrSpacing.Lg),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun TodayHeader(date: LocalDateValue) {
    RoutinePageHeader(
        title = "오늘",
        eyebrow = "${date.month}월 ${date.day}일 ${weekdayLabel(date)}요일",
    )
}

@Composable
private fun TodaySupplementItem(
    item: ScheduledIntakeRecord,
    highlighted: Boolean = false,
    onClick: () -> Unit,
) {
    DailyIntakeRow(
        name = item.supplement.name,
        schedule = "${formatTime(item.record.scheduledTime)} / ${scheduleLabelText(item.label)}",
        dosage = "1회 ${formatDosage(item.supplement.dosageValue)}${item.supplement.dosageUnit} / 하루 ${item.supplement.dailyCount}회",
        checked = item.record.isDone,
        highlighted = highlighted,
        onToggle = onClick,
    )
}

@Composable
private fun CompletionFeedbackCard(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    RoutineSlotCard(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        leading = {
            RoutineIconBadge(
                icon = SrIcon.Calendar,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        },
        title = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = if (actionLabel == null || onActionClick == null) {
            null
        } else {
            {
                RoutinePillButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    height = SrSize.CalendarDay,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

internal data class CompletionFeedback(
    val message: String,
    val actionLabel: String? = null,
    val actionRecord: IntakeRecord? = null,
    val durationMillis: Long = 2200,
)

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
