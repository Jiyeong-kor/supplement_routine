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
import androidx.compose.ui.unit.dp

@Composable
internal fun routineGlassCardColors(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
): CardColors {
    return CardDefaults.cardColors(containerColor = containerColor)
}

@Composable
internal fun routineGlassCardElevation(): CardElevation {
    return CardDefaults.cardElevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp,
        focusedElevation = 0.dp,
        hoveredElevation = 0.dp,
    )
}

@Composable
internal fun routineGlassBorder(alpha: Float = 1f): BorderStroke {
    return BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = alpha))
}

internal fun Modifier.routineGlassSheen(): Modifier {
    return drawWithContent {
        drawContent()
    }
}
