package com.jiyeong.supplementroutine.kmp.android.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.github.takahirom.roborazzi.annotations.ManualClockOptions
import com.github.takahirom.roborazzi.annotations.RoboComposePreviewOptions
import com.jiyeong.supplementroutine.kmp.android.notification.NotificationPermissionState
import com.jiyeong.supplementroutine.kmp.android.presentation.SupplementRoutineUiState
import com.jiyeong.supplementroutine.kmp.android.ui.LoadingState
import com.jiyeong.supplementroutine.kmp.android.ui.RoutineAppScaffold
import com.jiyeong.supplementroutine.kmp.android.ui.history.HistoryRoute
import com.jiyeong.supplementroutine.kmp.android.ui.history.HistoryScreenState
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsDialog
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsScreenState
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.DeleteSupplementDialog
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementFormScreen
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementFormState
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementsScreen
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.validationMessage
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SupplementRoutineTheme
import com.jiyeong.supplementroutine.kmp.android.ui.today.CompletionFeedback
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayRoute
import com.jiyeong.supplementroutine.kmp.android.ui.today.TodayScreenState
import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeMethod
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import com.jiyeong.supplementroutine.shared.form.SupplementFormValidationError
import com.jiyeong.supplementroutine.shared.scheduling.SchedulingService

private enum class FlowScene {
    Loading,
    TodayEmpty,
    TodayPending,
    TodayPartial,
    TodayComplete,
    TodayUndo,
    TodaySaved,
    TodayPermission,
    TodayError,
    SupplementsEmpty,
    SupplementsList,
    SupplementsDelete,
    FormRoutine,
    FormFixed,
    FormInterval,
    FormEdit,
    FormNameError,
    FormDosageError,
    FormSlotError,
    FormSaving,
    FormSaveError,
    FormExtraUnits,
    FormOptions,
    HistoryEmpty,
    HistoryCalendar,
    HistorySelected,
    HistoryRecent,
    SettingsDefault,
    SettingsNotificationOff,
    SettingsMealEdit,
    SettingsPermissionsAllowed,
    SettingsPermissionsBlocked,
    SettingsInformation,
    SettingsReset,
    SettingsResetDone,
    SettingsGuide,
    SettingsDisclaimer,
    SettingsTestSent,
    SettingsTestBlocked,
    SettingsTestScheduled,
}

private object PreviewFixtures {
    val today = LocalDateValue(2026, 11, 18)
    val mealTimes = MealTimeSettings()
    val routine = Supplement(
        id = "preview-vitamin-d", name = "비타민 D", dailyCount = 1,
        method = IntakeMethod.MealBased, dosageUnit = "정", dosageValue = 1.0,
        selectedSlots = listOf(IntakeSlot(MealType.Breakfast, IntakeCondition.AfterMeal)),
        isNotificationEnabled = true, memo = "아침 식사 후 물과 함께",
    )
    val fixed = Supplement(
        id = "preview-omega-3", name = "오메가3", dailyCount = 1,
        method = IntakeMethod.FixedTime, dosageUnit = "캡슐", dosageValue = 1.0,
        fixedTimes = listOf(TimeOfDayValue(12, 30)), isNotificationEnabled = false,
    )
    val interval = Supplement(
        id = "preview-probiotic", name = "프로바이오틱스", dailyCount = 2,
        method = IntakeMethod.Interval, dosageUnit = "포", dosageValue = 1.0,
        startTime = TimeOfDayValue(8, 0), intervalHours = 8, isNotificationEnabled = true,
    )
    val supplements = listOf(routine, fixed, interval)
    val empty = SupplementRoutineUiState(isLoading = false, today = today)
    val pending = empty.copy(supplements = supplements)
    val completed = pending.todayItems.map { it.record.copy(isDone = true) }.associateBy { it.id }
    val partial = pending.copy(intakeRecords = completed.values.take(2).associateBy { it.id })
    val history = pending.copy(
        intakeRecords = (1..today.day).flatMap { day ->
            SchedulingService.createDailyIntakeRecords(
                supplements, LocalDateValue(today.year, today.month, day), mealTimes,
            ).mapIndexed { index, item ->
                val doneCount = if (day == today.day) 2 else day % 5
                item.record.copy(isDone = index < doneCount)
            }
        }.associateBy { it.id },
    )
}

@Composable
private fun FlowPreview(scene: FlowScene) {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        SupplementRoutineTheme(darkTheme = false) {
            val destination = when {
                scene.name.startsWith("History") -> "history"
                scene.name.startsWith("Settings") -> "settings"
                else -> "today"
            }
            RoutineAppScaffold(destination, onDestinationSelected = {}) { padding ->
                when {
                    scene == FlowScene.Loading -> LoadingState(Modifier.padding(padding))
                    scene.name.startsWith("Today") -> TodayFlowPreview(scene, padding)
                    scene.name.startsWith("Supplements") -> SupplementsFlowPreview(scene, padding)
                    scene.name.startsWith("Form") -> FormFlowPreview(scene, padding)
                    scene.name.startsWith("History") -> HistoryFlowPreview(scene, padding)
                    else -> SettingsFlowPreview(scene, padding)
                }
            }
        }
    }
}

@Composable
private fun TodayFlowPreview(scene: FlowScene, padding: PaddingValues) {
    val uiState = when (scene) {
        FlowScene.TodayEmpty -> PreviewFixtures.empty
        FlowScene.TodayComplete -> PreviewFixtures.pending.copy(intakeRecords = PreviewFixtures.completed)
        FlowScene.TodayPartial -> PreviewFixtures.partial
        FlowScene.TodaySaved -> PreviewFixtures.pending.copy(supplements = listOf(PreviewFixtures.routine))
        else -> PreviewFixtures.pending
    }
    val state = remember(scene) {
        TodayScreenState().apply {
            completionFeedback = when (scene) {
                FlowScene.TodayUndo -> CompletionFeedback(
                    message = "비타민 D 기록을 되돌렸어요.", actionLabel = "다시 완료",
                    actionRecord = PreviewFixtures.pending.todayItems.first {
                        it.supplement.id == PreviewFixtures.routine.id
                    }.record,
                )
                FlowScene.TodaySaved -> CompletionFeedback("오늘 일정에 추가됐어요.")
                else -> null
            }
        }
    }
    TodayRoute(
        contentPadding = padding, date = uiState.today, items = uiState.todayItems,
        errorMessage = if (scene == FlowScene.TodayError) "기록을 저장하지 못했어요. 다시 시도해주세요." else null,
        notificationNeedsAttention = scene == FlowScene.TodayPermission,
        notificationAttentionDismissed = false, onDismissNotificationAttention = {},
        highlightedSupplementId = null, onHighlightedSupplementConsumed = {},
        feedbackMessage = null, onFeedbackMessageConsumed = {},
        onAddSupplementClick = {}, onOpenNotificationSettingsClick = {}, onToggleRecord = {},
        state = state,
    )
}

@Composable
private fun SupplementsFlowPreview(scene: FlowScene, padding: PaddingValues) {
    SupplementsScreen(
        contentPadding = padding,
        supplements = if (scene == FlowScene.SupplementsEmpty) emptyList() else PreviewFixtures.supplements,
        onAddClick = {}, onEditClick = {}, onDeleteClick = {}, onToggleNotification = {},
    )
    if (scene == FlowScene.SupplementsDelete) {
        DeleteSupplementDialog(PreviewFixtures.routine, onDismiss = {}, onConfirm = {})
    }
}

@Composable
private fun FormFlowPreview(scene: FlowScene, padding: PaddingValues) {
    val initialSupplement = if (scene == FlowScene.FormEdit) PreviewFixtures.routine else null
    val state = remember(scene) {
        SupplementFormState(initialSupplement).apply {
            name = if (scene == FlowScene.FormNameError) "" else "비타민 D"
            selectedSlots = PreviewFixtures.routine.selectedSlots.orEmpty().toSet()
            when (scene) {
                FlowScene.FormFixed -> { isRoutineBased = false; isSpecificTime = true }
                FlowScene.FormInterval -> {
                    isRoutineBased = false; isSpecificTime = false; intervalCount = 2; intervalHours = 8
                }
                FlowScene.FormNameError -> validationErrorMessage = validationMessage(SupplementFormValidationError.EmptyName)
                FlowScene.FormDosageError -> {
                    dosageText = "0"
                    validationErrorMessage = validationMessage(SupplementFormValidationError.InvalidDosage)
                }
                FlowScene.FormSlotError -> {
                    selectedSlots = emptySet()
                    validationErrorMessage = validationMessage(SupplementFormValidationError.EmptyRoutineSlots)
                }
                FlowScene.FormSaving -> isSaving = true
                FlowScene.FormSaveError -> saveFailureMessage = "저장하지 못했어요. 다시 시도해주세요."
                FlowScene.FormExtraUnits -> showMoreDosageUnits = true
                FlowScene.FormOptions -> { isNotificationEnabled = false; memo = "아침 식사 후 물과 함께" }
                else -> Unit
            }
        }
    }
    SupplementFormScreen(
        contentPadding = padding, initialSupplement = initialSupplement, defaultNotificationEnabled = true,
        onDismiss = {}, onSubmit = { _, _ -> }, onValidationError = {}, state = state,
        listState = rememberLazyListState(initialFirstVisibleItemIndex = if (scene == FlowScene.FormOptions) 3 else 0),
    )
}

@Composable
private fun HistoryFlowPreview(scene: FlowScene, padding: PaddingValues) {
    val uiState = if (scene == FlowScene.HistoryEmpty) PreviewFixtures.empty else PreviewFixtures.history
    val state = remember(scene) {
        HistoryScreenState(if (scene == FlowScene.HistorySelected) LocalDateValue(2026, 11, 14) else uiState.today)
    }
    HistoryRoute(
        contentPadding = padding, today = uiState.today, historyViewState = uiState.historyViewState,
        onOpenNotificationSettingsClick = {}, state = state,
        listState = rememberLazyListState(initialFirstVisibleItemIndex = if (scene == FlowScene.HistoryRecent) 5 else 0),
    )
}

@Composable
private fun SettingsFlowPreview(scene: FlowScene, padding: PaddingValues) {
    val permissionsAllowed = scene != FlowScene.SettingsPermissionsBlocked && scene != FlowScene.SettingsTestBlocked
    val state = remember(scene) {
        SettingsScreenState().apply {
            mealTimesEditing = scene == FlowScene.SettingsMealEdit
            if (mealTimesEditing) draftMealTimeSettings = PreviewFixtures.mealTimes.copy(breakfastTime = TimeOfDayValue(8, 20))
            showTroubleshooting = scene == FlowScene.SettingsPermissionsAllowed || scene == FlowScene.SettingsPermissionsBlocked
            resetFeedbackVisible = scene == FlowScene.SettingsResetDone
            dialog = when (scene) {
                FlowScene.SettingsReset -> SettingsDialog.Reset
                FlowScene.SettingsGuide -> SettingsDialog.Guide
                FlowScene.SettingsDisclaimer -> SettingsDialog.Disclaimer
                FlowScene.SettingsTestSent -> SettingsDialog.TestNotificationSent
                FlowScene.SettingsTestBlocked -> SettingsDialog.TestNotificationBlocked
                FlowScene.SettingsTestScheduled -> SettingsDialog.TestNotificationScheduled
                else -> null
            }
        }
    }
    val initialItem = when {
        scene == FlowScene.SettingsInformation || state.dialog != null -> 5
        state.showTroubleshooting -> 3
        else -> 0
    }
    SettingsRoute(
        contentPadding = padding, mealTimeSettings = PreviewFixtures.mealTimes,
        notificationEnabled = scene != FlowScene.SettingsNotificationOff,
        notificationPermissionState = NotificationPermissionState(permissionsAllowed, permissionsAllowed),
        expandNotificationTroubleshooting = false, onNotificationTroubleshootingExpanded = {},
        onBreakfastTimeChanged = {}, onLunchTimeChanged = {}, onDinnerTimeChanged = {},
        onNotificationEnabledChanged = {}, onRequestNotificationPermission = {},
        onRequestExactAlarmPermission = {}, onRefreshNotificationPermissions = {},
        onSendTestNotification = { permissionsAllowed }, onScheduleTestNotification = { permissionsAllowed },
        onResetRoutineData = {}, state = state,
        listState = rememberLazyListState(initialFirstVisibleItemIndex = initialItem),
    )
}

@Preview(name = "01 불러오는 중", group = "시작", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun LoadingPreview() = FlowPreview(FlowScene.Loading)

@Preview(name = "02 첫 등록 전", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayEmptyPreview() = FlowPreview(FlowScene.TodayEmpty)

@Preview(name = "03 복용 전", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayPendingPreview() = FlowPreview(FlowScene.TodayPending)

@Preview(name = "04 일부 복용 완료", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayPartialPreview() = FlowPreview(FlowScene.TodayPartial)

@Preview(name = "05 모두 복용 완료", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayCompletePreview() = FlowPreview(FlowScene.TodayComplete)

@Preview(name = "06 체크 해제와 다시 완료", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayUndoPreview() = FlowPreview(FlowScene.TodayUndo)

@Preview(name = "07 첫 영양제 저장 완료", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodaySavedPreview() = FlowPreview(FlowScene.TodaySaved)

@Preview(name = "08 알림 권한 안내", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayPermissionPreview() = FlowPreview(FlowScene.TodayPermission)

@Preview(name = "09 기록 저장 오류", group = "오늘", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayErrorPreview() = FlowPreview(FlowScene.TodayError)

@Preview(name = "10 빈 목록", group = "영양제", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SupplementsEmptyPreview() = FlowPreview(FlowScene.SupplementsEmpty)

@Preview(name = "11 복용 방식별 목록", group = "영양제", widthDp = 400, heightDp = 1100, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SupplementsListPreview() = FlowPreview(FlowScene.SupplementsList)

@Preview(name = "12 삭제 확인", group = "영양제", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SupplementsDeletePreview() = FlowPreview(FlowScene.SupplementsDelete)

@Preview(name = "13 식사 기준 등록", group = "등록과 수정", widthDp = 400, heightDp = 1350, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormRoutinePreview() = FlowPreview(FlowScene.FormRoutine)

@Preview(name = "14 정해진 시간 등록", group = "등록과 수정", widthDp = 400, heightDp = 1350, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormFixedPreview() = FlowPreview(FlowScene.FormFixed)

@Preview(name = "15 일정 간격 등록", group = "등록과 수정", widthDp = 400, heightDp = 1350, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormIntervalPreview() = FlowPreview(FlowScene.FormInterval)

@Preview(name = "16 기존 영양제 수정", group = "등록과 수정", widthDp = 400, heightDp = 1350, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormEditPreview() = FlowPreview(FlowScene.FormEdit)

@Preview(name = "17 이름 입력 오류", group = "등록과 수정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormNameErrorPreview() = FlowPreview(FlowScene.FormNameError)

@Preview(name = "18 복용량 입력 오류", group = "등록과 수정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormDosageErrorPreview() = FlowPreview(FlowScene.FormDosageError)

@Preview(name = "19 복용 조건 입력 오류", group = "등록과 수정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormSlotErrorPreview() = FlowPreview(FlowScene.FormSlotError)

@Preview(name = "20 저장 중", group = "등록과 수정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormSavingPreview() = FlowPreview(FlowScene.FormSaving)

@Preview(name = "21 저장 실패와 재시도", group = "등록과 수정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormSaveErrorPreview() = FlowPreview(FlowScene.FormSaveError)

@Preview(name = "22 추가 복용량 단위", group = "등록과 수정", widthDp = 400, heightDp = 1100, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormExtraUnitsPreview() = FlowPreview(FlowScene.FormExtraUnits)

@Preview(name = "23 알림과 메모", group = "등록과 수정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormOptionsPreview() = FlowPreview(FlowScene.FormOptions)

@Preview(name = "24 기록이 없는 상태", group = "기록", widthDp = 400, heightDp = 1100, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoryEmptyPreview() = FlowPreview(FlowScene.HistoryEmpty)

@Preview(name = "25 월간 기록", group = "기록", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoryCalendarPreview() = FlowPreview(FlowScene.HistoryCalendar)

@Preview(name = "26 이전 날짜 선택", group = "기록", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistorySelectedPreview() = FlowPreview(FlowScene.HistorySelected)

@Preview(name = "27 최근 기록과 복용 패턴", group = "기록", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoryRecentPreview() = FlowPreview(FlowScene.HistoryRecent)

@Preview(name = "28 기본 설정", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsDefaultPreview() = FlowPreview(FlowScene.SettingsDefault)

@Preview(name = "29 새 영양제 알림 기본값 끔", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsNotificationOffPreview() = FlowPreview(FlowScene.SettingsNotificationOff)

@Preview(name = "30 식사 시간 편집", group = "설정", widthDp = 400, heightDp = 1100, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsMealEditPreview() = FlowPreview(FlowScene.SettingsMealEdit)

@Preview(name = "31 알림 권한 허용", group = "설정", widthDp = 400, heightDp = 1100, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsPermissionsAllowedPreview() = FlowPreview(FlowScene.SettingsPermissionsAllowed)

@Preview(name = "32 알림 권한 미허용", group = "설정", widthDp = 400, heightDp = 1100, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsPermissionsBlockedPreview() = FlowPreview(FlowScene.SettingsPermissionsBlocked)

@Preview(name = "33 앱 정보와 데이터 관리", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsInformationPreview() = FlowPreview(FlowScene.SettingsInformation)

@Preview(name = "34 데이터 초기화 확인", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@RoboComposePreviewOptions(manualClockOptions = [ManualClockOptions(advanceTimeMillis = 500L)])
@Composable
fun SettingsResetPreview() = FlowPreview(FlowScene.SettingsReset)

@Preview(name = "35 데이터 초기화 완료", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsResetDonePreview() = FlowPreview(FlowScene.SettingsResetDone)

@Preview(name = "36 사용 가이드", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsGuidePreview() = FlowPreview(FlowScene.SettingsGuide)

@Preview(name = "37 면책 고지", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsDisclaimerPreview() = FlowPreview(FlowScene.SettingsDisclaimer)

@Preview(name = "38 테스트 알림 전송 결과", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsTestSentPreview() = FlowPreview(FlowScene.SettingsTestSent)

@Preview(name = "39 테스트 알림 권한 안내", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsTestBlockedPreview() = FlowPreview(FlowScene.SettingsTestBlocked)

@Preview(name = "40 테스트 알림 예약 결과", group = "설정", widthDp = 400, heightDp = 900, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsTestScheduledPreview() = FlowPreview(FlowScene.SettingsTestScheduled)

@Preview(name = "큰 글자 오늘", group = "좁은 화면", widthDp = 360, heightDp = 900, fontScale = 1.3f, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayPartialLargeTextPreview() = FlowPreview(FlowScene.TodayPartial)

@Preview(name = "큰 글자 등록", group = "좁은 화면", widthDp = 360, heightDp = 900, fontScale = 1.3f, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FormRoutineLargeTextPreview() = FlowPreview(FlowScene.FormRoutine)

@Preview(name = "큰 글자 기록", group = "좁은 화면", widthDp = 360, heightDp = 900, fontScale = 1.3f, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoryCalendarLargeTextPreview() = FlowPreview(FlowScene.HistoryCalendar)

@Preview(name = "큰 글자 식사 시간", group = "좁은 화면", widthDp = 360, heightDp = 900, fontScale = 1.3f, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsMealEditLargeTextPreview() = FlowPreview(FlowScene.SettingsMealEdit)

@Preview(name = "확장 오늘", group = "확장 화면", widthDp = 1280, heightDp = 800, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TodayPartialExpandedPreview() = FlowPreview(FlowScene.TodayPartial)

@Preview(name = "확장 영양제", group = "확장 화면", widthDp = 1280, heightDp = 800, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SupplementsListExpandedPreview() = FlowPreview(FlowScene.SupplementsList)

@Preview(name = "확장 기록", group = "확장 화면", widthDp = 1280, heightDp = 800, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoryCalendarExpandedPreview() = FlowPreview(FlowScene.HistoryCalendar)

@Preview(name = "확장 설정", group = "확장 화면", widthDp = 1280, heightDp = 800, locale = "ko", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsDefaultExpandedPreview() = FlowPreview(FlowScene.SettingsDefault)
