package com.reyswumbo.lifeinbox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LifeInboxLightColors = lightColorScheme(
    primary = LifeInboxColors.Sage,
    onPrimary = LifeInboxColors.DarkText,
    primaryContainer = LifeInboxColors.PaleSage,
    onPrimaryContainer = LifeInboxColors.DarkText,
    secondary = LifeInboxColors.SoftYellow,
    onSecondary = LifeInboxColors.DarkText,
    secondaryContainer = LifeInboxColors.WarmCream,
    onSecondaryContainer = LifeInboxColors.DarkText,
    tertiary = LifeInboxColors.LightLavender,
    onTertiary = LifeInboxColors.DarkText,
    background = LifeInboxColors.Background,
    onBackground = LifeInboxColors.DarkText,
    surface = LifeInboxColors.SurfaceWhite,
    onSurface = LifeInboxColors.DarkText,
    surfaceVariant = LifeInboxColors.WarmCream,
    onSurfaceVariant = LifeInboxColors.SecondaryText,
    outline = LifeInboxColors.SecondaryText
)

@Composable
fun LifeInboxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LifeInboxLightColors,
        typography = LifeInboxTypography,
        content = content
    )
}
