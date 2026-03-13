package ca.boomerconx.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = Blue700,
    onPrimary = Gray50,
    primaryContainer = Blue300,
    secondary = Green700,
    onSecondary = Gray50,
    secondaryContainer = Green300,
    error = Red700,
    onError = Gray50,
    errorContainer = Red300,
    background = Gray50,
    onBackground = Gray900,
    surface = Gray50,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray800,
    outline = Gray600
)

private val HighContrastColorScheme = lightColorScheme(
    primary = HighContrastPrimary,
    onPrimary = HighContrastBackground,
    primaryContainer = Blue300,
    secondary = Green700,
    onSecondary = HighContrastBackground,
    secondaryContainer = Green300,
    error = HighContrastError,
    onError = HighContrastBackground,
    errorContainer = Red300,
    background = HighContrastBackground,
    onBackground = HighContrastOnBackground,
    surface = HighContrastBackground,
    onSurface = HighContrastOnBackground,
    surfaceVariant = Gray200,
    onSurfaceVariant = HighContrastOnBackground,
    outline = HighContrastOnBackground
)

@Composable
fun BoomerConXTheme(
    accessibilityState: AccessibilityState = AccessibilityState(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (accessibilityState.highContrast) {
        HighContrastColorScheme
    } else {
        LightColorScheme
    }

    CompositionLocalProvider(LocalAccessibilityState provides accessibilityState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BoomerTypography,
            content = content
        )
    }
}
