package com.paulos19.atelie.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DimColors.AccentSlate,
    onPrimary = DimColors.TextQuiet,
    primaryContainer = DimColors.BgElevated,
    onPrimaryContainer = DimColors.TextDimmed,
    secondary = DimColors.AccentActive,
    onSecondary = DimColors.TextQuiet,
    background = DimColors.BgCanvas,
    onBackground = DimColors.TextDimmed,
    surface = DimColors.BgSurface,
    onSurface = DimColors.TextDimmed,
    surfaceVariant = DimColors.BgElevated,
    onSurfaceVariant = DimColors.TextMuted,
    outline = DimColors.BorderSubtle,
    outlineVariant = DimColors.BorderFocus,
    error = DimColors.DangerMuted,
    onError = DimColors.TextQuiet
)

@Composable
fun AtelieTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DimColors.BgCanvas.toArgb()
                window.navigationBarColor = DimColors.BgCanvas.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
