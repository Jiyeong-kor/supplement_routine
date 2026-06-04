package com.jiyeong.supplementroutine.kmp.android.ui.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Immutable
class RoutineHapticFeedback internal constructor(
    private val perform: (HapticIntent) -> Unit,
) {
    fun intakeCompleted() = perform(HapticIntent.IntakeCompleted)

    fun saved() = perform(HapticIntent.Saved)

    fun destructiveConfirmed() = perform(HapticIntent.DestructiveConfirmed)

    fun validationWarning() = perform(HapticIntent.ValidationWarning)
}

enum class HapticIntent {
    IntakeCompleted,
    Saved,
    DestructiveConfirmed,
    ValidationWarning,
}

@Composable
fun rememberRoutineHapticFeedback(): RoutineHapticFeedback {
    val view = LocalView.current
    return remember(view) {
        RoutineHapticFeedback { intent ->
            val feedbackConstant = when (intent) {
                HapticIntent.IntakeCompleted,
                HapticIntent.Saved -> confirmFeedbackConstant()
                HapticIntent.DestructiveConfirmed -> HapticFeedbackConstants.LONG_PRESS
                HapticIntent.ValidationWarning -> rejectFeedbackConstant()
            }
            view.performHapticFeedback(feedbackConstant)
        }
    }
}

private fun confirmFeedbackConstant(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        HapticFeedbackConstants.CONFIRM
    } else {
        HapticFeedbackConstants.CONTEXT_CLICK
    }
}

private fun rejectFeedbackConstant(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        HapticFeedbackConstants.REJECT
    } else {
        HapticFeedbackConstants.LONG_PRESS
    }
}
