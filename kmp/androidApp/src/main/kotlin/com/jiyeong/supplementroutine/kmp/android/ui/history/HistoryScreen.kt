package com.jiyeong.supplementroutine.kmp.android.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePillButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSectionLabel
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSlotCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineStatusCard
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrIcon
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSize
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSpacing
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrStroke
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.history.DailyHistorySummary
import com.jiyeong.supplementroutine.shared.history.HistoryViewState
import com.jiyeong.supplementroutine.shared.history.MissedIntakePeriod
import java.util.Calendar
import kotlin.math.roundToInt

internal class HistoryScreenState(date: LocalDateValue) {
    var selectedDate by mutableStateOf(date)
}

@Composable
internal fun HistoryRoute(
    contentPadding: PaddingValues,
    today: LocalDateValue,
    historyViewState: HistoryViewState,
    onOpenNotificationSettingsClick: () -> Unit,
    state: HistoryScreenState = remember(today.year, today.month) { HistoryScreenState(today) },
    listState: LazyListState = rememberLazyListState(),
) = with(state) {
    val selectedSummary = remember(historyViewState.monthSummaries, historyViewState.todaySummary, selectedDate) {
        historyViewState.monthSummaries.firstOrNull { it.date == selectedDate }
            ?: if (historyViewState.todaySummary.date == selectedDate) {
                historyViewState.todaySummary
            } else {
                DailyHistorySummary(date = selectedDate, doneCount = 0, totalCount = 0)
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = SrSpacing.ScreenHorizontal,
            top = SrSpacing.Lg,
            end = SrSpacing.ScreenHorizontal,
            bottom = contentPadding.calculateBottomPadding() + SrSpacing.Xxl,
        ),
        verticalArrangement = Arrangement.spacedBy(SrSpacing.Lg),
    ) {
        item {
            RoutinePageHeader(
                title = "기록",
                eyebrow = "${today.year}년 ${today.month}월",
            )
        }
        item { HistoryOverviewCard(summary = historyViewState.todaySummary) }
        item {
            RoutineSectionLabel(title = "이번 달 완료 상태")
        }
        item {
            MonthHistoryCard(
                summaries = historyViewState.monthSummaries,
                today = today,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
            )
        }
        item { SelectedDateSummaryCard(summary = selectedSummary) }
        item {
            RoutineSectionLabel(title = "최근 2주 기록")
        }
        if (!historyViewState.isEmpty) {
            item {
                RoutinePatternCard(
                    summaries = historyViewState.recentSummaries,
                    onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
                )
            }
        }
        if (historyViewState.isEmpty) {
            item { HistoryEmptyState() }
        } else {
            historyViewState.recentSummaries.forEach { summary ->
                item(key = "${summary.date.year}-${summary.date.month}-${summary.date.day}") {
                    HistoryItem(summary = summary)
                }
            }
        }
    }
}

@Composable
private fun HistoryOverviewCard(summary: DailyHistorySummary) {
    val percent = (summary.completionRate * 100).roundToInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 152.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(SrSpacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "오늘의 기록",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (summary.isEmpty) "아직 기록이 없습니다" else "$percent%",
                    style = if (summary.isEmpty) MaterialTheme.typography.titleLarge else MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = if (summary.isEmpty) "복용 후 오늘 화면에서 체크해 주세요." else "${summary.totalCount}개 중 ${summary.doneCount}개 완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun MonthHistoryCard(
    summaries: List<DailyHistorySummary>,
    today: LocalDateValue,
    selectedDate: LocalDateValue,
    onDateSelected: (LocalDateValue) -> Unit,
) {
    val firstWeekdayOffset = summaries.firstOrNull()?.date?.let { firstDate ->
        weekdayIndex(firstDate) % 7
    } ?: 0
    val tiles = List<DailyHistorySummary?>(firstWeekdayOffset) { null } + summaries

    RoutineCard {
        Column(
            modifier = Modifier.padding(SrSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Md),
        ) {
            Row {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            tiles.chunked(7).forEach { week ->
                Row(horizontalArrangement = Arrangement.spacedBy(SrSpacing.None)) {
                    week.forEach { summary ->
                        Box(modifier = Modifier.weight(1f)) {
                            if (summary != null) {
                                MonthDayTile(
                                    summary = summary,
                                    isToday = summary.date == today,
                                    selected = summary.date == selectedDate,
                                    onClick = { onDateSelected(summary.date) },
                                )
                            }
                        }
                    }
                    repeat(7 - week.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
            Legend()
        }
    }
}

@Composable
private fun MonthDayTile(
    summary: DailyHistorySummary,
    isToday: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val status = historyStatus(summary)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = SrSize.MinTouchTarget)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "${summary.date.day}일, ${status.label}, ${summary.totalCount}개 중 ${summary.doneCount}개 완료"
            },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(SrSize.CalendarDay),
            shape = CircleShape,
            color = status.background,
            border = when {
                selected -> BorderStroke(SrStroke.Strong, MaterialTheme.colorScheme.primary)
                isToday -> BorderStroke(SrStroke.Strong, MaterialTheme.colorScheme.onSurface)
                summary.isEmpty -> BorderStroke(SrStroke.Thin, MaterialTheme.colorScheme.outlineVariant)
                else -> null
            },
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = summary.date.day.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = status.foreground,
                    fontWeight = if (selected || isToday) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun RoutinePatternCard(
    summaries: List<DailyHistorySummary>,
    onOpenNotificationSettingsClick: () -> Unit,
) {
    val daysWithSchedule = summaries.filterNot { it.isEmpty }
    val missedDays = daysWithSchedule.count { it.doneCount < it.totalCount }
    val message = when {
        daysWithSchedule.isEmpty() -> "기록이 쌓이면 최근 루틴 흐름을 여기서 볼 수 있어요."
        missedDays == 0 -> "최근 복용 일정은 모두 잘 기록됐어요."
        missedDays == 1 -> "최근 2주 중 하루는 일부 복용이 남았어요."
        else -> "최근 2주 중 ${missedDays}일은 일부 복용이 남았어요."
    }
    val hint = when {
        daysWithSchedule.isEmpty() -> "오늘 화면에서 첫 기록을 남겨보세요."
        missedDays == 0 -> "지금 설정을 유지해도 좋아요."
        daysWithSchedule.size < 3 -> "기록이 조금 더 쌓이면 놓치기 쉬운 시간대를 알려드릴게요."
        else -> missedPeriodHint(daysWithSchedule)
    }
    val canOfferSettingsAction = daysWithSchedule.size >= 3 && missedDays > 0

    RoutineCard(
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(SrSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Xs),
        ) {
            Text(
                text = "최근 루틴 흐름",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            if (canOfferSettingsAction) {
                RoutinePillButton(
                    text = "알림/식사 시간 조정",
                    onClick = onOpenNotificationSettingsClick,
                    height = SrSize.ControlCompact,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private fun missedPeriodHint(summaries: List<DailyHistorySummary>): String {
    val missedPeriod = summaries
        .flatMap { summary -> summary.missedPeriodCounts.entries }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, counts) -> counts.sum() }
        .maxByOrNull { it.value }
        ?.takeIf { it.value > 0 }
        ?.key

    return when (missedPeriod) {
        MissedIntakePeriod.Morning -> "아침 복용을 자주 놓쳤어요. 아침 알림이나 식사 시간을 조정해보세요."
        MissedIntakePeriod.Afternoon -> "오후 복용을 자주 놓쳤어요. 점심 이후 알림을 확인해보세요."
        MissedIntakePeriod.Evening -> "저녁 복용을 자주 놓쳤어요. 저녁 식사 시간이나 알림을 조정해보세요."
        null -> "자주 놓치는 날이 보이면 알림을 켜거나 식사 시간을 조정해보세요."
    }
}

@Composable
private fun SelectedDateSummaryCard(summary: DailyHistorySummary) {
    val percent = (summary.completionRate * 100).roundToInt()
    val status = historyStatus(summary)

    RoutineSlotCard(
        leading = {
            Surface(
                modifier = Modifier.size(SrSize.IconBadge),
                shape = CircleShape,
                color = status.background,
                border = BorderStroke(SrStroke.Thin, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(status.icon.drawableRes),
                        contentDescription = null,
                        tint = status.foreground,
                        modifier = Modifier.size(SrSize.Icon),
                    )
                }
            }
        },
        title = {
            Text(
                text = "${summary.date.month}월 ${summary.date.day}일",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        supporting = {
            Text(
                text = if (summary.isEmpty) {
                    "이날은 복용 일정이 없습니다."
                } else {
                    "$percent% / ${summary.totalCount}개 중 ${summary.doneCount}개 완료"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun Legend() {
    val colors = MaterialTheme.colorScheme
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
    ) {
        LegendItem(SrIcon.Check, colors.primary, colors.onPrimary, "높음")
        LegendItem(SrIcon.Chevron, colors.secondaryContainer, colors.onSecondaryContainer, "보통")
        LegendItem(SrIcon.Close, colors.tertiaryContainer, colors.onTertiaryContainer, "낮음")
        LegendItem(SrIcon.Close, colors.surfaceContainer, colors.onSurfaceVariant, "없음")
    }
}

@Composable
private fun LegendItem(
    icon: SrIcon,
    background: Color,
    foreground: Color,
    label: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(color = background, shape = CircleShape) {
            Icon(
                painter = painterResource(icon.drawableRes),
                contentDescription = null,
                tint = foreground,
                modifier = Modifier.padding(SrSpacing.Xxs).size(SrSize.IconTiny),
            )
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun HistoryItem(summary: DailyHistorySummary) {
    val percent = (summary.completionRate * 100).roundToInt()
    val status = historyStatus(summary)

    RoutineSlotCard(
        leading = {
            Surface(
                modifier = Modifier.size(SrSize.IconBadge),
                shape = CircleShape,
                color = status.background,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = percent.toString(),
                        color = status.foreground,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        title = {
            Text(
                text = "${summary.date.month}월 ${summary.date.day}일",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        },
        supporting = {
            Text(
                text = "$percent% / ${summary.totalCount}개 중 ${summary.doneCount}개 완료",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        actions = {
            LinearProgressIndicator(
                progress = { summary.completionRate.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SrSize.ProgressLineHeight)
                    .clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        },
    )
}

@Composable
private fun HistoryEmptyState() {
    RoutineStatusCard(
        icon = SrIcon.Calendar,
        title = "아직 기록이 없습니다",
        description = "오늘 화면에서 체크한 복용 기록이 쌓입니다.",
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        iconTint = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun historyStatus(summary: DailyHistorySummary): HistoryStatus {
    val colorScheme = MaterialTheme.colorScheme
    return when {
        summary.isEmpty -> HistoryStatus(
            label = "일정 없음",
            icon = SrIcon.Close,
            background = colorScheme.surfaceContainer,
            foreground = colorScheme.onSurfaceVariant,
        )
        summary.completionRate >= 0.8 -> HistoryStatus(
            label = "완료율 높음",
            icon = SrIcon.Check,
            background = colorScheme.primary,
            foreground = colorScheme.onPrimary,
        )
        summary.completionRate >= 0.4 -> HistoryStatus(
            label = "완료율 보통",
            icon = SrIcon.Chevron,
            background = colorScheme.secondaryContainer,
            foreground = colorScheme.onSecondaryContainer,
        )
        else -> HistoryStatus(
            label = "완료율 낮음",
            icon = SrIcon.Close,
            background = colorScheme.tertiaryContainer,
            foreground = colorScheme.onTertiaryContainer,
        )
    }
}

private data class HistoryStatus(
    val label: String,
    val icon: SrIcon,
    val background: Color,
    val foreground: Color,
)

private fun weekdayIndex(date: LocalDateValue): Int {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.month - 1)
        set(Calendar.DAY_OF_MONTH, date.day)
    }
    return calendar.get(Calendar.DAY_OF_WEEK) - 1
}
