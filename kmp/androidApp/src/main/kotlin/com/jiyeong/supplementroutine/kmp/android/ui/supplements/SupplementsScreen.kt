package com.jiyeong.supplementroutine.kmp.android.ui.supplements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatDosage
import com.jiyeong.supplementroutine.kmp.android.ui.common.methodLabelText
import com.jiyeong.supplementroutine.kmp.android.ui.common.sampleSupplements
import com.jiyeong.supplementroutine.shared.domain.Supplement

@Composable
fun SupplementsRoute(contentPadding: PaddingValues) {
    val supplements = remember { sampleSupplements() }
    SupplementsScreen(
        contentPadding = contentPadding,
        supplements = supplements,
    )
}

@Composable
private fun SupplementsScreen(
    contentPadding: PaddingValues,
    supplements: List<Supplement>,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = contentPadding.calculateBottomPadding() + 104.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "영양제",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "등록한 복용 규칙과 알림 상태를 확인합니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (supplements.isEmpty()) {
                item { SupplementEmptyState() }
            } else {
                items(
                    items = supplements,
                    key = { it.id },
                ) { supplement ->
                    SupplementCard(supplement = supplement)
                }
            }
        }

        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 20.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp,
                ),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "영양제 추가",
            )
        }
    }
}

@Composable
private fun SupplementCard(supplement: Supplement) {
    val notificationText = if (supplement.isNotificationEnabled) {
        "알림 켜짐"
    } else {
        "알림 꺼짐"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.semantics {
            contentDescription = "${supplement.name}, ${methodLabelText(supplement.method)}, 하루 ${supplement.dailyCount}회, $notificationText"
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = supplement.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MetaChip(text = "${methodLabelText(supplement.method)} · 하루 ${supplement.dailyCount}회")
                    MetaChip(text = "${formatDosage(supplement.dosageValue)} ${supplement.dosageUnit}")
                }
                NotificationState(enabled = supplement.isNotificationEnabled)
                supplement.memo?.let { memo ->
                    Text(
                        text = memo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            SupplementActions(isNotificationEnabled = supplement.isNotificationEnabled)
        }
    }
}

@Composable
private fun NotificationState(enabled: Boolean) {
    val text = if (enabled) "알림 켜짐" else "알림 꺼짐"
    val icon = if (enabled) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff
    val tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SupplementActions(isNotificationEnabled: Boolean) {
    Column(horizontalAlignment = Alignment.End) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = if (isNotificationEnabled) {
                    Icons.Outlined.NotificationsActive
                } else {
                    Icons.Outlined.NotificationsOff
                },
                contentDescription = if (isNotificationEnabled) "알림 끄기" else "알림 켜기",
                tint = if (isNotificationEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
            )
        }
        Row {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "수정",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun MetaChip(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SupplementEmptyState() {
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
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = "등록된 영양제가 없습니다",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "오른쪽 아래 + 버튼으로 복용할 영양제를 등록해보세요.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
