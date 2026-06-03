package com.jiyeong.supplementroutine.kmp.android.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.jiyeong.supplementroutine.kmp.android.ui.common.sampleSupplements
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.history.DailyHistorySummary
import com.jiyeong.supplementroutine.shared.history.HistorySummaryService
import com.jiyeong.supplementroutine.shared.history.minusDays
import com.jiyeong.supplementroutine.shared.scheduling.SchedulingService
import java.util.Calendar

@Composable
fun HistoryRoute(contentPadding: PaddingValues) {
    val today = rememberToday()
    val supplements = remember { sampleSupplements() }
    val records = remember(today, supplements) { sampleHistoryRecords(today) }
    val state = remember(today, supplements, records) {
        HistorySummaryService.createViewState(
            today = today,
            supplements = supplements,
            records = records,
            mealTimeSettings = MealTimeSettings(),
        )
    }

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
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "기록",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "완료율로 루틴 흐름을 빠르게 확인합니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item { HistoryOverviewCard(summary = state.todaySummary) }
        item {
            SectionHeader(
                title = "월간 기록",
                description = "날짜별 복용 완료 상태를 한눈에 봅니다.",
            )
        }
        item { MonthHistoryCard(summaries = state.monthSummaries, today = today) }
        item {
            SectionHeader(
                title = "최근 기록",
                description = "최근 2주 동안의 완료율을 확인합니다.",
            )
        }
        if (state.isEmpty) {
            item { HistoryEmptyState() }
        } else {
            state.recentSummaries.forEach { summary ->
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
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
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

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "오늘 복용 요약",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (summary.isEmpty) {
                    "오늘 예정된 복용 일정이 없습니다."
                } else {
                    "$percent% 완료 · ${summary.doneCount} / ${summary.totalCount} 완료"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { summary.completionRate.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.primaryContainer,
            )
        }
    }
}

@Composable
private fun MonthHistoryCard(
    summaries: List<DailyHistorySummary>,
    today: LocalDateValue,
) {
    val firstWeekdayOffset = summaries.firstOrNull()?.date?.let { firstDate ->
        weekdayIndex(firstDate) % 7
    } ?: 0
    val tiles = List<DailyHistorySummary?>(firstWeekdayOffset) { null } + summaries

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    week.forEach { summary ->
                        Box(modifier = Modifier.weight(1f)) {
                            if (summary != null) {
                                MonthDayTile(
                                    summary = summary,
                                    isToday = summary.date == today,
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
) {
    val status = historyStatus(summary)
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(status.background)
            .semantics {
                contentDescription = "${summary.date.day}일, ${status.label}"
            }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = summary.date.day.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = status.foreground,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
        )
        Icon(
            imageVector = status.icon,
            contentDescription = null,
            tint = status.foreground,
            modifier = Modifier.size(12.dp),
        )
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

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
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
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(36.dp),
            )
            Text(
                text = "아직 기록할 복용 일정이 없습니다.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun historyStatus(summary: DailyHistorySummary): HistoryStatus {
    val colorScheme = MaterialTheme.colorScheme
    return when {
        summary.isEmpty -> HistoryStatus(
            label = "일정 없음",
            icon = Icons.Outlined.Remove,
            background = colorScheme.surfaceContainerHighest,
            foreground = colorScheme.onSurfaceVariant,
            accent = colorScheme.outline,
        )
        summary.completionRate >= 0.8 -> HistoryStatus(
            label = "완료율 높음",
            icon = Icons.Filled.Check,
            background = colorScheme.primary,
            foreground = colorScheme.onPrimary,
            accent = colorScheme.primary,
        )
        summary.completionRate >= 0.4 -> HistoryStatus(
            label = "완료율 보통",
            icon = Icons.Filled.HorizontalRule,
            background = colorScheme.tertiaryContainer,
            foreground = colorScheme.onSurface,
            accent = colorScheme.tertiary,
        )
        else -> HistoryStatus(
            label = "완료율 낮음",
            icon = Icons.Filled.Close,
            background = colorScheme.outlineVariant,
            foreground = colorScheme.onSurface,
            accent = colorScheme.outline,
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

private fun sampleHistoryRecords(today: LocalDateValue): Map<String, IntakeRecord> {
    val supplements = sampleSupplements()
    val records = mutableMapOf<String, IntakeRecord>()

    (0..13).forEach { offset ->
        val date = today.minusDays(offset)
        val scheduled = SchedulingService.createDailyIntakeRecords(
            supplements = supplements,
            date = date,
            mealTimeSettings = MealTimeSettings(),
        )
        val doneLimit = when (offset % 4) {
            0 -> scheduled.size
            1 -> (scheduled.size * 0.6).toInt()
            2 -> 1
            else -> 0
        }
        scheduled.take(doneLimit).forEach { item ->
            records[item.record.id] = item.record.copy(isDone = true)
        }
    }

    return records
}

private fun rememberToday(): LocalDateValue {
    val calendar = Calendar.getInstance()
    return LocalDateValue(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH) + 1,
        day = calendar.get(Calendar.DAY_OF_MONTH),
    )
}

private fun weekdayIndex(date: LocalDateValue): Int {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.month - 1)
        set(Calendar.DAY_OF_MONTH, date.day)
    }
    return calendar.get(Calendar.DAY_OF_WEEK) - 1
}
