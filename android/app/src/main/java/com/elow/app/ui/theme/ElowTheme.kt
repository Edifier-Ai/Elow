package com.elow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElowColorScheme = lightColorScheme(
    primary = ElowColors.PrimaryBlue,
    onPrimary = Color.White,
    secondary = ElowColors.Gold,
    background = ElowColors.Background,
    surface = ElowColors.Surface,
    onSurface = ElowColors.Ink
)

@Composable
fun ElowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ElowColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

