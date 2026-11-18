package com.jiyeong.supplementroutine.kmp.android.ui.supplements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatDosage
import com.jiyeong.supplementroutine.kmp.android.ui.common.formatTime
import com.jiyeong.supplementroutine.kmp.android.ui.common.methodLabelText
import com.jiyeong.supplementroutine.kmp.android.ui.common.FruitAvatar
import com.jiyeong.supplementroutine.kmp.android.ui.common.GardenUi
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineFab
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineMoreButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePageHeader
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutinePillButton
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineSlotCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.RoutineStatusCard
import com.jiyeong.supplementroutine.kmp.android.ui.common.fruitVariantFor
import com.jiyeong.supplementroutine.kmp.android.ui.common.slotLabelText
import com.jiyeong.supplementroutine.kmp.android.ui.haptic.rememberRoutineHapticFeedback
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrIcon
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrOpacity
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSize
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSpacing
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
    onAddSupplement: (Supplement, onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit,
    onUpdateSupplement: (Supplement, onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit,
    onRemoveSupplement: (Supplement) -> Unit,
    onToggleNotification: (Supplement) -> Unit,
) {
    var formInitialSupplement by remember { mutableStateOf<Supplement?>(null) }
    var isAdding by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Supplement?>(null) }
    val hapticFeedback = rememberRoutineHapticFeedback()

    if (isAdding || formInitialSupplement != null) {
        SupplementFormScreen(
            contentPadding = contentPadding,
            initialSupplement = formInitialSupplement,
            defaultNotificationEnabled = defaultNotificationEnabled,
            onDismiss = {
                isAdding = false
                formInitialSupplement = null
            },
            onSubmit = { supplement, onFailure ->
                val closeForm = {
                    hapticFeedback.saved()
                    isAdding = false
                    formInitialSupplement = null
                }
                if (formInitialSupplement == null) {
                    onAddSupplement(supplement, closeForm, onFailure)
                } else {
                    onUpdateSupplement(supplement, closeForm, onFailure)
                }
            },
            onValidationError = hapticFeedback::validationWarning,
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
                hapticFeedback.destructiveConfirmed()
                onRemoveSupplement(supplement)
                deleteTarget = null
            },
        )
    }
}

@Composable
internal fun SupplementsScreen(
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
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = SrSpacing.ScreenHorizontal,
                top = SrSpacing.Lg,
                end = SrSpacing.ScreenHorizontal,
                bottom = contentPadding.calculateBottomPadding() + SrSize.BottomNavigationHeight + SrSpacing.ScreenBottomBuffer,
            ),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Lg),
        ) {
            item {
                RoutinePageHeader(
                    title = "영양제",
                    subtitle = "등록된 복용 규칙 ${supplements.size}개",
                )
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

        RoutineFab(
            onClick = onAddClick,
            icon = SrIcon.Plus,
            contentDescription = "영양제 추가",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = SrSpacing.ScreenHorizontal,
                    bottom = contentPadding.calculateBottomPadding() + SrSpacing.Lg,
                ),
        )
    }
}

internal class SupplementFormState(
    initialSupplement: Supplement? = null,
    defaultNotificationEnabled: Boolean = true,
) {
    var name by mutableStateOf(initialSupplement?.name.orEmpty())
    var dosageText by mutableStateOf(
        initialSupplement?.dosageValue?.let(SupplementFormPolicy::formatDosage)
            ?: SupplementFormPolicy.DEFAULT_DOSAGE_TEXT,
    )
    var dosageUnit by mutableStateOf(
        initialSupplement?.dosageUnit?.let(SupplementFormPolicy::normalizeDosageUnitForSelection)
            ?: SupplementFormPolicy.DEFAULT_UNIT,
    )
    var isRoutineBased by mutableStateOf(
        initialSupplement?.method != IntakeMethod.FixedTime && initialSupplement?.method != IntakeMethod.Interval,
    )
    var isSpecificTime by mutableStateOf(initialSupplement?.method != IntakeMethod.Interval)
    var selectedSlots by mutableStateOf(initialSupplement?.selectedSlots?.toSet() ?: SupplementFormPolicy.defaultRoutineSlots)
    var fixedCount by mutableIntStateOf(initialSupplement?.fixedTimes?.size ?: SupplementFormPolicy.DEFAULT_COUNT)
    val fixedTimes = mutableStateListOf<TimeOfDayValue>().apply {
        addAll(initialSupplement?.fixedTimes ?: listOf(SupplementFormPolicy.defaultTime))
    }
    var startTime by mutableStateOf(initialSupplement?.startTime ?: SupplementFormPolicy.defaultTime)
    var intervalHours by mutableIntStateOf(initialSupplement?.intervalHours ?: SupplementFormPolicy.DEFAULT_INTERVAL_HOURS)
    var intervalCount by mutableIntStateOf(
        if (initialSupplement?.method == IntakeMethod.Interval) initialSupplement.dailyCount
        else SupplementFormPolicy.DEFAULT_COUNT,
    )
    var isNotificationEnabled by mutableStateOf(initialSupplement?.isNotificationEnabled ?: defaultNotificationEnabled)
    var memo by mutableStateOf(initialSupplement?.memo.orEmpty())
    var validationErrorMessage by mutableStateOf<String?>(null)
    var saveFailureMessage by mutableStateOf<String?>(null)
    var isSaving by mutableStateOf(false)
    var showMoreDosageUnits by mutableStateOf(
        initialSupplement?.dosageUnit
            ?.let(SupplementFormPolicy::normalizeDosageUnitForSelection)
            ?.let { it !in SupplementFormPolicy.primaryDosageUnits }
            ?: false,
    )
}

@Composable
internal fun SupplementFormScreen(
    contentPadding: PaddingValues,
    initialSupplement: Supplement?,
    defaultNotificationEnabled: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Supplement, onFailure: (String) -> Unit) -> Unit,
    onValidationError: () -> Unit,
    state: SupplementFormState = remember(initialSupplement, defaultNotificationEnabled) {
        SupplementFormState(initialSupplement, defaultNotificationEnabled)
    },
    listState: LazyListState = rememberLazyListState(),
) = with(state) {
    val isEditMode = initialSupplement != null
    fun syncFixedTimes(count: Int) {
        fixedCount = count
        while (fixedTimes.size < count) fixedTimes.add(fixedTimes.lastOrNull() ?: SupplementFormPolicy.defaultTime)
        while (fixedTimes.size > count) fixedTimes.removeAt(fixedTimes.lastIndex)
    }

    fun submit() {
        val trimmedName = name.trim()
        val nameError = SupplementFormPolicy.validateName(trimmedName)
        if (nameError != null) {
            validationErrorMessage = validationMessage(nameError)
            saveFailureMessage = null
            onValidationError()
            return
        }

        val parsedDosage = SupplementFormPolicy.parseDosageInput(dosageText)
        if (parsedDosage == null) {
            validationErrorMessage = validationMessage(SupplementFormValidationError.InvalidDosage)
            saveFailureMessage = null
            onValidationError()
            return
        }
        val submittedDosageUnit = parsedDosage.unit ?: dosageUnit

        if (isRoutineBased) {
            val routineError = SupplementFormPolicy.validateRoutineSlots(selectedSlots)
            if (routineError != null) {
                validationErrorMessage = validationMessage(routineError)
                saveFailureMessage = null
                onValidationError()
                return
            }
        }

        val input = SupplementFormInput(
            name = trimmedName,
            dosageUnit = submittedDosageUnit,
            dosageValue = parsedDosage.value,
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

        isSaving = true
        validationErrorMessage = null
        saveFailureMessage = null
        onSubmit(
            SupplementFormMapper.toSupplement(
                input = input,
                initialSupplement = initialSupplement,
                idProvider = { System.currentTimeMillis().toString() },
            ),
        ) { _ ->
            saveFailureMessage = "저장하지 못했어요. 다시 시도해주세요."
            isSaving = false
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
                bottom = contentPadding.calculateBottomPadding() + SrSize.BottomNavigationHeight + SrSize.Fab,
            ),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Lg),
        ) {
            item {
                FormHeader(
                    title = if (isEditMode) "영양제 수정" else "영양제 추가",
                    onDismiss = onDismiss,
                    isDismissEnabled = !isSaving,
                )
            }
            validationErrorMessage?.let { message ->
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
                    Row(horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md)) {
                        OutlinedTextField(
                            value = dosageText,
                            onValueChange = { dosageText = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("복용량") },
                            supportingText = { Text("예: 1정, 400mg, 1000 IU") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
                        ) {
                            Text(
                                text = "단위",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
                                verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
                            ) {
                                val visibleDosageUnits = if (showMoreDosageUnits) {
                                    SupplementFormPolicy.dosageUnits
                                } else {
                                    SupplementFormPolicy.primaryDosageUnits
                                }
                                visibleDosageUnits.forEach { unit ->
                                    FilterChip(
                                        selected = dosageUnit == unit,
                                        onClick = { dosageUnit = unit },
                                        label = { Text(unit) },
                                    )
                                }
                                FilterChip(
                                    selected = showMoreDosageUnits,
                                    onClick = { showMoreDosageUnits = !showMoreDosageUnits },
                                    label = {
                                        Text(if (showMoreDosageUnits) "간단히" else "더보기")
                                    },
                                )
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
                        Column(modifier = Modifier.weight(1f)) {
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

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background.copy(alpha = SrOpacity.FloatingSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SrSpacing.ScreenHorizontal,
                        end = SrSpacing.ScreenHorizontal,
                        bottom = contentPadding.calculateBottomPadding() + SrSpacing.Md,
                        top = SrSpacing.Md,
                    ),
                verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
            ) {
                saveFailureMessage?.let { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        TextButton(
                            onClick = ::submit,
                            enabled = !isSaving,
                        ) {
                            Text("다시 시도")
                        }
                    }
                }
                Button(
                    onClick = ::submit,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        when {
                            isSaving -> "저장 중"
                            isEditMode -> "수정 완료"
                            else -> "저장"
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FormHeader(
    title: String,
    onDismiss: () -> Unit,
    isDismissEnabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Xxs),
        ) {
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
        TextButton(
            onClick = onDismiss,
            enabled = isDismissEnabled,
        ) {
            Text("취소")
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    RoutineCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SrSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Md),
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
    RoutineCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(SrSpacing.Lg),
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
    Row(horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm)) {
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
    Column(verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm)) {
        Text(
            text = "복용 타이밍",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
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
                Icon(
                    painter = painterResource(SrIcon.Close.drawableRes),
                    contentDescription = "$title 줄이기",
                )
            }
            Text(
                text = if (suffix.isEmpty()) value.toString() else "$value$suffix",
                modifier = Modifier.padding(horizontal = SrSpacing.Sm),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = { onChanged(value + 1) },
                enabled = value < max,
            ) {
                Icon(
                    painter = painterResource(SrIcon.Plus.drawableRes),
                    contentDescription = "$title 늘리기",
                )
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
    RoutineCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = SrOpacity.SoftContainer),
        ),
    ) {
        Column(
            modifier = Modifier.padding(SrSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Sm),
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
            Row(horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm)) {
                OutlinedButton(
                    onClick = { onTimeChanged(time.plusHours(-1)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("-1시간")
                }
                OutlinedButton(
                    onClick = { onTimeChanged(time.plusHours(1)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("+1시간")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(SrSpacing.Sm)) {
                OutlinedButton(
                    onClick = { onTimeChanged(time.plusMinutes(-10)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("-10분")
                }
                OutlinedButton(
                    onClick = { onTimeChanged(time.plusMinutes(10)) },
                    modifier = Modifier.weight(1f),
                ) {
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

    RoutineSlotCard(
        modifier = Modifier
            .semantics {
                contentDescription = "${supplement.name}, ${methodLabelText(supplement.method)}, 하루 ${supplement.dailyCount}회, $notificationText"
            },
        leading = {
            FruitAvatar(
                variant = fruitVariantFor(supplement.id.ifBlank { supplement.name }),
                modifier = Modifier.size(SrSize.IconBadge),
            )
        },
        title = {
            Text(
                text = supplement.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        supporting = {
            Text(
                text = "${methodLabelText(supplement.method)} / 하루 ${supplement.dailyCount}회\n1회 ${formatDosage(supplement.dosageValue)} ${supplement.dosageUnit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(SrSpacing.Xs)) {
                NotificationState(enabled = supplement.isNotificationEnabled)
            }
            supplement.memo?.let { memo ->
                Text(
                    text = memo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        trailing = {
            SupplementActions(
                isNotificationEnabled = supplement.isNotificationEnabled,
                onDeleteClick = onDeleteClick,
                onToggleNotification = onToggleNotification,
            )
        },
        actions = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            RoutinePillButton(
                text = "복용 규칙 수정",
                icon = SrIcon.Edit,
                onClick = onEditClick,
                height = SrSize.ControlCompact,
                containerColor = GardenUi.SurfaceSoft,
                contentColor = GardenUi.Ink,
            )
        },
    )
}

@Composable
private fun NotificationState(enabled: Boolean) {
    val text = if (enabled) "알림 켜짐" else "알림 꺼짐"
    val tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Row(
        horizontalArrangement = Arrangement.spacedBy(SrSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(SrIcon.Bell.drawableRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(SrSize.IconSmall),
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
    onDeleteClick: () -> Unit,
    onToggleNotification: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        RoutineMoreButton(onClick = { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(if (isNotificationEnabled) "알림 끄기" else "알림 켜기") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(SrIcon.Bell.drawableRes),
                        contentDescription = null,
                    )
                },
                onClick = {
                    expanded = false
                    onToggleNotification()
                },
            )
            DropdownMenuItem(
                text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(SrIcon.Trash.drawableRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                },
            )
        }
    }
}

@Composable
private fun SupplementEmptyState() {
    RoutineStatusCard(
        icon = SrIcon.Bottle,
        title = "등록된 영양제가 없습니다",
        description = "아래 + 버튼으로 추가해 주세요.",
        containerColor = GardenUi.SurfaceSoft,
        iconContainerColor = GardenUi.SkyBlue,
        iconTint = GardenUi.PrimaryBlue,
    )
}

@Composable
internal fun DeleteSupplementDialog(
    supplement: Supplement,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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

internal fun validationMessage(error: SupplementFormValidationError): String {
    return when (error) {
        SupplementFormValidationError.EmptyName -> "영양제 이름을 입력해주세요."
        SupplementFormValidationError.InvalidDosage -> "복용량은 0보다 큰 값과 지원 단위로 입력해주세요. 예: 1정, 400mg, 1000 IU"
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
