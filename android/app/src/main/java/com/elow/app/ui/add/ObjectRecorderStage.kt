package com.elow.app.ui.add

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType
import com.elow.app.ui.gl.ElowStageScene
import com.elow.app.ui.gl.GlVisualStage
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.SoftCard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ObjectRecorderStage(
    itemType: ItemType,
    itemDefinition: ItemDefinition = ItemCatalog.definitionFor(itemType),
    amountFraction: Double,
    strings: ElowStrings,
    onAmountChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val animatedFraction = remember(itemType) { Animatable(amountFraction.toFloat()) }
    var lastSnap by remember(itemType) { mutableStateOf(snapFraction(amountFraction.toFloat())) }
    val definition = itemDefinition
    val accent = if (definition.family == IntakeFamily.ALCOHOL && itemType == ItemType.WINE) {
        ElowColors.WineRed
    } else {
        ElowColors.PrimaryBlue
    }
    val displayMaxMl = displayMaxMl(itemType, definition)
    val displayAmountMl = (displayMaxMl * amountFraction).roundToInt()
    val recordFraction = displayAmountMl.toDouble() / definition.fullServingMl.toDouble()
    val metrics = MetricEstimator.estimate(definition, recordFraction)

    LaunchedEffect(itemType) {
        animatedFraction.snapTo(amountFraction.toFloat())
    }

    Box(
        modifier = modifier
            .pointerInput(itemType) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val next = (animatedFraction.value - dragAmount.y / size.height).coerceIn(0f, 1f)
                        val nearest = snapFraction(next)
                        if (nearest != lastSnap) {
                            lastSnap = nearest
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        scope.launch { animatedFraction.snapTo(next) }
                        onAmountChange(next.toDouble())
                    },
                    onDragEnd = {
                        val snapped = snapFraction(animatedFraction.value)
                        lastSnap = snapped
                        scope.launch {
                            animatedFraction.animateTo(
                                targetValue = snapped,
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    dampingRatio = 0.82f
                                )
                            )
                        }
                        onAmountChange(snapped.toDouble())
                    }
                )
            }
    ) {
        GlVisualStage(
            scene = ElowStageScene.AddObject(itemType, animatedFraction.value.toDouble()),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.82f)
                .fillMaxHeight(0.84f)
                .padding(top = 4.dp, bottom = 8.dp)
        )
        StageLevelGuide(
            itemType = itemType,
            fraction = animatedFraction.value,
            accent = accent,
            modifier = Modifier.align(Alignment.Center).fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricPill(strings.amount, "$displayAmountMl ml", accent)
            if (definition.family == IntakeFamily.SUGAR) {
                MetricPill(strings.estimatedSugar, "${metrics.sugarGrams.roundToInt()} g", ElowColors.Ink)
            } else {
                MetricPill(strings.about, strings.drinkCountValue(metrics.alcoholGrams / 14.0), ElowColors.WineRed)
            }
        }

        Ruler(
            fraction = animatedFraction.value,
            maxMl = displayMaxMl,
            accent = accent,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 4.dp, top = 34.dp, bottom = 34.dp)
        )
    }
}

@Composable
private fun StageLevelGuide(
    itemType: ItemType,
    fraction: Float,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val clamped = fraction.coerceIn(0f, 1f)
        val wine = itemType == ItemType.WINE
        val left = size.width * if (wine) 0.26f else 0.31f
        val right = size.width * if (wine) 0.72f else 0.68f
        val top = size.height * if (wine) 0.23f else 0.18f
        val bottom = size.height * if (wine) 0.69f else 0.78f
        val y = bottom - (bottom - top) * clamped
        val dash = PathEffect.dashPathEffect(floatArrayOf(11f, 8f), 0f)

        drawLine(
            color = Color.White.copy(alpha = 0.86f),
            start = Offset(left, y),
            end = Offset(right, y),
            strokeWidth = 5f,
            cap = StrokeCap.Round,
            pathEffect = dash
        )
        drawLine(
            color = accent.copy(alpha = 0.78f),
            start = Offset(left, y),
            end = Offset(right, y),
            strokeWidth = 2.8f,
            cap = StrokeCap.Round,
            pathEffect = dash
        )
        drawCircle(
            color = accent,
            radius = if (wine) 18f else 20f,
            center = Offset(right + size.width * 0.045f, y)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = if (wine) 10f else 11f,
            center = Offset(right + size.width * 0.045f, y - 4f)
        )
    }
}

@Composable
private fun MetricPill(label: String, value: String, accent: Color) {
    SoftCard(radius = 12.dp, elevation = 9.dp) {
        Column(modifier = Modifier.padding(horizontal = 11.dp, vertical = 10.dp)) {
            Text(label, color = ElowColors.Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = accent, fontSize = 21.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun Ruler(
    fraction: Float,
    maxMl: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val clamped = fraction.coerceIn(0f, 1f)
    val amount = (maxMl * clamped).roundToInt()

    Box(modifier = modifier.width(72.dp).height(320.dp)) {
        Text(
            "$maxMl ml",
            color = ElowColors.Ink,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Text(
            "$amount ml",
            color = ElowColors.Muted,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = ((1f - clamped) * 246f + 22f).dp)
        )
        Text(
            "0 ml",
            color = ElowColors.Muted,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp, top = 24.dp, bottom = 26.dp)
                .size(width = 34.dp, height = 254.dp)
        ) {
            val x = size.width * 0.72f
            drawLine(
                color = Color(0xFFCFD2D4),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 3.2f
            )
            repeat(11) { tick ->
                val y = size.height * tick / 10f
                drawLine(
                    color = Color(0xFFB7BABF),
                    start = Offset(x - if (tick % 5 == 0) 17f else 9f, y),
                    end = Offset(x + 1f, y),
                    strokeWidth = if (tick % 5 == 0) 2.8f else 2f
                )
            }
        }
        Canvas(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = ((1f - clamped) * 246f + 36f).dp, end = 1.dp)
                .size(width = 84.dp, height = 34.dp)
        ) {
            val y = size.height / 2f
            repeat(8) { tick ->
                val x = size.width * (0.14f + tick * 0.08f)
                drawLine(
                    color = Color(0xFFB7BABF),
                    start = Offset(x, y - if (tick % 3 == 0) 5f else 3f),
                    end = Offset(x, y + if (tick % 3 == 0) 5f else 3f),
                    strokeWidth = 2f
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = ((1f - clamped) * 246f + 38f).dp, end = 1.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(accent)
        )
    }
}

private fun displayMaxMl(itemType: ItemType, definition: ItemDefinition): Int =
    if (itemType == ItemType.WINE) 250 else definition.fullServingMl

private fun snapFraction(value: Float): Float {
    val stops = listOf(0f, 0.25f, 0.5f, 0.66f, 0.75f, 1f)
    return stops.minBy { kotlin.math.abs(it - value) }
}
