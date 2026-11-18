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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jiyeong.supplementroutine.kmp.android.R

private val LightColors: ColorScheme = lightColorScheme(
    primary = SrLightSemanticColors.actionPrimary,
    onPrimary = SrLightSemanticColors.actionOnPrimary,
    primaryContainer = SrPrimitiveColor.SkyBlue,
    onPrimaryContainer = SrLightSemanticColors.textPrimary,
    secondary = SrLightSemanticColors.actionPrimary,
    onSecondary = SrLightSemanticColors.actionOnPrimary,
    secondaryContainer = SrPrimitiveColor.Lemon,
    onSecondaryContainer = SrLightSemanticColors.textPrimary,
    tertiary = SrLightSemanticColors.warning,
    onTertiary = SrLightSemanticColors.textPrimary,
    tertiaryContainer = SrPrimitiveColor.SunYellow,
    onTertiaryContainer = SrLightSemanticColors.textPrimary,
    error = SrLightSemanticColors.error,
    onError = SrLightSemanticColors.actionOnPrimary,
    errorContainer = SrPrimitiveColor.ErrorSurface,
    onErrorContainer = SrLightSemanticColors.textPrimary,
    background = SrLightSemanticColors.appBackground,
    onBackground = SrLightSemanticColors.textPrimary,
    surface = SrLightSemanticColors.surface,
    onSurface = SrLightSemanticColors.textPrimary,
    surfaceVariant = SrLightSemanticColors.subtleBackground,
    onSurfaceVariant = SrLightSemanticColors.textSecondary,
    surfaceContainerLow = SrLightSemanticColors.surface,
    surfaceContainer = SrLightSemanticColors.surfaceSoft,
    surfaceContainerHigh = SrLightSemanticColors.subtleBackground,
    surfaceContainerHighest = SrPrimitiveColor.SkyBlue,
    outline = SrPrimitiveColor.Disabled,
    outlineVariant = SrLightSemanticColors.border,
    inverseSurface = SrLightSemanticColors.textPrimary,
    inverseOnSurface = SrLightSemanticColors.appBackground,
    inversePrimary = SrPrimitiveColor.SkyBlue,
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = SrDarkSemanticColors.actionPrimary,
    onPrimary = SrDarkSemanticColors.actionOnPrimary,
    primaryContainer = SrPrimitiveColor.DarkBlueSurface,
    onPrimaryContainer = SrDarkSemanticColors.textPrimary,
    secondary = SrDarkSemanticColors.actionPrimary,
    onSecondary = SrDarkSemanticColors.textInverse,
    secondaryContainer = SrPrimitiveColor.DarkLemonSurface,
    onSecondaryContainer = SrDarkSemanticColors.textPrimary,
    tertiary = SrDarkSemanticColors.warning,
    onTertiary = SrDarkSemanticColors.textInverse,
    tertiaryContainer = SrDarkSemanticColors.surfaceSoft,
    onTertiaryContainer = SrDarkSemanticColors.textPrimary,
    error = SrDarkSemanticColors.error,
    onError = SrPrimitiveColor.Ink,
    errorContainer = SrPrimitiveColor.Error,
    onErrorContainer = SrPrimitiveColor.White,
    background = SrDarkSemanticColors.appBackground,
    onBackground = SrDarkSemanticColors.textPrimary,
    surface = SrDarkSemanticColors.surface,
    onSurface = SrDarkSemanticColors.textPrimary,
    surfaceVariant = SrDarkSemanticColors.surfaceSoft,
    onSurfaceVariant = SrDarkSemanticColors.textSecondary,
    surfaceContainerLow = SrDarkSemanticColors.surface,
    surfaceContainer = SrDarkSemanticColors.surfaceSoft,
    surfaceContainerHigh = SrPrimitiveColor.DarkSurfaceSoft,
    surfaceContainerHighest = SrPrimitiveColor.DarkSurfaceSoft,
    outline = SrDarkSemanticColors.border,
    outlineVariant = SrDarkSemanticColors.border,
    inverseSurface = SrDarkSemanticColors.textPrimary,
    inverseOnSurface = SrDarkSemanticColors.appBackground,
    inversePrimary = SrPrimitiveColor.PrimaryBlue,
)

private val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_bold, FontWeight.ExtraBold),
)

private fun TextStyle.withPretendard(
    fontWeight: FontWeight,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
): TextStyle {
    return copy(
        fontFamily = Pretendard,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
    )
}

private val RoutineTypography = Typography().run {
    copy(
        displayMedium = displayMedium.withPretendard(FontWeight.Bold, 32.sp, 40.sp),
        displaySmall = displaySmall.withPretendard(FontWeight.Bold, 28.sp, 36.sp),
        displayLarge = displayLarge.withPretendard(FontWeight.ExtraBold, 32.sp, 40.sp),
        headlineLarge = headlineLarge.withPretendard(FontWeight.ExtraBold, 24.sp, 32.sp),
        headlineMedium = headlineMedium.withPretendard(FontWeight.ExtraBold, 24.sp, 32.sp),
        headlineSmall = headlineSmall.withPretendard(FontWeight.ExtraBold, 24.sp, 32.sp),
        titleLarge = titleLarge.withPretendard(FontWeight.SemiBold, 20.sp, 28.sp),
        titleMedium = titleMedium.withPretendard(FontWeight.SemiBold, 16.sp, 24.sp),
        titleSmall = titleSmall.withPretendard(FontWeight.SemiBold, 14.sp, 20.sp),
        bodyLarge = bodyLarge.withPretendard(FontWeight.Normal, 16.sp, 24.sp),
        bodyMedium = bodyMedium.withPretendard(FontWeight.Normal, 14.sp, 20.sp),
        bodySmall = bodySmall.withPretendard(FontWeight.Normal, 13.sp, 18.sp),
        labelLarge = labelLarge.withPretendard(FontWeight.SemiBold, 13.sp, 18.sp),
        labelMedium = labelMedium.withPretendard(FontWeight.SemiBold, 12.sp, 16.sp),
        labelSmall = labelSmall.withPretendard(FontWeight.SemiBold, 12.sp, 16.sp),
    )
}

private val RoutineShapes = Shapes(
    extraSmall = RoundedCornerShape(SrRadius.Xs),
    small = RoundedCornerShape(SrRadius.Sm),
    medium = RoundedCornerShape(SrRadius.Md),
    large = RoundedCornerShape(SrRadius.Lg),
    extraLarge = RoundedCornerShape(SrRadius.Xl),
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
