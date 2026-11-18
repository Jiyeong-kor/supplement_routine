package com.jiyeong.supplementroutine.kmp.android.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrPrimitiveColor
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrElevation
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrIcon
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSize
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrSpacing
import com.jiyeong.supplementroutine.kmp.android.ui.theme.SrStroke
import kotlin.math.cos
import kotlin.math.sin

internal object GardenUi {
    val SurfaceSoft = SrPrimitiveColor.SurfaceSoft
    val SkyBlue = SrPrimitiveColor.SkyBlue
    val PrimaryBlue = SrPrimitiveColor.PrimaryBlue
    val Lemon = SrPrimitiveColor.Lemon
    val SunYellow = SrPrimitiveColor.SunYellow
    val Ink = SrPrimitiveColor.Ink
}

internal enum class FruitVariant { Blueberry, Flower, Apple, EggFlower }

@Composable
internal fun RoutinePageHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    eyebrow: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(SrSpacing.Xs)) {
            eyebrow?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
internal fun RoutineSectionLabel(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f).semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        trailing?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
internal fun RoutineCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = colors,
        elevation = CardDefaults.cardElevation(defaultElevation = SrElevation.None),
        border = BorderStroke(SrStroke.Thin, MaterialTheme.colorScheme.outlineVariant),
        content = content,
    )
}

@Composable
internal fun RoutineSlotCard(
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    title: @Composable ColumnScope.() -> Unit,
    supporting: (@Composable ColumnScope.() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    RoutineCard(
        modifier = modifier.then(
            if (contentDescription == null) {
                Modifier
            } else {
                Modifier.semantics { this.contentDescription = contentDescription }
            },
        ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
                .padding(SrSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SrSpacing.Md),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SrSpacing.Md),
                verticalAlignment = Alignment.Top,
            ) {
                leading?.invoke()
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SrSpacing.Xs),
                ) {
                    title()
                    supporting?.invoke(this)
                }
                trailing?.invoke()
            }
            actions?.invoke(this)
        }
    }
}

@Composable
internal fun RoutineStatusCard(
    title: String,
    description: String,
    icon: SrIcon,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.primary,
) {
    RoutineSlotCard(
        modifier = modifier,
        leading = {
            RoutineIconBadge(
                icon = icon,
                containerColor = iconContainerColor,
                tint = iconTint,
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        supporting = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        },
        actions = if (actionLabel == null || onActionClick == null) {
            null
        } else {
            {
                RoutinePillButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    height = SrSize.ControlSmall,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        containerColor = containerColor,
    )
}

@Composable
internal fun RoutineIconBadge(
    icon: SrIcon,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
) {
    Surface(
        modifier = modifier.size(SrSize.IconBadge),
        shape = CircleShape,
        color = containerColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon.drawableRes),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(SrSize.Icon),
            )
        }
    }
}

@Composable
internal fun RoutinePillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: SrIcon? = null,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    height: Dp = SrSize.Control,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height.coerceAtLeast(SrSize.MinTouchTarget))
            .clickable(role = Role.Button, onClick = onClick),
        shape = CircleShape,
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SrSpacing.Lg, vertical = SrSpacing.Md),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    painter = painterResource(it.drawableRes),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(SrSize.IconSmall),
                )
            }
            Text(
                text = text,
                modifier = Modifier.padding(start = if (icon == null) SrSpacing.None else SrSpacing.Xs),
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun RoutineMoreButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .size(SrSize.MinTouchTarget)
            .clickable(role = Role.Button, onClick = onClick),
        shape = CircleShape,
        color = Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(SrIcon.Chevron.drawableRes),
                contentDescription = "더보기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(SrSize.IconSmall),
            )
        }
    }
}

@Composable
internal fun RoutineFab(
    onClick: () -> Unit,
    icon: SrIcon,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(SrSize.Fab),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = SrElevation.Low,
            pressedElevation = SrElevation.Pressed,
            focusedElevation = SrElevation.Low,
            hoveredElevation = SrElevation.Low,
        ),
    ) {
        Icon(
            painter = painterResource(icon.drawableRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(SrSize.IconLarge),
        )
    }
}

@Composable
internal fun RowScope.RoutineWeightedText(
    text: String,
    weight: Float,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
internal fun FruitAvatar(
    variant: FruitVariant,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(SrSize.IconBadge)) {
        when (variant) {
            FruitVariant.Blueberry -> {
                drawOval(GardenUi.PrimaryBlue, size = size)
                repeat(6) { index ->
                    val angle = Math.toRadians((index * 60).toDouble())
                    drawLine(
                        color = GardenUi.Ink,
                        start = center,
                        end = Offset(
                            x = center.x + cos(angle).toFloat() * size.minDimension * 0.28f,
                            y = center.y + sin(angle).toFloat() * size.minDimension * 0.28f,
                        ),
                        strokeWidth = size.minDimension * 0.08f,
                    )
                }
            }
            FruitVariant.Flower -> {
                repeat(6) { index ->
                    rotate(index * 60f, pivot = center) {
                        drawOval(
                            color = GardenUi.SunYellow,
                            topLeft = Offset(size.width * 0.36f, 0f),
                            size = Size(size.width * 0.28f, size.height * 0.62f),
                        )
                    }
                }
                drawCircle(Color.White, radius = size.minDimension * 0.16f)
            }
            FruitVariant.Apple -> {
                drawOval(
                    color = GardenUi.Lemon,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.22f),
                    size = Size(size.width * 0.8f, size.height * 0.72f),
                )
                drawLine(
                    color = GardenUi.Ink,
                    start = Offset(center.x + size.width * 0.04f, size.height * 0.28f),
                    end = Offset(center.x + size.width * 0.18f, size.height * 0.04f),
                    strokeWidth = size.minDimension * 0.09f,
                )
            }
            FruitVariant.EggFlower -> {
                repeat(5) { index ->
                    rotate(index * 72f, pivot = center) {
                        drawOval(
                            color = Color.White,
                            topLeft = Offset(size.width * 0.34f, 0f),
                            size = Size(size.width * 0.32f, size.height * 0.58f),
                        )
                    }
                }
                drawCircle(GardenUi.SunYellow, radius = size.minDimension * 0.18f)
            }
        }
    }
}

internal fun fruitVariantFor(key: String): FruitVariant {
    return when (kotlin.math.abs(key.hashCode()) % 4) {
        0 -> FruitVariant.Blueberry
        1 -> FruitVariant.Flower
        2 -> FruitVariant.Apple
        else -> FruitVariant.EggFlower
    }
}
