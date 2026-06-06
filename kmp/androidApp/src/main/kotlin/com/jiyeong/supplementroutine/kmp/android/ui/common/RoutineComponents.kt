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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MoreHoriz
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

internal object GardenUi {
    val WarmWhite = Color(0xFFF8F4EC)
    val Surface = Color(0xFFFFFDF8)
    val Paper = Color(0xFFF2EBDD)
    val SurfaceSoft = Color(0xFFFAF3E6)
    val MistBlue = Color(0xFFDDEAF1)
    val PrimaryBlue = Color(0xFF496DFF)
    val PrimaryBlueDark = Color(0xFFAEBBFF)
    val LeafGreen = Color(0xFFB9DE65)
    val LeafGreenDark = Color(0xFF5E7E2A)
    val FlowerRed = Color(0xFFFF4A3D)
    val Coral = Color(0xFFFF7A62)
    val EggYellow = Color(0xFFF4C052)
    val Ink = Color(0xFF202124)
    val InkMuted = Color(0xFF5F5B53)
    val Line = Color(0xFFE7DDCC)
    val Success = Color(0xFF6BBF73)
    val Danger = Color(0xFFD84C3F)
    val SuccessSurface = Color(0xFFF0FAEE)
    val DangerSurface = Color(0xFFFFF0EE)
}

internal enum class FruitVariant { Blueberry, Flower, Apple, EggFlower }

@Composable
internal fun RoutinePageHeader(
    title: String,
    subtitle: String? = null,
    eyebrow: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                style = MaterialTheme.typography.headlineSmall,
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
    trailing: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        content = content,
    )
}

@Composable
internal fun RoutineIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = containerColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
internal fun RoutineMetaChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun RoutinePillButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    height: Dp = 44.dp,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = containerColor,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(15.dp),
                )
            }
            Text(
                text = text,
                modifier = Modifier.padding(start = if (icon == null) 0.dp else 6.dp),
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun RoutineCheckButton(
    checked: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String,
) {
    Surface(
        modifier = modifier.size(34.dp),
        shape = CircleShape,
        color = if (checked) GardenUi.LeafGreen else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (checked) GardenUi.LeafGreen else MaterialTheme.colorScheme.outline),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (checked) Icons.Filled.Check else Icons.Outlined.Check,
                contentDescription = contentDescription,
                tint = if (checked) GardenUi.Ink else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(19.dp),
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
            .size(30.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = "더보기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
internal fun RoutineFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(64.dp),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            focusedElevation = 2.dp,
            hoveredElevation = 2.dp,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
internal fun RoutineEmptyCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    RoutineCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FruitAvatar(variant = FruitVariant.EggFlower, modifier = Modifier.size(58.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
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
    Canvas(modifier = modifier.size(44.dp)) {
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
                            color = GardenUi.FlowerRed,
                            topLeft = Offset(size.width * 0.36f, 0f),
                            size = Size(size.width * 0.28f, size.height * 0.62f),
                        )
                    }
                }
                drawCircle(Color.White, radius = size.minDimension * 0.16f)
            }
            FruitVariant.Apple -> {
                drawOval(
                    color = GardenUi.LeafGreen,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.22f),
                    size = Size(size.width * 0.8f, size.height * 0.72f),
                )
                drawLine(
                    color = Color(0xFF8A5A24),
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
                drawCircle(GardenUi.EggYellow, radius = size.minDimension * 0.18f)
            }
        }
    }
}

@Composable
internal fun RoutineProgressLeaves(
    done: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val visibleTotal = total.coerceAtLeast(5).coerceAtMost(8)
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(visibleTotal) { index ->
            LeafDot(active = index < done)
        }
    }
}

@Composable
private fun LeafDot(active: Boolean) {
    Canvas(modifier = Modifier.size(18.dp)) {
        rotate(degrees = -28f) {
            drawOval(
                color = if (active) GardenUi.LeafGreenDark else GardenUi.Line,
                topLeft = Offset(size.width * 0.22f, size.height * 0.06f),
                size = Size(size.width * 0.56f, size.height * 0.88f),
            )
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
