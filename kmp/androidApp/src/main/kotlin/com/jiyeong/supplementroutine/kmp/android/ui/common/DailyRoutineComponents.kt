package com.jiyeong.supplementroutine.kmp.android.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jiyeong.supplementroutine.kmp.android.R

@Composable
fun DailyProgressSummary(
    done: Int,
    total: Int,
    nextIntake: String?,
    modifier: Modifier = Modifier,
) {
    val progress = if (total > 0) (done.toFloat() / total).coerceIn(0f, 1f) else 0f
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(36.dp),
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("오늘의 루틴", style = MaterialTheme.typography.labelLarge)
                Icon(
                    painter = painterResource(R.drawable.ic_sr_leaf),
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "${done}개 완료",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 30.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum",
                    ),
                )
                Text(
                    text = "/ ${total}개",
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = colors.primary,
                trackColor = colors.surface,
                drawStopIndicator = {},
            )
            Text(
                text = nextIntake?.let { "다음 복용  $it" } ?: "오늘의 복용을 모두 기록했습니다.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun DailyWelcomeCard(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(36.dp),
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sr_leaf),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = "가볍게 시작하는\n나만의 루틴",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 36.sp,
                ),
            )
            Text(
                text = "챙겨 먹는 영양제와 시간을 등록해 주세요.\n복용 후에는 한 번 눌러 기록할 수 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sr_plus),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "첫 영양제 추가하기",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
fun DailyIntakeRow(
    name: String,
    schedule: String,
    dosage: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = when {
            checked -> colors.secondaryContainer
            highlighted -> colors.primaryContainer
            else -> colors.surface
        },
        border = BorderStroke(1.dp, if (highlighted) colors.primary else colors.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = { onToggle() })
                .semantics { stateDescription = if (checked) "복용 완료, 다시 누르면 취소" else "복용 전" }
                .heightIn(min = 96.dp)
                .padding(start = 20.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = if (checked) "$schedule / 완료" else schedule,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (highlighted && !checked) colors.onPrimaryContainer else colors.primary,
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (checked) colors.onSecondaryContainer else colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = dosage, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            Checkbox(checked = checked, onCheckedChange = null)
        }
    }
}
