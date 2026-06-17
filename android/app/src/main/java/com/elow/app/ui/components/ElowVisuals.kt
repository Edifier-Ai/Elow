package com.elow.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.R
import com.elow.app.ui.theme.ElowColors

enum class LineIcon {
    Back,
    Info,
    Calendar,
    Share,
    Check,
    ArrowRight,
    Camera,
    Chart,
    Star,
    Home,
    Profile,
    Pencil
}

@Composable
fun LineIconView(
    icon: LineIcon,
    modifier: Modifier = Modifier,
    color: Color = ElowColors.Ink,
    strokeWidth: Float = 3.2f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        when (icon) {
            LineIcon.Back -> {
                drawLine(color, Offset(w * 0.64f, h * 0.18f), Offset(w * 0.34f, h * 0.5f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.34f, h * 0.5f), Offset(w * 0.64f, h * 0.82f), strokeWidth, cap = StrokeCap.Round)
            }
            LineIcon.Info -> {
                drawCircle(color, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.5f, h * 0.32f))
                drawLine(color, Offset(w * 0.5f, h * 0.45f), Offset(w * 0.5f, h * 0.68f), strokeWidth, cap = StrokeCap.Round)
            }
            LineIcon.Calendar -> {
                drawRoundRect(color, Offset(w * 0.18f, h * 0.2f), Size(w * 0.64f, h * 0.62f), CornerRadius(5f, 5f), style = stroke)
                drawLine(color, Offset(w * 0.18f, h * 0.38f), Offset(w * 0.82f, h * 0.38f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.34f, h * 0.13f), Offset(w * 0.34f, h * 0.28f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.66f, h * 0.13f), Offset(w * 0.66f, h * 0.28f), strokeWidth, cap = StrokeCap.Round)
                repeat(2) { row ->
                    repeat(3) { col ->
                        drawCircle(color, radius = w * 0.028f, center = Offset(w * (0.33f + col * 0.17f), h * (0.52f + row * 0.15f)))
                    }
                }
            }
            LineIcon.Share -> {
                drawLine(color, Offset(w * 0.5f, h * 0.17f), Offset(w * 0.5f, h * 0.62f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.32f, h * 0.35f), Offset(w * 0.5f, h * 0.17f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.68f, h * 0.35f), Offset(w * 0.5f, h * 0.17f), strokeWidth, cap = StrokeCap.Round)
                drawRoundRect(color, Offset(w * 0.24f, h * 0.48f), Size(w * 0.52f, h * 0.36f), CornerRadius(6f, 6f), style = stroke)
            }
            LineIcon.Check -> {
                drawLine(color, Offset(w * 0.22f, h * 0.55f), Offset(w * 0.42f, h * 0.74f), strokeWidth * 1.25f, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.42f, h * 0.74f), Offset(w * 0.78f, h * 0.25f), strokeWidth * 1.25f, cap = StrokeCap.Round)
            }
            LineIcon.ArrowRight -> {
                drawLine(color, Offset(w * 0.2f, h * 0.5f), Offset(w * 0.78f, h * 0.5f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.58f, h * 0.28f), Offset(w * 0.78f, h * 0.5f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.58f, h * 0.72f), Offset(w * 0.78f, h * 0.5f), strokeWidth, cap = StrokeCap.Round)
            }
            LineIcon.Camera -> {
                drawRoundRect(color, Offset(w * 0.16f, h * 0.3f), Size(w * 0.68f, h * 0.46f), CornerRadius(8f, 8f), style = stroke)
                drawRoundRect(color, Offset(w * 0.32f, h * 0.2f), Size(w * 0.24f, h * 0.14f), CornerRadius(5f, 5f), style = stroke)
                drawCircle(color, radius = w * 0.13f, center = Offset(w * 0.5f, h * 0.53f), style = stroke)
                drawCircle(color, radius = w * 0.03f, center = Offset(w * 0.72f, h * 0.42f))
            }
            LineIcon.Chart -> {
                drawRoundRect(color.copy(alpha = 0.18f), Offset(w * 0.12f, h * 0.1f), Size(w * 0.76f, h * 0.76f), CornerRadius(w * 0.38f, w * 0.38f))
                repeat(3) { index ->
                    val x = w * (0.34f + index * 0.16f)
                    val top = h * (0.58f - index * 0.11f)
                    drawRoundRect(color, Offset(x, top), Size(w * 0.09f, h * 0.22f + index * h * 0.08f), CornerRadius(5f, 5f))
                }
            }
            LineIcon.Star -> drawStarShape(color, filled = false, strokeWidth = strokeWidth)
            LineIcon.Home -> {
                val roof = Path().apply {
                    moveTo(w * 0.18f, h * 0.48f)
                    lineTo(w * 0.5f, h * 0.18f)
                    lineTo(w * 0.82f, h * 0.48f)
                }
                drawPath(roof, color, style = stroke)
                drawRoundRect(color, Offset(w * 0.28f, h * 0.46f), Size(w * 0.44f, h * 0.36f), CornerRadius(5f, 5f), style = stroke)
            }
            LineIcon.Profile -> {
                drawCircle(color, radius = w * 0.15f, center = Offset(w * 0.5f, h * 0.36f), style = stroke)
                drawArc(color, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(w * 0.27f, h * 0.47f), size = Size(w * 0.46f, h * 0.38f), style = stroke)
            }
            LineIcon.Pencil -> {
                drawLine(color, Offset(w * 0.24f, h * 0.72f), Offset(w * 0.7f, h * 0.26f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.62f, h * 0.18f), Offset(w * 0.78f, h * 0.34f), strokeWidth, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.2f, h * 0.8f), Offset(w * 0.36f, h * 0.76f), strokeWidth, cap = StrokeCap.Round)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStarShape(
    color: Color,
    filled: Boolean,
    strokeWidth: Float
) {
    val points = listOf(
        Offset(size.width * 0.5f, size.height * 0.13f),
        Offset(size.width * 0.6f, size.height * 0.4f),
        Offset(size.width * 0.86f, size.height * 0.4f),
        Offset(size.width * 0.64f, size.height * 0.57f),
        Offset(size.width * 0.72f, size.height * 0.84f),
        Offset(size.width * 0.5f, size.height * 0.68f),
        Offset(size.width * 0.28f, size.height * 0.84f),
        Offset(size.width * 0.36f, size.height * 0.57f),
        Offset(size.width * 0.14f, size.height * 0.4f),
        Offset(size.width * 0.4f, size.height * 0.4f)
    )
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    if (filled) {
        drawPath(path, color)
    } else {
        drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
fun GlossyActionButton(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
    trailingIcon: LineIcon = LineIcon.ArrowRight,
    onClick: () -> Unit
) {
    val dark = if (accent == ElowColors.WineRed) ElowColors.WineRedDark else ElowColors.PrimaryBlueDark
    Box(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(22.dp), ambientColor = accent.copy(alpha = 0.42f), spotColor = accent.copy(alpha = 0.46f))
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.92f), dark)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp, start = 6.dp, end = 6.dp, bottom = 34.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(Color.White.copy(alpha = 0.18f))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.width(28.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            LineIconView(icon = trailingIcon, color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 3.8f)
        }
    }
}

@Composable
fun RoundIconButton(
    icon: LineIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(10.dp, RoundedCornerShape(15.dp), ambientColor = ElowColors.Shadow, spotColor = ElowColors.Shadow)
            .clip(RoundedCornerShape(15.dp))
            .background(color.copy(alpha = 0.96f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        LineIconView(icon = icon, modifier = Modifier.size(size * 0.52f), color = ElowColors.Ink, strokeWidth = 3f)
    }
}

@Composable
fun SoftShelf(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(20.dp)
                .fillMaxSize()
                .padding(horizontal = 6.dp)
        )
    }
}

@Composable
fun BlueStar(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawOval(
                color = Color(0x1E1D5F9C),
                topLeft = Offset(size.width * 0.18f, size.height * 0.64f),
                size = Size(size.width * 0.72f, size.height * 0.32f)
            )
            drawOval(
                color = Color(0x26377FC5),
                topLeft = Offset(size.width * 0.28f, size.height * 0.68f),
                size = Size(size.width * 0.58f, size.height * 0.22f)
            )
        }
        CutoutAsset(
            drawableResId = R.drawable.elow_blue_star,
            contentDescription = "blue star",
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 4.dp, bottom = 5.dp)
        )
    }
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun SugarJarObject(modifier: Modifier = Modifier, cubeCount: Int = 9) {
    CutoutAsset(R.drawable.elow_sugar_jar, "sugar jar", modifier)
}

@Composable
fun CoinStackObject(modifier: Modifier = Modifier) {
    CutoutAsset(R.drawable.elow_coin_stack, "coin stack", modifier)
}

@Composable
fun ShelfPlankAsset(modifier: Modifier = Modifier) {
    CutoutAsset(
        drawableResId = R.drawable.elow_shelf_plank,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun PedestalAsset(modifier: Modifier = Modifier) {
    CutoutAsset(
        drawableResId = R.drawable.elow_pedestal_oval,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun ContactShadowAsset(modifier: Modifier = Modifier) {
    CutoutAsset(
        drawableResId = R.drawable.elow_contact_shadow,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun LockedBottleAsset(modifier: Modifier = Modifier) {
    CutoutAsset(
        drawableResId = R.drawable.elow_locked_bottle,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun CutoutAsset(
    drawableResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null
) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = contentDescription,
        contentScale = contentScale,
        alignment = alignment,
        colorFilter = colorFilter,
        modifier = modifier.fillMaxSize()
    )
}
