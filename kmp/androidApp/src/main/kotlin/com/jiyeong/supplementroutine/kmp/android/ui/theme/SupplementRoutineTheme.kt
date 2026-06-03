package com.jiyeong.supplementroutine.kmp.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SeedPurple = Color(0xFF6B36F6)
private val LavenderContainer = Color(0xFFEADDFF)
private val Success = Color(0xFF20B486)

private val LightColors: ColorScheme = lightColorScheme(
    primary = SeedPurple,
    onPrimary = Color.White,
    primaryContainer = LavenderContainer,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Success,
    surface = Color(0xFFFFFBFE),
    surfaceContainerLow = Color(0xFFF8F2FA),
    surfaceContainerHighest = Color(0xFFE7E0EC),
    background = Color(0xFFFFFBFE),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
)

@Composable
fun SupplementRoutineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
