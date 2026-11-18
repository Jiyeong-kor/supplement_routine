package com.jiyeong.supplementroutine.kmp.android.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
internal object SrPrimitiveColor {
    val Background = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF3F6F8)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceSoft = Color(0xFFF3F6F8)
    val PrimaryBlue = Color(0xFF006199)
    val SkyBlue = Color(0xFF8ACFF8)
    val Lemon = Color(0xFFF4EB6C)
    val SunYellow = Color(0xFFFFD444)
    val White = Color(0xFFFFFFFF)
    val Ink = Color(0xFF172B3A)
    val InkMuted = Color(0xFF536675)
    val Line = Color(0xFFD4DFE6)
    val Disabled = Color(0xFF71818D)
    val Error = Color(0xFFB3261E)
    val ErrorSurface = Color(0xFFFFEDEA)
    val DarkError = Color(0xFFFFB4AB)
    val DarkBackground = Color(0xFF111C24)
    val DarkSurface = Color(0xFF1B2933)
    val DarkSurfaceSoft = Color(0xFF263844)
    val DarkBlueSurface = Color(0xFF163E56)
    val DarkLemonSurface = Color(0xFF3E3B19)
    val DarkText = Color(0xFFF1F6FA)
    val DarkTextSecondary = Color(0xFFB6C6D2)
}

@Immutable
internal data class SrSemanticColors(
    val appBackground: Color,
    val subtleBackground: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textInverse: Color,
    val border: Color,
    val actionPrimary: Color,
    val actionOnPrimary: Color,
    val warning: Color,
    val error: Color,
)

internal val SrLightSemanticColors = SrSemanticColors(
    appBackground = SrPrimitiveColor.Background,
    subtleBackground = SrPrimitiveColor.SurfaceMuted,
    surface = SrPrimitiveColor.Surface,
    surfaceSoft = SrPrimitiveColor.SurfaceSoft,
    textPrimary = SrPrimitiveColor.Ink,
    textSecondary = SrPrimitiveColor.InkMuted,
    textInverse = SrPrimitiveColor.White,
    border = SrPrimitiveColor.Line,
    actionPrimary = SrPrimitiveColor.PrimaryBlue,
    actionOnPrimary = SrPrimitiveColor.White,
    warning = SrPrimitiveColor.SunYellow,
    error = SrPrimitiveColor.Error,
)

internal val SrDarkSemanticColors = SrSemanticColors(
    appBackground = SrPrimitiveColor.DarkBackground,
    subtleBackground = SrPrimitiveColor.DarkSurfaceSoft,
    surface = SrPrimitiveColor.DarkSurface,
    surfaceSoft = SrPrimitiveColor.DarkSurfaceSoft,
    textPrimary = SrPrimitiveColor.DarkText,
    textSecondary = SrPrimitiveColor.DarkTextSecondary,
    textInverse = SrPrimitiveColor.Ink,
    border = SrPrimitiveColor.DarkSurfaceSoft,
    actionPrimary = SrPrimitiveColor.SkyBlue,
    actionOnPrimary = SrPrimitiveColor.Ink,
    warning = SrPrimitiveColor.SunYellow,
    error = SrPrimitiveColor.DarkError,
)

@Immutable
internal object SrSpacing {
    val None: Dp = 0.dp
    val Xxs: Dp = 4.dp
    val Xs: Dp = 6.dp
    val Sm: Dp = 8.dp
    val Md: Dp = 12.dp
    val Lg: Dp = 16.dp
    val Xl: Dp = 20.dp
    val Xxl: Dp = 24.dp

    val ScreenHorizontal: Dp = Xl
    val ScreenBottomBuffer: Dp = Xxl
}

@Immutable
internal object SrRadius {
    val Xs: Dp = 8.dp
    val Sm: Dp = 12.dp
    val Md: Dp = 20.dp
    val Lg: Dp = 28.dp
    val Xl: Dp = 36.dp
}

@Immutable
internal object SrSize {
    val IconTiny: Dp = 14.dp
    val Icon: Dp = 20.dp
    val IconSmall: Dp = 18.dp
    val IconMedium: Dp = 22.dp
    val IconLarge: Dp = 32.dp
    val IconBadge: Dp = 44.dp
    val Control: Dp = 44.dp
    val ControlSmall: Dp = 38.dp
    val ControlCompact: Dp = 34.dp
    val MinTouchTarget: Dp = 48.dp
    val BottomNavigationHeight: Dp = 72.dp
    val BottomNavigationIndicatorWidth: Dp = 52.dp
    val BottomNavigationIndicatorHeight: Dp = 30.dp
    val SkeletonRowHeight: Dp = 72.dp
    val SkeletonAvatar: Dp = 40.dp
    val ProgressLineHeight: Dp = 6.dp
    val CalendarDay: Dp = 32.dp
    val Fab: Dp = 64.dp
}

@Immutable
internal object SrStroke {
    val None: Dp = 0.dp
    val Thin: Dp = 1.dp
    val Strong: Dp = 2.dp
}

@Immutable
internal object SrElevation {
    val None: Dp = 0.dp
    val Low: Dp = 2.dp
    val Pressed: Dp = 4.dp
}

@Immutable
internal object SrOpacity {
    const val FloatingSurface: Float = 0.96f
    const val SoftContainer: Float = 0.58f
}
