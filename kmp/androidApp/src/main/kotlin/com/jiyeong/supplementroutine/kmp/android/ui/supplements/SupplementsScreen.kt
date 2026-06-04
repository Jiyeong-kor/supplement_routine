package com.jiyeong.supplementroutine.kmp.android.ui.supplements

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatDosage
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatTime
import com.jiyeong.supplementroutine.kmp.android.ui.common.methodLabelText
import com.jiyeong.supplementroutine.kmp.android.ui.common.slotLabelText
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import com.jiyeong.supplementroutine.shared.form.SupplementFormInput
import com.jiyeong.supplementroutine.shared.form.SupplementFormMapper
import com.jiyeong.supplementroutine.shared.form.SupplementFormPolicy
import com.jiyeong.supplementroutine.shared.form.SupplementFormValidationError

@Composable
fun SupplementsRoute(
    contentPadding: PaddingValues,
    supplements: List<Supplement>,
    defaultNotificationEnabled: Boolean,
    onAddSupplement: (Supplement) -> Unit,
    onUpdateSupplement: (Supplement) -> Unit,
    onRemoveSupplement: (Supplement) -> Unit,
    onToggleNotification: (Supplement) -> Unit,
) {
    var formInitialSupplement by remember { mutableStateOf<Supplement?>(null) }
    var isAdding by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Supplement?>(null) }

    if (isAdding || formInitialSupplement != null) {
        SupplementFormScreen(
            contentPadding = contentPadding,
            initialSupplement = formInitialSupplement,
            defaultNotificationEnabled = defaultNotificationEnabled,
            onDismiss = {
                isAdding = false
                formInitialSupplement = null
            },
            onSubmit = { supplement ->
                if (formInitialSupplement == null) {
                    onAddSupplement(supplement)
                } else {
                    onUpdateSupplement(supplement)
                }
                isAdding = false
                formInitialSupplement = null
            },
        )
    } else {
        SupplementsScreen(
            contentPadding = contentPadding,
            supplements = supplements,
            onAddClick = { isAdding = true },
            onEditClick = { supplement -> formInitialSupplement = supplement },
            onDeleteClick = { supplement -> deleteTarget = supplement },
            onToggleNotification = onToggleNotification,
        )
    }

    deleteTarget?.let { supplement ->
        DeleteSupplementDialog(
            supplement = supplement,
            onDismiss = { deleteTarget = null },
            onConfirm = {
                onRemoveSupplement(supplement)
                deleteTarget = null
            },
        )
    }
}

@Composable
private fun SupplementsScreen(
    contentPadding: PaddingValues,
    supplements: List<Supplement>,
    onAddClick: () -> Unit,
    onEditClick: (Supplement) -> Unit,
    onDeleteClick: (Supplement) -> Unit,
    onToggleNotification: (Supplement) -> Unit,
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
                    SupplementCard(
                        supplement = supplement,
                        onEditClick = { onEditClick(supplement) },
                        onDeleteClick = { onDeleteClick(supplement) },
                        onToggleNotification = { onToggleNotification(supplement) },
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
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
private fun SupplementFormScreen(
    contentPadding: PaddingValues,
    initialSupplement: Supplement?,
    defaultNotificationEnabled: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Supplement) -> Unit,
) {
    val isEditMode = initialSupplement != null
    var name by remember(initialSupplement) { mutableStateOf(initialSupplement?.name.orEmpty()) }
    var dosageText by remember(initialSupplement) {
        mutableStateOf(
            initialSupplement?.dosageValue?.let(SupplementFormPolicy::formatDosage)
                ?: SupplementFormPolicy.DEFAULT_DOSAGE_TEXT,
        )
    }
    var dosageUnit by remember(initialSupplement) {
        mutableStateOf(initialSupplement?.dosageUnit ?: SupplementFormPolicy.DEFAULT_UNIT)
    }
    var isRoutineBased by remember(initialSupplement) {
        mutableStateOf(initialSupplement?.method != IntakeMethod.FixedTime && initialSupplement?.method != IntakeMethod.Interval)
    }
    var isSpecificTime by remember(initialSupplement) {
        mutableStateOf(initialSupplement?.method != IntakeMethod.Interval)
    }
    var selectedSlots by remember(initialSupplement) {
        mutableStateOf(initialSupplement?.selectedSlots?.toSet().orEmpty())
    }
    var fixedCount by remember(initialSupplement) {
        mutableIntStateOf(initialSupplement?.fixedTimes?.size ?: SupplementFormPolicy.DEFAULT_COUNT)
    }
    val fixedTimes = remember(initialSupplement) {
        mutableStateListOf<TimeOfDayValue>().apply {
            addAll(initialSupplement?.fixedTimes ?: listOf(SupplementFormPolicy.defaultTime))
        }
    }
    var startTime by remember(initialSupplement) {
        mutableStateOf(initialSupplement?.startTime ?: SupplementFormPolicy.defaultTime)
    }
    var intervalHours by remember(initialSupplement) {
        mutableIntStateOf(initialSupplement?.intervalHours ?: SupplementFormPolicy.DEFAULT_INTERVAL_HOURS)
    }
    var intervalCount by remember(initialSupplement) {
        mutableIntStateOf(
            if (initialSupplement?.method == IntakeMethod.Interval) {
                initialSupplement.dailyCount
            } else {
                SupplementFormPolicy.DEFAULT_COUNT
            },
        )
    }
    var isNotificationEnabled by remember(initialSupplement, defaultNotificationEnabled) {
        mutableStateOf(initialSupplement?.isNotificationEnabled ?: defaultNotificationEnabled)
    }
    var memo by remember(initialSupplement) { mutableStateOf(initialSupplement?.memo.orEmpty()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun syncFixedTimes(count: Int) {
        fixedCount = count
        while (fixedTimes.size < count) fixedTimes.add(fixedTimes.lastOrNull() ?: SupplementFormPolicy.defaultTime)
        while (fixedTimes.size > count) fixedTimes.removeAt(fixedTimes.lastIndex)
    }

    fun submit() {
        val trimmedName = name.trim()
        val nameError = SupplementFormPolicy.validateName(trimmedName)
        if (nameError != null) {
            errorMessage = validationMessage(nameError)
            return
        }

        val dosageValue = SupplementFormPolicy.parseDosage(dosageText)
        if (dosageValue == null) {
            errorMessage = validationMessage(SupplementFormValidationError.InvalidDosage)
            return
        }

        if (isRoutineBased) {
            val routineError = SupplementFormPolicy.validateRoutineSlots(selectedSlots)
            if (routineError != null) {
                errorMessage = validationMessage(routineError)
                return
            }
        }

        val input = SupplementFormInput(
            name = trimmedName,
            dosageUnit = dosageUnit,
            dosageValue = dosageValue,
            isRoutineBased = isRoutineBased,
            isSpecificTime = isSpecificTime,
            selectedSlots = selectedSlots,
            fixedCount = fixedCount,
            fixedTimes = fixedTimes.toList(),
            startTime = startTime,
            intervalHours = intervalHours,
            intervalCount = intervalCount,
            isNotificationEnabled = isNotificationEnabled,
            memo = memo,
        )

        onSubmit(
            SupplementFormMapper.toSupplement(
                input = input,
                initialSupplement = initialSupplement,
                idProvider = { System.currentTimeMillis().toString() },
            ),
        )
    }

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
                FormHeader(
                    title = if (isEditMode) "영양제 수정" else "영양제 추가",
                    onDismiss = onDismiss,
                )
            }
            errorMessage?.let { message ->
                item { FormErrorCard(message = message) }
            }
            item {
                FormSection(title = "기본 정보") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("영양제 이름") },
                        singleLine = true,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = dosageText,
                            onValueChange = { dosageText = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("복용량") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "단위",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SupplementFormPolicy.dosageUnits.forEach { unit ->
                                    FilterChip(
                                        selected = dosageUnit == unit,
                                        onClick = { dosageUnit = unit },
                                        label = { Text(unit) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                FormSection(title = "복용 방식") {
                    ChoiceRow(
                        firstLabel = "식사 기준",
                        secondLabel = "직접 설정",
                        firstSelected = isRoutineBased,
                        onFirstSelected = { isRoutineBased = true },
                        onSecondSelected = { isRoutineBased = false },
                    )
                    if (isRoutineBased) {
                        RoutineSlotSelector(
                            selectedSlots = selectedSlots,
                            onToggleSlot = { slot ->
                                selectedSlots = if (selectedSlots.contains(slot)) {
                                    selectedSlots - slot
                                } else {
                                    selectedSlots + slot
                                }
                            },
                        )
                    } else {
                        ChoiceRow(
                            firstLabel = "정해진 시간",
                            secondLabel = "일정 간격",
                            firstSelected = isSpecificTime,
                            onFirstSelected = { isSpecificTime = true },
                            onSecondSelected = { isSpecificTime = false },
                        )
                        if (isSpecificTime) {
                            CounterRow(
                                title = "하루 횟수",
                                value = fixedCount,
                                onChanged = ::syncFixedTimes,
                            )
                            fixedTimes.forEachIndexed { index, time ->
                                TimeStepperRow(
                                    title = "${index + 1}번째 시간",
                                    time = time,
                                    onTimeChanged = { nextTime -> fixedTimes[index] = nextTime },
                                )
                            }
                        } else {
                            CounterRow(
                                title = "하루 횟수",
                                value = intervalCount,
                                onChanged = { intervalCount = it },
                            )
                            TimeStepperRow(
                                title = "시작 시간",
                                time = startTime,
                                onTimeChanged = { startTime = it },
                            )
                            CounterRow(
                                title = "간격",
                                value = intervalHours,
                                suffix = "시간",
                                min = SupplementFormPolicy.MIN_INTERVAL_HOURS,
                                max = SupplementFormPolicy.MAX_INTERVAL_HOURS,
                                onChanged = { intervalHours = it },
                            )
                            Text(
                                text = "시작 시간부터 설정한 간격으로 하루 횟수만큼 복용 일정을 만듭니다.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            item {
                FormSection(title = "기타") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "알림",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = if (isNotificationEnabled) "이 영양제 알림을 켭니다" else "이 영양제 알림을 끕니다",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = isNotificationEnabled,
                            onCheckedChange = { isNotificationEnabled = it },
                        )
                    }
                    OutlinedTextField(
                        value = memo,
                        onValueChange = { memo = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("메모") },
                        minLines = 2,
                    )
                }
            }
        }

        Button(
            onClick = ::submit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 20.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp,
                ),
        ) {
            Text(if (isEditMode) "수정 완료" else "저장")
        }
    }
}

@Composable
private fun FormHeader(
    title: String,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "복용 규칙을 저장하면 오늘 일정과 기록에 바로 반영됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = onDismiss) {
            Text("취소")
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            content()
        }
    }
}

@Composable
private fun FormErrorCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ChoiceRow(
    firstLabel: String,
    secondLabel: String,
    firstSelected: Boolean,
    onFirstSelected: () -> Unit,
    onSecondSelected: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = firstSelected,
            onClick = onFirstSelected,
            label = { Text(firstLabel) },
        )
        FilterChip(
            selected = !firstSelected,
            onClick = onSecondSelected,
            label = { Text(secondLabel) },
        )
    }
}

@Composable
private fun RoutineSlotSelector(
    selectedSlots: Set<IntakeSlot>,
    onToggleSlot: (IntakeSlot) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "복용 타이밍",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SupplementFormPolicy.routineSlots.forEach { slot ->
                FilterChip(
                    selected = selectedSlots.contains(slot),
                    onClick = { onToggleSlot(slot) },
                    label = { Text(slotLabelText(slot)) },
                )
            }
        }
    }
}

@Composable
private fun CounterRow(
    title: String,
    value: Int,
    onChanged: (Int) -> Unit,
    suffix: String = "",
    min: Int = SupplementFormPolicy.MIN_COUNT,
    max: Int = SupplementFormPolicy.MAX_COUNT,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onChanged(value - 1) },
                enabled = value > min,
            ) {
                Icon(Icons.Outlined.RemoveCircle, contentDescription = "$title 줄이기")
            }
            Text(
                text = if (suffix.isEmpty()) value.toString() else "$value$suffix",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = { onChanged(value + 1) },
                enabled = value < max,
            ) {
                Icon(Icons.Outlined.AddCircle, contentDescription = "$title 늘리기")
            }
        }
    }
}

@Composable
private fun TimeStepperRow(
    title: String,
    time: TimeOfDayValue,
    onTimeChanged: (TimeOfDayValue) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
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
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = formatTime(time),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onTimeChanged(time.plusHours(-1)) }) {
                    Text("-1시간")
                }
                OutlinedButton(onClick = { onTimeChanged(time.plusHours(1)) }) {
                    Text("+1시간")
                }
                OutlinedButton(onClick = { onTimeChanged(time.plusMinutes(-10)) }) {
                    Text("-10분")
                }
                OutlinedButton(onClick = { onTimeChanged(time.plusMinutes(10)) }) {
                    Text("+10분")
                }
            }
        }
    }
}

@Composable
private fun SupplementCard(
    supplement: Supplement,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleNotification: () -> Unit,
) {
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
            SupplementActions(
                isNotificationEnabled = supplement.isNotificationEnabled,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onToggleNotification = onToggleNotification,
            )
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
private fun SupplementActions(
    isNotificationEnabled: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleNotification: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.End) {
        IconButton(onClick = onToggleNotification) {
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
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "수정",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDeleteClick) {
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

@Composable
private fun DeleteSupplementDialog(
    supplement: Supplement,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("영양제 삭제") },
        text = { Text("${supplement.name}을 삭제하면 관련 복용 기록도 함께 정리됩니다.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("삭제", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
    )
}

private fun validationMessage(error: SupplementFormValidationError): String {
    return when (error) {
        SupplementFormValidationError.EmptyName -> "영양제 이름을 입력해주세요."
        SupplementFormValidationError.InvalidDosage -> "복용량은 0보다 큰 숫자로 입력해주세요."
        SupplementFormValidationError.EmptyRoutineSlots -> "복용 타이밍을 하나 이상 선택해주세요."
    }
}

private fun TimeOfDayValue.plusHours(hours: Int): TimeOfDayValue {
    return plusMinutes(hours * 60)
}

private fun TimeOfDayValue.plusMinutes(minutes: Int): TimeOfDayValue {
    val totalMinutes = ((hour * 60 + minute + minutes) % (24 * 60) + (24 * 60)) % (24 * 60)
    return TimeOfDayValue(
        hour = totalMinutes / 60,
        minute = totalMinutes % 60,
    )
}
