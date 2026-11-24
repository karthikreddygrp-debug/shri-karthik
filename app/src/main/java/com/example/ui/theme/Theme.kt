package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = CrispWhite,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = NavyDeep,
    secondary = NavyPrimary,
    onSecondary = CrispWhite,
    secondaryContainer = SurfaceMist,
    onSecondaryContainer = NavyDeep,
    tertiary = DairyAccent,
    onTertiary = CrispWhite,
    tertiaryContainer = DairySkyEnd,
    onTertiaryContainer = NavyDeep,
    background = SurfaceSnow,
    onBackground = TextPrimaryLight,
    surface = CrispWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceMist,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderSubtle,
    outlineVariant = SurfaceMist
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueLight,
    onPrimary = NavyDeep,
    primaryContainer = NavySurface,
    onPrimaryContainer = CrispWhite,
    secondary = ElectricBlueContainer,
    onSecondary = NavyDeep,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = DairyAccent,
    onTertiary = CrispWhite,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = TextPrimaryDark,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder,
    outlineVariant = DarkSurfaceVariant
)

val ShriKarthikShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ShriKarthikTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShriKarthikTypography,
        shapes = ShriKarthikShapes,
        content = content
    )
}
