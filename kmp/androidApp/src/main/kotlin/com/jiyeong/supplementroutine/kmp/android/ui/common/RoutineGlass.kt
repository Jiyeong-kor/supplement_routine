package com.jiyeong.supplementroutine.kmp.android.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
        defaultElevation = 3.dp,
        pressedElevation = 1.dp,
        focusedElevation = 4.dp,
        hoveredElevation = 4.dp,
    )
}

@Composable
internal fun routineGlassBorder(alpha: Float = 0.9f): BorderStroke {
    return BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = alpha))
}

internal fun Modifier.routineGlassSheen(): Modifier {
    return drawWithContent {
        drawContent()
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.24f),
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent,
                ),
                start = Offset.Zero,
                end = Offset(size.width * 0.82f, size.height * 0.42f),
            ),
            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
        )
    }
}
