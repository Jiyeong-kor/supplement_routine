package com.jiyeong.supplementroutine.kmp.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFFE95E7B),
    onPrimary = Color(0xFF20232B),
    primaryContainer = Color(0xFFFFE6EC),
    onPrimaryContainer = Color(0xFF4A1322),
    secondary = Color(0xFFFF9A76),
    onSecondary = Color(0xFF20232B),
    secondaryContainer = Color(0xFFFFE7DE),
    onSecondaryContainer = Color(0xFF472114),
    tertiary = Color(0xFF24B88A),
    onTertiary = Color(0xFF20232B),
    tertiaryContainer = Color(0xFFDDF8EE),
    onTertiaryContainer = Color(0xFF063829),
    error = Color(0xFFD6455D),
    onError = Color.White,
    errorContainer = Color(0xFFFFE2E7),
    onErrorContainer = Color(0xFF55111E),
    background = Color(0xFFFFFDF9),
    onBackground = Color(0xFF20232B),
    surface = Color.White,
    onSurface = Color(0xFF20232B),
    surfaceVariant = Color(0xFFF7F8FA),
    onSurfaceVariant = Color(0xFF667085),
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF7F8FA),
    surfaceContainerHigh = Color(0xFFF2F4F7),
    surfaceContainerHighest = Color(0xFFFFE6EC),
    outline = Color(0xFFE6E2DE),
    outlineVariant = Color(0xFFF0EDEA),
    inverseSurface = Color(0xFF20232B),
    inverseOnSurface = Color(0xFFF8F7F4),
    inversePrimary = Color(0xFFFF8AA2),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFFF8AA2),
    onPrimary = Color(0xFF37101B),
    primaryContainer = Color(0xFF5A1B2B),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFFFB096),
    onSecondary = Color(0xFF3E1E12),
    secondaryContainer = Color(0xFF5E3021),
    onSecondaryContainer = Color(0xFFFFDED3),
    tertiary = Color(0xFF6DE0B5),
    onTertiary = Color(0xFF073528),
    tertiaryContainer = Color(0xFF154D3B),
    onTertiaryContainer = Color(0xFFDDF8EE),
    error = Color(0xFFFF8C9C),
    onError = Color(0xFF4A101C),
    errorContainer = Color(0xFF6D1E2B),
    onErrorContainer = Color(0xFFFFDDE4),
    background = Color(0xFF17181C),
    onBackground = Color(0xFFF8F7F4),
    surface = Color(0xFF22242A),
    onSurface = Color(0xFFF8F7F4),
    surfaceVariant = Color(0xFF2C2F36),
    onSurfaceVariant = Color(0xFFC9CDD6),
    surfaceContainerLow = Color(0xFF1C1E23),
    surfaceContainer = Color(0xFF22242A),
    surfaceContainerHigh = Color(0xFF282B31),
    surfaceContainerHighest = Color(0xFF2C2F36),
    outline = Color(0xFF555A64),
    outlineVariant = Color(0xFF3B3F47),
    inverseSurface = Color(0xFFF8F7F4),
    inverseOnSurface = Color(0xFF20232B),
    inversePrimary = Color(0xFFE95E7B),
)

private val RoutineTypography = Typography().run {
    copy(
        headlineLarge = headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 30.sp),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 26.sp),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        titleSmall = titleSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
        bodyLarge = bodyLarge.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bodyMedium = bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodySmall = bodySmall.copy(fontWeight = FontWeight.Normal, fontSize = 12.sp),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
        labelMedium = labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
        labelSmall = labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
    )
}

private val RoutineShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun SupplementRoutineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = RoutineTypography,
        shapes = RoutineShapes,
        content = content,
    )
}
