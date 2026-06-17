package com.elow.app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ElowColors {
    val Background = Color(0xFFF7F8F7)
    val BackgroundTop = Color(0xFFFFFFFF)
    val BackgroundBottom = Color(0xFFF1F3F2)
    val Surface = Color(0xFFFFFFFF)
    val GlassSurface = Color(0xF7FFFFFF)
    val SurfaceWarm = Color(0xFFF4F1EA)
    val Ink = Color(0xFF171B20)
    val Muted = Color(0xFF6E747C)
    val Hairline = Color(0xFFE2E5E3)
    val PrimaryBlue = Color(0xFF247FE6)
    val PrimaryBlueDark = Color(0xFF1268C9)
    val WineRed = Color(0xFFE33A36)
    val WineRedDark = Color(0xFFC32222)
    val WineDark = Color(0xFF861B32)
    val SugarBlue = Color(0xFFEAF7FF)
    val AlcoholCream = Color(0xFFFFF4DF)
    val Gold = Color(0xFFE9B849)
    val Green = Color(0xFF79B96E)
    val Lavender = Color(0xFF8D72D9)
    val Shadow = Color(0x24000000)
    val DeepShadow = Color(0x33000000)
}

object ElowDimens {
    val ScreenPadding = 18.dp
    val CardRadius = 20.dp
    val TightRadius = 12.dp
    val BottomBarHeight = 96.dp
}

@Composable
fun LightShadowBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        ElowColors.BackgroundTop,
                        ElowColors.Background,
                        ElowColors.BackgroundBottom
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    radius: Dp = ElowDimens.CardRadius,
    color: Color = ElowColors.GlassSurface,
    elevation: Dp = 14.dp,
    border: BorderStroke? = BorderStroke(1.dp, Color.White.copy(alpha = 0.82f)),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = ElowColors.Shadow,
                spotColor = ElowColors.Shadow
            )
            .clip(shape)
            .background(color)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
    ) {
        content()
    }
}
