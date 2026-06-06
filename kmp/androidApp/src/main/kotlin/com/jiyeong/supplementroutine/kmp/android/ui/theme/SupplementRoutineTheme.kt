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
    primary = Color(0xFF496DFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDEAF1),
    onPrimaryContainer = Color(0xFF202124),
    secondary = Color(0xFFB9DE65),
    onSecondary = Color(0xFF202124),
    secondaryContainer = Color(0xFFF0FAEE),
    onSecondaryContainer = Color(0xFF202124),
    tertiary = Color(0xFFFF4A3D),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF7DF),
    onTertiaryContainer = Color(0xFF202124),
    error = Color(0xFFD84C3F),
    onError = Color.White,
    errorContainer = Color(0xFFFFF0EE),
    onErrorContainer = Color(0xFF202124),
    background = Color(0xFFF8F4EC),
    onBackground = Color(0xFF202124),
    surface = Color(0xFFFFFDF8),
    onSurface = Color(0xFF202124),
    surfaceVariant = Color(0xFFF2EBDD),
    onSurfaceVariant = Color(0xFF5F5B53),
    surfaceContainerLow = Color(0xFFFFFDF8),
    surfaceContainer = Color(0xFFFAF3E6),
    surfaceContainerHigh = Color(0xFFF2EBDD),
    surfaceContainerHighest = Color(0xFFDDEAF1),
    outline = Color(0xFFE7DDCC),
    outlineVariant = Color(0xFFE7DDCC),
    inverseSurface = Color(0xFF202124),
    inverseOnSurface = Color(0xFFF8F4EC),
    inversePrimary = Color(0xFFAEBBFF),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF8ED8F1),
    onPrimary = Color(0xFF052334),
    primaryContainer = Color(0xFF1F4E64),
    onPrimaryContainer = Color(0xFFDDF1FA),
    secondary = Color(0xFFFFE979),
    onSecondary = Color(0xFF352F00),
    secondaryContainer = Color(0xFF5B510E),
    onSecondaryContainer = Color(0xFFFFF7C7),
    tertiary = Color(0xFFA7DBD7),
    onTertiary = Color(0xFF0A2C2F),
    tertiaryContainer = Color(0xFF2A5659),
    onTertiaryContainer = Color(0xFFD9F0EA),
    error = Color(0xFFFF9AAD),
    onError = Color(0xFF55111E),
    errorContainer = Color(0xFF742235),
    onErrorContainer = Color(0xFFFFDDE4),
    background = Color(0xFF101C27),
    onBackground = Color(0xFFEAF3F8),
    surface = Color(0xFF162635),
    onSurface = Color(0xFFEAF3F8),
    surfaceVariant = Color(0xFF203241),
    onSurfaceVariant = Color(0xFFB8C7D3),
    surfaceContainerLow = Color(0xFF142331),
    surfaceContainer = Color(0xFF1A2B39),
    surfaceContainerHigh = Color(0xFF223543),
    surfaceContainerHighest = Color(0xFF294354),
    outline = Color(0xFF536D7E),
    outlineVariant = Color(0xFF304858),
    inverseSurface = Color(0xFFEAF3F8),
    inverseOnSurface = Color(0xFF06172B),
    inversePrimary = Color(0xFF59B9DF),
)

private val RoutineTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 40.sp),
        headlineLarge = headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 32.sp),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 32.sp),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 32.sp),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp),
        titleSmall = titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = bodyLarge.copy(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = bodyMedium.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = bodySmall.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp),
        labelMedium = labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
        labelSmall = labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
    )
}

private val RoutineShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
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
