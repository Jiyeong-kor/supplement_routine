package com.jiyeong.supplementroutine.kmp.android.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrElevation
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrStroke

@Composable
internal fun routineGlassCardColors(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
): CardColors {
    return CardDefaults.cardColors(containerColor = containerColor)
}

@Composable
internal fun routineGlassCardElevation(): CardElevation {
    return CardDefaults.cardElevation(
        defaultElevation = SrElevation.None,
        pressedElevation = SrElevation.None,
        focusedElevation = SrElevation.None,
        hoveredElevation = SrElevation.None,
    )
}

@Composable
internal fun routineGlassBorder(alpha: Float = 1f): BorderStroke {
    return BorderStroke(SrStroke.Thin, MaterialTheme.colorScheme.outline.copy(alpha = alpha))
}

internal fun Modifier.routineGlassSheen(): Modifier {
    return drawWithContent {
        drawContent()
    }
}
