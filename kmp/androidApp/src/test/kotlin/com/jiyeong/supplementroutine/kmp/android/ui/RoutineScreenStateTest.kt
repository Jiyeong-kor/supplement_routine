package com.jiyeong.supplementroutine.kmp.android.ui

import android.app.Application
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.jiyeong.supplementroutine.kmp.android.notification.NotificationPermissionState
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsRoute
import com.jiyeong.supplementroutine.kmp.android.ui.settings.SettingsScreenState
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementFormScreen
import com.jiyeong.supplementroutine.kmp.android.ui.supplements.SupplementFormState
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SupplementRoutineTheme
import com.jiyeong.supplementroutine.shared.domain.IntakeCondition
import com.jiyeong.supplementroutine.shared.domain.IntakeSlot
import com.jiyeong.supplementroutine.shared.domain.MealTimeSettings
import com.jiyeong.supplementroutine.shared.domain.MealType
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h900dp-xhdpi", application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RoutineScreenStateTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun invalidNameBlocksSaveAndFailedSaveKeepsInputForRetry() {
        val state = SupplementFormState().apply {
            selectedSlots = setOf(IntakeSlot(MealType.Breakfast, IntakeCondition.AfterMeal))
        }
        val submitted = mutableListOf<Supplement>()
        var failSave: ((String) -> Unit)? = null
        compose.setContent {
            SupplementRoutineTheme(darkTheme = false) {
                SupplementFormScreen(
                    contentPadding = PaddingValues(0.dp), initialSupplement = null,
                    defaultNotificationEnabled = true, onDismiss = {}, onValidationError = {},
                    onSubmit = { supplement, onFailure -> submitted.add(supplement); failSave = onFailure },
                    state = state,
                )
            }
        }
        compose.onNodeWithText("취소").assertIsDisplayed()
        compose.onNodeWithText("저장").performClick()
        compose.onNodeWithText("영양제 이름을 입력해주세요.").assertExists()
        compose.runOnIdle { assertTrue(submitted.isEmpty()) }

        compose.onNodeWithText("영양제 이름").performTextInput("비타민 D")
        compose.onNodeWithText("저장").performClick()
        compose.onNodeWithText("저장 중").assertIsNotEnabled()
        compose.runOnIdle { requireNotNull(failSave)("storage failure") }
        compose.onNodeWithText("다시 시도").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(listOf("비타민 D", "비타민 D"), submitted.map { it.name })
            assertEquals("비타민 D", state.name)
            assertEquals(1.0, submitted.last().dosageValue, 0.0)
        }
    }

    @Test
    fun mealTimeDraftIsDiscardedOnCancelAndPublishedOnlyOnSave() {
        val settings = MealTimeSettings()
        val state = SettingsScreenState(settings)
        val saved = mutableListOf<TimeOfDayValue>()
        compose.setContent {
            SupplementRoutineTheme(darkTheme = false) {
                SettingsRoute(
                    contentPadding = PaddingValues(0.dp), mealTimeSettings = settings,
                    notificationEnabled = true, notificationPermissionState = NotificationPermissionState(true, true),
                    expandNotificationTroubleshooting = false, onNotificationTroubleshootingExpanded = {},
                    onBreakfastTimeChanged = { saved.add(it) }, onLunchTimeChanged = { saved.add(it) },
                    onDinnerTimeChanged = { saved.add(it) }, onNotificationEnabledChanged = {},
                    onRequestNotificationPermission = {}, onRequestExactAlarmPermission = {},
                    onRefreshNotificationPermissions = {}, onSendTestNotification = { true },
                    onScheduleTestNotification = { true }, onResetRoutineData = {}, state = state,
                )
            }
        }
        compose.onNodeWithText("식사 시간 설정").performClick()
        compose.onAllNodesWithText("+10")[0].performClick()
        compose.onNodeWithText("취소").performClick()
        compose.runOnIdle {
            assertTrue(saved.isEmpty())
            assertFalse(state.mealTimesEditing)
        }
        compose.onNodeWithText("식사 시간 설정").performClick()
        compose.runOnIdle { assertEquals(settings, state.draftMealTimeSettings) }
        compose.onAllNodesWithText("+10")[0].performClick()
        compose.onNodeWithText("식사 시간 저장").performClick()
        compose.runOnIdle {
            assertEquals(listOf(TimeOfDayValue(8, 10), settings.lunchTime, settings.dinnerTime), saved)
            assertFalse(state.mealTimesEditing)
        }
    }
}
