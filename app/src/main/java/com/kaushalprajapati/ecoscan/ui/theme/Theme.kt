package com.kaushalprajapati.ecoscan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = LeafGreen,
    onPrimary = CardWhite,
    primaryContainer = SkyMint,
    onPrimaryContainer = LeafGreenDark,
    secondary = SoilBrown,
    background = CardWhite,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = DangerRed
)

private val DarkColors = darkColorScheme(
    primary = LeafGreenLight,
    onPrimary = TextPrimary,
    primaryContainer = LeafGreenDark,
    onPrimaryContainer = SkyMint,
    secondary = SoilBrown,
    background = DarkBackground,
    surface = DarkBackground,
    error = DangerRed
)

@Composable
fun EcoScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = EcoScanTypography,
        content = content
    )
}
