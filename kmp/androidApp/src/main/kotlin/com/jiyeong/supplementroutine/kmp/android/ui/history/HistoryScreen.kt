package com.jiyeong.supplementroutine.kmp.android.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Remove
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineEmptyCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSectionLabel
import com.jiyeong.supplementroutine.kmp.android.ui.common.FruitAvatar
import com.jiyeong.supplementroutine.kmp.android.ui.common.FruitVariant
import com.jiyeong.supplementroutine.kmp.android.ui.common.GardenUi
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.history.DailyHistorySummary
import com.jiyeong.supplementroutine.shared.history.HistoryViewState
import java.util.Calendar

@Composable
fun HistoryRoute(
    contentPadding: PaddingValues,
    today: LocalDateValue,
    historyViewState: HistoryViewState,
) {
    var selectedDate by remember(today.year, today.month) {
        mutableStateOf(today)
    }
    val selectedSummary = remember(historyViewState.monthSummaries, historyViewState.todaySummary, selectedDate) {
        historyViewState.monthSummaries.firstOrNull { it.date == selectedDate }
            ?: if (historyViewState.todaySummary.date == selectedDate) {
                historyViewState.todaySummary
            } else {
                DailyHistorySummary(date = selectedDate, doneCount = 0, totalCount = 0)
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 16.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            RoutinePageHeader(
                title = "기록",
                subtitle = "루틴이 얼마나 잘 지켜졌는지 살펴봐요",
            )
        }
        item { HistoryOverviewCard(summary = historyViewState.todaySummary) }
        item {
            SectionHeader(
                title = "월간 기록",
                description = "날짜별 복용 완료 상태를 한눈에 봅니다.",
            )
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
        item { RoutinePatternCard(summaries = historyViewState.recentSummaries) }
        item {
            SectionHeader(
                title = "최근 기록",
                description = "최근 2주 동안의 완료율을 확인합니다.",
            )
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
private fun SectionHeader(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RoutineSectionLabel(title = title)
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HistoryOverviewCard(summary: DailyHistorySummary) {
    val percent = (summary.completionRate * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = GardenUi.PrimaryBlue,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "오늘 완료율",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "${summary.doneCount} / ${summary.totalCount} 완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
            FruitAvatar(FruitVariant.EggFlower, Modifier.size(74.dp))
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
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${today.year}년 ${today.month}월",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "월간 완료 상태",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
    Surface(
        modifier = Modifier
            .size(32.dp)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "${summary.date.day}일, ${status.label}, ${summary.doneCount}개 중 ${summary.totalCount}개 완료"
            },
        shape = CircleShape,
        color = status.background,
        border = when {
            selected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            isToday -> BorderStroke(2.dp, GardenUi.Ink)
            summary.isEmpty -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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

@Composable
private fun RoutinePatternCard(summaries: List<DailyHistorySummary>) {
    val daysWithSchedule = summaries.filterNot { it.isEmpty }
    val missedDays = daysWithSchedule.count { it.doneCount < it.totalCount }
    val average = if (daysWithSchedule.isEmpty()) {
        0
    } else {
        (daysWithSchedule.map { it.completionRate }.average() * 100).toInt()
    }
    val message = when {
        daysWithSchedule.isEmpty() -> "기록이 쌓이면 최근 루틴 흐름을 여기서 볼 수 있어요."
        missedDays == 0 -> "최근 복용 일정은 모두 잘 기록됐어요."
        missedDays == 1 -> "최근 2주 중 하루는 일부 복용이 남았어요."
        else -> "최근 2주 중 ${missedDays}일은 일부 복용이 남았어요."
    }

    RoutineCard(
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = GardenUi.SurfaceSoft,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
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
            if (daysWithSchedule.isNotEmpty()) {
                Text(
                    text = "평균 완료율 $average%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SelectedDateSummaryCard(summary: DailyHistorySummary) {
    val percent = (summary.completionRate * 100).toInt()
    val status = historyStatus(summary)

    RoutineCard {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = status.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = status.icon,
                        contentDescription = null,
                        tint = status.foreground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${summary.date.month}월 ${summary.date.day}일",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (summary.isEmpty) {
                        "이날은 복용 일정이 없습니다."
                    } else {
                        "$percent% 완료 · ${summary.doneCount} / ${summary.totalCount} 완료"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LegendItem(Icons.Filled.Check, MaterialTheme.colorScheme.primary, "높음")
        LegendItem(Icons.Filled.HorizontalRule, MaterialTheme.colorScheme.tertiary, "보통")
        LegendItem(Icons.Filled.Close, MaterialTheme.colorScheme.outline, "낮음")
        LegendItem(Icons.Outlined.Remove, MaterialTheme.colorScheme.onSurfaceVariant, "없음")
    }
}

@Composable
private fun LegendItem(
    icon: ImageVector,
    color: Color,
    label: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp),
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun HistoryItem(summary: DailyHistorySummary) {
    val percent = (summary.completionRate * 100).toInt()

    RoutineCard {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = percent.toString(),
                        color = historyStatus(summary).accent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "${summary.date.month}월 ${summary.date.day}일",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$percent% 완료 · ${summary.doneCount} / ${summary.totalCount} 완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LinearProgressIndicator(
                    progress = { summary.completionRate.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
        }
    }
}

@Composable
private fun HistoryEmptyState() {
    RoutineEmptyCard(
        icon = Icons.Outlined.CalendarMonth,
        title = "아직 기록이 없습니다",
        description = "오늘 화면에서 복용을 체크하면 최근 기록이 쌓입니다.",
    )
}

@Composable
private fun historyStatus(summary: DailyHistorySummary): HistoryStatus {
    val colorScheme = MaterialTheme.colorScheme
    return when {
        summary.isEmpty -> HistoryStatus(
            label = "일정 없음",
            icon = Icons.Outlined.Remove,
            background = GardenUi.Paper,
            foreground = GardenUi.InkMuted,
            accent = GardenUi.Line,
        )
        summary.completionRate >= 0.8 -> HistoryStatus(
            label = "완료율 높음",
            icon = Icons.Filled.Check,
            background = GardenUi.PrimaryBlue,
            foreground = Color.White,
            accent = GardenUi.PrimaryBlue,
        )
        summary.completionRate >= 0.4 -> HistoryStatus(
            label = "완료율 보통",
            icon = Icons.Filled.HorizontalRule,
            background = GardenUi.LeafGreen,
            foreground = GardenUi.Ink,
            accent = GardenUi.LeafGreenDark,
        )
        else -> HistoryStatus(
            label = "완료율 낮음",
            icon = Icons.Filled.Close,
            background = GardenUi.EggYellow,
            foreground = GardenUi.Ink,
            accent = GardenUi.EggYellow,
        )
    }
}

private data class HistoryStatus(
    val label: String,
    val icon: ImageVector,
    val background: Color,
    val foreground: Color,
    val accent: Color,
)

private fun weekdayIndex(date: LocalDateValue): Int {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.month - 1)
        set(Calendar.DAY_OF_MONTH, date.day)
    }
    return calendar.get(Calendar.DAY_OF_WEEK) - 1
}
